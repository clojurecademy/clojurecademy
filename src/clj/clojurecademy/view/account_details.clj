(ns clojurecademy.view.account-details
  (:require [clojurecademy.view.common :as view.common]))

(defn- fullname-field
  [fullname]
  [:div.margin-bottom--1
   "Full Name" [:div.field.field--text
                [:input#fullname.ui-inited
                 {:value       fullname
                  :type        "text"
                  :size        "30"
                  :placeholder "Full Name"
                  :name        "fullname"}]
                [:div.field__status-icon]]])

(defn- username-field
  [username]
  [:div.margin-bottom--1
   "Username" [:div.field.field--text
               [:input#username.ui-inited
                {:value    username
                 :type     "text"
                 :size     "30"
                 :name     "username"
                 :readonly true}]
               [:div.field__status-icon]]])

(defn- email-field
  [email]
  [:div.margin-bottom--1
   "Email (required)" [:div.field.field--text
                       [:input#email.ui-inited
                        {:value       email
                         :type        "text"
                         :size        "30"
                         :placeholder "Email (required)"
                         :name        "email"}]
                       [:div.field__status-icon]]])

(defn- about-field
  [about]
  [:div.margin-bottom--1
   "About" [:div.field.field--text
            [:textarea#about.ui-inited
             {:rows        "4"
              :placeholder "About"
              :name        "about"
              :cols        "40"} about]
            [:div.field__status-icon]]])

(defn- website-field
  [website]
  [:div.margin-bottom--1
   "Website" [:div.field.field--text
              [:input#website.ui-inited
               {:value       website
                :type        "text"
                :size        "30"
                :placeholder "http://"
                :name        "website"}]
              [:div.field__status-icon]]])

(defn- github-field
  [github]
  [:div.margin-bottom--1
   "GitHub" [:div.field.field--text
             [:input#github.ui-inited
              {:value       github
               :type        "text"
               :size        "30"
               :placeholder "github-username"
               :name        "github"}]
             [:div.field__status-icon]]])

(defn- twitter-field
  [twitter]
  [:div.margin-bottom--1
   "Twitter" [:div.field.field--text
              [:input#twitter.ui-inited
               {:value       twitter
                :type        "text"
                :size        "30"
                :placeholder "twitter-username"
                :name        "twitter"}]
              [:div.field__status-icon]]])

(defn- linkedin-field
  [linkedin]
  [:div.margin-bottom--1
   "LinkedIn Url" [:div.field.field--text
                   [:input#linkedin.ui-inited
                    {:value       linkedin
                     :type        "text"
                     :size        "30"
                     :placeholder "LinkedIn Url"
                     :name        "linkedin"}]
                   [:div.field__status-icon]]])


(defn- stackoverflow-field
  [stackoverflow]
  [:div.margin-bottom--1
   "StackOverflow Url" [:div.field.field--text
                        [:input#stackoverflow.ui-inited
                         {:value       stackoverflow
                          :type        "text"
                          :size        "30"
                          :placeholder "StackOverflow Url"
                          :name        "stackoverflow"}]
                        [:div.field__status-icon]]])

(defn- update-button
  []
  [:input#update-account-details-button.button.ui-inited
   {:value "Update Profile" :type "submit"}])

(defn- activation-msg-field
  [activated?]
  (when-not activated?
    [:div#account-activation-msg-field
     "Your account " [:strong "has not been activated"] " check your mail or "
     [:a#resend-mail-link {:style "text-decoration: underline;cursor: pointer"}
      "resend activation mail"] "."]))

(defn- error-field
  []
  [:div.field-errors
   [:div#account-details-error-field.field-error]])

(defn basic-information-section
  [selected?]
  [(if selected? :div.menu_item.selected :div.menu_item)
   (when selected? {:style "background-color:#e9eaea"})
   [:h5
    [:a {:href "/account"} "Basic Information"]]])

(defn change-password-section
  [selected?]
  [(if selected? :div.menu_item.selected :div.menu_item)
   (when selected? {:style "background-color:#e9eaea"})
   [:h5
    [:a {:href "/account/changepassword"} "Change Password"]]])

(defn view-profile-section
  [username]
  [:div.menu_item
   [:h5
    [:a {:href (str "/users/" username)} "View Profile"]]])

(defn logout-section
  []
  [:div#logout.menu_item
   [:h5
    [:a
     {:style "color:red;cursor:pointer"} "Log out"]]])

(defn my-account-title
  []
  [:div.grid-row
   [:div.grid-col-4]
   [:div.grid-col-8 [:h1.margin-left--half "My Account"]]])

(defn- basic-information-fields
  [user]
  [:div.grid-col-8.grid-col-center.grid-col--no-spacing.margin-top--1.margin-bottom--5
   [:div#basic_info.color-scheme--white.grid-col-12.grid-row.padding--1
    {:style "background-color:#e9eaea"}
    [:h3 "Basic Information"]
    (activation-msg-field (:user/email-activated? user))
    (error-field)
    [:div.grid-col-6.grid-col--no-spacing
     [:div#edit_user.edit_user
      (fullname-field (:user/fullname user))
      (username-field (:user/username user))
      (email-field (:user/email user))
      (about-field (:user/about user))
      (website-field (:user/website user))
      (github-field (:user/github user))
      (twitter-field (:user/twitter user))
      (linkedin-field (:user/linkedin user))
      (stackoverflow-field (:user/stackoverflow user))
      (update-button)]]]])

(defn html
  [user]
  (view.common/full-html
    (str "Account Details - " (:user/username user))
    (view.common/header :logged-in)
    [:main.users.edit
     [:article.fit-fixed
      (my-account-title)
      [:div.grid-row.padding-top--none.margin-top--none
       [:div.grid-col-4.grid-col-center.grid-col--no-spacing.margin-top--1.margin-bottom--5.padding-right--3
        (basic-information-section true)
        (change-password-section false)
        (view-profile-section (:user/username user))
        (logout-section)]
       (basic-information-fields user)]]]))