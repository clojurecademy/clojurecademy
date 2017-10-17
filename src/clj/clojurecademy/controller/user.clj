(ns clojurecademy.controller.user
  (:require [clojurecademy.validation :as validation]
            [clojurecademy.util.resource :as resource.util :refer [check]]
            [clojurecademy.dao.user :as user.dao]
            [kezban.core :refer :all]
            [clojure.string :as str]
            [pandect.algo.sha256 :as hash]
            [clojurecademy.repl.util :as util]
            [clojurecademy.controller.course.learn :as course.learn]
            [clojurecademy.view.user-profile :as view.user-profile]
            [clojurecademy.dao.course :as course.dao]))


(defn- email-exists?
  [new-email current-email]
  (not (and (not= (str/lower-case new-email) current-email)
            (some? (user.dao/find-user-id-by-email new-email)))))


(defn- check-account-details
  [account-details user]
  (check (validation/fullname? (:fullname account-details)) "Fullname is invalid.It has to be between 1 and 60 chars long.")
  (check (validation/email? (:email account-details)) "Email is invalid.")
  (check (email-exists? (:email account-details) (:user/email user)) "Email already exists.")
  (check (validation/about? (:about account-details)) "About is invalid.It has to be between 1 and 350 chars long.")
  (check (validation/url? (:website account-details)) "Website url is invalid.")
  (check (validation/github-or-twitter? (:github account-details)) "GitHub username is invalid.")
  (check (validation/github-or-twitter? (:twitter account-details)) "Twitter username is invalid.")
  (check (validation/linkedin? (:linkedin account-details)) "LinkedIn url is invalid.")
  (check (validation/stackoverflow? (:stackoverflow account-details)) "StackOverflow url is invalid."))


(defn- map-with-values
  [m]
  (->> m
       (filter (fn [[_ v]] (some? v)))
       (map (fn [[k v]] [k (str/trim v)]))
       (into {})))


(defn- create-user-entity
  [updated-user]
  (->> (map-with-values updated-user)
       (map (fn [[k v]]
              [(keyword (str "user/" (name k))) v]))
       (into {})))


(defn update-account-details
  [data-key ctx]
  (let [user            (:user ctx)
        account-details (resource.util/convert-data-map (data-key ctx))
        _               (check-account-details account-details user)
        user-entity     (create-user-entity account-details)
        new-email       (str/lower-case (:user/email user-entity))
        activation-map  (when-not (= new-email (:user/email user)) {:user/email-activated? false
                                                                    :user/activation-time  0})]
    (user.dao/update-user (merge user-entity
                                 {:user/username (:user/username user)
                                  :user/email    new-email}
                                 activation-map))))


(defn- check-passwords
  [passwords user]
  (check (validation/password? (:password passwords)) "Password is invalid.")
  (check (validation/password? (:password-confirmation passwords)) "Password confirmation is invalid.")
  (check (= (:password passwords) (:password-confirmation passwords)) "Passwords don't match.")
  (check (validation/password? (:current-password passwords)) "Current password is invalid.")
  (check (= (hash/sha256 (:current-password passwords)) (:user/password user)) "Current password is wrong.")
  (check (not (= (:password passwords) (:current-password passwords))) "Current password and new password are same."))


(defn change-password
  [data-key ctx]
  (let [passwords (resource.util/convert-data-map (data-key ctx))]
    (check-passwords passwords (:user ctx))
    (user.dao/update-user (assoc {:user/username (:user/username (:user ctx))} :user/password (hash/sha256 (:password passwords))))))


(defn- check-preferences
  [data]
  (cond
    (and (:font-size data) (integer? (:font-size data)))
    {:user/font-size (:font-size data)}

    (or (true? (:wide-size-on? data)) (false? (:wide-size-on? data)))
    {:user/wide-size-on? (:wide-size-on? data)}

    (or (true? (:learn-wide-size-on? data)) (false? (:learn-wide-size-on? data)))
    {:user/learn-wide-size-on? (:learn-wide-size-on? data)}

    :else
    (util/runtime-ex "Bad editor preferences request!")))


(defn update-preferences
  [data-key ctx]
  (let [data (resource.util/convert-data-map (data-key ctx))
        pref (check-preferences data)]
    (user.dao/update-user (merge {:user/username (:user/username (:user ctx))} pref))))


(defn- get-user-infos
  [user]
  {:username      (:user/username user)
   :fullname      (:user/fullname user)
   :about         (:user/about user)
   :website       (:user/website user)
   :github        (:user/github user)
   :twitter       (:user/twitter user)
   :linkedin      (:user/linkedin user)
   :stackoverflow (:user/stackoverflow user)})


(defn- get-created-courses
  [username]
  (let [courses  (course.dao/find-course-id-and-title-count-by-owner-name username)
        m        (reduce (fn [v [id title count]]
                           (conj v {:id id :title title :count count})) [] courses)
        sorted-m (sort-by :count #(compare %2 %1) m)]
    sorted-m))


(defn- get-course-name-and-percentage-maps
  [user-id]
  (->> user-id
       course.learn/get-enrolled-courses
       (sort-by :course-percentage #(compare %2 %1))))


(defn get-user-profile
  [username ctx]
  (let [user             (user.dao/find-user-by-username username)
        infos            (get-user-infos user)
        enrolled-courses (get-course-name-and-percentage-maps (:db/id user))
        created-courses  (get-created-courses username)]
    (view.user-profile/profile {:auth?            (boolean (resource.util/authorized? ctx))
                                :info             infos
                                :enrolled-courses enrolled-courses
                                :created-courses  created-courses})))

(defn stats
  [_]
  (let [number-of-users                 (or (user.dao/find-number-of-users) 0)
        number-of-email-activated-users (or (user.dao/find-number-of-email-activated-users) 0)
        number-of-courses               (or (course.dao/find-number-of-courses) 0)
        number-of-released-courses      (or (course.dao/find-number-of-released-courses) 0)
        number-of-enrollments           (reduce (fn [sum [_ _ _ enrolled-count]]
                                                  (+ sum enrolled-count)) 0 (course.dao/find-all-released-courses))]
    (str "Users: " number-of-users "<br>"
         "Activated users: " number-of-email-activated-users "<br>"
         "Gap between users: " (- number-of-users number-of-email-activated-users) "<br>" "<br>"
         "Courses: " number-of-courses "<br>"
         "Released courses: " number-of-released-courses "<br>"
         "Gap between courses: " (- number-of-courses number-of-released-courses) "<br>" "<br>"
         "Enrollments: " number-of-enrollments)))