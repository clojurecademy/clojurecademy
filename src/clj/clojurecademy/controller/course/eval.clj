(ns clojurecademy.controller.course.eval
  (:require [clojurecademy.controller.course.common :as course.common]
            [clojurecademy.dao.progress :as progress.dao]
            [clojurecademy.dao.db :as db]
            [clojurecademy.dao.chapter :as chapter.dao]
            [clojurecademy.dao.sub-chapter :as sub-chapter.dao]
            [clojurecademy.dao.course :as course.dao]
            [clojurecademy.repl.util :as util]
            [clojurecademy.repl.runner :as r]
            [clojure.set :as s]
            [kezban.core :refer :all]))


(defn- get-biggest-indexed-sub-ins
  [sub-instructions]
  (->> sub-instructions
       (sort-by :index #(compare %2 %1))
       first))


(defn- get-lowest-indexed-sub-ins
  [left-sub-ins sub-instructions]
  (->> sub-instructions
       (filter #(util/in? (:id %) left-sub-ins))
       (sort-by :index)
       first))


(defn- get-left-sub-ins
  [subject progress]
  (let [sub-instructions     (-> subject :subject :instruction :sub-instructions)
        sub-ins-ids          (into #{} (map :id sub-instructions))
        progress-sub-ins-ids (into #{} (map :db/id (:progress/sub-instructions progress)))]
    (s/difference sub-ins-ids progress-sub-ins-ids)))


(defn- get-current-sub-ins
  [subject progress]
  (if progress
    (let [sub-instructions (-> subject :subject :instruction :sub-instructions)
          left-sub-ins     (get-left-sub-ins subject progress)]
      (if (= (count left-sub-ins) 0)
        (:name (get-biggest-indexed-sub-ins sub-instructions))
        (:name (get-lowest-indexed-sub-ins left-sub-ins sub-instructions))))
    (-> subject :subject :instruction :sub-instructions first :name)))


(defn- assoc-done?-if-needed
  [result subject progress progress-map]
  (let [sub-instructions (-> subject :subject :instruction :sub-instructions)
        left-sub-ins     (get-left-sub-ins subject progress)]
    (if (and (not (:progress/done? progress))
             (every? :passed (:sub-ins-tests result))
             (or (not (seq left-sub-ins))
                 (= (first left-sub-ins) (:id (get-biggest-indexed-sub-ins sub-instructions)))))
      (assoc progress-map :progress/done? true)
      progress-map)))


(defn- persist-progress
  [user progress subject result d]
  (let [subject-id     (-> subject :subject :id)
        user-id        (:db/id user)
        course-id      (course.dao/find-course-id-by-subject-id-non-active-also subject-id)
        release-t      (course.common/get-release user-id course-id)
        sub-chapter-id (sub-chapter.dao/find-sub-chapter-id-by-subject-id release-t subject-id)
        chapter-id     (chapter.dao/find-chapter-id-by-sub-chapter-id release-t sub-chapter-id)
        passed-sub-ins (into #{} (map :id (filter :passed (:sub-ins-tests result))))
        progress-map   {:db/id                     (or (:db/id progress) (db/temp-id))
                        :progress/user-id          user-id
                        :progress/course-id        course-id
                        :progress/chapter-id       chapter-id
                        :progress/sub-chapter-id   sub-chapter-id
                        :progress/subject-id       subject-id
                        :progress/instruction-id   (-> subject :subject :instruction :id)
                        :progress/sub-instructions passed-sub-ins
                        :progress/last-updated     (System/currentTimeMillis)
                        :progress/code             (:client-code d)}
        final-progress (assoc-done?-if-needed result subject progress progress-map)]
    (progress.dao/create-progress final-progress)
    final-progress))


(defn- persist-progress-for-no-ins
  [user progress subject]
  (let [subject-id          (-> subject :subject :id)
        user-id             (:db/id user)
        course-id           (course.dao/find-course-id-by-subject-id-non-active-also subject-id)
        release-t           (course.common/get-release user-id course-id)
        sub-chapter-id      (sub-chapter.dao/find-sub-chapter-id-by-subject-id release-t subject-id)
        chapter-id          (chapter.dao/find-chapter-id-by-sub-chapter-id release-t sub-chapter-id)
        progress-no-ins-map {:db/id                   (or (:db/id progress) (db/temp-id))
                             :progress/user-id        user-id
                             :progress/course-id      course-id
                             :progress/chapter-id     chapter-id
                             :progress/sub-chapter-id sub-chapter-id
                             :progress/subject-id     subject-id
                             :progress/last-updated   (System/currentTimeMillis)
                             :progress/no-ins?        true}]
    (progress.dao/create-progress progress-no-ins-map)))


(defn get-helper-fns
  [user-id subject]
  (let [subject-id (-> subject :subject :id)
        course-id  (course.dao/find-course-id-by-subject-id-non-active-also subject-id)]
    (-> (course.common/get-release user-id course-id)
        (course.dao/find-helper-fns-by-course-id course-id)
        read-string)))

(defn- persist-progress-and-return-execution-result
  [user subject d]
  (let [progress                    (progress.dao/find-progress-by-subject-id-and-user-id (-> subject :subject :id) (:db/id user))
        current-sub-ins             (get-current-sub-ins subject progress)
        subject-and-current-sub-ins {:subject (-> subject :subject) :current-sub-ins current-sub-ins}
        helper-fns                  (get-helper-fns (:db/id user) subject)
        result                      (r/eval-code (:user/username user) subject-and-current-sub-ins helper-fns (:client-code d))
        final-progress              (when-not (:error result) (persist-progress user progress subject result d))]
    (assoc result :done? (:progress/done? final-progress))))


(defn- persist-progress-for-no-ins-and-return-execution-result
  [user subject d]
  (let [progress (progress.dao/find-progress-by-subject-id-and-user-id (-> subject :subject :id) (:db/id user))
        result   (r/eval-code (:user/username user) {:subject (-> subject :subject) :current-sub-ins nil} '() (:client-code d))]
    (when-not (:error result)
      (persist-progress-for-no-ins user progress subject))
    result))


(defn- create-sub-ins-structure
  [sub-instructions]
  (reduce (fn [v sub-ins]
            (conj v (into {} (reduce (fn [v [k val]]
                                       (if-not (or (= k :index) (= k :id))
                                         (conj v [k (read-string val)])
                                         (conj v [k val]))) [] sub-ins)))) [] sub-instructions))


(defn create-subject-structure
  [m]
  (if (:instruction m)
    {:subject {:id          (-> m :subject :id)
               :ns          (some-> m :subject :ns symbol)
               :instruction {:id               (-> m :instruction :id)
                             :name             (-> m :subject :name symbol)
                             :run-pre-tests?   (-> m :instruction :run-pre-tests?)
                             :initial-code     (try-> m :instruction :initial-code read-string)
                             :rule             (try-> m :instruction :rule read-string)
                             :sub-instructions (create-sub-ins-structure (:sub-instructions m))}}}
    {:subject {:id (-> m :subject :id)
               :ns (some-> m :subject :ns symbol)}}))


(defn get-result
  [user subject d]
  (when (> (count (:client-code d)) 30000)
    (util/runtime-ex "Your input is too long, you can't evaluate more than 30.000 characters"))
  (if (-> subject :subject :instruction)
    (persist-progress-and-return-execution-result user subject d)
    (persist-progress-for-no-ins-and-return-execution-result user subject d)))


(defn get-repl-result
  [username code]
  (when (> (count code) 30000)
    (util/runtime-ex "Your input is too long, you can't evaluate more than 30.000 characters"))
  (r/eval-repl-code username code))