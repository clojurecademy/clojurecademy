(ns clojurecademy.resource.home
  (:require [clojurecademy.util.resource :as resource.util :refer [resource]]
            [clojurecademy.view.home :as view.home]
            [clojurecademy.view.about :as view.about]
            [clojurecademy.view.404 :as view.404]
            [clojurecademy.dao.user :as user.dao]
            [compojure.core :as c]
            [compojure.route :as r]))


(resource home-page
          :get ["/"]
          :content-type :html
          :redirect-auth "/account"
          :handle-ok view.home/html)


(resource health-check
          :get ["/health"]
          :content-type :json
          :handle-ok (fn [_]
                       ;;checking transactor's health
                       (user.dao/create-dummy-user-foobar)
                       (when-not (= :user (:user/role (user.dao/find-user-by-username "foobar")))
                         ;;possible Datomic Bug!
                         (throw (RuntimeException. "DB deleted!")))
                       {:status "ok"}))


(resource about
          :get ["/about"]
          :content-type :html
          :handle-ok view.about/html)


;;exception....
(c/defroutes not-found
             (r/not-found (view.404/html)))

