(ns clojurecademy.middleware.session-and-cookie
  (:require [clojurecademy.util.resource :as resource.util]
            [clojurecademy.middleware.anti-forgery :as anti-forgery]
            [clojure.tools.logging :as log]
            [kezban.core :refer :all]))


(defn- get-request?
  [{method :request-method}]
  (or (= method :head)
      (= method :get)
      (= method :options)))


(defn wrap-session-and-cookie
  [handler ignored-csrf-paths]
  (fn [request]
    (let [auth-token "auth-token"]
      (log/info (str "Request path: " (:uri request)))
      (if (and (in? (:uri request) ignored-csrf-paths) (not (get-request? request)))
        (handler request)
        (let [response   (handler request)
              cookie-val (:value (get (:cookies request) auth-token))
              token      anti-forgery/*anti-forgery-token*]
          (if (= cookie-val token)
            response
            (assoc response :cookies {auth-token {:value token :same-site :strict :path "/"}})))))))