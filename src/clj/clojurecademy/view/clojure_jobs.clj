(ns clojurecademy.view.clojure-jobs
  (:require [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :as hiccup]
            [clojurecademy.view.common :as view.common]))

;;TODO Don't forget to change full-html head meta datas!!!!

(def packages {:starter     {:name "Starter" :link "https://pay.paddle.com/checkout/519754"}
               :standard    {:name "Standard" :link "https://pay.paddle.com/checkout/519756"}
               :advanced    {:name "Advanced" :link "https://pay.paddle.com/checkout/519757"}
               :premium     {:name "Premium" :link "https://pay.paddle.com/checkout/519758"}
               :sponsorship {:name "Sponsorship" :link "https://pay.paddle.com/checkout/519759"}})


(defn- package->mailto
  [package]
  (str "mailto:jobs@clojurecademy.com?"
       "subject=Purchase%20" (:name package) "%20Package"
       "&body=Hi%20There!%0D%0APlease%20fill%20the%20following%20information%20above%20then%20purchase%20package%20from%20"
       (:link package)
       "%0D%0A" "%0D%0A"
       "Job%20Title:"
       "%0D%0A" "%0D%0A"
       "Job%20Type(permanent,%20contract,%20unspecified):"
       "%0D%0A" "%0D%0A"
       "Compensation(Optional, $Min-$Max):"
       "%0D%0A" "%0D%0A"
       "Job%20Description:"
       "%0D%0A" "%0D%0A"
       "Job%20Location:"
       "%0D%0A" "%0D%0A"
       "Remote?:"
       "%0D%0A" "%0D%0A"
       "Application%20Instructions%20(e.g.%20Send%20resume%20to%20jobs@your-co.com):"
       "%0D%0A" "%0D%0A"
       "Company%20Name:"
       "%0D%0A" "%0D%0A"
       "Company%20Url(Optional):"
       "%0D%0A" "%0D%0A"
       "Company%20Elevator%20Pitch(Optional,%20max%2080%20characters):"
       "%0D%0A" "%0D%0A"
       "Attach%20Your%20Logo%20to%20mail(Optional)"))


(defn html
  [auth?]
  (view.common/full-html
    "Clojure Job Posting Plans"
    (view.common/header (if auth? :course :non-logged-in-wide))
    [:div
     [:div._1rAxWTzJWa6aEYjrnUdya
      [:span {:style "position: relative;"}]
      [:div._1YGSFNfaFzXho9TQuUZGUL
       {:style "opacity: 1;"}
       [:div.P21R5CHnYJ9fh2LiHt7nK
        [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3r4ras_0_D__hjVccXuL5e
         [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b
          [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp.ClDPHTSyy3N_oC37Gyuqc
           "If you have any questions, please email "
           [:span
            {:style "padding: 0 0 0 3px;"}
            [:a
             {:href "mailto:jobs@clojurecademy.com"}
             "jobs@clojurecademy.com"]]
           "."]]]]
       [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp.tCwPXBJl48Dnhq_s8dJB8._3jMSPXhXmV4dFYeHfDYVQL._3xv0fx4GG3nDJP8Eg7f1QE
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:starter packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Starter"]
             [:div "60 Day Job Listing"]
             [:div "Posting Tweets"]
             [:div "Email alert sent to thousands of candidates"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               [:strong "$129"]]]]]]]]
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:standard packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Standard"]
             [:div "120 Day Job Listing"]
             [:div "Posting Tweets"]
             [:div "Email alert sent to thousands of candidates"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               [:strong "$249"]]]]]]]]
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:advanced packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Advanced"]
             [:div "240 Day Job Listing"]
             [:div "Posting Tweets"]
             [:div "Email alert sent to thousands of candidates"]
             [:div "Highlighted listing"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               [:strong "$479"]]]]]]]]
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:premium packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Premium"]
             [:div "365 Day Job Listing"]
             [:div "Posting Tweets"]
             [:div "Email alert sent to thousands of candidates"]
             [:div "Highlighted listing"]
             [:div "Featured job"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               [:strong "$699"]]]]]]]]
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:sponsorship packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Sponsorship"]
             [:div "+Premium Package"]
             [:div "Your company logo will be featured in one of the courses and sponsors page"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               [:strong "$2499"]]]]]]]]]]]]))