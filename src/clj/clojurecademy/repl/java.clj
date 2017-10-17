(ns clojurecademy.repl.java
  (:require [clojurecademy.repl.util :as util]
            [kezban.core :refer :all]))

(defn- get-import-deps
  [ns-form]
  (->> ns-form
       (filter #(and (list? %) (= :import (first %))))
       (apply concat)
       (filter (complement keyword?))))

(defn- sym-dep->str-dep
  [dep]
  (str dep))

(defn- package-dep->str-dep
  [package-form]
  (let [package (first package-form)]
    (reduce (fn [v c]
              (conj v (str package "." c))) [] (drop-first package-form))))

(defn- dep->str
  [v dep]
  (conj v (if (sequential? dep)
            (package-dep->str-dep dep)
            (sym-dep->str-dep dep))))

;;We are doing this because Clojail sometimes does not support sandboxing for Java classes,
;;so we make it clear for Clojail to see those classes
(defn get-java-deps
  [ns-form]
  (->> (get-import-deps ns-form)
       (reduce dep->str [])
       (flatten)
       (map #(Class/forName %))
       (util/list-gen '(do))
       (flatten)))