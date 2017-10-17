(ns clojurecademy.view.login
  (:require [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :as hiccup]
            [clojurecademy.view.common :as view.common]))


(defn username-or-email-field
  []
  [:div.form-field
   [:div.field.field--text
    [:input#login-username-or-email.ui-inited
     {:autofocus   true
      :placeholder "Username or email"
      :name        "username-or-email"
      :type        "text"}]]])

(defn password-field
  []
  [:div.form-field
   [:div.field.field--text
    [:input#login-password.ui-inited
     {:placeholder "Password"
      :name        "password"
      :type        "password"}]]])

(defn error-field
  []
  [:div.field-errors
   [:div#login-error-field.field-error]])

(defn submit-field
  []
  [:input#login-button.button.button--large.button--secondary.button--fill-space.ui-inited
   {:value "Log in"
    :name  "commit"
    :type  "submit"}])

(defn forgot-field
  []
  [:a.forgot-password
   {:href "/forgotpass"} "Forgot your password?"])

(defn html
  [_]
  (view.common/full-html
    "Login"
    [:main.sessions.new
     [:article.fit-fixed
      [:div.grid-row
       [:div.grid-col-12
        [:div.grid-row.grid-col-12
         [:div.grid-col-6.grid-col--center.login-registration-form.grid-col--no-padding
          [:div
           {:data-react-class "LoginRegistrationForms"}
           [:div.login-registration-forms
            [:div.login-registration-form
             ;(text-field)
             [:div.new_user
              (username-or-email-field)
              (password-field)
              (error-field)
              (submit-field)]
             (forgot-field)]]]]]]]]]))