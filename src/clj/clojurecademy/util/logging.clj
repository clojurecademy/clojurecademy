(ns clojurecademy.util.logging
  (:import (org.slf4j LoggerFactory)
           (java.util UUID)))

(defmacro info
  ([^String msg]
   `(let [l# (LoggerFactory/getLogger ^String (str ~*ns*))]
      (.info l# ~msg)))
  ([^String msg ^Throwable t]
   `(let [l# (LoggerFactory/getLogger ^String (str ~*ns*))]
      (.info l# ~msg ~t))))


(defmacro debug
  ([^String msg]
   `(let [l# (LoggerFactory/getLogger ^String (str ~*ns*))]
      (.debug l# ~msg)))
  ([^String msg ^Throwable t]
   `(let [l# (LoggerFactory/getLogger ^String (str ~*ns*))]
      (.debug l# ~msg ~t))))


(defmacro error
  ([^String msg]
   `(let [l# (LoggerFactory/getLogger ^String (str ~*ns*))]
      (.error l# (str "Error id: " (UUID/randomUUID) " | " ~msg))))
  ([^String msg ^Throwable t]
   `(let [l# (LoggerFactory/getLogger ^String (str ~*ns*))]
      (.error l# (str "Error id: " (UUID/randomUUID) " | " ~msg) ~t))))


(defmacro warn
  ([^String msg]
   `(let [l# (LoggerFactory/getLogger ^String (str ~*ns*))]
      (.warn l# ~msg)))
  ([^String msg ^Throwable t]
   `(let [l# (LoggerFactory/getLogger ^String (str ~*ns*))]
      (.warn l# ~msg ~t))))