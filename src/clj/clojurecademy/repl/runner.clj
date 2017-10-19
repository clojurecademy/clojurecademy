(ns clojurecademy.repl.runner
  (:require [clojurecademy.repl.sandbox :as sandbox]
            [clojurecademy.repl.rule :as rule]
            [clojurecademy.repl.deps :as deps]
            [clojurecademy.repl.initial-code :as initial-code]
            [clojurecademy.repl.helper-fns :as helper-fns]
            [clojurecademy.repl.sub-instructions :as sub-ins]
            [clojurecademy.repl.java :as java]
            [clojurecademy.repl.util :as util]
            [clojurecademy.util.logging :as log]
            [clojure.pprint :as pp]
            [clojure.set :as set])
  (:import (java.util ArrayList)
           (java.util.concurrent TimeoutException)
           (java.io StringWriter)))


(def current-executions (atom {}))
(def sb-ns-prefix "sandbox-ns-")
(def sb-helper-ns-prefix "sandbox-helper-ns-")


(defn- get-body-form
  [subject body]
  (if (= 'ns (and (list? (first body)) (ffirst body)))
    body
    (let [ns* (some-> subject :instruction :initial-code :form util/wrap-code read-string)]
      (if (= 'ns (ffirst ns*))
        (cons ns* body)
        (cons (util/list-gen '(ns) (-> subject :ns)) body)))))


(defn- get-repl-body-form
  [body]
  (if (= 'ns (and (list? (first body)) (ffirst body)))
    body
    (cons '(ns dummy-ns) body)))


(defn- wrap-ex
  [form err-msg]
  (util/list-gen '(try)
                 form
                 (util/list-gen '(catch Exception e)
                                (util/list-gen '(throw)
                                               (util/list-gen '(RuntimeException.)
                                                              (util/list-gen '(str)
                                                                             err-msg
                                                                             '(.getMessage e)))))))


(defn- has-instruction?
  [code-map]
  (some? (-> code-map :subject :instruction)))


(defn- get-code-body-and-test-sub-ins
  [code-map]
  (if (has-instruction? code-map)
    {:code-body     (util/list-gen '(util/with-out-str-data-map) (:body code-map))
     :sub-ins-tests (sub-ins/get-sub-ins-tests-execution-ds (-> code-map :subject) (-> code-map :current-sub-ins))}
    {:code-body (util/list-gen '(util/with-out-str-data-map) (:body code-map))}))


(defn- get-sb-code-ds
  [code-map]
  (util/list-gen '(do)
                 (wrap-ex (deps/get-deps (:ns code-map)) "Something went wrong when installing dependencies.")
                 ;;TODO why initial code here???!!!
                 (wrap-ex (initial-code/get-initial-code (:subject code-map)) "Something went wrong when installing initial code.")
                 (wrap-ex (java/get-java-deps (:ns code-map)) "Something went wrong when checking Java dependencies.")
                 (get-code-body-and-test-sub-ins code-map)))

(defn- get-repl-sb-code-ds
  [code-map]
  (util/list-gen '(do)
                 (wrap-ex (deps/get-deps (:ns code-map)) "Something went wrong when installing dependencies.")
                 (wrap-ex (java/get-java-deps (:ns code-map)) "Something went wrong when checking Java dependencies.")
                 (mapv #(list 'util/with-out-str-data-map %) (:body code-map))))


(defn- check-helper-fns
  [helper-fns-ds username]
  (let [helper-checker-ns (gensym (str "helper-checker-ns-" username))]
    (try
      ((sandbox/make-sandbox-for-helpers helper-checker-ns) helper-fns-ds)
      (catch Throwable t
        (throw (RuntimeException. ^String (str "Your helper fns are invalid : " (.getMessage t)))))
      (finally
        (remove-ns helper-checker-ns)))))


(defn- create-helper-fns-ns
  [sb-ns sb-helper-ns helper-fns-ds]
  (binding [*ns* (create-ns sb-helper-ns)]
    (refer-clojure)
    (eval helper-fns-ds)
    (binding [*ns* (the-ns sb-ns)]
      (refer sb-helper-ns))))


;;TODO refactor!!!
(defn eval-code
  [username subject-and-current-sub-ins helper-fns body]
  (try
    (if (@current-executions username)
      (util/runtime-ex "You have an execution going on, you can't eval multiple code at the same time!")
      (let [_               (swap! current-executions assoc username true)
            subject         (:subject subject-and-current-sub-ins)
            body-form       (get-body-form subject (read-string (str "(\n" body "\n)")))
            rest-of-body    (rest body-form)
            ns-form         (first body-form)
            body-without-ns (cons 'do rest-of-body)]
        (rule/check-rules subject rest-of-body)
        (let [helper-fns-ds (or (helper-fns/get-helper-fns-source helper-fns) '(do))
              _             (check-helper-fns helper-fns-ds username)
              sb-ns         (symbol (str sb-ns-prefix username))
              sb-helper-ns  (symbol (str sb-helper-ns-prefix username))
              sb            (sandbox/make-sandbox sb-ns body-form (rule/get-restricted-fns subject))
              _             (create-helper-fns-ns sb-ns sb-helper-ns helper-fns-ds)
              before        (set (vals (ns-map (the-ns sb-ns))))
              complete-ds   (get-sb-code-ds {:ns              ns-form
                                             :body            body-without-ns
                                             :subject         subject
                                             :current-sub-ins (:current-sub-ins subject-and-current-sub-ins)})
              result        (sb complete-ds)
              after         (set (vals (ns-map (the-ns sb-ns))))]
          (if-let [f (first (set/difference before after))]
            (throw (RuntimeException. ^String (str "You can not override built-in function: `" (-> f meta :name) "`")))
            result))))
    (catch TimeoutException e
      (log/error (str "Eval timeout occurred. User: " username) e)
      {:error         true
       :exception-msg "Execution Time Out! Your code execution time took more than 2.5 seconds."})
    (catch SecurityException e
      (log/error (str "Bad code. User: " username) e)
      {:error         true
       :exception-msg (str "You can not use blacklisted functions, symbols, namespaces etc. " (util/client-exception-message e))})
    (catch Exception e
      (log/error (str "Eval exception occurred. User: " username) e)
      {:error         true
       :exception-msg (util/client-exception-message e)})
    (catch Throwable t
      (log/error (str "Eval throwable occurred. User: " username) t)
      {:error         true
       :exception-msg (util/client-exception-message t)})
    (finally
      (swap! current-executions dissoc username)
      (-> (str sb-ns-prefix username) symbol remove-ns)
      (-> (str sb-helper-ns-prefix username) symbol remove-ns))))


(defn eval-repl-code
  [username body]
  (try
    (if (@current-executions username)
      (util/runtime-ex "You have an execution going on, you can't eval multiple code at the same time!")
      (let [_            (swap! current-executions assoc username true)
            body-form    (get-repl-body-form (read-string (str "(\n" body "\n)")))
            rest-of-body (rest body-form)
            _            (rule/check-external-blacklisted-symbols rest-of-body)
            ns-form      (first body-form)
            sb-ns        (symbol (str sb-ns-prefix username))
            sb           (sandbox/make-sandbox-for-repl sb-ns body-form)
            complete-ds  (get-repl-sb-code-ds {:ns ns-form :body rest-of-body})
            err-writer   (StringWriter.)]
        {:results (sb complete-ds {#'*err* err-writer})
         :err-str (str err-writer)}))
    (catch TimeoutException e
      (log/error (str "Eval timeout occurred. User: " username) e)
      {:error         true
       :exception-msg "Execution Time Out! Your code execution time took more than 2.5 seconds."})
    (catch SecurityException e
      (log/error (str "Bad code. User: " username) e)
      {:error         true
       :exception-msg (str "You can not use blacklisted functions, symbols, namespaces etc. " (util/client-exception-message e))})
    (catch Exception e
      (log/error (str "Eval exception occurred. User: " username) e)
      {:error         true
       :exception-msg (util/client-exception-message e)})
    (catch Throwable t
      (log/error (str "Eval throwable occurred. User: " username) t)
      {:error         true
       :exception-msg (util/client-exception-message t)})
    (finally
      (swap! current-executions dissoc username)
      (-> (str sb-ns-prefix username) symbol remove-ns))))