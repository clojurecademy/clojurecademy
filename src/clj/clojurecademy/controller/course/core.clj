(ns clojurecademy.controller.course.core
  (:require [clojurecademy.dao.course :as course.dao]
            [clojurecademy.dao.progress :as progress.dao]
            [clojurecademy.dao.sub-chapter :as sub-chapter.dao]
            [clojurecademy.dao.subject :as subject.dao]
            [clojurecademy.controller.course.eval :as course.eval]
            [clojurecademy.controller.course.subject :as course.subject]
            [clojurecademy.controller.course.resume :as course.resume]
            [clojurecademy.controller.course.common :as course.common]
            [clojurecademy.controller.course.learn :as course.learn]
            [clojurecademy.util.resource :as resource.util :refer [check]]
            [clojurecademy.repl.util :as util]
            [clojurecademy.view.learn :as view.learn]
            [clojurecademy.view.courses :as view.courses]
            [clojurecademy.dsl.validator :as dsl.validator]
            [clojurecademy.controller.auth :as controller.auth]
            [clojurecademy.dao.user :as user.dao]
            [clojurecademy.dao.db :as db]
            [clojurecademy.util.logging :as log]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [kezban.core :refer :all]))


(defn- not-enrolled?
  [course-id user]
  (if (or (= (:user/username user) (course.dao/find-course-owner-by-course-id course-id))
          (= (:user/role user) :admin))
    false
    (not (course.dao/enrolled? course-id (:db/id user)))))


(defn resume-course
  [ctx course-id]
  (let [user (:user ctx)
        user-id (:db/id user)]
    (log/info (str "User: " (:user/username user) " resuming course."))
    (cond
      (not (course.dao/find-course-name-by-course-id course-id))
      {:does-not-exists? true}

      (not-enrolled? course-id user)
      {:not-enrolled? true :course-id course-id}

      :else
      (if (progress.dao/find-progress-id-by-course-id-and-user-id course-id user-id)
        (course.resume/get-tracked-latest-subject-map user course-id)
        (course.resume/get-first-subject-of-course-map user course-id)))))


(defn release-course
  [ctx course-id]
  (let [user (:user ctx)
        user-id (:db/id user)]
    (log/info (str "User: " (:user/username user) " releasing course."))
    (cond
      (not (course.dao/find-course-name-by-course-id course-id))
      {:does-not-exists? true}

      (not= (course.dao/find-course-owner-by-course-id course-id) (:user/username user))
      {:not-owner? true}

      :else
      (do
        (db/transact {:db/id                 course-id
                      :course/latest-release (System/currentTimeMillis)
                      :course/released?      true})
        {:success true}))))


(defn resume-sub-chapter
  [ctx sub-chapter-id]
  (let [course-id (course.dao/find-course-id-by-sub-chapter-id-non-active-also sub-chapter-id)
        user (:user ctx)
        user-id (:db/id user)
        release-t (some->> course-id (course.common/get-release user-id))]
    (log/info (str "User: " (:user/username user) " resuming course via sub chapter."))
    (cond
      (or (not course-id)
          (not (sub-chapter.dao/find-sub-chapter-name-by-sub-chapter-id release-t sub-chapter-id))
          (not (course.dao/find-course-name-by-course-id course-id)))
      {:does-not-exists? true}

      (not-enrolled? course-id user)
      {:not-enrolled? true :course-id course-id}

      :else
      (if (progress.dao/find-progress-id-by-sub-chapter-id-and-user-id sub-chapter-id user-id)
        (course.resume/get-tracked-latest-subject-of-sub-chapter-map user sub-chapter-id)
        (course.resume/get-first-subject-of-sub-chapter-map user sub-chapter-id course-id)))))


(defn subject
  [ctx subject-id]
  (let [course-id (course.dao/find-course-id-by-subject-id-non-active-also subject-id)
        user (:user ctx)
        user-id (:db/id user)
        release-t (some->> course-id (course.common/get-release user-id))]
    (log/info (str "User: " (:user/username user) " resuming course via subject."))
    (cond
      (or (not course-id)
          (not (subject.dao/find-subject-name-by-subject-id release-t subject-id))
          (not (course.dao/find-course-name-by-course-id course-id)))
      {:does-not-exists? true}

      (not-enrolled? course-id user)
      {:not-enrolled? true :course-id course-id}

      (and (:instruction (subject.dao/collect-subject-and-childs release-t subject-id))
           (not (:progress/done? (progress.dao/find-progress-by-subject-id-and-user-id subject-id user-id)))
           (not (course.common/able-to-access-all-subjects? course-id user)))
      {:locked-subject true}

      :else
      (course.subject/get-subject user subject-id :course-finished?))))


(defn next-subject
  [ctx subject-id]
  (let [course-id (course.dao/find-course-id-by-subject-id-non-active-also subject-id)
        user (:user ctx)
        user-id (:db/id user)
        release-t (some->> course-id (course.common/get-release user-id))]
    (log/info (str "User: " (:user/username user) " requested next subject."))
    (cond
      (or (not course-id)
          (not (subject.dao/find-subject-name-by-subject-id release-t subject-id))
          (not (course.dao/find-course-name-by-course-id course-id)))
      {:does-not-exists? true}

      (not-enrolled? course-id user)
      {:not-enrolled? true :course-id course-id}

      (and (:instruction (subject.dao/collect-subject-and-childs release-t subject-id))
           (not (:progress/done? (progress.dao/find-progress-by-subject-id-and-user-id subject-id user-id)))
           (not (course.common/able-to-access-all-subjects? course-id user)))
      {:locked-subject true}

      :else
      (course.subject/get-subject user (course.subject/get-next-subject-id release-t subject-id) :course-finished?))))


(defn pre-subject
  [ctx subject-id]
  (let [course-id (course.dao/find-course-id-by-subject-id-non-active-also subject-id)
        user (:user ctx)
        user-id (:db/id user)
        release-t (some->> course-id (course.common/get-release user-id))]
    (log/info (str "User: " (:user/username user) " requested pre subject."))
    (cond
      (or (not course-id)
          (not (subject.dao/find-subject-name-by-subject-id release-t subject-id))
          (not (course.dao/find-course-name-by-course-id course-id)))
      {:does-not-exists? true}

      (not-enrolled? course-id user)
      {:not-enrolled? true :course-id course-id}

      :else
      (course.subject/get-subject user (course.subject/get-pre-subject-id release-t subject-id) :no-pre-left?))))


(defn eval-code
  [ctx]
  (let [user (:user ctx)
        _ (log/info (str "Eval requested from: " (:user/username user)))
        d (resource.util/convert-data-map (:request-data ctx))
        subject-id (:subject-id d)
        course-id (course.dao/find-course-id-by-subject-id-non-active-also subject-id)
        release-t (course.common/get-release (:db/id user) course-id)
        subject-and-childs (subject.dao/collect-subject-and-childs release-t subject-id)
        subject (course.eval/create-subject-structure subject-and-childs)
        result (course.eval/get-result user subject d)]
    {:result result}))


(defn eval-repl-code
  [ctx]
  (let [username (-> ctx :user :user/username)
        _ (log/info (str "Repl Eval requested from: " username))
        d (resource.util/convert-data-map (:request-data ctx))]
    {:result (course.eval/get-repl-result username (:client-code d))}))


(defn return-execution-result
  [c]
  (let [result (:result c)]
    (update-in result [:code-body :result] (constantly (-> result :code-body :result str)))))


(defn- print-result
  [r]
  (cond
    (nil? r)
    "=> nil"

    (lazy? r)
    (str "=> " (pr-str r))

    :else
    (str "=> " r)))

(defn- print-new-line-if-needed
  [x]
  (if (str/ends-with? x "\n")
    x
    (str x "\n")))

(defn return-repl-execution-result
  [c]
  (let [result (:result c)]
    (if (:error result)
      result
      {:out-str (apply str
                       (reduce
                         (fn [v r]
                           (if (str/blank? (:str r))
                             (conj v (str (print-result (:result r)) "\n\n"))
                             (conj v (str (print-new-line-if-needed (:str r)) (print-result (:result r)) "\n\n"))))
                         [] (:results result)))
       :err-str (:err-str result)})))


(defn start-course
  [ctx course-id]
  (if (or (not (course.dao/find-course-name-by-course-id course-id))
          (not (course.dao/find-course-released?-by-course-id course-id)))
    (util/runtime-ex "Course does not exist or has not released yet.")
    (let [user-id (-> ctx :user :db/id)
          username (user.dao/find-username-by-user-id user-id)]
      (course.dao/enroll-user-to-course course-id user-id)
      (log/info (str username " started course id: " course-id)))))


(defn course-syllabus
  [ctx course-id]
  (view.learn/syllabus (assoc (course.learn/get-user-progress-map-for-syllabus ctx course-id) :course-id course-id)))


(defn course-overview
  [ctx course-id]
  (view.learn/overview (assoc (course.learn/get-user-progress-map-for-overview ctx course-id) :course-id course-id)))


(defn- get-latest-updated-course-id
  [user-id enrolled-course-ids]
  (->> enrolled-course-ids
       (progress.dao/find-progresses-last-updates-by-user-id-and-course-ids user-id)
       (progress.dao/find-progress-id-by-user-id-and-last-updated user-id)
       course.dao/find-course-id-by-progress-id))


(defn learn-dispatcher
  [ctx]
  (if-let [user (-> ctx resource.util/authorized? :user)]
    (if-let [enrolled-course-ids (seq (map :db/id (:user/courses user)))]
      (if-not (progress.dao/find-any-progress-id-by-user-id (:db/id user))
        [true (str "/courses/" (last enrolled-course-ids))]
        [true (str "/courses/" (get-latest-updated-course-id (:db/id user) enrolled-course-ids))])
      [true (str "/courses/" (-> (course.dao/find-all-released-course-ids) shuffle first))])
    [true "/"]))


(defn course-dispatcher
  [ctx course-id]
  (if-let* [user-id (-> ctx resource.util/authorized? :user :db/id)
            _ (or (course.dao/enrolled? course-id user-id) (course.dao/owner? user-id course-id))]
           [true (str "/courses/" course-id "/learn/syllabus")]
           [true (str "/courses/" course-id "/learn/overview")]))


(defn- sort-by-user-counts-desc
  [courses]
  (sort-by #(nth % 3) #(compare %2 %1) courses))


(defn get-all-courses-for-user
  [user-id]
  (let [courses (sort-by-user-counts-desc (course.dao/find-all-released-courses))
        courses-with-percentage (course.learn/get-enrolled-courses user-id)]
    (reduce (fn [v course]
              (if-let [percentage (some #(when (= (:course-id %) (first course)) (:course-percentage %)) courses-with-percentage)]
                (conj v (conj course percentage))
                (conj v course))) [] courses)))


(defn courses
  [ctx]
  (if-let [user (-> ctx resource.util/authorized? :user)]
    (view.courses/all-courses true (get-all-courses-for-user (:db/id user)))
    (view.courses/all-courses false (sort-by-user-counts-desc (course.dao/find-all-released-courses)))))


(defn- get-file
  [ctx]
  (if-let [file (-> ctx :request :params (get "file") :tempfile)]
    file
    (util/runtime-ex "Apparently you could not upload file properly.Try again please.")))


(defn- validate-form
  [content]
  (try
    (read-string content)
    (catch Exception e
      (util/runtime-ex "The file format is not proper lisp format!"))))


(defn- validate-course
  [course]
  (dsl.validator/validate course)
  course)


(defn- validate-course-map-and-get-helper-fns
  [[course & helper-fns]]
  {:course (validate-course course) :helper-fns helper-fns})


(defn- validate-content
  [content]
  (if (str/blank? content)
    (util/runtime-ex "Apparently you uploaded empty file!")
    (-> content validate-form validate-course-map-and-get-helper-fns)))


(defn- persist-course-and-helper-fns
  [username-or-email course helper-fns]
  (try
    (let [user (user.dao/find-user-by-username-or-email username-or-email)
          owner (:user/username user)
          course-id (db/persist-course course owner)]
      (db/transact {:db/id                course-id
                    :course/helper-fns    (if (seq helper-fns)
                                            (str helper-fns)
                                            "()")
                    :course/users         (:db/id user)
                    :course/owner         owner
                    :course/latest-commit (System/currentTimeMillis)})
      (db/transact {:db/id        (:db/id user)
                    :user/courses course-id}))
    (catch Throwable t
      (log/error "Course could not get persisted!" t)
      (throw t))))


(defn- check-email-activation
  [username-or-email]
  (check (:user/email-activated? (user.dao/find-user-by-username-or-email username-or-email))
         "You need to activate your account.Go to your profile and send an activation mail."))


(defn- check-auth
  [ctx]
  (let [headers (-> ctx :request :headers)
        username-or-email (get headers "username-or-email")
        password (get headers "password")]
    (when (or (str/blank? username-or-email)
              (str/blank? password))
      (util/runtime-ex "Username/e-mail and password don't match."))
    (controller.auth/check-credentials {:username-or-email username-or-email
                                        :password          password})
    (check-email-activation username-or-email)
    username-or-email))


(defn upload-course
  [ctx]
  (let [username-or-email (check-auth ctx)
        file (get-file ctx)]
    (log/info (str "User: " username-or-email " uploaded course."))
    (with-open [rdr (io/reader file)]
      (let [content (->> rdr line-seq (str/join "\n"))
            {:keys [course helper-fns]} (validate-content content)]
        (persist-course-and-helper-fns username-or-email course helper-fns)))))