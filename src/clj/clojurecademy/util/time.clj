(ns clojurecademy.util.time
  (:import (java.util Date)))

(defn now
  []
  (.getTime (Date.)))
