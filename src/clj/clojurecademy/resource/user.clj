(ns clojurecademy.resource.user
  (:require [clojurecademy.util.resource :as resource.util :refer [resource]]
            [clojurecademy.controller.user :as user.controller]
            [clojurecademy.view.account-details :as view.account-details]
            [clojurecademy.view.change-password :as view.change-password]
            [clojurecademy.dao.user :as user.dao]))


(defn- auth-with-redirect
  [ctx]
  (if-let [user (resource.util/authorized? ctx)]
    (merge user {:redirect-required? false})
    {:redirect-required? true}))


(resource update-account-details
          :put ["/account"]
          :content-type :json
          :auth-required? true
          :put! #(user.controller/update-account-details :request-data %))


(resource account-details-page
          :get ["/account"]
          :content-type :html
          :authorized? auth-with-redirect
          :redirect-not-auth "/"
          :handle-ok #(view.account-details/html (:user %)))


(resource change-password
          :put ["/account/changepassword"]
          :content-type :json
          :auth-required? true
          :put! #(user.controller/change-password :request-data %))


(resource update-editor-preferences
          :put ["/account/update-editor-preferences"]
          :content-type :json
          :auth-required? true
          :put! #(user.controller/update-preferences :request-data %))


(resource change-password-page
          :get ["/account/changepassword"]
          :content-type :html
          :authorized? auth-with-redirect
          :redirect-not-auth "/"
          :handle-ok #(view.change-password/html (-> % :user :user/username)))


(resource user-profile-page
          :get ["/users/:username" [username]]
          :content-type :html
          :redirect! [(not (user.dao/find-user-id-by-username username)) "/404"]
          :handle-ok #(user.controller/get-user-profile username %))


(resource stats
          :get ["/stats"]
          :content-type :html
          :allowed? #(some-> % resource.util/authorized? :user :user/role (= :admin))
          :handle-ok user.controller/stats)