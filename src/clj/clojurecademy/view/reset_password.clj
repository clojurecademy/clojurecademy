(ns clojurecademy.view.reset-password
  (:require [clojurecademy.view.common :as view.common]))


(defn html
  [_]
  (view.common/full-html
    "Reset Password"
    [:main.passwords.new
     [:article.fit-full
      [:div.fit-fixed.grid-row
       [:div.grid-col-6.grid-col--center.grid-col--align-center
        [:div.grid-row
         [:div.grid-col-12.grid-col [:h2 "Reset your password"]]]
        [:div.grid-row.login-registration-form
         [:div
          {:data-react-class "PasswordResetForm"}
          [:div.login-registration-form.passoword-reset-form
           [:div.form-field
            [:div.field.field--text
             [:input#reset-password.ui-inited
              {:autofocus true :name "reset-password" :placeholder "New Password" :type "password"}]]]
           [:div.field-errors
            [:div#reset-password-error-field.field-error]]
           [:input#reset-password-button.button.button--large.button--secondary.button--fill-space.ui-inited
            {:value "Reset password" :type "submit"}]]]]]]]]))