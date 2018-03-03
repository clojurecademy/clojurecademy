(ns clojurecademy.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [compojure.core :as compojure]
            [clojurecademy.middleware.anti-forgery :as anti-forgery :refer [wrap-anti-forgery]]
            [clojurecademy.middleware.multipart-params :refer [wrap-multipart-params]]
            [clojurecademy.middleware.session-and-cookie :refer [wrap-session-and-cookie]]
            [clojurecademy.util.resource :as resource.util]
            [clojure.tools.namespace.find :as f]
            [prone.middleware :refer [wrap-exceptions]]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojurecademy.util.config :as conf]
            [clojurecademy.util.logging :as log]
            [clojurecademy.dao.db :as db]
            [clojure.java.io :as io])
  (:gen-class)
  (:import (java.io File)))

(def ignored-csrf-paths ["/course/upload"])


(def handler (-> (resource.util/get-routes "clojurecademy.resource." 'clojurecademy.resource.home/not-found)
                 (wrap-session-and-cookie ignored-csrf-paths)
                 (wrap-anti-forgery {:ignored-paths ignored-csrf-paths})
                 (wrap-multipart-params {:max-file-size 11000000})
                 wrap-params
                 (wrap-defaults (dissoc (dissoc site-defaults :security :anti-forgery) :params :multipart))
                 wrap-exceptions
                 wrap-reload
                 wrap-gzip))

(defn- init!
  []
  (conf/init!)
  (db/create-db!))

(defn start
  [port]
  (jetty/run-jetty handler {:port port}))

;;TODO there is a bug in resume percentage -> it does not count no sub ins!!!
(defn -main
  []
  (init!)
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (start port)))

(comment
  (-main))
