(ns clojurecademy.util.config
  (:refer-clojure :exclude [get])
  (:require [clojure.java.io :as io]
            [cpath-clj.core :as cp]))


(def conf (atom nil))


(defn init!
  []
  (doseq [[path uris] (cp/resources (io/resource "config.edn"))
          :let [uri (first uris)]]
    (reset! conf (read-string (slurp (str uri))))))


(defn get
  [& ks]
  (if (seq ks)
    (get-in @conf ks)
    @conf))