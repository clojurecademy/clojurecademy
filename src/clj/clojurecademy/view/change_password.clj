(ns clojurecademy.view.change-password
  (:require [clojurecademy.view.common :as view.common]
            [clojurecademy.view.account-details :refer [my-account-title
                                                        basic-information-section
                                                        change-password-section
                                                        view-profile-section
                                                        logout-section]]))

(defn- error-field
  []
  [:div.field-errors
   [:div#update-account-details-error-field.field-error]])

(defn- password-field
  []
  [:div.field.field--text
   [:input#password.ui-inited
    {:type         "password",
     :size         "30",
     :placeholder  "New Password",
     :name         "password",
     :autocomplete "off"}]
   [:div.field__status-icon]])

(defn- password-confirmation-field
  []
  [:div.field.field--text
   [:input#password-confirmation.ui-inited
    {:type        "password",
     :size        "30",
     :placeholder "New Password Confirmation",
     :name        "password-confirmation"}]
   [:div.field__status-icon]])

(defn- current-password-field
  []
  (list
    [:i "We need your current password to confirm your changes"]
    [:br]
    [:div.field.field--text
     [:input#current-password.ui-inited
      {:type        "password",
       :size        "30",
       :placeholder "Current Password",
       :name        "current-password"}]
     [:div.field__status-icon]]))

(defn- change-password-button
  []
  [:input#change-password-button.button.ui-inited
   {:value "Change Password", :type "submit", :name "commit"}])


(defn- password-fields
  []
  [:div.grid-col-8.grid-col-center.grid-col--no-spacing.margin-top--1.margin-bottom--5
   [:div#change_password.color-scheme--white.grid-col-12.grid-row.padding--1
    {:style "background-color:#e9eaea"}
    [:h3 "Change Password"]
    (error-field)
    [:div#edit_user_535ab7bd8c1ccc14df0002fe.edit_user
     (password-field)
     (password-confirmation-field)
     (current-password-field)
     (change-password-button)]]])

(defn html
  [username]
  (view.common/full-html
    (str "Change Password - " username)
    (view.common/header :logged-in)
    [:main.users.edit
     [:article.fit-fixed
      (my-account-title)
      [:div.grid-row.padding-top--none.margin-top--none
       [:div.grid-col-4.grid-col-center.grid-col--no-spacing.margin-top--1.margin-bottom--5.padding-right--3
        (basic-information-section false)
        (change-password-section true)
        (view-profile-section username)
        (logout-section)]
       (password-fields)]]]))