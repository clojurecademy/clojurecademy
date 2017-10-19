(ns clojurecademy.view.course
  (:require [hiccup.page :refer [include-js include-css html5]]
            [clojurecademy.util.resource :as resource.util]))

(defn- head
  []
  [:head
   [:meta
    {:content "text/html; charset=UTF-8", :http-equiv "Content-Type"}]
   [:meta {:content "ie=edge", :http-equiv "x-ua-compatible"}]
   [:title "Learn Clojure | Clojurecademy"]
   [:link
    {:type "image/x-icon",
     :rel  "shortcut icon",
     :href "/img/fav.ico"}]

   (include-css "/css/course/core.css"
                "/css/course/exercise.css"
                "/css/course/platform.css"
                "/css/course/vendor.css"
                "/css/course/codemirror.css"
                "/css/course/codemirror-default.css"
                "/css/course/icon-style.css")
   [:script (resource.util/create-google-analytics-code-spa)]])

(defn header []
  [:header._2m_hDXMtwvETHFo3eGaTXE
   {:data-react-header "true"}
   [:nav.y7tvmmg7fKu4JpdaYECtN._2Tp4ILE5wDYNdLO-nT8psj

    [:section._2zSA2d9oAw82vmGaiCI6HM.vE2U5Z1_1p0OevhTkRrpb

     [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S

      [:a._1xOZs_2pgZHAoG4MALaCgS
       {:href "https://www.Clojurecademy.com/"}
       [:img._39eW0wHuW9g7pG5q6TF7cE
        {:src "/img/logo.svg",
         :alt "Clojurecademy logo"}]]]]
    [:section#course-header._2zSA2d9oAw82vmGaiCI6HM.AcoUR5q2eKWoCC4M_4RZD
     [:a._10tr0UBdh7IJTF1w-ahcOa._2Ik8dH0qI7ki4IMCQbG6pZ
      [:i._2s1fAUUBvIvC87s1stozEY.fcn-icon.fcn-icon-arrowPrev]]]
    [:section._2zSA2d9oAw82vmGaiCI6HM._1KKDVD6unnpNtKqV9PPUQP
     [:a {:href "/account"}
      [:div._3bI54mnYg5essxyEwnBsko._1f1KmfrKUtLxZllTyx0TrC._3nDVtg6dS-0_R6p0nDL-1S
       [:div._3wrXGt5j2uE4fjlPLrDXEA
        [:div#dropdown-toggle._3beJdIRG9xxfeiRwaBD9Cp
         [:div.Y8dknuoW8NLiudcM-zQib._1pl0auxsnexFW5uqwCQW_x._2tbl3cOSPjsfIWBMlHZgcG
          [:img.wXEAS7H2e7UsikVkXNety {:src "/img/menu.svg"}]]]]]]]]])

(defn learn-section
  []
  [:div.accordion-section
   [:div.accordion-section-heading
    {:data-index "0"}
    [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.learn-header
     [:i._3QwoyZ-5nUFRrbC-LsvaCS._1Yp0Evc34IpeX4cliaOuxn._-28pXLJ13HOUfP_2pXrEi
      {:name "learn"}]
     "Learn"
     [:span#learn-wide-size-button.fcn-tabs__tab.fcn-tabs__tab--align-right.fcn-button.fcn-button--tab.fcn-button--tab--inactive.fcn-control-learn.icon-enlarge2]]]
   [:div.accordion-section-body
    [:div.jfdpfOzslotw2euWgxJNN._1PzvoBkjTI2SFvb5o9YAHW]]])


(defn left-page
  []
  [:div#discovery-resize-03.fcn-slot.fcn-slot--no-top-neighbor.fcn-slot--no-bottom-neighbor.fcn-slot--no-left-neighbor
   {:style                "position: absolute; top: 0%; right: 66.6667%; bottom: 0%; left: 0%;",
    :data-fcn-arrangement "03"}
   [:div.fcn-component
    [:div.overlay]
    [:div
     [:div.accordion-container
      [:div#accordion-container
       (learn-section)]]]]])


(defn middle-page []
  [:div#discovery-resize-14.fcn-slot.fcn-slot--no-top-neighbor.fcn-slot--no-bottom-neighbor
   {:style                "position: absolute; top: -1%; right: 0%; bottom: 49%; left: 33.3333%;",
    :data-fcn-arrangement "14"}
   [:div.fcn-component
    [:div.overlay]
    [:div._3xwXqfnfophSAz4k43MZx0
     [:div]
     [:div.UOyJ1QHGjs_CaX0WcMfJz
      [:div.editor._1fKr1-XLqTz9M9XDJjrWvo._2JXE1l2L0dOZccw6BPjV5n
       [:div.editor-container
        ;{:style "height:auto"}
        [:div#codemirror-container._34io1W8CNlzGxPabtqLq9q._3b_341TEqA36zu-puR7tRZ
         {:style "overflow-y:scroll"}]]
       [:div.editor-footer-container
        [:div.editor-footer
         [:div.fcn-tabs-container
          [:div.editor-footer__buttons
           [:div._2rMsW52-vRt_jDn9YIoIoj
            [:span._2ieC2TsGV0VNSmeBczgbwo "⌥(Alt) + Enter"]
            [:div.bCD0tEslEFIV_i4IX_5ip]
            [:button#run-button-container._2fDy3KzGIsY8FHMg74ib-V.lx0K4MugD9fFT3l5pAqK1
             {:style "min-width: 90px;" :data-btn "true"}
             [:div#run-button
              "Run"]]]
           [:div._2rMsW52-vRt_jDn9YIoIoj
            [:span._2ieC2TsGV0VNSmeBczgbwo "⌘(Ctrl) + Enter"]
            [:div.bCD0tEslEFIV_i4IX_5ip]
            [:button#repl-button-container._2fDy3KzGIsY8FHMg74ib-V.lx0K4MugD9fFT3l5pAqK122
             {:style "min-width: 90px" :data-btn "true"}
             [:div#repl-button
              "REPL"]]]
           [:div._2rMsW52-vRt_jDn9YIoIoj
            [:span._2ieC2TsGV0VNSmeBczgbwo "Wide Size Screen"]
            [:div.bCD0tEslEFIV_i4IX_5ip]
            [:span#wide-size-button.fcn-tabs__tab.fcn-tabs__tab--align-right.fcn-button.fcn-button--tab.fcn-button--tab--inactive.fcn-control.icon-enlarge2
             {:style    "max-width: 200px;"
              :overflow "false"}]]
           [:div._2rMsW52-vRt_jDn9YIoIoj
            [:span._2ieC2TsGV0VNSmeBczgbwo "Full Size Screen"]
            [:div.bCD0tEslEFIV_i4IX_5ip]
            [:span#full-size-button.fcn-tabs__tab.fcn-tabs__tab--align-right.fcn-button.fcn-button--tab.fcn-button--tab--inactive.fcn-control.icon-enlarge
             {:style    "max-width: 200px;"
              :overflow "false"}]]
           [:div._2rMsW52-vRt_jDn9YIoIoj
            [:span._2ieC2TsGV0VNSmeBczgbwo "Console"]
            [:div.bCD0tEslEFIV_i4IX_5ip]
            [:span#terminal-button.fcn-tabs__tab.fcn-tabs__tab--align-right.fcn-button.fcn-button--tab.fcn-button--tab--inactive.fcn-control.icon-terminal
             {:style    "max-width: 200px;"
              :overflow "false"}]]
           [:div._2rMsW52-vRt_jDn9YIoIoj
            [:span._2ieC2TsGV0VNSmeBczgbwo "Increase Font Size"]
            [:div.bCD0tEslEFIV_i4IX_5ip]
            [:span#inc-font-size-button.fcn-tabs__tab.fcn-tabs__tab--align-right.fcn-button.fcn-button--tab.fcn-button--tab--inactive.fcn-control.icon-plus
             {:style    "max-width: 200px;"
              :overflow "false"}]]
           [:div._2rMsW52-vRt_jDn9YIoIoj
            [:span._2ieC2TsGV0VNSmeBczgbwo "Decrease Font Size"]
            [:div.bCD0tEslEFIV_i4IX_5ip]
            [:span#dec-font-size-button.fcn-tabs__tab.fcn-tabs__tab--align-right.fcn-button.fcn-button--tab.fcn-button--tab--inactive.fcn-control.icon-minus
             {:style    "max-width: 200px;"
              :overflow "false"}]]]]]]]]]]])

(defn right-page []
  [:div#discovery-resize-25.fcn-slot.fcn-slot--no-top-neighbor.fcn-slot--no-right-neighbor.fcn-slot--no-bottom-neighbor
   {:style                "position: absolute; top: 51.5%; right: 0.25%; bottom: 0%; left: 33.3333%;",
    :data-fcn-arrangement "25"}
   [:div.fcn-component
    [:div.overlay]
    [:div.fill-container
     [:div.fcn-terminal-container
      [:div.fcn-terminal.fcn-terminal--active.jqconsole
       [:pre.jqconsole
        {:style
         "position: absolute; top: 0px; bottom: 0px; right: 0px; left: 0px; margin: 0px; overflow: auto;"}
        [:span.jqconsole-header [:span]]
        [:span#console-stderr.jqconsole-stderr
         {:style "font-size:90%"}]
        [:span.jqconsole-stdout
         {:style "font-size:90%"}
         [:div
          [:span#console-stdout]]]
        [:span.jqconsole-input
         [:span]
         [:span
          [:span]
          [:span]
          [:span.jqconsole-cursor
           {:style
            "color: transparent; display: inline; z-index: 0; position: absolute;"}
           " "]
          [:span {:style "position: relative;"}]]
         [:span]]]
       [:div
        {:style
         "position: relative; width: 1px; height: 0px; top: -999973px; overflow: hidden; left: 10px;"}
        [:textarea
         {:style          "position: absolute; width: 2px;",
          :autocomplete   "off",
          :spellcheck     "false",
          :autocorrect    "off",
          :autocapitalize "off",
          :wrap           "off"}]]]]]]])

(defn bottom-navigation []
  [:nav.y7tvmmg7fKu4JpdaYECtN.uLjMYUmq7NHQCvtXjbgKs._3bR5y3xpD2FPv1oUg-7n_p
   [:section._2zSA2d9oAw82vmGaiCI6HM.vE2U5Z1_1p0OevhTkRrpb
    [:div._3bI54mnYg5essxyEwnBsko
     [:a.sQnqIKjyMIkRzU8m4doIc
      [:span#subject-title._3oO95TdRvdS0utiixuV0t3
       "1. What's your name?"]]]]
   [:section._2zSA2d9oAw82vmGaiCI6HM.AcoUR5q2eKWoCC4M_4RZD
    [:div.fcn-nav-wrapper
     [:div.fcn-nav-wrapper__center.fcn-progress-container
      [:button#back-subject-button.fcn-button.fcn-button--everyday.fcn-button--active "Back"]
      [:div#subject-progress.fnc-nav__exercise-count]
      [:button#next-subject-button.fcn-button.fcn-button--everyday.fcn-button--inactive "Next"]]]]
   [:section._2zSA2d9oAw82vmGaiCI6HM._1KKDVD6unnpNtKqV9PPUQP]])

(defn- body
  []
  [:body
   {:browser "mac"}
   [:div.react-root
    {:data-react-class "PlatformRouter"}
    [:section._255U1OEW5iAFH7qz_pHXM8
     [:div#workspace.francine
      (header)
      [:div.discovery-cue.discovery-cue--hidden
       {:style "left:NaN%;top:NaN%;"}
       [:div.discovery-cue--content-container
        [:div.discovery-cue__arrow]
        [:p.discovery-cue--content
         "Drag the edges to resize the window."]]
       [:div.pulse]]
      [:div.fcn-grid
       {:style "cursor: auto;"}
       (left-page)
       (middle-page)
       (right-page)]
      (bottom-navigation)]]]
   [:div#loading-page-container
    [:div._1ExHXGlOxKpCylTzG5QMT6._1huMPl6-RtzoV17I0HmoRf._3dkNckTVR4VPImWXsThMLq._1SsvwN3XXhA0W2Jo_T7CsX
     [:div.sSQDykDehz7XXlU3XotWJ
      [:span]
      [:div.cssload-loader
       {:style "width:75px;height:75px"}
       [:div.cssload-inner.cssload-one
        {:style "border-bottom:1px solid #34b3a0"}]
       [:div.cssload-inner.cssload-two
        {:style "border-right:1px solid #34b3a0"}]
       [:div.cssload-inner.cssload-three
        {:style "border-top:1px solid #34b3a0"}]]]]]
   (include-js "/course/js/course.js")])

(defn html
  [_]
  (hiccup.page/html5
    [:html
     {:lang "en"}
     (head)
     (body)]))