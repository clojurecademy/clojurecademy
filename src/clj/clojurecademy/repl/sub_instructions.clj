(ns clojurecademy.repl.sub-instructions
  (:require [clojurecademy.repl.util :as util]))


(defn get-err-msg-form-for-macro
  [test]
  `(case ~(:error-message-type (:is test))

     :none (format "%s\n"
                   ~(:error-message (:is test)))

     :simple (format "%s\n"
                     (or ~(:error-message (:is test)) "This assertion does not return true!"))

     :advanced (format "%s -> %s\n"
                       (quote ~(:form (:is test)))
                       (or ~(:error-message (:is test)) "This assertion does not return true!"))))


;;TODO refactor here
(defn- get-form
  [test]
  `(let [f#      '~(first (read-string (:form (:is test))))
         macro?# (util/macro? f#)]
     (if macro?#
       (if ~(read-string (:form (:is test)))
         true
         (util/runtime-ex ~(get-err-msg-form-for-macro test)))
       (let [form-eval# (util/form-eval f# [~@(drop 1 (read-string (:form (:is test))))])]
         (if (:result form-eval#)
           true
           (util/runtime-ex (case ~(:error-message-type (:is test))

                              :none (format "%s\n"
                                            ~(:error-message (:is test)))

                              :simple (format "%s -> %s\n"
                                              (:form form-eval#)
                                              (or ~(:error-message (:is test)) "This assertion does not return true!"))

                              :advanced (format "%s => %s -> %s\n"
                                                (quote ~(:form (:is test)))
                                                (:form form-eval#)
                                                (or ~(:error-message (:is test)) "This assertion does not return true!")))))))))


(defn get-sub-instructions
  [subject current-sub-ins]
  (if (-> subject :instruction :run-pre-tests?)
    (util/take-while-and-n-more #(not= current-sub-ins (:name %)) 1 (-> subject :instruction :sub-instructions))
    (keep #(when (= current-sub-ins (:name %)) %) (-> subject :instruction :sub-instructions))))


(defn get-sub-ins-tests-execution-ds
  [subject current-sub-ins]
  (let [sub-instructions (get-sub-instructions subject current-sub-ins)]
    (cons 'list (map (fn [test]
                       `(try
                          (assoc (util/with-out-str-data-map ~(get-form test))
                            :id '~(-> test :sub-ins :id)
                            :passed true
                            :sub-ins-name '~(-> test :sub-ins :name)
                            :index '~(-> test :sub-ins :index))
                          (catch Throwable t#
                            {:id            '~(-> test :sub-ins :id)
                             :passed        false
                             :sub-ins-name  '~(-> test :sub-ins :name)
                             :index         '~(-> test :sub-ins :index)
                             :exception-msg (util/client-exception-message t#)})))
                     (for [sub-ins sub-instructions
                           is      (-> sub-ins :testing :is)]
                       {:is      is
                        :sub-ins sub-ins})))))