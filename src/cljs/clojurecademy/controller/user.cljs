(ns clojurecademy.controller.user
  (:require [clojurecademy.util :as util]
            [clojurecademy.validation :as validation]))


(defn- update-account-details-validations
  [data]
  [[(validation/fullname? (:fullname data)) "Full Name is invalid."]
   [(validation/email? (:email data)) "Email is invalid."]
   [(validation/about? (:about data)) "About should be between 1 and 350 chars long."]
   [(validation/url? (:website data)) "Website is invalid."]
   [(validation/github-or-twitter? (:github data)) "GitHub username is invalid."]
   [(validation/github-or-twitter? (:twitter data)) "Twitter username is invalid."]
   [(validation/linkedin? (:linkedin data)) "LinkedIn url is invalid."]
   [(validation/stackoverflow? (:stackoverflow data)) "StackOverflow url is invalid."]])

(defn update-account-details
  [_]
  (let [error-field-id            "account-details-error-field"
        account-details-field-ids ["fullname" "email" "about" "website" "github" "twitter" "linkedin" "stackoverflow"]
        data                      (util/create-field-val-map account-details-field-ids)]
    (when (util/valid-input? error-field-id (update-account-details-validations data))
      (util/ajax :put "/account"
                 :data data
                 :success #(do
                             (util/show-success-text error-field-id "Successfully Updated.")
                             (util/scroll-to-top))
                 :error (fn [{:keys [status response]}]
                          (util/show-error-text error-field-id (:error response))
                          (util/scroll-to-top))))))

(defn- change-password-validations
  [data]
  [[(validation/password? (:password data)) "Password is invalid."]
   [(validation/password? (:password-confirmation data)) "Password confirmation is invalid."]
   [(validation/? (= (:password data) (:password-confirmation data))) "Passwords don't match."]
   [(validation/password? (:current-password data)) "Current password is invalid."]
   [(validation/? (not= (:password data) (:current-password data))) "New password and current password are same."]])

(defn change-password
  [_]
  (let [error-field-id            "update-account-details-error-field"
        change-password-field-ids ["password" "password-confirmation" "current-password"]
        data                      (util/create-field-val-map change-password-field-ids)]
    (when (util/valid-input? error-field-id (change-password-validations data))
      (util/ajax :put "/account/changepassword"
                 :data data
                 :success #(do
                             (util/show-success-text error-field-id "Successfully Updated.")
                             (util/scroll-to-top)
                             (util/clear-input-fields ["password" "password-confirmation" "current-password"]))
                 :error (fn [{:keys [status response]}]
                          (util/show-error-text error-field-id (:error response))
                          (util/scroll-to-top))))))


(defn resend-activation
  []
  (util/ajax :put "/resend-activation"
             :success #(util/show-text "account-activation-msg-field" "Successfully sent activation email.(Also, check spam mails)" "green")
             :error (fn [{:keys [status response]}]
                      (util/show-error-text "account-activation-msg-field" (:error response)))))