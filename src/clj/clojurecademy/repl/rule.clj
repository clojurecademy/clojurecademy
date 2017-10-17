(ns clojurecademy.repl.rule
  (:require [clojure.walk :as walk]
            [clojure.set :as s]
            [clojurecademy.repl.util :as util]
            [kezban.core :refer :all]))

(def external-blacklisted-symbols '#{push-thread-bindings pop-thread-bindings})

(defn- check-one-fn
  [rule body]
  (when (and (:only-use-one-fn? rule) (or (not= (count body) 1) ((complement list?) (first body))))
    (util/runtime-ex "Rule `only use one fn` is enabled.You should define a function and you can't define more than 1 function.")))

(defn- namespace-sym->sym
  [coll]
  (map (comp symbol name) coll))

(defn- post-walk
  [a coll body]
  (walk/postwalk (fn [s]
                   (when-let* [_ (symbol? s)
                               named-sym ((comp symbol name) s)
                               _ (util/in? named-sym coll)]
                              (swap! a (fn [v]
                                         (conj v named-sym))))) body))

(defn- find-used-fns-in-required-fns!
  [used-fns required-fns body]
  (post-walk used-fns required-fns body))

(defn- check-required-fn
  [rule body]
  (when (:required-fns rule)
    (check-one-fn rule body)
    (let [required-fns (set (namespace-sym->sym (:required-fns rule)))
          used-fns     (atom #{})]
      (find-used-fns-in-required-fns! used-fns required-fns body)
      (when-not (= required-fns @used-fns)
        (util/runtime-ex (str "The function does not contain required fns. -> " (vec (s/difference required-fns @used-fns))))))))

(defn check-external-blacklisted-symbols
  [body]
  (let [used-black-listed-symbols (atom #{})]
    (post-walk used-black-listed-symbols external-blacklisted-symbols body)
    (when (> (count @used-black-listed-symbols) 0)
      (util/runtime-ex (str "You've used blacklisted symbols: " (vec @used-black-listed-symbols))))))

(defn check-rules
  [subject body]
  (let [rule (-> subject :instruction :rule)]
    (check-one-fn rule body)
    (check-required-fn rule body)
    (check-external-blacklisted-symbols body)))

(defn get-restricted-fns
  [subject]
  (let [rule (-> subject :instruction :rule)]
    (if (:restricted-fns rule)
      (namespace-sym->sym (:restricted-fns rule))
      [])))