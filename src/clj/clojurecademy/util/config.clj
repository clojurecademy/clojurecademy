(ns clojurecademy.util.config
  (:refer-clojure :exclude [get])
  (:require [clojure.java.io :as io]
            [cpath-clj.core :as cp]))


(def conf (atom nil))
(def clojure-jobs (atom nil))


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


(defn get-clojure-jobs!
  []
  (if @clojure-jobs
    @clojure-jobs
    (do
      (doseq [[path uris] (cp/resources (io/resource "clojure-jobs.edn"))
              :let [uri (first uris)]]
        (reset! clojure-jobs (read-string (slurp (str uri)))))
      @clojure-jobs)))