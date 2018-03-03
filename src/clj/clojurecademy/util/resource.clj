(ns clojurecademy.util.resource
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [liberator.representation :as rep]
            [clojurecademy.dao.db :as db]
            [liberator.core :as liberator]
            [clojurecademy.dao.user :as user.dao]
            [compojure.core :as compojure]
            [kezban.core :refer :all]
            [clojurecademy.util.logging :as log]
            [clojurecademy.util.config :as conf]
            [postal.core :as postal]
            [clojure.tools.namespace.find :as f])
  (:import (java.util Date UUID)
           (java.io File)))

(def json-media-types ["application/json" "application/json; charset=utf-8" "application/json; charset=UTF-8"])

(def html-media-types ["text/html" "text/html; charset=utf-8" "text/html; charset=UTF-8"])

(def multipart-media-types ["application/octet-stream" "multipart/form-data" "multipart/mixed" "multipart/related"])


(defn- body-as-string
  [ctx]
  (if-let [body (get-in ctx [:request :body])]
    (condp instance? body
      String body
      (slurp (io/reader body)))))


(defn check-content-type
  [ctx content-types]
  (if (#{:put :post} (get-in ctx [:request :request-method]))
    (or
      (some #{(get-in ctx [:request :headers "content-type"])}
            content-types)
      [false {:message "Unsupported Content-Type"}])
    true))


(defn parse-json
  [ctx key]
  (when (#{:put :post} (get-in ctx [:request :request-method]))
    (try
      (if-let [body (body-as-string ctx)]
        (let [data (json/read-str body)]
          [false {key data}])
        {:message "No body"})
      (catch Exception e
        (.printStackTrace e)
        {:message (format "IOException: %s" (.getMessage e))}))))


(defn create-cookie
  [cookie]
  (str "user=" cookie "; expires="
       (->> (* 1000 60 60 24 365 10)
            (+ (.getTime (Date.)))
            (Date.)
            (.toString))
       "; path=/; HttpOnly; SameSite=strict"))


(defn delete-cookie
  [cookie]
  (str "user=" cookie "; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/; HttpOnly"))


(defn convert-data-map
  "Converts clojure map's string keywords to keyword functions"
  [json-clojure-map]
  (reduce #(assoc %1 (keyword (first %2)) (second %2)) {} json-clojure-map))


(defn get-cookie
  [ctx]
  (-> ctx :request :cookies (get "user") :value))


(defn get-username-from-cookie
  [ctx]
  (if-let [cookie (get-cookie ctx)]
    (.substring cookie 0 (str/index-of cookie "&"))))


(defn fix-if-conn-ex
  [ex]
  (let [err-msg (.getMessage ex)]
    (when (str/includes? err-msg ":db.error")
      (future
        (try
          (postal/send-message {:user (conf/get :email :user) :pass (conf/get :email :password)
                                :host (conf/get :email :host)
                                :port 587}
                               {:from    "\"Clojurecademy\" <noreply@clojurecademy.com>" :to (conf/get :emergency-email)
                                :subject "Datomic Failure!" :body (str "Error message: " err-msg "\n" ex)})
          (catch Exception e
            (log/error "Emergency mail couldn't be sent." e)))))
    (when (any? #(str/includes? err-msg %) [":db.error/transactor-unavailable" ":db.error/connection-released"])
      (db/establish-conn))))


(defn get-exception-message
  [ctx]
  (let [e   (:exception ctx)
        msg (.getMessage e)]
    (log/error msg e)
    (fix-if-conn-ex e)
    {:error msg}))


(defn random-password
  ([] (random-password 9))
  ([n]
   (let [chars    (map char (range 35 127))
         password (take n (repeatedly #(rand-nth chars)))]
     (apply str password))))


(defn generate-cookie
  []
  (str "&" (-> (UUID/randomUUID)
               .toString
               (.replace "-" ""))))


(defn error
  [^String msg]
  (throw (RuntimeException. msg)))


(defn create-cookie-if-no-exception
  [d ctx]
  (if-not (:exception ctx)
    (-> (rep/as-response d ctx)
        (assoc-in [:headers "Set-Cookie"] (create-cookie (-> ctx :user-cookie))))
    (rep/as-response d ctx)))


(defn delete-cookie-if-no-exception
  [d ctx]
  (if-not (:exception ctx)
    (-> (rep/as-response d ctx)
        (assoc-in [:headers "Set-Cookie"] (delete-cookie (-> ctx :user :user/cookie))))
    (rep/as-response d ctx)))


(defn authorized?
  [ctx]
  (when-let* [cookie (get-cookie ctx)
              username (get-username-from-cookie ctx)
              user (user.dao/find-user-by-username username)
              _ (= cookie (:user/cookie user))]
             {:user user}))


(defn check
  ([result err-msg]
   (when-not result
     (error err-msg)))
  ([fn val err-msg]
   (when-not (fn val)
     (error err-msg))))


(defn get-method-map
  [method]
  (if (= method :put)
    {:allowed-methods      [:put]
     :new?                 false
     :respond-with-entity? true}
    {:allowed-methods [method]}))


(defn get-media-type-map
  [media-type]
  (case media-type
    :json {:available-media-types json-media-types
           :known-content-type?   #(check-content-type % json-media-types)
           :malformed?            #(parse-json % :request-data)
           :handle-exception      #(get-exception-message %)}
    :html {:available-media-types html-media-types
           :handle-exception      (fn [ctx]
                                    (let [ex      (:exception ctx)
                                          err-msg (.getMessage ex)]
                                      (log/error err-msg ex)
                                      (fix-if-conn-ex ex)
                                      "Something went wrong"))}
    :multipart {:available-media-types multipart-media-types
                :handle-exception      (fn [ctx]
                                         (let [ex      (:exception ctx)
                                               err-msg (.getMessage ex)]
                                           (log/error err-msg ex)
                                           (fix-if-conn-ex ex)
                                           err-msg))}))


(defn multi-params->map
  [params]
  (->> params
       (partition 2)
       (map vec)
       (into {})))


(defn get-handle-ok-or-create-map
  [method media-type]
  (cond
    (and (= method :put) (= media-type :json))
    {:handle-ok (fn [& args] {:success true})}

    (and (= method :post) (= media-type :json))
    {:handle-created (fn [& args] {:success true})}))


(defn get-redirect-map-based-on-auth
  [m]
  (when-let [path (or (:redirect-auth m) (:redirect-not-auth m))]
    {:authorized?        #(cond
                            (and (:redirect-auth m) (authorized? %))
                            {:redirect-required? true
                             :redirect-path      (:redirect-auth m)}

                            (and (:redirect-not-auth m) (not (authorized? %)))
                            {:redirect-required? true
                             :redirect-path      (:redirect-not-auth m)}

                            :else
                            {:redirect-required? false})
     :moved-temporarily? (fn [ctx]
                           {:location (or (:redirect-path ctx) "/")})}))


(defn get-redirect-based-on-pred
  [m]
  (let [r (:redirect! m)]
    (if (fn? r)
      (let [result (atom nil)
            path   (atom nil)]
        {:exists?            (fn [ctx]
                               ;;TODO CHECK HERE!!!!
                               (let [[result-i path-i] (r ctx)]
                                 (reset! result result-i)
                                 (reset! path path-i))
                               (not @result))
         :existed?           (fn [_] @result)
         :moved-temporarily? (fn [_]
                               {:location (or @path "/")})
         :redirect-vec       [result path]})
      (when-let [[result path] (:redirect! m)]
        {:exists?            (fn [_] (not result))
         :existed?           (fn [_] result)
         :moved-temporarily? (fn [_]
                               {:location (or path "/")})
         :redirect-vec       [result path]}))))


(defn get-auth-and-redirect-maps
  [m]
  (let [auth-req      (and (:auth-required? m) {:authorized? authorized?})
        redirect-auth (get-redirect-map-based-on-auth m)
        exit-maps     {:exists?  (fn [ctx] (not (:redirect-required? ctx)))
                       :existed? (fn [ctx] (:redirect-required? ctx))}
        redirect      (get-redirect-based-on-pred m)]
    (merge auth-req redirect-auth exit-maps redirect)))


(defmacro resource
  [name method endpoint-and-binding _ media-type & opts]
  (let [resource-name (symbol (str "resource-" name))]
    `(def ~(vary-meta resource-name assoc :resource? true)
       (compojure/defroutes ~name
                            (~(case method
                                :get `compojure/GET
                                :post `compojure/POST
                                :put `compojure/PUT
                                :delete `compojure/DELETE)
                              ~(first endpoint-and-binding)
                              ~(if (seq (second endpoint-and-binding)) (second endpoint-and-binding) [])
                              (let [method-map#     ~(get-method-map method)
                                    type-map#       ~(get-media-type-map media-type)
                                    m#              ~(multi-params->map opts)
                                    handle-ok-maps# ~(get-handle-ok-or-create-map method media-type)
                                    auth-maps#      (get-auth-and-redirect-maps m#)
                                    r#              (merge method-map# type-map# handle-ok-maps# auth-maps# m#)]
                                (liberator/resource r#)))))))


(defn get-routes
  [resource-base-ns not-found-symbol]
  (let [resource-vars (->> (System/getProperty "user.dir")
                           (File.)
                           f/find-namespaces-in-dir
                           (filter #(clojure.string/starts-with? % resource-base-ns))
                           (map #(do (require %) %))
                           (reduce #(conj %1 (vals (ns-publics %2))) [])
                           flatten
                           (filter #(:resource? (meta %)))
                           vec)
        all-routes    (conj resource-vars (resolve not-found-symbol))]
    (apply compojure/routes all-routes)))


(defn create-google-analytics-code
  [ga]
  (str "(function(i,s,o,g,r,a,m)
  {i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();
  a=s.createElement(o),\n  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n  })
  (window,document,'script','https://www.google-analytics.com/analytics.js','ga');\n\n
  ga('create', " (str "'" ga "'") ", 'auto');\n  ga('send', 'pageview');\n"))


(defn create-google-analytics-code-spa
  []
  (str "(function(i,s,o,g,r,a,m)
  {i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();
  a=s.createElement(o),\n  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n  })
  (window,document,'script','https://www.google-analytics.com/analytics.js','ga');"))
