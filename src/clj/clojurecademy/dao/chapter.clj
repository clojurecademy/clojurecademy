(ns clojurecademy.dao.chapter
  (:require [clojurecademy.dao.db :as db]))


(defn find-chapters-by-course-id
  [t course-id]
  (->> (db/q-as-of t '[:find ?c
                       :in $ ?course-id
                       :where
                       [?c :chapter/course-id ?course-id]
                       [?c :chapter/active? true]]
                   course-id)
       db/query-seq
       (map #(db/entity-as-of t %))))


(defn find-next-chapter-id-by-chapter-id
  [t chapter-id]
  (when-let [chapter (db/entity-as-of t chapter-id)]
    (db/q-as-of t '[:find ?s .
                    :in $ ?course-id ?index
                    :where
                    [?s :chapter/course-id ?course-id]
                    [?s :chapter/index ?index]
                    [?s :chapter/active? true]]
                (:db/id (:chapter/course-id chapter)) (+ (:chapter/index chapter) 1))))


(defn find-previous-chapter-id-by-chapter-id
  [t chapter-id]
  (when-let [chapter (db/entity-as-of t chapter-id)]
    (db/q-as-of t '[:find ?s .
                    :in $ ?course-id ?index
                    :where
                    [?s :chapter/course-id ?course-id]
                    [?s :chapter/index ?index]
                    [?s :chapter/active? true]]
                (:db/id (:chapter/course-id chapter)) (- (:chapter/index chapter) 1))))


(defn find-chapter-id-by-sub-chapter-id
  [t sub-chapter-id]
  (db/q-as-of t '[:find ?chapter-id .
                  :in $ ?sub-chapter-id
                  :where
                  [?sub-chapter-id :sub-chapter/chapter-id ?chapter-id]
                  [?sub-chapter-id :sub-chapter/active? true]]
              sub-chapter-id))


(defn find-chapter-index-by-subject-id
  [t subject-id]
  (db/q-as-of t '[:find ?index .
                  :in $ ?subject-id
                  :where
                  [?subject-id :subject/sub-chapter-id ?sub-chapter-id]
                  [?subject-id :subject/active? true]
                  [?sub-chapter-id :sub-chapter/chapter-id ?chapter-id]
                  [?sub-chapter-id :sub-chapter/active? true]
                  [?chapter-id :chapter/index ?index]
                  [?chapter-id :chapter/active? true]]
              subject-id))


(defn find-chapter-by-sub-chapter-id
  [t sub-chapter-id]
  (some->> (find-chapter-id-by-sub-chapter-id t sub-chapter-id) (db/entity-as-of t)))