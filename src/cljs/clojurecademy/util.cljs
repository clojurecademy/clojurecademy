(ns clojurecademy.util
  (:require [ajax.core :as ajax :refer [GET POST PUT DELETE]]
            [clojurecademy.validation :as validation]
            [clojure.string :as str]
            [clojure.set :as set]
            [goog.dom :as dom]
            [goog.net.cookies :as cks]))

(defn create-field-val-map
  [field-ids]
  (reduce #(assoc %1 (keyword (.-name (dom/getElement %2)))
                     (.-value (dom/getElement %2)))
          {} field-ids))

(defn get-cookie
  [k]
  (.get goog.net.cookies (name k) nil))

(defn change-url
  [path]
  (set! window.location.href path))

(defn get-auth-token
  []
  (-> (get-cookie "auth-token")
      (str/replace "%2F" "/")
      (str/replace "%2B" "+")
      (str/replace "%3D" "=")))

(defn- set-innerText!
  [id text]
  (set! (.-innerText (dom/getElement id)) text))

(defn- set-value!
  [id text]
  (set! (.-value (dom/getElement id)) text))

(defn- show-text
  ([id text]
   (show-text id text "black"))
  ([id text color]
   (when-let [dom (dom/getElement id)]
     (set! (-> dom .-style .-color) color)
     (set-innerText! id text))))

(defn show-error-text
  [id text]
  (show-text id text "red"))

(defn show-success-text
  [id text]
  (show-text id text "green")
  (js/setTimeout #(set-innerText! id "") 5000))

(defn redirect!
  [path]
  (set! js/window.location path))

(defn scroll-to-top
  []
  (.scrollTo js/window 0 0))

(defn valid-input?
  [error-id validators]
  (loop [v validators]
    (if (seq v)
      (let [[result err-msg] (first v)]
        (if result
          (recur (rest v))
          (do (show-error-text error-id err-msg) (scroll-to-top) false)))
      true)))

(defn clear-input-fields
  [[id & others :as field-ids]]
  (when (seq field-ids)
    (set! (-> (dom/getElement id) .-value) "")
    (recur others)))

(defn any-pred
  [& preds]
  (complement (apply every-pred (map complement preds))))

(defn call-fn-if-enter-pressed
  [f]
  (fn [e]
    (when (= 13 (.-keyCode e))
      (f e))))

(defn- ajax-key->ajax-type
  [type]
  (case type
    :get GET
    :post POST
    :put PUT
    :delete DELETE
    GET))

(defn- create-opts-map
  [opts]
  (let [m {:headers         {"X-CSRF-Token" (get-auth-token)}
           :format          (ajax/json-request-format)
           :response-format (ajax/json-response-format {:keywords? true})}]
    (merge m (-> (into {} (map vec (partition 2 opts)))
                 (set/rename-keys {:data    :params
                                   :success :handler
                                   :error   :error-handler})))))

(defn ajax
  [type uri & opts]
  ((ajax-key->ajax-type type) uri (create-opts-map opts)))

(defn set-event-handler!
  [attr id fn]
  (some-> (dom/getElement id) (aset attr fn)))