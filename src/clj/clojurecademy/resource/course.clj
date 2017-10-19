(ns clojurecademy.resource.course
  (:require [clojurecademy.util.resource :as resource.util :refer [resource]]
            [clojurecademy.view.course :as view.course]
            [clojurecademy.dao.course :as course.dao]
            [clojurecademy.controller.course.core :as course.controller]
            [compojure.coercions :as coercion]))


(resource courses
          :get ["/courses"]
          :content-type :html
          :handle-ok course.controller/courses)


(resource course-dispatcher
          :get ["/courses/:course-id{[0-9]+}" [course-id :<< coercion/as-int]]
          :content-type :html
          :redirect! #(course.controller/course-dispatcher % course-id))


(resource course-page
          :get ["/course"]
          :content-type :html
          :redirect-not-auth "/"
          :handle-ok view.course/html)


(resource start-course
          :put ["/courses/:course-id{[0-9]+}/start" [course-id :<< coercion/as-int]]
          :content-type :json
          :auth-required? true
          :put! #(course.controller/start-course % course-id))


(resource resume-course
          :get ["/courses/:course-id{[0-9]+}/resume" [course-id :<< coercion/as-int]]
          :content-type :json
          :auth-required? true
          :handle-ok #(course.controller/resume-course % course-id))


(resource release-course
          :put ["/courses/:course-id{[0-9]+}/release" [course-id :<< coercion/as-int]]
          :content-type :json
          :auth-required? true
          :handle-ok #(course.controller/release-course % course-id))


(resource upload-course
          :post ["/course/upload"]
          :content-type :multipart
          :post! course.controller/upload-course
          ;;TODO will look into that -> resource.util scope does not work
          :handle-exception #(.getMessage (:exception %)))


(resource resume-sub-chapter
          :get ["/sub-chapters/:sub-chapter-id{[0-9]+}/resume" [sub-chapter-id :<< coercion/as-int]]
          :content-type :json
          :auth-required? true
          :handle-ok #(course.controller/resume-sub-chapter % sub-chapter-id))


(resource subject
          :get ["/subjects/:subject-id{[0-9]+}" [subject-id :<< coercion/as-int]]
          :content-type :json
          :auth-required? true
          :handle-ok #(course.controller/subject % subject-id))


(resource next-subject
          :get ["/subjects/:subject-id{[0-9]+}/next" [subject-id :<< coercion/as-int]]
          :content-type :json
          :auth-required? true
          :handle-ok #(course.controller/next-subject % subject-id))


(resource pre-subject
          :get ["/subjects/:subject-id{[0-9]+}/pre" [subject-id :<< coercion/as-int]]
          :content-type :json
          :auth-required? true
          :handle-ok #(course.controller/pre-subject % subject-id))


(resource eval-code
          :put ["/eval"]
          :content-type :json
          :auth-required? true
          :put! course.controller/eval-code
          :handle-ok course.controller/return-execution-result)


(resource eval-repl-code
          :put ["/eval-repl"]
          :content-type :json
          :auth-required? true
          :put! course.controller/eval-repl-code
          :handle-ok course.controller/return-repl-execution-result)


(resource course-syllabus
          :get ["/courses/:course-id{[0-9]+}/learn/syllabus" [course-id :<< coercion/as-int]]
          :content-type :html
          :redirect! [(not (course.dao/find-course-name-by-course-id course-id)) "/404"]
          :handle-ok #(course.controller/course-syllabus % course-id))


(resource course-overview
          :get ["/courses/:course-id{[0-9]+}/learn/overview" [course-id :<< coercion/as-int]]
          :content-type :html
          :redirect! [(not (course.dao/find-course-name-by-course-id course-id)) "/404"]
          :handle-ok #(course.controller/course-overview % course-id))


(resource course-learn
          :get ["/courses/learn"]
          :content-type :html
          :redirect! course.controller/learn-dispatcher)