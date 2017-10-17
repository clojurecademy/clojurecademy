(ns clojurecademy.dao.progress
  (:require [clojurecademy.dao.db :as db]
            [kezban.core :refer :all]))


(defn create-progress
  [progress]
  (db/transact progress))


(defn find-any-progress-id-by-user-id
  [user-id]
  (db/q '[:find ?p .
          :in $ ?user-id
          :where
          [?p :progress/user-id ?user-id]]
        user-id))


(defn find-progress-id-by-course-id-and-user-id
  [course-id user-id]
  (db/q '[:find ?p .
          :in $ ?course-id ?user-id
          :where
          [?p :progress/course-id ?course-id]
          [?p :progress/user-id ?user-id]
          [?p :progress/chapter-id ?chapter-id]
          [?chapter-id :chapter/active? true]]
        course-id user-id))


(defn find-progresses-last-updated-by-user-id-and-course-id
  [user-id course-id]
  (db/q '[:find (max ?last-updated) .
          :in $ ?user-id ?course-id
          :where
          [?p :progress/user-id ?user-id]
          [?p :progress/course-id ?course-id]
          [?p :progress/last-updated ?last-updated]]
        user-id course-id))


(defn find-progresses-last-updates-by-user-id-and-course-ids
  [user-id course-ids]
  (db/q '[:find (max ?last-updated) .
          :in $ ?user-id [?course-id ...]
          :where
          [?p :progress/user-id ?user-id]
          [?p :progress/course-id ?course-id]
          [?p :progress/last-updated ?last-updated]]
        user-id course-ids))


(defn find-progress-id-by-user-id-and-last-updated
  [user-id last-updated]
  (db/q '[:find ?p .
          :in $ ?user-id ?last-updated
          :where
          [?p :progress/user-id ?user-id]
          [?p :progress/last-updated ?last-updated]]
        user-id last-updated))


(defn find-progress-id-by-sub-chapter-id-and-user-id
  [sub-chapter-id user-id]
  (db/q '[:find ?p .
          :in $ ?sub-chapter-id ?user-id
          :where
          [?p :progress/sub-chapter-id ?sub-chapter-id]
          [?p :progress/user-id ?user-id]]
        sub-chapter-id user-id))


(defn find-progress-chapter-ids-by-course-id-and-user-id
  [course-id user-id]
  (db/query-seq (db/q '[:find ?chapter-id
                        :in $ ?course-id ?user-id
                        :where
                        [?s :progress/course-id ?course-id]
                        [?s :progress/chapter-id ?chapter-id]
                        [?s :progress/user-id ?user-id]]
                      course-id user-id)))


(defn find-progress-sub-ins-ids-by-course-id-and-user-id
  [user-id course-id]
  (db/query-seq (db/q '[:find ?sub-instructions
                        :in $ ?course-id ?user-id
                        :where
                        [?s :progress/user-id ?user-id]
                        [?s :progress/course-id ?course-id]
                        [?s :progress/sub-instructions ?sub-instructions]]
                      course-id user-id)))


(defn find-progress-sub-chapter-ids-by-course-id-and-user-id
  [user-id sub-chapter-id]
  (db/query-seq (db/q '[:find ?sub-instructions
                        :in $ ?user-id ?sub-chapter-id
                        :where
                        [?s :progress/user-id ?user-id]
                        [?s :progress/sub-chapter-id ?sub-chapter-id]
                        [?s :progress/sub-instructions ?sub-instructions]]
                      user-id sub-chapter-id)))


(defn find-progress-sub-chapter-ids-by-chapter-id-and-user-id
  [chapter-id user-id]
  (db/query-seq (db/q '[:find ?sub-chapter-id
                        :in $ ?chapter-id ?user-id
                        :where
                        [?s :progress/chapter-id ?chapter-id]
                        [?s :progress/sub-chapter-id ?sub-chapter-id]
                        [?s :progress/user-id ?user-id]]
                      chapter-id user-id)))


(defn find-progress-subject-ids-by-sub-chapter-id-and-user-id
  [sub-chapter-id user-id]
  (db/query-seq (db/q '[:find ?subject-id
                        :in $ ?sub-chapter-id ?user-id
                        :where
                        [?s :progress/sub-chapter-id ?sub-chapter-id]
                        [?s :progress/subject-id ?subject-id]
                        [?s :progress/user-id ?user-id]]
                      sub-chapter-id user-id)))


(defn find-progress-by-subject-id-and-user-id
  [subject-id user-id]
  (when-let [progress-id (db/q '[:find ?p .
                                 :in $ ?subject-id ?user-id
                                 :where
                                 [?p :progress/subject-id ?subject-id]
                                 [?p :progress/user-id ?user-id]]
                               subject-id user-id)]
    (db/entity progress-id)))