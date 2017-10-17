(ns clojurecademy.controller.auth
  (:require [clojurecademy.validation :as validation]
            [clojurecademy.util :as util]
            [cemerick.url :as url]))


(defn- signup-validations
  [data]
  [[(validation/email? (:email data)) "Email is invalid."]
   [(validation/username? (:username data)) "Username is invalid."]
   [(validation/password? (:password data)) "Password is invalid."]])


(defn signup
  [_]
  (let [signup-error-field-id "signup-error-field"
        signup-field-ids      ["signup-username" "signup-password" "signup-email"]
        data                  (util/create-field-val-map signup-field-ids)]
    (when (util/valid-input? signup-error-field-id (signup-validations data))
      (util/ajax :post "/signup"
                 :data data
                 :success #(util/change-url "/courses")
                 :error (fn [{:keys [status response]}]
                          (util/set-innerText! signup-error-field-id (:error response)))))))


(defn- login-validations
  [data]
  [[((util/any-pred validation/username? validation/email?) (:username-or-email data)) "Username or email is invalid."]
   [(validation/password? (:password data)) "Password is invalid."]])


(defn login
  [_]
  (let [login-error-field-id "login-error-field"
        login-field-ids      ["login-username-or-email" "login-password"]
        data                 (util/create-field-val-map login-field-ids)]
    (when (util/valid-input? login-error-field-id (login-validations data))
      (util/ajax :put "/login"
                 :data data
                 :success #(util/change-url "/courses/learn")
                 :error (fn [{:keys [status response]}]
                          (util/show-error-text login-error-field-id (:error response))
                          (util/scroll-to-top))))))


(defn logout
  [_]
  (util/ajax :put "/logout"
             :success #(util/change-url "/")
             :error (fn [{:keys [status response]}]
                      (util/show-error-text "user-update-error-field" (:error response))
                      (util/scroll-to-top))))


(defn- forgotpass-validations
  [data]
  [[(validation/email? (:forgot-pass-email data)) "Email is invalid."]])


(defn send-reset-pass-email
  [_]
  (let [forgot-pass-error-field "forgot-pass-error-field"
        forgot-pass-ids         ["forgot-pass-email"]
        data                    (util/create-field-val-map forgot-pass-ids)]
    (when (util/valid-input? forgot-pass-error-field (forgotpass-validations data))
      (util/ajax :put "/send-reset-password-activation"
                 :data data
                 :success #(do
                             (util/show-success-text forgot-pass-error-field "Successfully sent recovery email.")
                             (util/clear-input-fields ["forgot-pass-email"]))
                 :error (fn [{:keys [status response]}]
                          (util/show-error-text forgot-pass-error-field (:error response)))))))


(defn- reset-password-validations
  [data]
  [[(validation/password? (:reset-password data)) "Password is invalid."]])


(defn reset-password
  [_]
  (let [reset-password-error-field "reset-password-error-field"
        recover-pass-ids           ["reset-password"]
        query                      (:query (url/url (-> js/window .-location .-href)))
        data                       (assoc (util/create-field-val-map recover-pass-ids)
                                     :username (get query "username")
                                     :activation-key (get query "activation-key"))]
    (when (util/valid-input? reset-password-error-field (reset-password-validations data))
      (util/ajax :put "/resetpassword"
                 :data data
                 :success (fn [_]
                            (util/show-success-text
                              reset-password-error-field
                              "Password successfully updated.You will be redirected to login...")
                            (js/setTimeout #(util/redirect! "/login") 2500))
                 :error (fn [{:keys [status response]}]
                          (util/show-error-text reset-password-error-field (:error response)))))))