(ns clojurecademy.repl.initial-code
  (:require [clojurecademy.repl.util :as util]))

(defn get-initial-code
  [subject]
  (let [initial-code (-> subject :instruction :initial-code)
        forms (some-> initial-code :form util/wrap-code read-string)]
    (if (= 'ns (ffirst forms))
      (cons 'do (rest forms))
      (cons 'do forms))))