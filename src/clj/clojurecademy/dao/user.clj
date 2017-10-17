(ns clojurecademy.dao.user
  (:require [datomic.api :as d]
            [clojurecademy.dao.db :as db]
            [pandect.algo.sha256 :as hash]
            [clojure.string :as str]))


(defn create-user
  [user]
  (db/transact {:db/id                (d/tempid :db.part/user)
                :user/username        (str/lower-case (:username user))
                :user/password        (hash/sha256 (:password user))
                :user/email           (str/lower-case (:email user))
                :user/last-login      (:last-login user)
                :user/created-time    (:created-time user)
                :user/role            (:role user)
                :user/cookie          (:cookie user)
                :user/activation-key  (:activation-key user)
                :user/activation-time (:activation-time user)
                :user/active?         true}))


(defn update-user
  [user]
  (db/transact user))


(defn find-user-id-by-username
  [username]
  (db/q '[:find ?e .
          :in $ ?username
          :where
          [?e :user/username ?username]]
        (str/lower-case username)))


(defn find-username-by-user-id
  [user-id]
  (db/q '[:find ?username .
          :in $ ?user-id
          :where
          [?user-id :user/username ?username]]
        user-id))


(defn find-user-by-username
  [username]
  (when-let [user-id (find-user-id-by-username username)]
    (db/entity user-id)))


(defn find-user-id-by-email
  [email]
  (db/q '[:find ?e .
          :in $ ?email
          :where
          [?e :user/email ?email]]
        (str/lower-case email)))


(defn find-username-by-email
  [email]
  (db/q '[:find ?username .
          :in $ ?email
          :where
          [?e :user/email ?email]
          [?e :user/username ?username]]
        (str/lower-case email)))


(defn find-user-id-by-username-or-email
  [username-or-email]
  (db/q '[:find ?e .
          :in $ ?username-or-email
          :where
          (or [?e :user/username ?username-or-email]
              [?e :user/email ?username-or-email])]
        (str/lower-case username-or-email)))


(defn find-user-by-username-or-email
  [username-or-email]
  (some-> username-or-email str/lower-case find-user-id-by-username-or-email db/entity))


(defn update-activation-key-and-activation-time
  [activation-key username]
  (db/transact {:user/username        (str/lower-case username)
                :user/activation-key  activation-key
                :user/activation-time (System/currentTimeMillis)}))


(defn find-activation-key-by-username
  [username]
  (db/q '[:find ?activation-key .
          :in $ ?username
          :where
          [?e :user/username ?username]
          [?e :user/activation-key ?activation-key]]
        (str/lower-case username)))


(defn find-activation-time-by-username
  [username]
  (db/q '[:find ?activation-time .
          :in $ ?username
          :where
          [?e :user/username ?username]
          [?e :user/activation-time ?activation-time]]
        (str/lower-case username)))


(defn find-number-of-users
  []
  (db/q '[:find (count ?u) .
          :where
          [?u :user/active? true]]))


(defn find-number-of-email-activated-users
  []
  (db/q '[:find (count ?u) .
          :where
          [?u :user/active? true]
          [?u :user/email-activated? true]]))


(defn create-dummy-user-foobar
  []
  (db/transact {:user/username "foobar"}))