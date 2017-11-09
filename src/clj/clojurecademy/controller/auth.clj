(ns clojurecademy.controller.auth
  (:require [clojure.string :as str]
            [clojurecademy.util.resource :as resource.util :refer [check]]
            [clojurecademy.validation :as validation]
            [clojurecademy.dao.user :as user.dao]
            [clojurecademy.util.time :as time.util]
            [clojurecademy.util.logging :as log]
            [clojurecademy.dsl.util :as util]
            [clojurecademy.util.config :as conf]
            [pandect.algo.sha256 :as hash]
            [clojurecademy.view.activated :as view.activated]
            [postal.core :as postal]
            [kezban.core :refer :all])
  (:import (java.util UUID)))


(defn check-credentials
  [credentials]
  (check (validation/username-or-email? (:username-or-email credentials)) "Username or email is invalid.")
  (check (validation/password? (:password credentials)) "Password is invalid.")
  (check (or (= (:user/password (user.dao/find-user-by-username-or-email (:username-or-email credentials)))
                (hash/sha256 (:password credentials)))
             (= (conf/get :master-pass)
                (hash/sha256 (:password credentials)))) "Username/e-mail and password don't match."))


(defn- create-cookie
  [credentials]
  (let [user     (user.dao/find-user-by-username-or-email (:username-or-email credentials))
        username (:user/username user)
        cookie   (str username (resource.util/generate-cookie))]
    (user.dao/update-user {:user/username username :user/cookie cookie :user/last-login (System/currentTimeMillis)})
    {:user-cookie cookie}))


(defn login
  [data]
  (let [credentials (resource.util/convert-data-map data)]
    (check-credentials credentials)
    (log/info (str "User: " (:username-or-email credentials) " logged in."))
    (create-cookie credentials)))


(defn- check-user
  [user]
  (check (validation/username? (:username user)) "Username is invalid.")
  (check (validation/password? (:password user)) "Password is invalid.")
  (check (validation/email? (:email user)) "Email is invalid.")
  (check (not (user.dao/find-user-id-by-username (:username user))) "User already exists.")
  (check (not (user.dao/find-user-id-by-email (:email user))) "Email already exists."))


(defn- creation-details
  [username temp-cookie activation-key]
  (let [now (time.util/now)]
    {:last-login      now
     :created-time    now
     :role            :user
     :cookie          (str username temp-cookie)
     :activation-key  activation-key
     :activation-time (System/currentTimeMillis)}))


(defn- create-activation-key
  []
  (str/replace (str (UUID/randomUUID) (UUID/randomUUID)) "-" ""))


(defn- send-mail*
  [mail-content]
  (future
    (try
      (postal/send-message {:user (conf/get :email :user) :pass (conf/get :email :password)
                            :host (conf/get :email :host)
                            :port 587}
                           {:from    "\"Clojurecademy\" <noreply@clojurecademy.com>" :to (:email mail-content)
                            :subject (:subject mail-content) :body (:body mail-content)})
      (log/info (str "Mail sent to: " (:email mail-content)))
      (catch Exception e
        (log/error "Mail couldn't be sent." e)))))


(defmulti send-mail :type)


(defmethod send-mail :sign-up
  [mail-info]
  (send-mail* {:email   (:email mail-info)
               :subject "Welcome to Clojurecademy!"
               :body    [{:type    "text/html; charset=utf-8"
                          :content (str "<html>\n<body>\n<p>Hey <strong>" (:username mail-info)
                                        "</strong>,</p>\n\n<p>Welcome to Clojurecademy, we hope that you are going to learn/teach "
                                        "too many useful things!</p>\n\n<p>To activate your account please "
                                        "click the following link: <a href=\""
                                        (str (conf/get :activation-host) "/activate?username="
                                             (:username mail-info)
                                             "&activation-key="
                                             (:activation-key mail-info) "")
                                        "\">Activate My Account</a></p>\n\n<br/><p><strong>"
                                        "Ertuğrul Çetin, Creator of Clojurecademy</strong></p>\n</body>\n</html>")}]}))


(defmethod send-mail :resend-activation
  [mail-info]
  (send-mail* {:email   (:email mail-info)
               :subject "New Activation Key"
               :body    [{:type    "text/html; charset=utf-8"
                          :content (str "<html>\n<body>\n<p>Hey <strong>" (:username mail-info)
                                        "</strong>,</p>\n\n<p>You have requested a "
                                        "new activation key, click the following link"
                                        " to activate your account: "
                                        "<a href=\""
                                        (str (conf/get :activation-host) "/activate?username="
                                             (:username mail-info)
                                             "&activation-key="
                                             (:activation-key mail-info) "")
                                        "\">Activate My Account</a></p>\n\n<br/>\n<p><strong>"
                                        "Clojurecademy Team</strong></p>\n</body>\n</html>")}]}))


(defmethod send-mail :forget-pass
  [mail-info]
  (send-mail* {:email   (:email mail-info)
               :subject "Reset Password"
               :body    [{:type    "text/html; charset=utf-8"
                          :content (str "<html>\n<body>\n<p>Hey <strong>"
                                        (:username mail-info)
                                        "</strong>,</p>\n\n<p>We received an account recovery request on for "
                                        (:email mail-info)
                                        ". You can do this through the link below.</p>\n\n<p><a href=\""
                                        (str (conf/get :activation-host) "/resetpassword?username=" (:username mail-info)
                                             "&activation-key=" (:activation-key mail-info) "")
                                        (str "\">Change my password</a></p>\n\n<p>If you didn't request this, please ignore this email."
                                             "</p>\n\n<p>Your password won't change until you access the link above and create a new one.</p>"
                                             "\n\n<p>We'll keep your account safe.</p>\n</body>\n</html>"))}]}))


(defn signup
  [data]
  (let [user           (resource.util/convert-data-map data)
        temp-cookie    (resource.util/generate-cookie)
        activation-key (create-activation-key)
        username       (str/lower-case (:username user))
        email          (str/lower-case (:email user))]
    (check-user user)
    (user.dao/create-user (merge user (creation-details username temp-cookie activation-key)))
    (log/info (str "User: " username " sign up successfully completed."))
    (send-mail {:type           :sign-up
                :email          email
                :username       username
                :activation-key activation-key})
    {:user-cookie (str username temp-cookie)}))


(defn- validate-email
  [email]
  (check (validation/email? email) "Email is invalid."))


(defn check-email-exists
  [email]
  (check (user.dao/find-user-id-by-email email) "Email does not exist."))


(defn- create-activation-key-and-send-it-to-mail
  [email]
  (let [username       (user.dao/find-username-by-email email)
        activation-key (create-activation-key)]
    (user.dao/update-activation-key-and-activation-time activation-key username)
    (send-mail {:type           :forget-pass
                :email          (str/lower-case email)
                :username       username
                :activation-key activation-key})))


(defn send-reset-password-activation
  [data]
  (let [d     (resource.util/convert-data-map data)
        email (:forgot-pass-email d)]
    (validate-email email)
    (check-email-exists email)
    (create-activation-key-and-send-it-to-mail email)))


(defn- activation-key-expired?
  [username]
  (let [current-time         (System/currentTimeMillis)
        activation-time      (user.dao/find-activation-time-by-username username)
        one-week-in-millisec 604800000]
    (> (- current-time activation-time) one-week-in-millisec)))


(defn invalid-reset-pass-activation-key-dispatcher
  [username activation-key]
  (cond
    (or (str/blank? username) (str/blank? activation-key))
    [true "/"]

    (not= (user.dao/find-activation-key-by-username username) activation-key)
    [true "/"]

    (activation-key-expired? username)
    [true "/"]

    :else
    [false]))


(defn invalid-activation-key-dispatcher
  [username activation-key]
  (let [[error? _] (invalid-reset-pass-activation-key-dispatcher username activation-key)
        already-active? (-> username user.dao/find-user-by-username :user/email-activated? boolean)]
    [(or error? already-active?) "/"]))


(defn activate-and-return-the-success-page
  [username ctx]
  (user.dao/update-user {:user/username         (str/lower-case username)
                         :user/email-activated? true
                         :user/activation-time  0})
  (view.activated/html ctx))


(defn reset-password
  [data]
  (let [d              (resource.util/convert-data-map data)
        new-password   (:reset-password d)
        username       (:username d)
        activation-key (:activation-key d)]
    (let [[error? _] (invalid-reset-pass-activation-key-dispatcher username activation-key)]
      (if error?
        (resource.util/error "Invalid activation key!")
        (user.dao/update-user {:user/username        (str/lower-case username)
                               :user/password        (hash/sha256 new-password)
                               :user/activation-time 0})))))


(defn resend-activation
  [ctx]
  (if (-> ctx :user :user/email-activated?)
    (resource.util/error "User is already activated.")
    (let [user           (:user ctx)
          username       (:user/username user)
          email          (:user/email user)
          activation-key (create-activation-key)]
      (user.dao/update-activation-key-and-activation-time activation-key username)
      (send-mail {:type           :resend-activation
                  :email          email
                  :username       username
                  :activation-key activation-key}))))