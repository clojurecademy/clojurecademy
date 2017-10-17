(ns clojurecademy.dao.course
  (:require [datomic.api :as d]
            [clojurecademy.dao.db :as db]))


(defn find-number-of-courses
  []
  (db/q '[:find (count ?course-id) .
          :where
          [?course-id :course/active? true]]))


(defn find-number-of-released-courses
  []
  (db/q '[:find (count ?course-id) .
          :where
          [?course-id :course/active? true]
          [?course-id :course/released? true]]))


(defn find-all-released-course-ids
  []
  (db/query-seq (db/q '[:find ?course-id
                        :where
                        [?course-id :course/active? true]
                        [?course-id :course/released? true]])))


(defn find-all-released-courses
  []
  (db/q '[:find ?course-id ?title ?short-description (count ?users)
          :where
          [?course-id :course/active? true]
          [?course-id :course/released? true]
          [?course-id :course/title ?title]
          [?course-id :course/short-description ?short-description]
          [?course-id :course/users ?users]]))


(defn find-course-id-by-progress-id
  [progress-id]
  (db/q '[:find ?course-id .
          :in $ ?progress-id
          :where
          [?progress-id :progress/course-id ?course-id]
          [?course-id :course/active? true]]
        progress-id))


(defn find-course-name-by-course-id
  [course-id]
  (db/q '[:find ?course-name .
          :in $ ?course-id
          :where
          [?course-id :course/name ?course-name]
          [?course-id :course/active? true]]
        course-id))


(defn find-course-title-by-course-id
  [course-id]
  (db/q '[:find ?course-title .
          :in $ ?course-id
          :where
          [?course-id :course/title ?course-title]
          [?course-id :course/active? true]]
        course-id))


(defn find-course-short-description-by-course-id
  [course-id]
  (db/q '[:find ?short-description .
          :in $ ?course-id
          :where
          [?course-id :course/short-description ?short-description]
          [?course-id :course/active? true]]
        course-id))


(defn find-course-long-description-by-course-id
  [course-id]
  (db/q '[:find ?long-description .
          :in $ ?course-id
          :where
          [?course-id :course/long-description ?long-description]
          [?course-id :course/active? true]]
        course-id))


(defn find-course-who-is-this-course-for-by-course-id
  [course-id]
  (db/q '[:find ?who-is-this-course-for .
          :in $ ?course-id
          :where
          [?course-id :course/who-is-this-course-for ?who-is-this-course-for]
          [?course-id :course/active? true]]
        course-id))


(defn find-course-id-by-course-name
  [course-name]
  (db/q '[:find ?course-id .
          :in $ ?course-name
          :where
          [?course-id :course/name ?course-name]
          [?course-id :course/active? true]]
        course-name))


(defn find-course-released?-by-course-id
  [course-id]
  (db/q '[:find ?released .
          :in $ ?course-id
          :where
          [?course-id :course/active? true]
          [?course-id :course/released? ?released]]
        course-id))


(defn find-course-id-by-chapter-id
  [chapter-id]
  (db/q '[:find ?course-id .
          :in $ ?chapter-id
          :where
          [?chapter-id :chapter/active? true]
          [?chapter-id :chapter/course-id ?course-id]]
        chapter-id))


(defn find-course-id-by-chapter-id-non-active-also
  [chapter-id]
  (db/q '[:find ?course-id .
          :in $ ?chapter-id
          :where
          [?chapter-id :chapter/course-id ?course-id]]
        chapter-id))


(defn find-course-release-by-course-id
  [course-id]
  (db/q '[:find ?release .
          :in $ ?course-id
          :where
          [?course-id :course/latest-release ?release]
          [?course-id :course/active? true]]
        course-id))


(defn find-course-commit-by-course-id
  [course-id]
  (db/q '[:find ?commit .
          :in $ ?course-id
          :where
          [?course-id :course/latest-commit ?commit]
          [?course-id :course/active? true]]
        course-id))


(defn find-course-owner-by-course-id
  [course-id]
  (db/q '[:find ?owner .
          :in $ ?course-id
          :where
          [?course-id :course/owner ?owner]
          [?course-id :course/active? true]]
        course-id))


(defn find-course-is-skip?-by-course-id
  [t course-id]
  (db/q-as-of t '[:find ?skip .
                  :in $ ?course-id
                  :where
                  [?course-id :course/skip? ?skip]
                  [?course-id :course/active? true]]
              course-id))


(defn find-course-id-by-sub-chapter-id
  [sub-chapter-id]
  (db/q '[:find ?course-id .
          :in $ ?sub-chapter-id
          :where
          [?sub-chapter-id :sub-chapter/course-id ?course-id]
          [?sub-chapter-id :sub-chapter/active? true]]
        sub-chapter-id))


(defn find-course-id-by-sub-chapter-id-non-active-also
  [sub-chapter-id]
  (db/q '[:find ?course-id .
          :in $ ?sub-chapter-id
          :where
          [?sub-chapter-id :sub-chapter/course-id ?course-id]]
        sub-chapter-id))


(defn find-course-id-by-subject-id
  [subject-id]
  (db/q '[:find ?course-id .
          :in $ ?subject-id
          :where
          [?subject-id :subject/course-id ?course-id]
          [?subject-id :subject/active? true]]
        subject-id))


(defn find-course-id-by-subject-id-non-active-also
  [subject-id]
  (db/q '[:find ?course-id .
          :in $ ?subject-id
          :where
          [?subject-id :subject/course-id ?course-id]]
        subject-id))


;;TODO warning -> ?courses returns all courses like active false released false....
(defn find-course-ids-by-user-id
  [user-id]
  (db/query-seq (db/q '[:find ?courses
                        :in $ ?user-id
                        :where
                        [?user-id :user/courses ?courses]]
                      user-id)))


(defn find-created-course-ids-by-owner
  [owner]
  (db/query-seq (db/q '[:find ?c
                        :in $ ?owner
                        :where
                        [?c :course/owner ?owner]
                        [?c :course/active? true]]
                      owner)))


(defn find-course-released?-and-active?-by-course-id
  [course-id]
  (db/q '[:find ?name .
          :in $ ?course-id
          :where
          [?course-id :course/released? true]
          [?course-id :course/active? true]
          [?course-id :course/name ?name]]
        course-id))


(defn find-course-active?-by-course-id
  [course-id]
  (db/q '[:find ?name .
          :in $ ?course-id
          :where
          [?course-id :course/active? true]
          [?course-id :course/name ?name]]
        course-id))


(defn enroll-user-to-course
  [course-id user-id]
  (db/transact {:db/id course-id :course/users user-id})
  (db/transact {:db/id user-id :user/courses course-id}))


(defn find-course-id-and-title-count-by-owner-name
  [owner]
  (db/q '[:find ?c ?title (count ?users)
          :in $ ?owner
          :where
          [?c :course/owner ?owner]
          [?c :course/active? true]
          [?c :course/released? true]
          [?c :course/title ?title]
          [?c :course/users ?users]]
        owner))


(defn find-report-bug-email-or-link
  [t course-id]
  (db/q-as-of t '[:find ?report-bug-email-or-link .
                  :in $ ?course-id
                  :where
                  [?e :course/report-bug-email-or-link ?report-bug-email-or-link]
                  [?e :course/active? true]]
              course-id))


(defn find-helper-fns-by-course-id
  [t course-id]
  (db/q-as-of t '[:find ?helper-fns .
                  :in $ ?course-id
                  :where
                  [?course-id :course/active? true]
                  [?course-id :course/helper-fns ?helper-fns]]
              course-id))



(defn enrolled?
  [course-id user-id]
  (let [course (db/entity course-id)]
    (some #(= user-id %) (map :db/id (:course/users course)))))


(defn owner?
  [user-id course-id]
  (db/q '[:find ?user-id .
          :in $ ?user-id ?course-id
          :where
          [?course-id :course/active? true]
          [?user-id :user/username ?username]
          [?course-id :course/owner ?username]]
        user-id course-id))