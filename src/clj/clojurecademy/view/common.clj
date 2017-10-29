(ns clojurecademy.view.common
  (:require [hiccup.page :refer [include-js include-css html5]]
            [clojurecademy.util.resource :as resource.util]
            [clojurecademy.util.config :as config]))

(def clojure-logo-svg [:svg#Warstwa_1
                       {:xml:space "preserve",
                        :style     "enable-background:new 0 0 300 300;",
                        :viewbox   "0 0 300 300",
                        :height    "125px",
                        :width     "125px",
                        :y         "0px",
                        :x         "0px",
                        :version   "1.1"}
                       [:style {:type "text/css"} "\n\t.st0 {\n        fill: #FFFFFF;\n    }\n"]
                       [:path.st0 {:d "M153.4,143.2c4,12.8,7.8,24.8,13.9,33.2c3.3,4.5,6.9,6.7,11.2,7.6c11.7-8.6,19.2-22.5,18.9-38.1
                       c-0.4-25.5-21.5-45.8-46.9-45.3c-4.8,0.1-9.3,0.9-13.6,2.3C145.1,113.2,148.7,128.4,153.4,143.2z"}]
                       [:path.st0 {:d "M160.1,184.6c-5.7-7.9-10.1-21.3-13.7-32.7c-2.2,3.6-5,9.1-6.9,14.5c-2.7,7.7-4.4,15.1-5.1,23.2
                       c5.5,2.2,11.5,3.3,17.8,3.2c4.6-0.1,9-0.8,13.2-2.2C163.5,188.9,161.7,186.9,160.1,184.6z"}]
                       [:path.st0 {:d "M143.6,143c-4.3-13.6-9.1-27.2-17.6-34.8c-12.8,8.4-21.1,23-20.8,39.4c0.3,15,7.6,28.1,18.8,36.4
                       C127.5,165.7,139.3,148.9,143.6,143z"}]
                       [:path.st0 {:d "M208.9,145.7c0.3,16.1-6,30.7-16.4,41.3c8.4,0.8,34.6,1.6,43.4-18.2c2-7.5,2.9-15.5,2.8-23.6
                       c-0.9-48.3-40.7-86.7-89-85.9c-26.8,0.5-50.6,13-66.3,32.3c24.7-10.2,40.4,0.1,43.8,2.7c7-3.2,14.8-5.1,23.1-5.3
                       C182.1,88.5,208.4,113.8,208.9,145.7z"}]
                       [:path.st0 {:d "M178.6,197.5c-7.8,4.2-16.8,6.7-26.3,6.9c-31.8,0.6-58.1-24.8-58.7-56.6c-0.3-17.1,6.9-32.6,18.5-43.4
                                   c-5.2-0.8-33.9-4-46.2,23.1c-1.5,6.7-2.2,13.6-2.1,20.8c0.9,48.3,40.7,86.7,89,85.9c28.9-0.5,54.3-15,69.8-36.9
                                   C203.2,204.2,184.6,199.4,178.6,197.5z"}]
                       [:path.st0 {:d "M151,45.9c55.8,0,101.1,45.3,101.1,101.1S206.8,248.1,151,248.1S49.9,202.8,49.9,147S95.2,45.9,151,45.9
                                    M151,35.9c-29.7,0-57.6,11.6-78.5,32.5c-21,21-32.5,48.9-32.5,78.5s11.6,57.6,32.5,78.5c21,21,48.9,32.5,78.5,32.5
                                    s57.6-11.6,78.5-32.5c21-21,32.5-48.9,32.5-78.5s-11.6-57.6-32.5-78.5C208.6,47.5,180.7,35.9,151,35.9L151,35.9z"}]])

(def plus-svg [:svg#Capa_1
               {:height    "105px",
                :width     "105px",
                :xml:space "preserve",
                :style     "enable-background:new 0 0 52 52;",
                :viewbox   "0 0 52 52",
                :y         "0px",
                :x         "0px",
                :version   "1.1"}
               [:g
                [:path {:fill "#FFFFFF",
                        :d    "M26,0C11.664,0,0,11.663,0,26s11.664,26,26,26s26-11.663,26-26S40.336,0,26,0z M26,50C12.767,50,2,39.233,2,
                        26   S12.767,2,26,2s24,10.767,24,24S39.233,50,26,50z"}]
                [:path {:fill "#FFFFFF",
                        :d    "M38.5,25H27V14c0-0.553-0.448-1-1-1s-1,0.447-1,1v11H13.5c-0.552,0-1,0.447-1,1s0.448,1,1,1H25v12c0,
                        0.553,0.448,1,1,1   s1-0.447,1-1V27h11.5c0.552,0,1-0.447,1-1S39.052,25,38.5,25z"}]]])

(defn- get-title
  [title]
  (if title
    (str title " | Clojurecademy")
    "Clojurecademy | Learning Clojure Made Easy"))

(defn- head
  ([]
   (head nil))
  ([title]
   [:head
    [:meta {:content "text/html; charset=UTF-8" :http-equiv "Content-Type"}]
    [:meta {:content "ie=edge" :http-equiv "x-ua-compatible"}]
    [:title (get-title title)]
    [:link {:type "image/x-icon" :rel "shortcut icon" :href "/img/fav.ico"}]
    [:meta {:content "width=device-width, initial-scale=1.0" :name "viewport"}]
    [:meta {:content "clojurecademy, clojure, clojurescript, lisp, functional programming, coding, code, programming, clojure jobs", :name "keywords"}]
    [:meta {:content "website" :property "og:type"}]
    (include-css "/css/core.css" "/css/show.css" "/css/vendor.css" "/css/portal.css")]))

(defn- header-non-logged-in
  ([]
   (header-non-logged-in :narrow))
  ([type]
   [:div
    {:data-react-class "PortalRouter"}
    [:section._18CgQDg3Su8I2jcMRQBUZ6._3ZDCOnjpiqdd8TAMpRPXVi
     [:header._2m_hDXMtwvETHFo3eGaTXE
      [(if (= type :narrow)
         :nav.y7tvmmg7fKu4JpdaYECtN.fit-fixed._2AyE6BSVwPoQZ6EJoCRCVT
         :nav.y7tvmmg7fKu4JpdaYECtN._2TUK8AnYJkvbFL7BtLPoiZ._2AyE6BSVwPoQZ6EJoCRCVT)
       [:section._2zSA2d9oAw82vmGaiCI6HM.vE2U5Z1_1p0OevhTkRrpb
        [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S
         [:a._1xOZs_2pgZHAoG4MALaCgS
          {:href "/"}
          [:img._39eW0wHuW9g7pG5q6TF7cE {:src "/img/logo.svg" :alt "Clojurecademy logo"}]]]]

       [:section._2zSA2d9oAw82vmGaiCI6HM._1KKDVD6unnpNtKqV9PPUQP
        [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S.NiKQEilPGVQiLL-faXn41
         [:a._1le84AU2Tvn8hDQEBK-7LY._5VTI96d-hitjuNGCfLO6X._2kwp78w4gxSQjvTSajoa6Q
          {:href "/clojure-jobs"} "Jobs"]]

        [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S.NiKQEilPGVQiLL-faXn41
         [:a._1le84AU2Tvn8hDQEBK-7LY._5VTI96d-hitjuNGCfLO6X._2kwp78w4gxSQjvTSajoa6Q
          {:href "/courses"} "Courses"]]

        [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S
         [:a#header__sign-in._2fDy3KzGIsY8FHMg74ib-V._2bwYMUTOYpw6i1TbVOJS4A._1mQgyp76JXoCrTvLgR_p-d
          {:style "min-width:100px;" :href "/login" :data-btn "true"} "Log in"]]

        [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S._1i-AgeYCGUoeEtEwBt-ES
         [:a#header__sign-up._2fDy3KzGIsY8FHMg74ib-V._2bwYMUTOYpw6i1TbVOJS4A
          {:style "min-width:100px;" :href "/" :data-btn "true"} "Sign up"]]]]]]]))

(defn- get-nav-bar
  [type]
  (let [narrow :nav.y7tvmmg7fKu4JpdaYECtN.fit-fixed._2AyE6BSVwPoQZ6EJoCRCVT
        wide   :nav.y7tvmmg7fKu4JpdaYECtN._2TUK8AnYJkvbFL7BtLPoiZ._2AyE6BSVwPoQZ6EJoCRCVT]
    (case type
      :narrow narrow
      :wide wide)))

(defn- get-selection-style
  [selection]
  (let [selected     :a._1le84AU2Tvn8hDQEBK-7LY._3-_43iWhlATOxVv877QCRO._5VTI96d-hitjuNGCfLO6X._2kwp78w4gxSQjvTSajoa6Q
        non-selected :a._1le84AU2Tvn8hDQEBK-7LY._5VTI96d-hitjuNGCfLO6X._2kwp78w4gxSQjvTSajoa6Q]
    (case selection
      :selected selected
      :non-selected non-selected)))

(defn- header-logged-in
  ([jobs-selection learn-selection course-selection]
   (header-logged-in :narrow jobs-selection learn-selection course-selection))
  ([type jobs-selection learn-selection course-selection]
   [:header._2m_hDXMtwvETHFo3eGaTXE
    [(get-nav-bar type)
     [:section._2zSA2d9oAw82vmGaiCI6HM.vE2U5Z1_1p0OevhTkRrpb
      [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S
       [:a._1xOZs_2pgZHAoG4MALaCgS {:href "/"}
        [:img._39eW0wHuW9g7pG5q6TF7cE {:src "/img/logo.svg", :alt "Codecademy logo"}]]]]
     [:section._2zSA2d9oAw82vmGaiCI6HM._1KKDVD6unnpNtKqV9PPUQP
      [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S.NiKQEilPGVQiLL-faXn41
       [(get-selection-style jobs-selection) {:href "/clojure-jobs"} "Jobs"]]
      [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S.NiKQEilPGVQiLL-faXn41
       [(get-selection-style learn-selection) {:href "/courses/learn"} "Learn"]]
      [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S.NiKQEilPGVQiLL-faXn41
       [(get-selection-style course-selection) {:href "/courses"} "Courses"]]
      [:a {:href "/account"}
       [:div._3bI54mnYg5essxyEwnBsko._1f1KmfrKUtLxZllTyx0TrC._3nDVtg6dS-0_R6p0nDL-1S
        [:div._3wrXGt5j2uE4fjlPLrDXEA
         [:div#dropdown-toggle._3beJdIRG9xxfeiRwaBD9Cp
          [:div.Y8dknuoW8NLiudcM-zQib._1pl0auxsnexFW5uqwCQW_x._2tbl3cOSPjsfIWBMlHZgcG
           [:img.wXEAS7H2e7UsikVkXNety {:src "/img/menu.svg"}]]]]]]]]]))

(defn header
  ([]
   (header nil))
  ([option]
   (case option
     :jobs (header-logged-in :wide :selected :non-selected :non-selected)
     :learn (header-logged-in :wide :non-selected :selected :non-selected)
     :course (header-logged-in :wide :non-selected :non-selected :selected)
     :logged-in (header-logged-in :non-selected :non-selected :non-selected)
     :non-logged-in-narrow (header-non-logged-in)
     :non-logged-in-wide (header-non-logged-in :wide)
     (header-non-logged-in))))

(defn- footer-copyright
  []
  [:div.grid-col-6.grid-col--no-margin.margin-top--none.margin-bottom--none
   [:div#footer__legal__copyright
    [:small "Made in Istanbul & Berlin © 2017 Clojurecademy"]]])

(defn- footer-links
  []
  [:div.grid-col-6.grid-col--no-margin.grid-row.margin-top--none.margin-bottom--none
   [:div#footer__locale.grid-col-12.grid-col--no-spacing.margin-top-bottom--none.grid-col--align-right
    [:div#footer__legal__links
     [:small
      [:span
       [:a {:href "/clojure-jobs"} "Clojure Jobs"]]
      [:span
       [:a {:href "/courses"} "Courses"]]
      [:span
       [:a {:href "/about"} "About"]]
      [:span
       [:a {:href "https://clojurecademy.github.io/dsl-documentation/"} "Docs"]]
      [:span
       [:a {:href "https://github.com/clojurecademy"} "GitHub"]]
      [:span
       [:a
        {:href "mailto:info@clojurecademy.com"}
        "Contact"]]]]]])

(defn- footer
  []
  (list [:footer#footer.color-scheme--darkgrey
         [:hr]
         [:article#footer__legal
          [:div.grid-row.fit-fixed
           (footer-copyright)
           (footer-links)]]]
        (include-js "/js/app.js")
        [:script (resource.util/create-google-analytics-code (config/get :google-analytics-UA))]))

(defn full-html
  ([body]
   (full-html nil body))
  ([title body]
   (full-html title nil body))
  ([title header-body body]
   (hiccup.page/html5
     [:html
      {:lang "en"}
      (head title)
      [:body
       (list (if header-body header-body (header)) body)
       (footer)]])))