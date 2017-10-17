(ns clojurecademy.view.learn
  (:require [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :as hiccup]
            [clojurecademy.view.common :as view.common]
            [kezban.core :refer :all]
            [clojure.string :as str])
  (:import (java.util Date)
           (java.text SimpleDateFormat)
           (com.ocpsoft.pretty.time PrettyTime)))


(defn render-courses-board
  [enrolled-courses created-courses course-id]
  (when (or (seq enrolled-courses) (seq created-courses))
    [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY._35_ApOObcdtpKSKsYkgqw
     [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY._1j41bG2W9g1_oL90-ATwiB
      [:div._2VZkqzwOzxn8s-FjGD7oYR
       [:div

        (when (seq enrolled-courses)
          [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY.undefined.vm46sjjg1qGzd1W5AlU06
           [:span]
           [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3lM-dyGHg6vUavvAsjd592
            [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO._3QuICUvwhh1fgENCdtcohH._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3S7PnFVgZNQQI_R56zc79T
             [:i._3QwoyZ-5nUFRrbC-LsvaCS._1vH4iWOWu95AyfFlNEqoyU._3xS2LmCFvyaA5cOMib6orc
              {:name "arrow-filled-down"}]]
            [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b
             "Enrolled Courses"]]
           [:span._3d27JsQrpTldEOk76fx7x0]
           [:div
            {:style "display: block;"}
            [:div.iIhDUZBYnPVP3sB3teoLs

             (for [course enrolled-courses]
               [(if (= course-id (:course-id course)) :div.x7BSCWp_DOs4TTdeZRHtE._3sELoDpvv8xP8no-ZUjxKk :div._3sELoDpvv8xP8no-ZUjxKk)
                [:span
                 [:a._22RERy4DiR1bpvW8q7DmiS
                  {:href (str "/courses/" (:course-id course))}
                  [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._1juhe3aAlSAWH4lAsSvqrg
                   [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b._2G-oFyVX_CvgQmbGMlqtVB
                    (:course-title course)]
                   [:div._3l5vJxhTbS1dLnkuNf4EaO._3QuICUvwhh1fgENCdtcohH._2HzVsADtbr_GzQ7adkKXyA (str (:course-percentage course)) "%"]]]]])]]])

        (when (seq created-courses)
          [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY.undefined.vm46sjjg1qGzd1W5AlU06
           [:span]
           [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3lM-dyGHg6vUavvAsjd592
            [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO._3QuICUvwhh1fgENCdtcohH._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3S7PnFVgZNQQI_R56zc79T
             [:i._3QwoyZ-5nUFRrbC-LsvaCS._1vH4iWOWu95AyfFlNEqoyU._3xS2LmCFvyaA5cOMib6orc
              {:name "arrow-filled-down"}]]
            [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b
             "Created Courses"]]
           [:span._3d27JsQrpTldEOk76fx7x0]
           [:div
            {:style "display: block;"}
            [:div.iIhDUZBYnPVP3sB3teoLs

             (for [course created-courses]
               [(if (= course-id (:course-id course)) :div.x7BSCWp_DOs4TTdeZRHtE._3sELoDpvv8xP8no-ZUjxKk :div._3sELoDpvv8xP8no-ZUjxKk)
                [:span
                 [:a._22RERy4DiR1bpvW8q7DmiS
                  {:href (str "/courses/" (:course-id course))}
                  [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._1juhe3aAlSAWH4lAsSvqrg
                   [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b._2G-oFyVX_CvgQmbGMlqtVB
                    (:course-title course)]]]]])]]])]]]]))


(defn- get-date-format
  [ago date]
  (if (any? #(str/ends-with? ago %) ["week ago" "weeks ago" "month ago" "months ago" "year ago" "years ago"])
    date
    ago))


(defn format-date
  [^Long l]
  (if l
    (let [pattern   "dd/MM/yyy"
          formatter (SimpleDateFormat. pattern)
          date      (.format formatter (Date. l))
          ago       (.format (PrettyTime.) (Date. l))]
      (get-date-format ago date))
    "-"))


(defn render-course-info-box
  [m course-id]
  [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._2byEnjl05l2axNIdF6blc2._33ttQNVJhbR5az_RWoA2_v._2wO0S1-Qgm1JCmGlcUOiC9.cc-at--learncta
   [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b._2OvvmRIMuApp9TWMONyVY._20UskCTMv6TVA68MUDGjHs
    [:div._1QsaezjSk_ZYMkJAL09Bqy (:title m)]
    [:div._1zyITo1jMMHimRtCv7Pn__ (str "This course created by ")
     [:strong
      [:u
       [:a {:href (str "/users/" (:owner m))} (:owner m)]]]]

    [:div._1zyITo1jMMHimRtCv7Pn__ (str "Last updated: " (format-date (:last-updated m)))]

    (when (:owner? m)
      [:div._1zyITo1jMMHimRtCv7Pn__ (str "Last commit: " (format-date (:last-commit m)))])

    [:div._2jOrDIGJeI1gWBFwofJRmI
     (cond
       (:owner? m)
       [:div
        [:a.no-underline
         {:href (str "/course#!/courses/" course-id "/resume")}
         [:button._2fDy3KzGIsY8FHMg74ib-V._2bwYMUTOYpw6i1TbVOJS4A._1W3U2VJf-f_UqBcW7TFpBs._248uK18apzCUDR-gFWvNIm
          (str "Resume (" (:course-percentage m) "%)")]]
        [:a.no-underline
         [:button#release-button._2fDy3KzGIsY8FHMg74ib-V._2bwYMUTOYpw6i1TbVOJS4A._1W3U2VJf-f_UqBcW7TFpBs._248uK18apzCUDR-gFWvNIm
          {:name course-id}
          "Release"]]]

       (:enrolled? m)
       [:div
        [:a.no-underline
         {:href (str "/course#!/courses/" course-id "/resume")}
         [:button._2fDy3KzGIsY8FHMg74ib-V._2bwYMUTOYpw6i1TbVOJS4A._1W3U2VJf-f_UqBcW7TFpBs._248uK18apzCUDR-gFWvNIm
          (str "Resume (" (:course-percentage m) "%)")]]]

       (:no-auth? m)
       [:div
        [:a.no-underline
         {:href "/"}
         [:button._2fDy3KzGIsY8FHMg74ib-V._2bwYMUTOYpw6i1TbVOJS4A._1W3U2VJf-f_UqBcW7TFpBs._248uK18apzCUDR-gFWvNIm
          "Sign Up"]]]

       :else
       [:div
        [:a.no-underline
         {:href (str "/course#!/courses/" course-id "/start")}
         [:button._2fDy3KzGIsY8FHMg74ib-V._2bwYMUTOYpw6i1TbVOJS4A._1W3U2VJf-f_UqBcW7TFpBs._248uK18apzCUDR-gFWvNIm
          "Start"]]])]]])


(defn render-chapters-and-sub-chapters
  [auth? skip? chapter-and-sub-chapters-percentage-maps sub-chapter-resume-id]
  [:div
   [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY
    (let [counter (atom 0)]
      (for [chapter chapter-and-sub-chapters-percentage-maps]
        [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY.EMVMunLT_0DPg2OiRxhX7._2j-HYKPTGvHJd1brao4luQ
         [:span]
         [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1Hkoq21Bw7WTlcRIrFQmgo
          {:style "cursor:auto"}
          [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO._3QuICUvwhh1fgENCdtcohH._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj
           (swap! counter inc)]
          [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b._2OvvmRIMuApp9TWMONyVY.GEk9Sbt3OmLggMB2aQtSj.nRncy5fYROWNVerSJ_FcD
           [:div._3B0-TNViRhD8YA6wWWO0K1
            (:title chapter)]]
          [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3UkifyLsW4JZ3h72-kkxhP
           (when auth?
             [:div._3l5vJxhTbS1dLnkuNf4EaO._1UZ4qzPdfuPSs5JS0KCKIS._3QuICUvwhh1fgENCdtcohH._3egFldcXB3b_YZ0ifrNFen
              [:div._37X57C3qguWEHca8BV2kwJ
               (str (:chapter-percentage chapter) "%")]])]]
         [:span]
         [:div
          {:style "display: block;"}
          [:span]
          [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY._2Y7Y2TmcTZauwOiEXrHGls
           {:style "opacity: 1; transform: translateY(0px);"}
           [:div

            (for [sub-chapter (:sub-chapters chapter)]
              [:div
               [:a.no-underline._38Sx2k6p3Ok4Haalb6QO8t
                {:href (str "/course#!/sub-chapters/" (:id sub-chapter) "/resume")}
                [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj.l6JDn1Ryl3BS7QUat5MIl
                 {:title (:title sub-chapter)}

                 [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO._1UZ4qzPdfuPSs5JS0KCKIS._3QuICUvwhh1fgENCdtcohH._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj.XP7f9-4BHEDYcEzh9pdHX
                  (when (and (not skip?) (= (:id sub-chapter) sub-chapter-resume-id))
                    [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1gpYBJPYnKDJvohBOOlY2x
                     {:title "Up next"}
                     [:i._3QwoyZ-5nUFRrbC-LsvaCS._2nHXVSz3Al4OkZ10RCTkwO.aAGd_Qn9ZEEMhRRglcwci
                      {:name "arrow-thin-right"}]])]

                 [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO._1UZ4qzPdfuPSs5JS0KCKIS._3QuICUvwhh1fgENCdtcohH._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1KMWQFL4uGSedq45DBLDbA
                  [:div._3iEc0jhM5TYUGgM1ex9tMy.bco-950mfKJvvBbiTmNW4]

                  (cond
                    (:locked? sub-chapter)
                    [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1dhlSBWyItcFC4ZC8h21_-._2diD0A92Rms7qy3rAnMT0p
                     [:i._3QwoyZ-5nUFRrbC-LsvaCS._1hUjUwHHzwvl5ohLjAD2ho
                      {:name "lock"}]]

                    (not= (:percentage sub-chapter) 100)
                    [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1dhlSBWyItcFC4ZC8h21_-
                     [:i._3QwoyZ-5nUFRrbC-LsvaCS.e9cLhm1YOpw_0I1l9soC0
                      {:name "editor"}]]

                    (= (:percentage sub-chapter) 100)
                    [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1dhlSBWyItcFC4ZC8h21_-._1ipUJK2aad2aTwZovQFiSI
                     [:i._3QwoyZ-5nUFRrbC-LsvaCS._3NOi3m1n7Xu3hEwGNT1JsC
                      {:name "check-thin"}]])]

                 [:div._3l5vJxhTbS1dLnkuNf4EaO._1UZ4qzPdfuPSs5JS0KCKIS._2tEfM0IkMaX9GwzuRYnX2b._1IZuIq_3UdGonCYbFCY_3Z
                  (:title sub-chapter)]
                 [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._3QuICUvwhh1fgENCdtcohH._17AXeiZMJImGcBhoIQtxmb]
                 (when auth?
                   [:div._3l5vJxhTbS1dLnkuNf4EaO._1UZ4qzPdfuPSs5JS0KCKIS._3QuICUvwhh1fgENCdtcohH._3egFldcXB3b_YZ0ifrNFen
                    [:div._37X57C3qguWEHca8BV2kwJ
                     (str (:percentage sub-chapter) "%")]])]]])]]]]))]])


(defn syllabus
  [syllabus-data]
  (let [course-id (:course-id syllabus-data)]
    (view.common/full-html
      (str (:title syllabus-data) " - Syllabus")
      (view.common/header (if (:no-auth? syllabus-data) :non-logged-in-wide :learn))
      [:div
       [:div
        [:div._3BSdp5s1odnueQ5421rOLN
         [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._15yNfRp6FfW9IAh1lmrOud
          (render-courses-board (:enrolled-courses syllabus-data) (:created-courses syllabus-data) course-id)
          [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b._2OvvmRIMuApp9TWMONyVY._1JPXAP_sAyk1Fo8TBj0wP-
           [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b._2OvvmRIMuApp9TWMONyVY
            [:div._3r-UqOa1LGGlBr6P5YGtnu
             (render-course-info-box syllabus-data course-id)
             [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY._2_7rwTyT98MQvcOgBadUZy._33ttQNVJhbR5az_RWoA2_v
              [:div._1XRkeRcO7hh1axYhmKiQMM._2WxAtNj-ZeaCg0vVZkoQqC
               [:a._22RERy4DiR1bpvW8q7DmiS
                {:href (str "/courses/" course-id "/learn/overview")}
                [:button._1le84AU2Tvn8hDQEBK-7LY._1ThqMf5gF5SJLRJOcmn7sm
                 "Overview"]]
               [:a._22RERy4DiR1bpvW8q7DmiS
                {:href (str "/courses/" course-id "/learn/syllabus")}
                [:button._1le84AU2Tvn8hDQEBK-7LY._3-_43iWhlATOxVv877QCRO._1ThqMf5gF5SJLRJOcmn7sm
                 "Syllabus"]]]
              (render-chapters-and-sub-chapters (not (:no-auth? syllabus-data))
                                                (:skip? syllabus-data)
                                                (:chapter-and-sub-chapters-maps syllabus-data)
                                                (:sub-chapter-resume-id syllabus-data))]]]]]
         [:div._3Fd8W0iX0ntfvE_CEXJkTZ]]]
       [:div#msg-container]])))


(defn overview
  [overview-data]
  (let [course-id (:course-id overview-data)]
    (view.common/full-html
      (str (:title overview-data) " - Overview")
      (view.common/header (if (:no-auth? overview-data) :non-logged-in-wide :learn))
      [:div
       [:div
        [:div._3BSdp5s1odnueQ5421rOLN
         [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._15yNfRp6FfW9IAh1lmrOud
          (render-courses-board (:enrolled-courses overview-data) (:created-courses overview-data) course-id)
          [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b._2OvvmRIMuApp9TWMONyVY._1JPXAP_sAyk1Fo8TBj0wP-
           [:div._1XRkeRcO7hh1axYhmKiQMM._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b._2OvvmRIMuApp9TWMONyVY
            [:div._3r-UqOa1LGGlBr6P5YGtnu
             (render-course-info-box overview-data course-id)
             [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY._2_7rwTyT98MQvcOgBadUZy._33ttQNVJhbR5az_RWoA2_v
              [:div._1XRkeRcO7hh1axYhmKiQMM._2WxAtNj-ZeaCg0vVZkoQqC
               [:a._22RERy4DiR1bpvW8q7DmiS
                {:href (str "/courses/" course-id "/learn/overview")}
                [:button._1le84AU2Tvn8hDQEBK-7LY._1ThqMf5gF5SJLRJOcmn7sm._3-_43iWhlATOxVv877QCRO
                 "Overview"]]
               [:a._22RERy4DiR1bpvW8q7DmiS
                {:href (str "/courses/" course-id "/learn/syllabus")}
                [:button._1le84AU2Tvn8hDQEBK-7LY._1ThqMf5gF5SJLRJOcmn7sm
                 "Syllabus"]]]
              [:div
               [:div._1XRkeRcO7hh1axYhmKiQMM._2OvvmRIMuApp9TWMONyVY._39K0C3ph1hpmLw6tsBfNnG
                [:div._17BgxvIc-BHyTKH0aINNIt._1x0W6KHoREjDnuWK-RgOvS._1KkRXbiANYOGRsR-qjedaG._33ttQNVJhbR5az_RWoA2_v._3g9BbXCwOg0Jf9L1Z-Evv5
                 [:h3 "Course Description"]
                 [:p (:short-description overview-data)]
                 [:h3 "About this course"]
                 [:p (:long-description overview-data)]
                 [:h3 "Who is this course for?"]
                 [:p (:who-is-this-course-for overview-data)]]]]]]]]]]]
       [:div#msg-container]])))
