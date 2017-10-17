(ns clojurecademy.view.404
  (:require [clojurecademy.view.common :as view.common]
            [clojurecademy.util.resource :as resource.util]))

(defn- header-without-links
  []
  [:div
   {:data-react-class "PortalRouter"}
   [:section._18CgQDg3Su8I2jcMRQBUZ6._3ZDCOnjpiqdd8TAMpRPXVi
    [:header._2m_hDXMtwvETHFo3eGaTXE
     [:nav.y7tvmmg7fKu4JpdaYECtN.fit-fixed._2AyE6BSVwPoQZ6EJoCRCVT
      [:section._2zSA2d9oAw82vmGaiCI6HM.vE2U5Z1_1p0OevhTkRrpb
       [:div._3bI54mnYg5essxyEwnBsko._3nDVtg6dS-0_R6p0nDL-1S
        [:a._1xOZs_2pgZHAoG4MALaCgS
         {:href "/"}
         [:img._39eW0wHuW9g7pG5q6TF7cE {:src "/img/logo.svg" :alt "Clojurecademy logo"}]]]]]]]])

(defn html
  []
  (view.common/full-html
    "Page Not Found"
    (header-without-links)
    [:main.errors.rescue_from_routing_error
     [:article.fit-fixed
      [:div.grid-row.margin-top--5.margin-bottom--5
       [:div.grid-col-12.grid-col--align-center
        [:h1 "404 error"]
        [:p "This page doesn't exist."]]]]]))