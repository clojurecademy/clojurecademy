(ns clojurecademy.view.activated
  (:require [clojurecademy.view.common :as view.common]
            [clojurecademy.util.resource :as resource.util]))


(defn html
  [ctx]
  (view.common/full-html
    "Account Email Activation"
    (when (resource.util/authorized? ctx)
      (view.common/header :logged-in))
    [:main.passwords.new
     [:article.fit-full
      [:div.fit-fixed.grid-row
       [:div.grid-col-12.grid-col--center.grid-col--align-center
        [:div.grid-row
         [:div.grid-col-12.grid-col [:h2 "Your account has been activated successfully!"]]]]]]]))