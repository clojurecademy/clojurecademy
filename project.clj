(defproject clojurecademy "0.1.0"

  :description "Clojurecademy, Learning Clojure Made Easy"

  :url "https://clojurecademy.com"

  :author "Ertuğrul Çetin"

  :email "ertu.ctn@gmail.com"

  :license {:name "MIT License"
            :url  "https://opensource.org/licenses/MIT"}

  :repositories [["my.datomic.com" {:url "https://my.datomic.com/repo" :creds :gpg}]]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.562"]
                 [org.slf4j/slf4j-api "1.7.25"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.logback-extensions/logback-ext-loggly "0.1.5"]
                 [com.datomic/datomic-pro "0.9.5561" :exclusions [org.slf4j/slf4j-api]]
                 [com.amazonaws/aws-java-sdk-dynamodb "1.11.171"]
                 [clojurecademy "0.1.0"]
                 [ring-server "0.4.0"]
                 [reagent "0.6.1"]
                 [reagent-utils "0.2.1"]
                 [ring "1.6.0"]
                 [ring/ring-defaults "0.3.0"]
                 [amalloy/ring-gzip-middleware "0.1.3"]
                 [cljs-ajax "0.5.4" :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [liberator "0.14.1"]
                 [compojure "1.6.0"]
                 [cljsjs/codemirror "5.24.0-1"]
                 [hiccup "1.0.5"]
                 [yogthos/config "0.8"]
                 [secretary "1.2.3"]
                 [pandect "0.6.1"]
                 [kezban "0.1.7"]
                 [cpath-clj "0.1.2"]
                 [com.draines/postal "2.0.2"]
                 [clojail "1.0.6"]
                 [prone "1.1.4"]
                 [philoskim/debux "0.3.1"]
                 [com.cemerick/url "0.1.1"]
                 [com.ocpsoft/ocpsoft-pretty-time "1.0.7"]
                 [com.google.guava/guava "22.0"]]

  :jvm-opts ["-server"
             "-Djava.security.policy=example.policy"
             "-XX:+UseConcMarkSweepGC"
             "-XX:+CMSParallelRemarkEnabled"
             "-XX:+UseCMSInitiatingOccupancyOnly"
             "-XX:CMSInitiatingOccupancyFraction=70"
             "-XX:+ScavengeBeforeFullGC"
             "-XX:+CMSScavengeBeforeRemark"]

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-asset-minifier "0.2.7" :exclusions [org.clojure/clojure]]
            [lein-ring "0.12.0"]
            [lein-ancient "0.6.10"]
            [lein-figwheel "0.5.10"]]

  :ring {:handler clojurecademy.core/handler}

  :min-lein-version "2.5.0"

  :uberjar-name "clojurecademy.jar"

  :main clojurecademy.core

  :source-paths ["src/clj" "src/cljc" "src/cljs"]

  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets {:assets {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:min-app    {:source-paths ["src/cljs" "src/cljc"]
                                    :compiler     {:main          "clojurecademy.core"
                                                   :output-to     "resources/public/js/app.js"
                                                   :optimizations :advanced
                                                   :pretty-print  false}}
                       :app        {:source-paths ["src/cljs" "src/cljc"]
                                    :compiler     {:main          "clojurecademy.core"
                                                   :asset-path    "/js/out"
                                                   :output-to     "resources/public/js/app.js"
                                                   :output-dir    "resources/public/js/out"
                                                   :source-map    true
                                                   :optimizations :none
                                                   :pretty-print  true}}
                       :course     {:source-paths ["src/cljs" "src/cljc"]
                                    :figwheel     {:load-warninged-code true}
                                    :compiler     {:main          "clojurecademy.course"
                                                   :asset-path    "/course/js/out"
                                                   :output-to     "resources/public/course/js/course.js"
                                                   :output-dir    "resources/public/course/js/out"
                                                   :source-map    true
                                                   :optimizations :none
                                                   :pretty-print  true}}
                       :min-course {:source-paths ["src/cljs" "src/cljc"]
                                    :compiler     {:main          "clojurecademy.course"
                                                   :output-to     "resources/public/course/js/course.js"
                                                   :optimizations :advanced
                                                   :pretty-print  false}}}}

  :figwheel {:http-server-root "public"
             ;:server-ip        "192.168.2.119"
             :server-port      3475
             ;:nrepl-port       7002
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
             :css-dirs         ["resources/public/css"]
             ;:ring-handler     clojurecademy.core/handler
             })