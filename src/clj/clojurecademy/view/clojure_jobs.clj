(ns clojurecademy.view.clojure-jobs
  (:require [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :as hiccup]
            [clojurecademy.view.common :as view.common]
            [clojurecademy.util.config :as conf]
            [kezban.core :refer :all]))

;;TODO Don't forget to change full-html head meta datas!!!!
;;TODO fix Course selected

(def packages {:standard    {:name "Standard" :link "https://pay.paddle.com/checkout/519756"}
               :advanced    {:name "Advanced" :link "https://pay.paddle.com/checkout/519757"}
               :premium     {:name "Premium" :link "https://pay.paddle.com/checkout/519758"}
               :sponsorship {:name "Sponsorship" :link "https://pay.paddle.com/checkout/519759"}})


(defn- package->mailto
  [package]
  (str "mailto:jobs@clojurecademy.com?"
       "subject=Purchase%20" (:name package) "%20Package"
       "&body=Hi%20There!%0D%0APlease%20fill%20the%20following%20fields%20below"
       "(also%20you%20can%20attach%20an%20existing%20job%20description%20document)"
       "%20then%20purchase%20package%20from%20"
       (:link package) "%0D%0A" "%0D%0A"
       "Job%20Title:" "%0D%0A" "%0D%0A"
       "Job%20Type(permanent,%20contract,%20unspecified):" "%0D%0A" "%0D%0A"
       "Compensation(Optional,%20$Min-$Max):" "%0D%0A" "%0D%0A"
       "Job%20Description:" "%0D%0A" "%0D%0A"
       "Job%20Location:" "%0D%0A" "%0D%0A"
       "Remote?:" "%0D%0A" "%0D%0A"
       "Application%20Instructions%20(e.g.%20Send%20resume%20to%20jobs@your-co.com):" "%0D%0A" "%0D%0A"
       "Company%20Name:" "%0D%0A" "%0D%0A"
       "Company%20Url(Optional):" "%0D%0A" "%0D%0A"
       "Company%20Elevator%20Pitch(Optional,%20max%2080%20characters):" "%0D%0A" "%0D%0A"
       "Attach%20Your%20Logo%20to%20mail(Optional)"))


(defn plans
  [auth?]
  (view.common/full-html
    "Clojure Job Posting Plans"
    (view.common/header (if auth? :jobs :non-logged-in-wide))
    [:div
     [:div._17S6vVdEJfvMGdpWLenGHU
      [:span {:style "position: relative;"}]
      [:div._1YGSFNfaFzXho9TQuUZGUL
       {:style "opacity: 1;"}
       [:div.P21R5CHnYJ9fh2LiHt7nK
        [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3r4ras_0_D__hjVccXuL5e
         [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b
          [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp.ClDPHTSyy3N_oC37Gyuqc
           "If you have any questions, please email "
           [:span
            {:style "padding: 0 0 0 4px;"}
            [:a
             {:href "mailto:jobs@clojurecademy.com"}
             "jobs@clojurecademy.com"]]
           "."]]]]
       [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp.tCwPXBJl48Dnhq_s8dJB8._3jMSPXhXmV4dFYeHfDYVQL._3xv0fx4GG3nDJP8Eg7f1QE
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:standard packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Standard"]
             [:div "90 Day Job Listing"]
             [:div "Posting Tweets"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               "$199"]]]]]]]
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:advanced packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Advanced"]
             [:div "90 Day Job Listing"]
             [:div "Posting Tweets"]
             [:div "Email alert sent to thousands of candidates"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               "$239"]
              [:div {:style "font-size: 25px;text-decoration: line-through;color:red"}
               "$300"]]]]]]]
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:premium packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Premium"]
             [:div "90 Day Job Listing"]
             [:div "Posting Tweets"]
             [:div "Email alert sent to thousands of candidates"]
             [:div "Highlighted listing"]
             [:div "Featured job"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               "$299"]
              [:div {:style "font-size: 25px;text-decoration: line-through;color:red"}
               "$359"]]]]]]]
        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href (package->mailto (:sponsorship packages))}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1TlackAU2XUAy3_AeDmSP0
             {:style "height:14rem"}
             [:div.Y1w9vprpw1N8chvfIrbyI "Sponsorship"]
             [:div "365 Day Job Listing"]
             [:div "+Premium Package"]
             [:div "Your company logo will be featured in one of the courses and sponsors page"]]
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
             [:div
              [:div {:style "font-size: 25px"}
               "$599"]]]]]]]]]]]))


(defn- key->val
  [package]
  (case package
    :standard 1
    :advanced 1
    :premium 2
    :sponsorship 3))


(defn- get-jobs
  []
  (->> (conf/get-clojure-jobs!)
       (filter :active?)
       (map #(assoc % :priority (key->val (:package %))))
       (sort #(multi-comp [:priority :created-time] %2 %1))))


(defn jobs
  [auth?]
  (view.common/full-html
    "Clojure Jobs"
    (view.common/header (if auth? :jobs :non-logged-in-wide))
    [:div
     [:div._17S6vVdEJfvMGdpWLenGHU
      [:span {:style "position: relative;"}]
      [:div._1YGSFNfaFzXho9TQuUZGUL
       {:style "opacity: 1;"}
       [:div.P21R5CHnYJ9fh2LiHt7nK
        [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._3r4ras_0_D__hjVccXuL5e
         [:div._3l5vJxhTbS1dLnkuNf4EaO.CGAmiC7XFUbztkDJDC6gJ._2tEfM0IkMaX9GwzuRYnX2b
          [:a._2fDy3KzGIsY8FHMg74ib-V._2bwYMUTOYpw6i1TbVOJS4A._1mQgyp76JXoCrTvLgR_p-d
           {:style "min-width:100px;color:#00adff;border-color:#00adff" :href "/clojure-job-plans"}
           "Post a Job"]]]]
       [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp.tCwPXBJl48Dnhq_s8dJB8._3jMSPXhXmV4dFYeHfDYVQL._3xv0fx4GG3nDJP8Eg7f1QE

        (for [job (get-jobs)]
          [:div._9eQUodKoyDfzljPjd3uUv
           {:style (when (#{:premium :sponsorship} (:package job)) "box-shadow: 5px 6px 21px -2px rgba(0, 145, 255, 0.84);")}
           [:span
            [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
             {:href (str "/clojure-jobs/" (:endpoint job))}
             [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
              [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj.K7OhfgT3YlRfjysLFZpo6
               {:style "background-color:#ffffff;"}
               (case (:package job)
                 :premium [:div._3ezJQYZvf0HcV59OM0YsyP "Featured"]
                 :sponsorship [:div._3ezJQYZvf0HcV59OM0YsyP "Sponsored"]
                 nil)
               [:img {:src (-> job :logo :url) :width (-> job :logo :width) :height (-> job :logo :height)}]]
              [:div._1TlackAU2XUAy3_AeDmSP0
               [:div.Y1w9vprpw1N8chvfIrbyI (:title job)]
               [:div (:company-name job) " - " (:company-elevator-pitch job)]]
              [:div.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj._1LqddMNwj5jKijAxFVxWQ2
               [:div (:location job) (when (:compensation job) (str " | " (:compensation job)))]]]]]])

        [:div._9eQUodKoyDfzljPjd3uUv
         [:span
          [:a._1qDNbq8Z4KbMrFkW-w9Q8Y
           {:href "/clojure-job-plans"}
           [:div._2H7I4SklrPHUaMJgIYxiFH._1naCNmn3BaYUQ5ZMt297ht
            [:div._1XRkeRcO7hh1axYhmKiQMM.tkzWKCisgMvVwAnypX-jp._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj.K7OhfgT3YlRfjysLFZpo6
             [:div view.common/plus-svg]]
            [:div._1TlackAU2XUAy3_AeDmSP0
             [:div.Y1w9vprpw1N8chvfIrbyI "Post a Job"]
             [:div "You can contribute to Clojurecademy by purchasing Job Ads"]]]]]]]]]]))


(defn job-page
  [auth? job]
  (view.common/full-html
    (str (:title job) " - Clojure Jobs")
    (view.common/header (if auth? :jobs :non-logged-in-wide))
    [:main.site.tos
     [:div.fit-fixed
      [:div.grid-row
       [:article.grid-col-12.grid-col--center.padding-bottom--5.tos
        (read-string (:description job))]]]]))