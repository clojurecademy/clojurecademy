(ns clojurecademy.controller.course.common
  (:require [clojurecademy.dao.course :as course.dao]
            [clojurecademy.dao.sub-chapter :as sub-chapter.dao]
            [clojurecademy.dao.subject :as subject.dao]
            [clojurecademy.dao.chapter :as chapter.dao]
            [clojurecademy.dao.user :as user.dao]
            [clojurecademy.validation :as validation]
            [clojurecademy.repl.util :as util]
            [clojure.string :as str]
            [clojurecademy.util.logging :as log]))

(def additional-db-persist-time 5000)

(defn get-release
  [user-id course-id]
  (when-not course-id
    (log/error (str "Get release course id is nil! Corresponding user id: " user-id)))
  (let [owner-id (some-> course-id course.dao/find-course-owner-by-course-id user.dao/find-user-id-by-username)]
    (when-not (= owner-id user-id)
      (+ (course.dao/find-course-release-by-course-id course-id) additional-db-persist-time))))


(defn- get-bug-report-email-or-link
  [course-id t]
  (let [email-or-link (course.dao/find-report-bug-email-or-link t course-id)]
    (if (validation/url? email-or-link)
      {:link email-or-link}
      {:email email-or-link})))


(defn able-to-access-all-subjects?
  [course-id user]
  (or (course.dao/find-course-is-skip?-by-course-id (get-release (:db/id user) course-id) course-id)
      (= (:user/username user) (course.dao/find-course-owner-by-course-id course-id))
      (= (:user/role user) :admin)))


(defn get-completed-sub-ins
  [progress subject-and-childs]
  (let [all-sub-ins       (:sub-instructions subject-and-childs)
        completed-sub-ins (map :db/id (:progress/sub-instructions progress))]
    (->> all-sub-ins
         (filter #(util/in? (:id %) completed-sub-ins))
         (map :name))))


(defn get-initial-code
  [subject-and-childs]
  (let [subject-ns       (-> subject-and-childs :subject :ns)
        ins-initial-code (some-> subject-and-childs :instruction :initial-code)]
    (if (str/blank? ins-initial-code)
      (str "(ns " subject-ns ")")
      (let [init-code (-> ins-initial-code read-string :form)
            ns*       (-> init-code util/wrap-code read-string)]
        (if (= 'ns (ffirst ns*))
          init-code
          (str "(ns " subject-ns ")\n" init-code))))))


(defn get-sub-ins-texts
  [sub-ins]
  (reduce #(conj %1 {:name (-> %2 :name)
                     :text (-> %2 :instruction-text read-string)}) [] sub-ins))


(defn get-common-data-map
  [release-t user subject-and-childs]
  (let [subject    (-> subject-and-childs :subject)
        subject-id (:id subject)
        course-id  (course.dao/find-course-id-by-subject-id-non-active-also subject-id)]
    {:font-size                (:user/font-size user)
     :wide-size-on?            (:user/wide-size-on? user)
     :learn-wide-size-on?      (:user/learn-wide-size-on? user)
     :learn                    (-> subject-and-childs :subject :learn read-string :texts)
     :subject-id               subject-id
     :subject-title            (:title subject)
     :subject-count            (subject.dao/find-count-of-subjects-by-subject-id release-t subject-id)
     :subject-index            (subject.dao/find-subject-index-by-subject-id release-t subject-id)
     :chapter-index            (chapter.dao/find-chapter-index-by-subject-id release-t subject-id)
     :sub-chapter-index        (sub-chapter.dao/find-sub-chapter-index-by-subject-id release-t subject-id)
     :title                    (course.dao/find-course-title-by-course-id course-id)
     :course-id                course-id
     :report-bug-email-or-link (get-bug-report-email-or-link course-id release-t)
     :skip?                    (course.dao/find-course-is-skip?-by-course-id release-t course-id)
     :owner?                   (= (:user/username user) (course.dao/find-course-owner-by-course-id course-id))
     :admin?                   (= (:user/role user) :admin)}))