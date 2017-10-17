(ns clojurecademy.repl.helper-fns)

(defn get-helper-fns-source
  [helper-fns]
  (reverse (reduce #(cons %2 %1) '(do) helper-fns)))
