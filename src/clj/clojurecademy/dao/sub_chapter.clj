(ns clojurecademy.dao.sub-chapter
  (:require [clojurecademy.dao.db :as db]
            [kezban.core :refer :all]))


(defn find-next-sub-chapter-id-by-sub-chapter-id
  [t sub-chapter-id]
  (when-let [sub-chapter (db/entity-as-of t sub-chapter-id)]
    (db/q-as-of t '[:find ?s .
                    :in $ ?chapter-id ?index
                    :where
                    [?s :sub-chapter/chapter-id ?chapter-id]
                    [?s :sub-chapter/index ?index]
                    [?s :sub-chapter/active? true]]
                (:db/id (:sub-chapter/chapter-id sub-chapter)) (+ (:sub-chapter/index sub-chapter) 1))))


(defn find-previous-sub-chapter-id-by-sub-chapter-id
  [t sub-chapter-id]
  (when-let [sub-chapter (db/entity-as-of t sub-chapter-id)]
    (db/q-as-of t '[:find ?s .
                    :in $ ?chapter-id ?index
                    :where
                    [?s :sub-chapter/chapter-id ?chapter-id]
                    [?s :sub-chapter/index ?index]
                    [?s :sub-chapter/active? true]]
                (:db/id (:sub-chapter/chapter-id sub-chapter)) (- (:sub-chapter/index sub-chapter) 1))))


(defn find-max-indexed-sub-chapter-id-by-chapter-id
  [t chapter-id]
  (let [index-sub-chapter-id-set (db/q-as-of t '[:find ?index ?c
                                                 :in $ ?chapter-id
                                                 :where
                                                 [?c :sub-chapter/chapter-id ?chapter-id]
                                                 [?c :sub-chapter/index ?index]
                                                 [?c :sub-chapter/active? true]]
                                             chapter-id)
        m                        (reduce (fn [m [k v]]
                                           (conj m {:index k :id v})) [] index-sub-chapter-id-set)]
    (->> (sort-by :index #(compare %2 %1) m)
         first
         :id)))


(defn find-sub-chapter-id-by-subject-id
  [t subject-id]
  (db/q-as-of t '[:find ?sub-chapter-id .
                  :in $ ?subject-id
                  :where
                  [?subject-id :subject/sub-chapter-id ?sub-chapter-id]
                  [?subject-id :subject/active? true]]
              subject-id))


(defn find-sub-chapter-name-by-sub-chapter-id
  [t sub-chapter-id]
  (db/q-as-of t '[:find ?sub-chapter-name .
                  :in $ ?sub-chapter-id
                  :where
                  [?sub-chapter-id :sub-chapter/name ?sub-chapter-name]
                  [?sub-chapter-id :sub-chapter/active? true]]
              sub-chapter-id))


(defn find-sub-chapter-index-by-subject-id
  [t subject-id]
  (db/q-as-of t '[:find ?index .
                  :in $ ?subject-id
                  :where
                  [?subject-id :subject/sub-chapter-id ?sub-chapter-id]
                  [?subject-id :subject/active? true]
                  [?sub-chapter-id :sub-chapter/index ?index]
                  [?sub-chapter-id :sub-chapter/active? true]]
              subject-id))


(defn find-sub-chapters-by-chapter-id
  [t chapter-id]
  (->> (db/q-as-of t '[:find ?s
                       :in $ ?chapter-id
                       :where
                       [?s :sub-chapter/chapter-id ?chapter-id]
                       [?s :sub-chapter/active? true]]
                   chapter-id)
       db/query-seq
       (map #(db/entity-as-of t %))))


(defn find-first-sub-chapter-id-of-course-by-course-id
  [t course-id]
  (db/q-as-of t '[:find ?s .
                  :in $ ?course-id
                  :where
                  [?c :chapter/course-id ?course-id]
                  [?c :chapter/index 0]
                  [?c :chapter/active? true]
                  [?s :sub-chapter/chapter-id ?c]
                  [?s :sub-chapter/index 0]
                  [?s :sub-chapter/active? true]]
              course-id))