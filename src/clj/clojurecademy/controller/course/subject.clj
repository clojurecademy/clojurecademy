(ns clojurecademy.controller.course.subject
  (:require [clojurecademy.dao.progress :as progress.dao]
            [clojurecademy.dao.chapter :as chapter.dao]
            [clojurecademy.dao.subject :as subject.dao]
            [clojurecademy.dao.sub-chapter :as sub-chapter.dao]
            [clojurecademy.dao.course :as course.dao]
            [clojurecademy.controller.course.common :as course.common]
            [kezban.core :refer :all]))


(defn- get-subject-map
  [user subject-id]
  (let [course-id          (course.dao/find-course-id-by-subject-id-non-active-also subject-id)
        release-t          (course.common/get-release (:db/id user) course-id)
        subject-and-childs (subject.dao/collect-subject-and-childs release-t subject-id)
        common-map         (course.common/get-common-data-map release-t user subject-and-childs)]
    (if-let [instruction (:instruction subject-and-childs)]
      (let [initial-code (course.common/get-initial-code subject-and-childs)
            sub-ins-text (course.common/get-sub-ins-texts (:sub-instructions subject-and-childs))
            before-start (merge {:run-pre-tests? (:run-pre-tests? instruction)} (try-> instruction :rule read-string))]
        (assoc common-map :initial-code initial-code :sub-instructions sub-ins-text :before-start before-start))
      (assoc common-map :no-ins? true))))


(defn- get-tracked-subject-map
  [user subject-id]
  (let [progress           (progress.dao/find-progress-by-subject-id-and-user-id subject-id (:db/id user))
        course-id          (course.dao/find-course-id-by-subject-id-non-active-also subject-id)
        release-t          (course.common/get-release (:db/id user) course-id)
        subject-and-childs (subject.dao/collect-subject-and-childs release-t subject-id)
        common-map         (course.common/get-common-data-map release-t user subject-and-childs)]
    (if-let [instruction (:instruction subject-and-childs)]
      (let [initial-code      (course.common/get-initial-code subject-and-childs)
            sub-ins-text      (course.common/get-sub-ins-texts (:sub-instructions subject-and-childs))
            completed-sub-ins (course.common/get-completed-sub-ins progress subject-and-childs)
            before-start      (merge {:run-pre-tests? (:run-pre-tests? instruction)} (try-> instruction :rule read-string))]
        (assoc common-map :initial-code (:progress/code progress)
                          :sub-instructions sub-ins-text
                          :completed-sub-instructions completed-sub-ins
                          :before-start before-start
                          :done? (:progress/done? progress)))
      (assoc common-map :no-ins? true))))


(defn get-pre-subject-id
  [release-t subject-id]
  (if-let [pre-subject-id (subject.dao/find-pre-subject-id-by-subject-id release-t subject-id)]
    pre-subject-id
    (let [sub-chapter-id (sub-chapter.dao/find-sub-chapter-id-by-subject-id release-t subject-id)]
      (if-let [pre-sub-chapter-id (some->> sub-chapter-id (sub-chapter.dao/find-previous-sub-chapter-id-by-sub-chapter-id release-t))]
        (subject.dao/find-max-indexed-subject-id-by-sub-chapter-id release-t pre-sub-chapter-id)
        (let [chapter-id (chapter.dao/find-chapter-id-by-sub-chapter-id release-t sub-chapter-id)]
          (when-let [pre-chapter-id (some->> chapter-id (chapter.dao/find-previous-chapter-id-by-chapter-id release-t))]
            (subject.dao/find-latest-subject-id-of-chapter-by-chapter-id release-t pre-chapter-id)))))))


(defn get-next-subject-id
  [release-t subject-id]
  (if-let [next-subject-id (subject.dao/find-next-subject-id-by-subject-id release-t subject-id)]
    next-subject-id
    (let [sub-chapter-id (sub-chapter.dao/find-sub-chapter-id-by-subject-id release-t subject-id)]
      (if-let [next-sub-chapter-id (some->> sub-chapter-id (sub-chapter.dao/find-next-sub-chapter-id-by-sub-chapter-id release-t))]
        (subject.dao/find-first-subject-id-of-sub-chapter-by-sub-chapter-id release-t next-sub-chapter-id)
        (let [chapter-id (chapter.dao/find-chapter-id-by-sub-chapter-id release-t sub-chapter-id)]
          (when-let [next-chapter-id (some->> chapter-id (chapter.dao/find-next-chapter-id-by-chapter-id release-t))]
            (subject.dao/find-first-subject-id-of-chapter-by-chapter-id release-t next-chapter-id)))))))


(defn get-subject
  [user subject-id k]
  (if subject-id
    (if-let [progress (progress.dao/find-progress-by-subject-id-and-user-id subject-id (:db/id user))]
      (get-tracked-subject-map user subject-id)
      (get-subject-map user subject-id))
    {k true}))
