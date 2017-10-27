(ns clojurecademy.resource.clojure-jobs
  (:require [clojurecademy.util.resource :as resource.util :refer [resource]]
            [clojurecademy.view.clojure-jobs :as view.clojure-jobs]))


(resource jobs
          :get ["/clojure-job-plans"]
          :content-type :html
          :handle-ok #(view.clojure-jobs/html (-> % resource.util/authorized? :user boolean)))