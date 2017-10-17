(ns clojurecademy.controller.course.resume
  (:require [clojurecademy.dao.subject :as subject.dao]
            [clojurecademy.dao.progress :as progress.dao]
            [clojurecademy.dao.db :as db]
            [clojurecademy.dao.chapter :as chapter.dao]
            [clojurecademy.dao.sub-chapter :as sub-chapter.dao]
            [clojurecademy.dao.course :as course.dao]
            [clojurecademy.controller.course.common :as course.common]
            [kezban.core :refer :all]))


(defn- get-biggest-indexed-entity
  [e-ids e-keyword]
  (let [combine-keys (comp keyword #(str (name e-keyword) "/" %))
        active-k     (combine-keys "active?")
        index-k      (combine-keys "index")]
    (->> e-ids
         (map db/entity)
         (filter active-k)
         (sort-by index-k #(compare %2 %1))
         first
         :db/id)))


(defn get-biggest-indexed-chapter-id
  [course-id user-id]
  (let [chapter-ids (progress.dao/find-progress-chapter-ids-by-course-id-and-user-id course-id user-id)]
    (-> chapter-ids (get-biggest-indexed-entity :chapter))))


(defn get-biggest-indexed-sub-chapter-id
  [chapter-id user-id]
  (let [sub-chapter-ids (progress.dao/find-progress-sub-chapter-ids-by-chapter-id-and-user-id chapter-id user-id)]
    (-> sub-chapter-ids (get-biggest-indexed-entity :sub-chapter))))


(defn- get-biggest-indexed-subject-id
  [sub-chapter-id user-id]
  (let [subject-ids (progress.dao/find-progress-subject-ids-by-sub-chapter-id-and-user-id sub-chapter-id user-id)]
    (-> subject-ids (get-biggest-indexed-entity :subject))))


(defn- able-to-resume-sub-chapter-first-subject?
  [user sub-chapter-id course-id]
  (let [user-id     (:db/id user)
        sub-chapter (db/entity sub-chapter-id)
        release-t   (course.common/get-release user-id course-id)
        chapter     (chapter.dao/find-chapter-by-sub-chapter-id release-t sub-chapter-id)]
    (if (or (= (:sub-chapter/index sub-chapter) (:chapter/index chapter) 0)
            (course.common/able-to-access-all-subjects? course-id user))
      true
      (if-let [pre-sub-chapter-id (sub-chapter.dao/find-previous-sub-chapter-id-by-sub-chapter-id release-t sub-chapter-id)]
        (let [max-indexed-subject-id (subject.dao/find-max-indexed-subject-id-by-sub-chapter-id release-t pre-sub-chapter-id)
              progress               (progress.dao/find-progress-by-subject-id-and-user-id max-indexed-subject-id user-id)]
          (true? (:progress/done? progress)))
        ;;TODO chain with ->>....refactor
        (let [previous-chapter-id         (chapter.dao/find-previous-chapter-id-by-chapter-id release-t (:db/id chapter))
              max-indexded-sub-chapter-id (sub-chapter.dao/find-max-indexed-sub-chapter-id-by-chapter-id release-t previous-chapter-id)
              max-indexed-subject-id      (subject.dao/find-max-indexed-subject-id-by-sub-chapter-id release-t max-indexded-sub-chapter-id)
              progress                    (progress.dao/find-progress-by-subject-id-and-user-id max-indexed-subject-id user-id)]
          (true? (:progress/done? progress)))))))


(defn get-tracked-latest-subject-map
  [user course-id]
  (let [user-id            (:db/id user)
        chapter-id         (get-biggest-indexed-chapter-id course-id user-id)
        sub-chapter-id     (get-biggest-indexed-sub-chapter-id chapter-id user-id)
        subject-id         (get-biggest-indexed-subject-id sub-chapter-id user-id)
        progress           (progress.dao/find-progress-by-subject-id-and-user-id subject-id user-id)
        release-t          (course.common/get-release user-id course-id)
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


(defn get-first-subject-of-sub-chapter-map
  [user sub-chapter-id course-id]
  (if-not (able-to-resume-sub-chapter-first-subject? user sub-chapter-id course-id)
    {:locked-subject true}
    (let [release-t          (course.common/get-release (:db/id user) course-id)
          subject            (subject.dao/find-first-subject-of-sub-chapter-by-sub-chapter-id release-t sub-chapter-id)
          subject-id         (:db/id subject)
          subject-and-childs (subject.dao/collect-subject-and-childs release-t subject-id)
          common-map         (course.common/get-common-data-map release-t user subject-and-childs)]
      (if-let [instruction (:instruction subject-and-childs)]
        (let [initial-code (course.common/get-initial-code subject-and-childs)
              sub-ins-text (course.common/get-sub-ins-texts (:sub-instructions subject-and-childs))
              before-start (merge {:run-pre-tests? (:run-pre-tests? instruction)} (try-> instruction :rule read-string))]
          (assoc common-map :initial-code initial-code :sub-instructions sub-ins-text :before-start before-start))
        (assoc common-map :no-ins? true)))))


(defn get-tracked-latest-subject-of-sub-chapter-map
  [user sub-chapter-id]
  (let [user-id            (:db/id user)
        subject-id         (get-biggest-indexed-subject-id sub-chapter-id user-id)
        course-id          (course.dao/find-course-id-by-subject-id subject-id)
        progress           (progress.dao/find-progress-by-subject-id-and-user-id subject-id user-id)
        release-t          (course.common/get-release user-id course-id)
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


(defn get-first-subject-of-course-map
  [user course-id]
  (let [release-t          (course.common/get-release (:db/id user) course-id)
        subject            (subject.dao/find-first-subject-of-course-by-course-id release-t course-id)
        subject-id         (:db/id subject)
        subject-and-childs (subject.dao/collect-subject-and-childs release-t subject-id)
        common-map         (course.common/get-common-data-map release-t user subject-and-childs)]
    (if-let [instruction (:instruction subject-and-childs)]
      (let [initial-code (course.common/get-initial-code subject-and-childs)
            sub-ins-text (course.common/get-sub-ins-texts (:sub-instructions subject-and-childs))
            before-start (merge {:run-pre-tests? (:run-pre-tests? instruction)} (try-> instruction :rule read-string))]
        (assoc common-map :initial-code initial-code :sub-instructions sub-ins-text :before-start before-start))
      (assoc common-map :no-ins? true))))