(ns clojurecademy.controller.course
  (:require [clojurecademy.util :as util]
            [reagent.core :as reagent]
            [goog.dom :as dom]))


(defn- close
  [_]
  (reagent/render [(fn [_] [:div])] (dom/getElement "msg-container")))


(defn- refresh
  [_]
  (.reload js/location))


(defn- render-info-window
  [title text]
  (reagent/render [(fn [_]
                     [:div._1ExHXGlOxKpCylTzG5QMT6._1huMPl6-RtzoV17I0HmoRf._3dkNckTVR4VPImWXsThMLq._1SsvwN3XXhA0W2Jo_T7CsX
                      [:div.sSQDykDehz7XXlU3XotWJ
                       [:span]
                       [:div._34wkp9FRhoKrHb1WnMWiYL
                        [:div._2_bCPSnIBzBKgXvdAQqXRU
                         [:div
                          [:h2 title]
                          [:p text]]
                         [:div.e61uogkN-YwkT8xPS61b5
                          [:button#release-close._2fDy3KzGIsY8FHMg74ib-V.Q2qWh46WAtbrJjOvkx6Hq._1mQgyp76JXoCrTvLgR_p-d._141p_JCTXbXubgx5gGZfVe
                           "Close"]]]]]])] (dom/getElement "msg-container")) [:div])


(defn- release-it
  [e]
  (let [course-id (-> e .-target .-name)]
    (util/ajax :put (str "/courses/" course-id "/release")
               :success (fn [d]
                          (cond
                            (:does-not-exists? d)
                            (render-info-window "Course Does Not Exist" "The course that you are trying to release does not exist!")

                            (:not-owner? d)
                            (render-info-window "Authorization Error" "You are not the owner of course that you are trying to release!")

                            (:success d)
                            (render-info-window "Released!" "You've successfully released new version of your course!"))
                          (util/set-event-handler! "onclick" "release-close" refresh))
               :error (fn [{:keys [status response]}]
                        (render-info-window "Error!" (str "Something went wrong: " response " - Status Code: " status))
                        (util/set-event-handler! "onclick" "release-close" refresh)))))


(defn release
  [e]
  (reagent/render [(fn [_]
                     [:div
                      [:div._1ExHXGlOxKpCylTzG5QMT6._1huMPl6-RtzoV17I0HmoRf._3dkNckTVR4VPImWXsThMLq._1SsvwN3XXhA0W2Jo_T7CsX
                       [:div.sSQDykDehz7XXlU3XotWJ
                        [:span]
                        [:div._34wkp9FRhoKrHb1WnMWiYL
                         [:div._2_bCPSnIBzBKgXvdAQqXRU
                          [:div
                           [:h2 "Release Course"]
                           [:p
                            "Your latest changes will affect enrolled users to this course but it won't delete/change their codes."]
                           [:p
                            "Are you sure you want to release new version of your course?"]]
                          [:div.e61uogkN-YwkT8xPS61b5
                           [:a.no-underline._141p_JCTXbXubgx5gGZfVe
                            [:button#release-it._2fDy3KzGIsY8FHMg74ib-V.Q2qWh46WAtbrJjOvkx6Hq
                             {:name (-> e .-target .-name)}
                             "Release It!"]]
                           [:button#release-close._2fDy3KzGIsY8FHMg74ib-V.Q2qWh46WAtbrJjOvkx6Hq._1mQgyp76JXoCrTvLgR_p-d._141p_JCTXbXubgx5gGZfVe
                            "Cancel"]]]]]]])]
                  (dom/getElement "msg-container"))
  (util/set-event-handler! "onclick" "release-it" release-it)
  (util/set-event-handler! "onclick" "release-close" close))


