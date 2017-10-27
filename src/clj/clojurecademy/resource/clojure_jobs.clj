(ns clojurecademy.resource.clojure-jobs
  (:require [clojurecademy.util.resource :as resource.util :refer [resource]]
            [clojurecademy.view.clojure-jobs :as view.clojure-jobs]
            [clojurecademy.util.config :as conf]
            [clojurecademy.repl.util :as util]))


(resource plans
          :get ["/clojure-job-plans"]
          :content-type :html
          :handle-ok #(view.clojure-jobs/plans (-> % resource.util/authorized? :user boolean)))


(resource jobs
          :get ["/clojure-jobs"]
          :content-type :html
          :handle-ok #(view.clojure-jobs/jobs (-> % resource.util/authorized? :user boolean)))


(resource job-page
          :get ["/clojure-jobs/:job-title" [job-title]]
          :content-type :html
          :redirect! [(->> (conf/get-clojure-jobs!)
                           (filter :active?)
                           (some #(= job-title (:endpoint %)))
                           not) "/404"]
          :handle-ok #(view.clojure-jobs/job-page (-> % resource.util/authorized? :user boolean)
                                                  (->> (conf/get-clojure-jobs!)
                                                       (filter :active?)
                                                       (some (fn [x] (when (= job-title (:endpoint x)) x))))))