(ns clojurecademy.resource.clojure-jobs
  (:require [clojurecademy.util.resource :as resource.util :refer [resource]]
            [clojurecademy.view.clojure-jobs :as view.clojure-jobs]))


(resource plans
          :get ["/clojure-job-plans"]
          :content-type :html
          :handle-ok #(view.clojure-jobs/plans (-> % resource.util/authorized? :user boolean)))


(resource jobs
          :get ["/clojure-jobs"]
          :content-type :html
          :handle-ok #(view.clojure-jobs/jobs (-> % resource.util/authorized? :user boolean)))