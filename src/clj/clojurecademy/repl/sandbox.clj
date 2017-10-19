(ns clojurecademy.repl.sandbox
  (:require [clojure.string :as str]
            [clojure.set :as s]
            [clojail.testers :as testers]
            [clojail.core :refer [sandbox]]
            [clojurecademy.repl.util :as util]
            [kezban.core :refer :all]))


(def allowed-nses '#{clojure.core
                     clojure.string
                     clojure.set
                     clojure.pprint
                     clojure.walk})

(def allowed-packages #{"clojure"
                        "clojure.lang"
                        "clojure.core"
                        "clojure.core.protocols"
                        "clojure.pprint"
                        "java.lang"
                        "java.math"
                        "java.util"
                        "java.util.regex"
                        "java.util.stream"
                        "java.util.function"
                        "java.time"
                        "java.io"})

(def blacklist-symbols '#{alter-var-root
                          intern
                          eval
                          *read-eval*
                          ;read-string
                          ;catch
                          load-string
                          load-reader
                          loaded-libs
                          addMethod
                          resolve
                          ;deref
                          find-var
                          set!
                          ns
                          ns-publics
                          ns-unmap
                          ns-map
                          ns-interns
                          ns-resolve
                          ns-unalias
                          the-ns
                          in-ns
                          all-ns
                          create-ns
                          find-ns
                          refer
                          refer-clojure
                          ;push-thread-bindings
                          ;pop-thread-bindings
                          future-call
                          agent
                          send
                          send-off
                          pmap
                          pcalls
                          pvals
                          System/out
                          System/in
                          System/err
                          System/getProperty
                          with-redefs-fn
                          Class/forName})

(def blacklist-objects #{clojure.lang.RT
                         clojure.lang.Ref
                         clojure.lang.Var
                         clojure.lang.Compiler
                         clojure.lang.Reflector
                         clojure.lang.Namespace})

(defn- clojure-nses
  []
  (->> (all-ns)
       (map str)
       (filter #(str/starts-with? % "clojure."))
       (map symbol)))

(defn blacklist-nses
  []
  (s/difference (set (clojure-nses)) allowed-nses))

(defn blacklist-packages
  []
  (s/difference (->> (Package/getPackages)
                     (map #(.getName ^Package %))
                     set)
                allowed-packages))

(defn- tester-blacklist-nses
  []
  (testers/blacklist-nses (blacklist-nses)))

(defn- tester-blacklist-packages
  []
  (testers/blacklist-packages (blacklist-packages)))

(defn- tester-blacklist-symbols
  []
  (testers/blacklist-symbols blacklist-symbols))

(defn- tester-blacklist-objects
  []
  (testers/blacklist-objects blacklist-objects))

(defn- secure-tester
  []
  [(tester-blacklist-nses)
   (tester-blacklist-packages)
   (tester-blacklist-symbols)
   (tester-blacklist-objects)])

(defn- generate-repl-fns
  [body-form]
  (util/list-gen '(do)
                 (util/list-gen '(defn all-forms
                                   ([]
                                    (all-forms false)))
                                (util/list-gen '([with-ns?])
                                               (util/list-gen '(if with-ns?)
                                                              (util/list-gen '(quote) body-form)
                                                              (util/list-gen '(quote) (rest body-form)))))
                 '(do
                    (defmacro form-used?
                      ([form]
                       `((complement not-any?) #(= % '~form) '~(all-forms true))))

                    (defmacro form-used-nes?
                      ([form]
                       `((complement not-any?) #(= % '~form) (tree-seq seq? identity '~(all-forms true)))))

                    (defmacro fn-used?
                      [f]
                      `((complement not-any?) #(= % '~f) (filter symbol? (tree-seq seq? identity '~(all-forms true)))))

                    (defmacro ns-form
                      []
                      `(first '~(all-forms true)))

                    (defmacro first-form
                      []
                      `(first '~(all-forms)))

                    (defmacro second-form
                      []
                      `(second '~(all-forms)))

                    (defmacro nth-form
                      [n]
                      `(nth '~(all-forms) ~n nil))

                    (defmacro eval-ds
                      [ds]
                      `(eval ~ds))

                    (defmacro fills-the-blank?
                      [k form]
                      (if (= (count (all-forms)) 0)
                        `(throw (RuntimeException. "Empty form is not allowed!"))
                        (let [f-form         (first (seq (all-forms)))
                              ff-form        (if (string? f-form) (str "\"" f-form "\"") (str f-form))
                              final-form-str (clojure.string/replace (str form) (str k) ff-form)]
                          `(if ~(read-string final-form-str)
                             true
                             (throw (RuntimeException. (str ~final-form-str " -> This form does not return true!")))))))

                    (defmacro throws?
                      [ex-type code]
                      `(= ~ex-type (try
                                     ~code
                                     (catch Throwable t#
                                       (type t#))))))))

(defn- create-tester
  [additional-symbols]
  (conj (secure-tester) (testers/blacklist-symbols additional-symbols)))

(defn- load-deps []
  '(do
     (require '[clojurecademy.repl.util :as util])))

(defn- create-init-code
  [body-form]
  (util/list-gen '(do)
                 (load-deps)
                 (generate-repl-fns body-form)))

(defn- create-repl-init-code
  []
  (util/list-gen '(do)
                 (load-deps)))

(defn make-sandbox
  [sb-ns body-form additional-symbols]
  (sandbox (create-tester additional-symbols)
           :timeout 2500
           :namespace sb-ns
           :init (create-init-code body-form)))

(defn make-sandbox-for-helpers
  [sb-helper-ns]
  (sandbox (secure-tester)
           :timeout 2500
           :namespace sb-helper-ns))

(defn make-sandbox-for-repl
  [sb-ns body-form]
  (sandbox (secure-tester)
           :timeout 2500
           :namespace sb-ns
           :init (create-repl-init-code)))