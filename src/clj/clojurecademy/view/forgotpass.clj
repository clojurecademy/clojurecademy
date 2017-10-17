(ns clojurecademy.view.forgotpass
  (:require [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :as hiccup]
            [clojurecademy.view.common :as view.common]))


(defn forgotpass-page-body
  [_]
  (view.common/full-html
    "Forgot Password"
    [:main.passwords.new
     [:article.fit-full
      [:div.fit-fixed.grid-row
       [:div.grid-col-6.grid-col--center.grid-col--align-center
        [:div.grid-row
         [:div.grid-col-12.grid-col [:h2 "Forgot your password?"]]]
        [:div.grid-row.login-registration-form
         [:div
          {:data-react-class "PasswordResetForm"}
          [:div.login-registration-form.passoword-reset-form
           [:div.form-field
            [:div.field.field--text
             [:input#forgot-pass-email.ui-inited
              {:autofocus true :name "forgot-pass-email" :placeholder "Email" :type "email"}]]]
           [:div.field-errors
            [:div#forgot-pass-error-field.field-error]]
           [:input#send-recovery-email-button.button.button--large.button--secondary.button--fill-space.ui-inited
            {:value "Send recovery email" :type "submit"}]]]]]]]]))