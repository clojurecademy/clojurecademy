(ns clojurecademy.controller.course.learn
  (:require [clojurecademy.dao.progress :as progress.dao]
            [clojurecademy.dao.sub-instruction :as sub-ins.dao]
            [clojurecademy.dao.user :as user.dao]
            [clojurecademy.dao.course :as course.dao]
            [clojurecademy.dao.chapter :as chapter.dao]
            [clojurecademy.dao.sub-chapter :as sub-chapter.dao]
            [clojurecademy.dao.subject :as subject.dao]
            [clojurecademy.dao.db :as db]
            [clojurecademy.util.resource :as resource.util]
            [clojurecademy.controller.course.resume :as course.resume]
            [clojurecademy.controller.course.common :as course.common]
            [clojure.set :as s]
            [kezban.core :refer :all]))


(defn- get-percentage
  [all-sub-ins progress-sub-ins]
  (let [all-sub-ins           (set all-sub-ins)
        progress-sub-ins      (set progress-sub-ins)
        left-sun-ins-ids      (s/difference all-sub-ins progress-sub-ins)
        count-all-sub-ins     (if (= (count all-sub-ins) 0) 1 (count all-sub-ins))
        count-left-course-ids (count left-sun-ins-ids)]
    (int (* (/ (- count-all-sub-ins count-left-course-ids) count-all-sub-ins) 100.0))))


(defn- get-course-completed-percentage
  [user-id course-id]
  (let [release-t (course.common/get-release user-id course-id)]
    (get-percentage (sub-ins.dao/find-sub-ins-ids-by-course-id release-t course-id)
                    (progress.dao/find-progress-sub-ins-ids-by-course-id-and-user-id user-id course-id))))


(defn- get-sub-chapter-completed-percentage
  [user-id sub-chapter-id]
  (let [release-t (course.common/get-release user-id (course.dao/find-course-id-by-sub-chapter-id-non-active-also sub-chapter-id))]
    (get-percentage (sub-ins.dao/find-sub-ins-ids-by-sub-chapter-id-and-course-id release-t sub-chapter-id)
                    (progress.dao/find-progress-sub-chapter-ids-by-course-id-and-user-id user-id sub-chapter-id))))


(defn- course-id->enrolled-course-map
  [user-id course-id]
  {:course-id                    course-id
   :course-title                 (course.dao/find-course-title-by-course-id course-id)
   :course-percentage            (get-course-completed-percentage user-id course-id)
   :course-progress-last-updated (progress.dao/find-progresses-last-updated-by-user-id-and-course-id user-id course-id)})


(defn- course-id->created-course-map
  [course-id]
  {:course-id          course-id
   :course-title       (course.dao/find-course-title-by-course-id course-id)
   :course-last-commit (course.dao/find-course-commit-by-course-id course-id)})


(defn- assoc-percentage
  [user-id sub-chapters]
  (reduce #(conj %1 (assoc %2 :percentage (get-sub-chapter-completed-percentage user-id (:id %2)))) [] sub-chapters))


(defn- get-sub-chapters
  [release-t chapter-id]
  (->> chapter-id
       (sub-chapter.dao/find-sub-chapters-by-chapter-id release-t)
       (map #(db/get-non-qua-and-given-keys % [:id :index :title]))
       (sort-by :index)))


(defn get-chapter-percentage
  [sub-chapters]
  (->> (count sub-chapters)
       (/ (reduce #(+ %1 (:percentage %2)) 0 sub-chapters))
       int))


(defn lock-needed?
  [user-id sub-chapter]
  (let [sub-chapter-id (:id sub-chapter)
        course-id      (course.dao/find-course-id-by-sub-chapter-id-non-active-also sub-chapter-id)
        release-t      (course.common/get-release user-id course-id)]
    (cond
      (progress.dao/find-progress-id-by-sub-chapter-id-and-user-id sub-chapter-id user-id)
      false

      (= sub-chapter-id (sub-chapter.dao/find-first-sub-chapter-id-of-course-by-course-id release-t course-id))
      false

      (when-let* [pre-sub-ch-id (sub-chapter.dao/find-previous-sub-chapter-id-by-sub-chapter-id release-t sub-chapter-id)
                  max-indexed-subject-id (subject.dao/find-max-indexed-subject-id-by-sub-chapter-id release-t pre-sub-ch-id)
                  progress (progress.dao/find-progress-by-subject-id-and-user-id max-indexed-subject-id user-id)]
                 (:progress/done? progress))
      false

      (when-let* [_ (not (sub-chapter.dao/find-previous-sub-chapter-id-by-sub-chapter-id release-t sub-chapter-id))
                  chapter-id (chapter.dao/find-chapter-id-by-sub-chapter-id release-t sub-chapter-id)
                  pre-chapter-id (chapter.dao/find-previous-chapter-id-by-chapter-id release-t chapter-id)
                  max-indexed-sub-chapter-id (sub-chapter.dao/find-max-indexed-sub-chapter-id-by-chapter-id release-t pre-chapter-id)
                  max-indexed-subject-id (subject.dao/find-max-indexed-subject-id-by-sub-chapter-id release-t max-indexed-sub-chapter-id)
                  progress (progress.dao/find-progress-by-subject-id-and-user-id max-indexed-subject-id user-id)]
                 (:progress/done? progress))
      false

      :else
      true)))


(defn assoc-locked-if-needed
  [user-id v sub-chapter]
  (if (lock-needed? user-id sub-chapter)
    (conj v (assoc sub-chapter :locked? true))
    (conj v sub-chapter)))


(defn assoc-locked-if-not-skip?
  [user-id chapter-id sub-chapters]
  (let [course-id (course.dao/find-course-id-by-chapter-id-non-active-also chapter-id)
        skip?     (course.dao/find-course-is-skip?-by-course-id (course.common/get-release user-id course-id) course-id)]
    (if skip?
      sub-chapters
      (reduce #(assoc-locked-if-needed user-id %1 %2) [] sub-chapters))))


(defn- chapter->chapter-sub-chapter-percentage-maps
  [user-id chapter]
  (let [chapter-id   (:db/id chapter)
        course-id    (course.dao/find-course-id-by-chapter-id-non-active-also chapter-id)
        release-t    (course.common/get-release user-id course-id)
        sub-chapters (assoc-locked-if-not-skip? user-id chapter-id (assoc-percentage user-id (get-sub-chapters release-t chapter-id)))]
    {:title              (:chapter/title chapter)
     :index              (:chapter/index chapter)
     :chapter-percentage (get-chapter-percentage sub-chapters)
     :sub-chapters       sub-chapters}))


(defn- chapter->chapter-sub-chapter-maps
  [release-t chapter]
  (let [chapter-id   (:db/id chapter)
        sub-chapters (get-sub-chapters release-t chapter-id)]
    {:title        (:chapter/title chapter)
     :index        (:chapter/index chapter)
     :sub-chapters sub-chapters}))


(defn get-enrolled-courses
  [user-id]
  (->> user-id
       course.dao/find-course-ids-by-user-id
       (filter course.dao/find-course-released?-and-active?-by-course-id)
       (map #(course-id->enrolled-course-map user-id %))
       (sort-by :course-progress-last-updated #(compare %2 %1))))


(defn get-created-courses
  [username user-id]
  (->> username
       course.dao/find-created-course-ids-by-owner
       (filter course.dao/find-course-active?-by-course-id)
       (map course-id->created-course-map)
       (sort-by :course-last-commit #(compare %2 %1))))


(defn get-chapter-and-sub-chapters-percentage-maps
  [user-id course-id]
  (->> course-id
       (chapter.dao/find-chapters-by-course-id (course.common/get-release user-id course-id))
       (map #(chapter->chapter-sub-chapter-percentage-maps user-id %))
       (sort-by :index)))


(defn get-chapter-and-sub-chapters-maps
  [user-id course-id]
  (let [release-t (course.common/get-release user-id course-id)]
    (->> course-id
         (chapter.dao/find-chapters-by-course-id release-t)
         (map #(chapter->chapter-sub-chapter-maps release-t %))
         (sort-by :index))))


(defn get-course-percentage
  [course-name-and-percentage-maps course-id]
  (some #(when (= (:course-id %) course-id) (:course-percentage %)) course-name-and-percentage-maps))


(defn get-sub-chapter-resume-id
  [user-id course-id]
  (if (progress.dao/find-progress-id-by-course-id-and-user-id course-id user-id)
    (let [chapter-id     (course.resume/get-biggest-indexed-chapter-id course-id user-id)
          sub-chapter-id (course.resume/get-biggest-indexed-sub-chapter-id chapter-id user-id)]
      sub-chapter-id)
    (sub-chapter.dao/find-first-sub-chapter-id-of-course-by-course-id (course.common/get-release user-id course-id) course-id)))


(defn get-common-syllabus-data-map
  [course-id]
  {:owner        (course.dao/find-course-owner-by-course-id course-id)
   :title        (course.dao/find-course-title-by-course-id course-id)
   :last-updated (course.dao/find-course-release-by-course-id course-id)})


(defn get-common-overview-data-map
  [course-id]
  (merge (get-common-syllabus-data-map course-id)
         {:short-description      (course.dao/find-course-short-description-by-course-id course-id)
          :long-description       (course.dao/find-course-long-description-by-course-id course-id)
          :who-is-this-course-for (course.dao/find-course-who-is-this-course-for-by-course-id course-id)}))


(defn get-user-progress-map-for-syllabus
  [ctx course-id]
  (if-let [user (:user (resource.util/authorized? ctx))]
    (let [user-id          (:db/id user)
          enrolled-courses (get-enrolled-courses user-id)
          common-data-map  (get-common-syllabus-data-map course-id)]
      (merge common-data-map {:enrolled-courses              enrolled-courses
                              :created-courses               (get-created-courses (:user/username user) user-id)
                              :chapter-and-sub-chapters-maps (get-chapter-and-sub-chapters-percentage-maps user-id course-id)
                              :course-percentage             (get-course-percentage enrolled-courses course-id)
                              :sub-chapter-resume-id         (get-sub-chapter-resume-id user-id course-id)
                              :owner?                        (= (:user/username user) (:owner common-data-map))
                              :enrolled?                     (course.dao/enrolled? course-id user-id)
                              :skip?                         (course.dao/find-course-is-skip?-by-course-id (course.common/get-release user-id course-id) course-id)
                              :last-commit                   (course.dao/find-course-commit-by-course-id course-id)}))
    (merge (get-common-syllabus-data-map course-id) {:chapter-and-sub-chapters-maps (get-chapter-and-sub-chapters-maps nil course-id)} {:no-auth? true})))


(defn get-user-progress-map-for-overview
  [ctx course-id]
  (if-let [user (:user (resource.util/authorized? ctx))]
    (let [user-id                         (:db/id user)
          course-name-and-percentage-maps (get-enrolled-courses user-id)
          common-data-map                 (get-common-overview-data-map course-id)]
      (merge common-data-map {:enrolled-courses  course-name-and-percentage-maps
                              :created-courses   (get-created-courses (:user/username user) user-id)
                              :course-percentage (get-course-percentage course-name-and-percentage-maps course-id)
                              :owner?            (= (:user/username user) (:owner common-data-map))
                              :enrolled?         (course.dao/enrolled? course-id user-id)
                              :last-commit       (course.dao/find-course-commit-by-course-id course-id)}))
    (merge (get-common-overview-data-map course-id) {:no-auth? true})))



