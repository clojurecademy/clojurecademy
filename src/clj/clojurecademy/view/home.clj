(ns clojurecademy.view.home
  (:require [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :as hiccup]
            [clojurecademy.view.common :as view.common]))

(defn- learn-clojure-text
  []
  [:div.grid-row.padding-top--1
   [:div.grid-col-12.grid-col--center.grid-col--align-center
    [:h1 "Learning Clojure Made Easy, for free."]
    [:h3 {:style "font-style:italic"} "Community-Powered Courses"]
    [:iframe
     {:height "30px",
      :width "160px",
      :scrolling "0",
      :frameborder "0",
      :src
      "https://ghbtns.com/github-btn.html?user=clojurecademy&repo=clojurecademy&type=star&count=true&size=large"}]]])

(defn- clojure-svg-logo
  []
  [:div.grid-col-7.grid-col--no-padding.screen-col
   [:img {:src "/img/big-clojure-logo.svg"}]])

(defn- email-field
  []
  [:div.form-field
   [:div.field.field--text
    [:input#signup-email.ui-inited
     {:placeholder "Email"
      :name        "email"
      :type        "text"}]]])

(defn- username-field
  []
  [:div.form-field
   [:div.field.field--text
    [:input#signup-username.ui-inited
     {:placeholder "Username"
      :name        "username"
      :type        "text"}]]])

(defn- password-field
  []
  [:div.form-field
   [:div.field.field--text
    [:input#signup-password.ui-inited
     {:placeholder "Password"
      :name        "password"
      :type        "password"}]]])

(defn- error-field
  []
  [:div.field-errors
   [:div#signup-error-field.field-error]])

(defn- signup-button
  []
  [:input#signup-button.button.button--large.button--secondary.button--fill-space.ui-inited
   {:value "Sign up" :type "submit"}])


(defn- what-is-clojurecademy
  []
  [:div.color-scheme--white.learn-more
   [:div.fit-fixed.grid-row.grid-row-no-collapse
    [:div.grid-col-12.grid-col--align-center
     [:h2 "What is Clojurecademy?"]
     [:p "Clojurecademy is like " [:span {:style "font-style:italic"} "Codecademy"] " that focuses only Clojure and its ecosystem, it teaches Clojure programming language or anything related to programming in general(e.g. Algorithms & Data Structures to Code Koans/Katas in " [:span {:style "font-weight:bold"} "Clojure"] ") interactively."]
     [:p "Clojurecademy embraces " [:span {:style "font-style:italic"} "Codecademy's"] " interactive approach which makes coding fun and easy to learn."]
     [:p "With " [:span {:style "font-weight:bold"} "Clojurecademy's powerful DSL"] " developers who know Clojure can create any courses."]
     [:p "Want to spread Clojure? Then "
      [:a {:href   "https://clojurecademy.github.io/dsl-documentation/"
           :target "_blank"
           :style  "text-decoration:underline;font-weight: bold"} "check the documentation"] " for creating cool courses!"]]]])

(defn- registration
  []
  [:div#home__cta.grid-col-5
   [:div.grid-row.grid-row--no-collapse.padding-top--3
    [:div.homepage-registration.homepage-form
     [:div
      {:data-react-class "LoginRegistrationForms"}
      [:div.login-registration-forms
       [:div.login-registration-form
        [:h4.registration-form__title "Sign up and start coding in Clojure"]
        [:div#new-user-id.new_user
         (email-field)
         (username-field)
         (password-field)
         (error-field)
         (signup-button)]]]]]]])

(defn html
  [_]
  (view.common/full-html
    [:main.home.show
     [:article.fit-full.color-scheme--grey.top-section.fit-fixed
      (learn-clojure-text)
      [:div.grid-row.padding-top--3.grid-row-no-collapse
       (clojure-svg-logo)
       (registration)]]
     (what-is-clojurecademy)]))