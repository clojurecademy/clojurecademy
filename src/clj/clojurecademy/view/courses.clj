(ns clojurecademy.view.courses
  (:require [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :as hiccup]
            [clojurecademy.view.common :as view.common]))


(defn- truncate
  ([string length]
   (truncate string length "..."))
  ([string length suffix]
   (let [string-len (count string)
         suffix-len (count suffix)]
     (if (<= string-len length)
       string
       (str (subs string 0 (- length suffix-len)) suffix)))))


(defn all-courses
  [auth? courses]
  (view.common/full-html
    "All Courses"
    (view.common/header (if auth? :course :non-logged-in-wide))
    [:div
     [:div._17S6vVdEJfvMGdpWLenGHU
      [:span {:style "position: relative;"}]
      [:div._1YGSFNfaFzXho9TQuUZGUL
       {:style "opacity: 1;"}
       [:div.P21R5CHnYJ9fh2LiHt7nK
        [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3r4ras_0_D__hjVccXuL5e
         [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b
          [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp.ClDPHTSyy3N_oC37Gyuqc
           [:button._1le84AU2Tvn8hDQEBK-7LY._3-_43iWhlATOxVv877QCRO._10YJ6SjyJ_T0M-Maev-gzZ
            "All Courses"]]]
         ;;TODO will be added in near future...
         ;[:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1DcgywFUaUN7eEGF3wjjSE
         ; [:div._2vDJWSmPtCxkJ_VdlhEg_g "Sort by"]
         ; [:div._2zxbm5tqK_xhoqNdZGLJMJ
         ;  [:select._1R8Nr-VRr4m5wtq0VIYm-L
         ;   {:required ""}
         ;   [:option {:value "default"} "Default"]
         ;   [:option {:value "popular"} "Most Popular"]
         ;   [:option {:value "newest"} "Release Date"]]
         ;  [:i._3QwoyZ-5nUFRrbC-LsvaCS._1BPz1pLsBqpj9dBgZcf2kG.w_HIDrfuHt1ORWgaGtCdx
         ;   {:name "arrow-thin-down"}]]]
         ]]
       [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp.tCwPXBJl48Dnhq_s8dJB8._3jMSPXhXmV4dFYeHfDYVQL._3xv0fx4GG3nDJP8Eg7f1QE
        (for [[course-id title short-desc users-count percentage] courses]
          [:div._9eQUodKoyDfzljPjd3uUv
           [:span
            [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
             {:href (str "/courses/" course-id)}
             [(if percentage
                :div._240ef6eUVqVu8O2wNetAWi._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
                :div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht)
              [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj.K7OhfgT3YlRfjysLFZpo6
               [:div
                view.common/clojure-logo-svg]]
              [:div._1TlackAU2XUAy3_AeDmSP0
               [:div.Y1w9vprpw1N8chvfIrbyI (truncate title 45)]
               [:div (truncate short-desc 105)]]
              (if percentage
                [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
                 [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ
                  [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG._275VAaj8c6H-5RD1EQXdcL
                   {:style  "height: 0.4rem; background-color: rgb(233, 234, 234);",
                    :height "0.4rem",
                    :value  (str percentage),
                    :fill   "#76787B"}
                   [:div._3ZhC5MXWzUal9_UxXlijtj
                    {:style (str "width: " percentage "%; " "height: 0.4rem; background-color: rgb(118, 120, 123);")}]]]
                 [:div._2KsT9libIegU2ai2MMcF6t
                  [:div (str percentage "%")]]]
                [:div._1LqddMNwj5jKijAxFVxWQ2.Ra_hsV_17bz0s0s5B29cl
                 (str users-count " Clojurists Enrolled")])]]]])

        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href "https://clojurecademy.github.io/dsl-documentation/"}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj.K7OhfgT3YlRfjysLFZpo6
             [:div view.common/plus-svg]]
            [:div._1TlackAU2XUAy3_AeDmSP0
             [:div.Y1w9vprpw1N8chvfIrbyI "How about creating a Clojure Course?"]
             [:div "You can contribute to Clojurecademy by creating courses"]]]]]]]]]]))
