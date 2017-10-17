(ns clojurecademy.view.user-profile
  (:require [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :as hiccup]
            [clojurecademy.view.common :as view.common]
            [clojure.string :as str]))

(def p-elem-spacing "display: inline-block;\n    text-decoration: none;\n    padding: 5px;\n    margin: 5px;")


(defn profile
  [user-data]
  (let [username (-> user-data :info :username)
        fullname (-> user-data :info :fullname)]
    (view.common/full-html (if (str/blank? fullname) username (str username " (" fullname ")"))
                           (view.common/header (if (:auth? user-data) :logged-in :non-logged-in-narrow))
                           [:main.profiles.show
                            [:article.fit-full.color-scheme--grey
                             [:article.fit-fixed
                              [:div.grid-row
                               [:div.grid-col-6.grid-col--center.grid-col--align-center.grid-col--extra-margin-top
                                [:p]
                                [:h3 (if (str/blank? fullname) username fullname)]
                                [:p]
                                (when-not (str/blank? (-> user-data :info :about))
                                  [:p (-> user-data :info :about)])
                                [:p
                                 (when-not (str/blank? (-> user-data :info :website))
                                   [:a
                                    {:href (-> user-data :info :website) :target "_blank" :style p-elem-spacing}
                                    [:span.new-cc-icon.icon-link.icon--
                                     {:style "font-size:22px"}]])
                                 (when-not (str/blank? (-> user-data :info :stackoverflow))
                                   [:a
                                    {:href (-> user-data :info :stackoverflow) :target "_blank" :style p-elem-spacing}
                                    [:span.new-cc-icon.icon-stackoverflow.icon--
                                     {:style "font-size:22px"}]])
                                 (when-not (str/blank? (-> user-data :info :github))
                                   [:a
                                    {:href (str "https://github.com/" (-> user-data :info :github)) :target "_blank" :style p-elem-spacing}
                                    [:span.new-cc-icon.icon-github.icon--
                                     {:style "font-size:22px"}]])
                                 (when-not (str/blank? (-> user-data :info :twitter))
                                   [:a
                                    {:href (str "https://twitter.com/" (-> user-data :info :twitter)) :target "_blank" :style p-elem-spacing}
                                    [:span.new-cc-icon.icon-twitter.icon--
                                     {:style "font-size:22px"}]])
                                 (when-not (str/blank? (-> user-data :info :linkedin))
                                   [:a
                                    {:href (-> user-data :info :linkedin) :target "_blank" :style p-elem-spacing}
                                    [:span.new-cc-icon.icon-linkedin.icon--
                                     {:style "font-size:22px"}]])]]]]]
                            [:article.fit-full.color-scheme--white
                             [:div.fit-fixed
                              [:div.grid-row
                               [:div.grid-col-12.grid-col--center
                                [:div.grid-row
                                 [:div.color-scheme--white.columns
                                  [:div.fit-fixed.grid-row

                                   [:article.grid-col-6.grid-col--align-center.grid-col--no-margin.column.column--link.link-area
                                    {:style "background-color:#dedede"}
                                    [:article.grid-col-12.grid-col--align-center [:h3 (count (:enrolled-courses user-data))]]
                                    [:article.grid-col-12.grid-col--align-center [:p (str "Enrolled Course"
                                                                                          (when-not (= (count (:enrolled-courses user-data)) 1)
                                                                                            "s"))]]
                                    [:a.link--target {:href "#enrolled-courses"}]]

                                   [:article.grid-col-6.grid-col--align-center.grid-col--no-margin.column.column--link.link-area
                                    {:style "background-color:#dedede"}
                                    [:article.grid-col-12.grid-col--align-center [:h3 (count (:created-courses user-data))]]
                                    [:article.grid-col-12.grid-col--align-center [:p (str "Created Course"
                                                                                          (when-not (= (count (:created-courses user-data)) 1)
                                                                                            "s"))]]
                                    [:a.link--target {:href "#created-courses"}]]]]]]]]]
                            [:article#enrolled-courses.fit-full.color-scheme--grey.margin-top--1
                             [:div.fit-fixed
                              [:div#completed-body.grid-row
                               [:div.grid-col-12.grid-col--align-center
                                [:h3 "Enrolled Courses"]]
                               (for [{:keys [course-id course-title course-percentage]} (:enrolled-courses user-data)]
                                 [:div.grid-col-12.grid-col--no-padding
                                  {:style "box-shadow: 5px 6px 21px -2px rgba(83,108,127,0.84);"}
                                  [:div.grid-col-12.grid-row.color-scheme--white.table-row.completed
                                   [:div.grid-col-4.grid-col--no-margin
                                    [:h5.text--ellipsis course-title]]
                                   [:div.grid-col-5.table-row__progress
                                    [:div.progress
                                     [:div.progress__bar
                                      [:div.progress__bar__complete {:style (str "width:" course-percentage "%")}]]]]
                                   [:div.grid-col-3.table-row__last-active
                                    [:small.text--ellipsis (if (= 100 course-percentage)
                                                             "Completed"
                                                             (str course-percentage "% Completed"))]]
                                   [:div.table-row__right-arrow
                                    [:span.new-cc-icon.icon-rightarrow.icon--]]
                                   [:a.link--target {:href (str "/courses/" course-id)}]]])]]]
                            [:article#created-courses.fit-full.color-scheme--grey.margin-top--1
                             [:div.fit-fixed
                              [:div#completed-body.grid-row
                               [:div.grid-col-12.grid-col--align-center
                                [:h3 "Created Courses"]]
                               (for [{:keys [id title]} (:created-courses user-data)]
                                 [:div.grid-col-12.grid-col--no-padding
                                  {:style "box-shadow: 5px 6px 21px -2px rgba(83,108,127,0.84);"}
                                  [:div.grid-col-12.grid-row.color-scheme--white.table-row.completed
                                   [:div.grid-col-4.grid-col--no-margin
                                    [:h5.text--ellipsis title]]
                                   [:div.table-row__right-arrow
                                    [:span.new-cc-icon.icon-rightarrow.icon--]]
                                   [:a.link--target {:href (str "/courses/" id)}]]])]]]])))
