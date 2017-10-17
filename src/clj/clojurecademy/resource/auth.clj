(ns clojurecademy.resource.auth
  (:require [clojurecademy.util.resource :as resource.util :refer [resource]]
            [clojurecademy.view.login :as view.login]
            [clojurecademy.view.forgotpass :as view.forgotpass]
            [clojurecademy.view.reset-password :as view.recover-password]
            [clojurecademy.controller.auth :as auth.controller]))


(resource signup
          :post ["/signup"]
          :content-type :json
          :post! #(auth.controller/signup (:request-data %))
          :as-response (fn [d ctx] (resource.util/create-cookie-if-no-exception d ctx)))


(resource login
          :put ["/login"]
          :content-type :json
          :redirect-auth "/"
          :put! #(auth.controller/login (:request-data %))
          :as-response (fn [d ctx] (resource.util/create-cookie-if-no-exception d ctx)))


(resource login-page
          :get ["/login"]
          :content-type :html
          :redirect-auth "/"
          :handle-ok view.login/html)


(resource logout
          :put ["/logout"]
          :content-type :json
          :auth-required? true
          :as-response (fn [d ctx] (resource.util/delete-cookie-if-no-exception d ctx)))


(resource forgot-password-page
          :get ["/forgotpass"]
          :content-type :html
          :redirect-auth "/"
          :handle-ok view.forgotpass/forgotpass-page-body)


(resource activate
          :get ["/activate" [username activation-key]]
          :content-type :html
          :redirect! (fn [_] (auth.controller/invalid-activation-key-dispatcher username activation-key))
          :handle-ok #(auth.controller/activate-and-return-the-success-page username %))


(resource resend-activation
          :put ["/resend-activation"]
          :content-type :json
          :auth-required? true
          :put! auth.controller/resend-activation)


(resource send-reset-password-activation
          :put ["/send-reset-password-activation"]
          :content-type :json
          :put! #(auth.controller/send-reset-password-activation (:request-data %)))


(resource reset-password-page
          :get ["/resetpassword" [username activation-key]]
          :content-type :html
          :redirect! (fn [_] (auth.controller/invalid-reset-pass-activation-key-dispatcher username activation-key))
          :handle-ok view.recover-password/html)


(resource reset-password
          :put ["/resetpassword"]
          :content-type :json
          :put! #(auth.controller/reset-password (:request-data %)))

