(ns clojurecademy.view.about
  (:require [clojurecademy.view.common :as view.common]
            [clojurecademy.util.resource :as resource.util]))

(defn html
  [ctx]
  (view.common/full-html
    "About"
    (when (resource.util/authorized? ctx)
      (view.common/header :logged-in))
    [:main.site.tos
     [:div.fit-fixed
      [:div.grid-row
       [:article.grid-col-12.grid-col--center.padding-bottom--5.tos
        [:div
         [:h2 "About Clojurecademy"]
         [:p
          "Clojurecademy is designed as an interactive platform that teaches Clojure Programming Language and it's ecosystem, Clojure is a functional programming language that can be run on JVM, JavaScript and .Net runtimes and it's a dialect of LISP programming language.I believe that Clojure is a beautiful language that you can develop robust programs easily and have fun at the same time."]
         [:p "If you are new to programming or a seasoned programmer you can use Clojurecademy to learn Clojure or Programming in general very easily.Even if you are a Clojure developer you can do code katas, koans to keep your skills sharp."]
         [:p "Because of its interactive mechanism(hands on experience), you can easily follow instructions step by step and learn while doing it."]
         [:p "Clojurecademy's source of inspiration is coming from Codecademy platform, Codecademy is a very nice platform that teaches tons of programming languages, libraries, and frameworks etc.So I was like \"Why not to create such a platform for Clojure?\" and decided to create Clojurecademy, also Clojurecademy differs from Codecademy in a couple of ways, first thing is, with Clojurecademy's DSL developers who know Clojure Programming Language can create any courses they want dynamically.The second thing is Clojudecademy only focuses Clojure Technology Ecosystem(Clojure, ClojureScript, and Datomic etc.) not other languages, platforms."]

         [:p "The reason I created this site is that Clojure has changed my perspective to programming and made me a happy programmer so I want Clojure ecosystem to grow and want to be an accelerator effect with this site.Clojure community grows steadily but I think we can make it a little bit faster."]

         [:p "If you want to be a part of this journey please create cool courses and help me to spread Clojure to the world!"]

         [:p "Best,"]
         [:p "Ertuğrul Çetin"
          [:br "Creator of Clojurecademy"]]
         [:p
          [:a
           {:href "https://ertugrulcetin.com" :target "_blank"}
           [:u "https://ertugrulcetin.com"]]]]]]]]))
