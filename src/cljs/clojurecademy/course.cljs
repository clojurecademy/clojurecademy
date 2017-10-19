(ns clojurecademy.course
  (:require-macros [secretary.core :refer [defroute]])
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [clojurecademy.util :as util]
            [secretary.core :as secretary]
            [goog.dom :as dom]
            [goog.dom.classes :as cl]
            [goog.events :as e]
    ;;CodeMirror
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.mode.ruby]
            [cljsjs.codemirror.mode.clike]
            [cljsjs.codemirror.mode.haskell]
            [cljsjs.codemirror.mode.javascript]
            [cljsjs.codemirror.mode.python]
            [cljsjs.codemirror.mode.scheme]
            [cljsjs.codemirror.mode.commonlisp]
            [cljsjs.codemirror.mode.erlang]
            [cljsjs.codemirror.addon.edit.matchbrackets]
            [cljsjs.codemirror.addon.search.match-highlighter]
            [cljsjs.codemirror.addon.edit.closebrackets])
  (:import goog.History
           goog.History.EventType))

(enable-console-print!)

(declare resume)

(def learn (dom/getElement "discovery-resize-03"))
(def editor (dom/getElement "discovery-resize-14"))
(def terminal (dom/getElement "discovery-resize-25"))

(def loading-page (dom/getElement "loading-page-container"))

(def editor-atom (atom nil))
(def pre-editor-height (atom nil))
(def pre-terminal-left-pos (atom nil))

(def full-size-screen-on? (atom false))

(def terminal-on? (atom true))

(def pref-loaded? (atom false))
(def font-size (atom 12))
(def wide-size-screen-on? (atom false))
(def learn-wide-size-screen-on? (atom false))

(def pref-delay-millisec 1500)

(def done? (atom false))
(def no-ins? (atom false))

(def subject-loaded? (atom false))

(def code-running? (atom false))

(def back-button-active? (atom false))
(def next-button-active? (atom false))

(def subject-id (atom nil))
(def subject-index (atom nil))
(def chapter-index (atom nil))

(def sample-code-count (atom 0))
(def sample-code-maps (atom []))

(def input (atom nil))

(def timer (atom 0))


(defn- adjust-terminal-left-pos
  []
  (let [editor-left (-> editor .-style .-left)]
    (reset! pre-terminal-left-pos editor-left)
    (set! (-> terminal .-style .-left) editor-left)))


(defn- adjust-editor-size
  []
  (fn [_]
    (let [new-h (- (-> "codemirror-container" dom/getElement .-offsetHeight) 5)
          pre-h @pre-editor-height
          e     @editor-atom]
      (adjust-terminal-left-pos)
      (when-not (= new-h pre-h)
        (reset! pre-editor-height new-h)
        (.setSize e "100%" new-h)))))


(defn- observe-editor-dom-changes
  []
  (let [observer (new js/MutationObserver
                      (fn [muts]
                        (.forEach muts (adjust-editor-size))))]
    (.observe observer editor #js{:attributes      true
                                  :attributeFilter #js["style"]})))


(defn- open-triggered?
  [a]
  (if @a false true))


(defn delay-event
  []
  (fn [callback ms]
    (js/clearTimeout @timer)
    (reset! timer (js/setTimeout callback ms))))


(defn update-editor-preferences
  [k v]
  (util/ajax :put "/account/update-editor-preferences" :data {k v}))


(defn open-learn-wide-size-screen
  []
  (if @no-ins?
    (do
      (set! (-> learn .-style .-right) "0%")
      (set! (-> editor .-style .-display) "none")
      (set! (-> terminal .-style .-display) "none"))
    (do
      (set! (-> learn .-style .-right) "53%")
      (set! (-> editor .-style .-left) "47%")
      (set! (-> editor .-style .-display) "block")
      (set! (-> terminal .-style .-display) "block")))
  (cl/addRemove (dom/getElement "learn-wide-size-button") "icon-enlarge2" "icon-shrink2")
  (reset! learn-wide-size-screen-on? true))


(defn close-learn-wide-size-screen
  []
  (if @no-ins?
    (do
      (set! (-> learn .-style .-right) "50%")
      (set! (-> editor .-style .-left) "50%"))
    (do
      (set! (-> learn .-style .-right) "66.6667%")
      (set! (-> editor .-style .-left) "33.3333%")))
  (cl/addRemove (dom/getElement "learn-wide-size-button") "icon-shrink2" "icon-enlarge2")
  (set! (-> editor .-style .-display) "block")
  (set! (-> terminal .-style .-display) "block")
  (reset! learn-wide-size-screen-on? false))


(defn create-learn-wide-size-screen-event
  []
  (fn [_]
    (if (open-triggered? learn-wide-size-screen-on?)
      (open-learn-wide-size-screen)
      (close-learn-wide-size-screen))
    ((delay-event) (fn [_]
                     (update-editor-preferences :learn-wide-size-on? @learn-wide-size-screen-on?)) pref-delay-millisec)))


(defn redirect!
  [path]
  (set! js/window.location path))


(defn create-go-to-courses-event
  []
  (fn [_]
    (redirect! "/courses")))


(defn- open-wide-size-screen
  []
  (do
    (set! (-> editor .-style .-bottom) "0%")
    (set! (-> terminal .-style .-top) "100%")
    (cl/addRemove (dom/getElement "wide-size-button") "icon-enlarge2" "icon-shrink2")
    (reset! terminal-on? false)
    (reset! wide-size-screen-on? true)))


(defn- close-wide-size-screen
  []
  (do
    (reset! terminal-on? true)
    (set! (-> editor .-style .-bottom) "49%")
    (set! (-> terminal .-style .-top) "51.5%")
    (cl/addRemove (dom/getElement "wide-size-button") "icon-shrink2" "icon-enlarge2")
    (reset! wide-size-screen-on? false)))


(defn- create-wide-size-screen-event
  []
  (fn [_]
    (when-not @full-size-screen-on?
      (if (open-triggered? wide-size-screen-on?)
        (open-wide-size-screen)
        (close-wide-size-screen))
      ((delay-event) (fn [_]
                       (update-editor-preferences :wide-size-on? @wide-size-screen-on?)) pref-delay-millisec))))


(defn- open-full-size-screen
  []
  (do
    (set! (-> editor .-style .-bottom) "0%")
    (set! (-> terminal .-style .-top) "100%")
    (set! (-> editor .-style .-left) "0%")
    (set! (-> learn .-style .-right) "100%")
    (cl/addRemove (dom/getElement "full-size-button") "icon-enlarge" "icon-shrink")
    (reset! terminal-on? false)
    (reset! full-size-screen-on? true)))


(defn- close-full-size
  []
  (do
    (set! (-> editor .-style .-bottom) "49%")
    (set! (-> terminal .-style .-top) "51.5%")
    (set! (-> editor .-style .-left) "33.3333%")
    (set! (-> learn .-style .-right) "66.6667%")
    (cl/addRemove (dom/getElement "full-size-button") "icon-shrink" "icon-enlarge")
    (reset! full-size-screen-on? false)))


(defn- close-full-size-screen
  []
  (if @wide-size-screen-on?
    (do
      (close-full-size)
      (open-wide-size-screen)
      (when @learn-wide-size-screen-on?
        (open-learn-wide-size-screen)))
    (close-full-size)))


(defn- create-full-size-event
  []
  (fn [_]
    (if (open-triggered? full-size-screen-on?)
      (open-full-size-screen)
      (close-full-size-screen))))


(defn- change-editor-font-size!
  [f]
  (fn [_]
    (let [editor-wrap (dom/getElement "codemirror-container")
          result      (f @font-size)]
      (when (and (>= result 9) (<= result 30))
        (reset! font-size result)
        (set! (-> editor-wrap .-style .-fontSize) (str result "px")))
      ((delay-event) (fn [_]
                       (update-editor-preferences :font-size @font-size)) pref-delay-millisec))))


(defn- open-terminal
  []
  (do
    (reset! terminal-on? true)
    (set! (-> terminal .-style .-left) (-> editor .-style .-left))
    (set! (-> terminal .-style .-top) "51.5%")
    (set! (-> editor .-style .-bottom) "49%")))


(defn- close-terminal
  []
  (do
    (reset! terminal-on? false)
    (set! (-> terminal .-style .-left) (-> editor .-style .-left))
    (set! (-> editor .-style .-bottom) "0%")
    (set! (-> terminal .-style .-top) "100%")))


(defn- clear-terminal
  []
  (reagent/render [(fn [_] [:span#console-stdout])] (dom/getElement "console-stdout"))
  (reagent/render [(fn [_] [:span#console-stderr])] (dom/getElement "console-stderr")))


(defn- create-terminal-event
  []
  (fn [_]
    (if (open-triggered? terminal-on?)
      (open-terminal)
      (close-terminal))))


(defn editor-did-mount [input]
  (fn [this]
    (let [cm (.fromTextArea js/CodeMirror
                            (reagent/dom-node this)
                            #js {:mode              "clojure"
                                 :lineNumbers       true
                                 :lineWrapping      true
                                 :matchBrackets     true
                                 :autoCloseBrackets true})]
      (reset! editor-atom cm)
      (.setSize cm "100%" (- (-> (dom/getElement "codemirror-container") .-offsetHeight) 5))
      (.on cm "change" (fn [e]
                         (reset! input (.getValue e)))))))


(defn editor-comp [input]
  (reagent/create-class
    {:render              (fn [] [:textarea
                                  {:default-value ""
                                   :auto-complete "off"}])
     :component-did-mount (editor-did-mount input)}))


(defn- create-editor-comp
  []
  (fn []
    [:div
     [editor-comp input]]))


(defn- create-readonly-editor
  [this lang val]
  (let [cm (.fromTextArea js/CodeMirror (reagent/dom-node this)
                          #js {:mode          lang
                               :lineNumbers   false
                               :lineWrapping  true
                               :readOnly      true
                               :matchBrackets true})]
    (.setValue cm val)
    cm))


(defn- create-code-sample-comp
  ([val]
   (create-code-sample-comp "clojure" val))
  ([lang val]
   (fn []
     [:div
      [(reagent/create-class
         {:render              (fn [] [:textarea])
          :component-did-mount (fn [this]
                                 (create-readonly-editor this lang val))})]])))


(defn show-run-button-loading
  []
  (reagent/render [(fn [_]
                     [:div.cssload-loader
                      [:div.cssload-inner.cssload-one]
                      [:div.cssload-inner.cssload-two]
                      [:div.cssload-inner.cssload-three]])] (dom/getElement "run-button")))


(defn show-repl-button-loading
  []
  (reagent/render [(fn [_]
                     [:div.cssload-loader
                      [:div.cssload-inner.cssload-one]
                      [:div.cssload-inner.cssload-two]
                      [:div.cssload-inner.cssload-three]])] (dom/getElement "repl-button")))


(defn close-run-button-loading
  []
  (reagent/render [(fn [_]
                     [:div "Run"])] (dom/getElement "run-button")))


(defn close-repl-button-loading
  []
  (reagent/render [(fn [_]
                     [:div "REPL"])] (dom/getElement "repl-button")))


(defn remove-all-classes
  [sub-ins]
  (cl/remove (dom/getElement (str sub-ins "-checkpoint")) "fcn-checkpoint--satisfied" "fcn-checkpoint--unsatisfied")
  (cl/remove (dom/getElement (str sub-ins "-checkbox")) "fcn-checkpoint__checkbox--satisfied" "fcn-checkpoint__checkbox--unsatisfied")
  (cl/remove (dom/getElement (str sub-ins "-icon")) "fcn-icon-checkmark" "fcn-icon-close"))


(defn render-sub-ins-passed
  [sub-ins]
  (remove-all-classes sub-ins)
  (cl/add (dom/getElement (str sub-ins "-checkpoint")) "fcn-checkpoint--satisfied")
  (cl/add (dom/getElement (str sub-ins "-checkbox")) "fcn-checkpoint__checkbox--satisfied")
  (cl/add (dom/getElement (str sub-ins "-icon")) "fcn-icon-checkmark"))


(defn render-sub-ins-failed
  [sub-ins]
  (remove-all-classes sub-ins)
  (cl/add (dom/getElement (str sub-ins "-checkpoint")) "fcn-checkpoint--unsatisfied")
  (cl/add (dom/getElement (str sub-ins "-checkbox")) "fcn-checkpoint__checkbox--unsatisfied")
  (cl/add (dom/getElement (str sub-ins "-icon")) "fcn-icon-close"))


(defn render-sub-ins-failed-and-print-err-to-console
  [sub-ins failed-tests std-err]
  (let [index (-> failed-tests first :index)]
    (swap! std-err conj (str (+ index 1) ". instruction failed.\n\n" (apply str (interpose "\n" (map :exception-msg failed-tests))) "\n\n"))
    (render-sub-ins-failed sub-ins)))


(defn scroll-to-id
  [elem target-id]
  (let [speed            750
        moving-frequency 15
        target           (dom/getElement target-id)
        elem-scroll-top  (-> elem .-scrollTop)
        hop-count        (/ speed moving-frequency)
        gap              (/ (- (-> target .-offsetTop) elem-scroll-top) hop-count)]
    (doseq [i (range 1 (inc hop-count))]
      (let [move-to (+ elem-scroll-top (* gap i))
            timeout (* moving-frequency i)]
        (js/setTimeout #(set! (-> elem .-scrollTop) move-to) timeout)))))


(defn scroll-instruction-for-sub-ins-result
  [sub-ins-tests]
  (let [instruction-nav (aget (dom/getElementsByClass "accordion-container") 0)
        failed-tests    (filter #(false? (:passed %)) sub-ins-tests)
        sub-ins-el      (if (seq failed-tests)
                          (str (:sub-ins-name (first (sort-by :index failed-tests))) "-checkpoint")
                          (str (:sub-ins-name (first (sort-by :index #(compare %2 %1) sub-ins-tests))) "-checkpoint"))]
    (scroll-to-id instruction-nav sub-ins-el)))


(defn- scroll-to-top-instruction
  []
  (let [el (aget (dom/getElementsByClass "accordion-container") 0)]
    (set! (-> el .-scrollTop) 0)))


(defn render-result-with-sub-ins
  [sub-ins-tests output]
  (let [std-err (atom [])]
    (loop [s (group-by :sub-ins-name (sort-by :index sub-ins-tests))]
      (when (seq s)
        (let [[sub-ins result] (first s)
              failed-tests (filter #(false? (:passed %)) result)]
          (if (seq failed-tests)
            (render-sub-ins-failed-and-print-err-to-console sub-ins failed-tests std-err)
            (render-sub-ins-passed sub-ins))
          (recur (rest s)))))
    (scroll-instruction-for-sub-ins-result sub-ins-tests)
    (let [stderr-msg (apply str @std-err)]
      (when-not (str/blank? stderr-msg)
        (open-terminal))
      (reagent/render [(fn [_]
                         [:span#console-stderr
                          (apply str @std-err)])] (dom/getElement "console-stderr"))
      (reagent/render [(fn [_]
                         [:span#console-stdout
                          output])] (dom/getElement "console-stdout")))))


(defn render-result-with-no-instructions
  [output]
  (reagent/render [(fn [_]
                     [:span#console-stdout
                      output])] (dom/getElement "console-stdout")))


(defn render-eval-results
  [sub-ins-tests output]
  (if (seq sub-ins-tests)
    (render-result-with-sub-ins sub-ins-tests output)
    (render-result-with-no-instructions output)))


(defn- parse-to-hiccup
  [v t]
  (conj v (cond
            (:normal-text t)
            (:text t)

            (:italic t)
            [:i (:text t)]

            (:hi t)
            [:code (:text t)]

            (:bold t)
            [:strong (:text t)]

            (:link t)
            [:a
             {:href (:src t) :target "_blank"}
             (or (:title t) (:src t))])))


(defn- get-paragraph
  [v p]
  (conj v (reduce parse-to-hiccup [:p] (:texts p))))


(defn- get-code
  [v code]
  (let [n     (swap! sample-code-count inc)
        k-str (str "sample-code-size-id-" n)]
    (swap! sample-code-maps conj (assoc code :element-id k-str))
    (conj v [:div [(keyword (str "div#" k-str "._34io1W8CNlzGxPabtqLq9q._3xp4c3Gvny8Oap6jE2-Q_"))]
             [:br]])))


(defn- parse
  [v data]
  (if (:p data)
    (get-paragraph v data)
    (get-code v data)))


(defn parser
  [data]
  (reduce parse [] data))


(defn create-learn-section
  [learn]
  [:div.accordion-section
   [:div.accordion-section-heading
    [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.learn-header
     [:i._3QwoyZ-5nUFRrbC-LsvaCS._1Yp0Evc34IpeX4cliaOuxn._-28pXLJ13HOUfP_2pXrEi]
     "Learn"
     [:span#learn-wide-size-button.fcn-tabs__tab.fcn-tabs__tab--align-right.fcn-button.fcn-button--tab.fcn-button--tab--inactive.fcn-control-learn.icon-enlarge2]]]
   [:div.accordion-section-body
    [:div.jfdpfOzslotw2euWgxJNN._1PzvoBkjTI2SFvb5o9YAHW
     (vec (cons :div (parser learn)))]]])


(defn- interpose-fns-names-comp
  [fns]
  [:code (apply str (interpose ", " fns))])


(defn create-before-start-section
  [before-start]
  (when (or (:run-pre-tests? before-start) (> (count (keys before-start)) 1))
    [:div.accordion-section
     [:div.accordion-section-heading
      [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.learn-header
       [:i._3QwoyZ-5nUFRrbC-LsvaCS._1Yp0Evc34IpeX4cliaOuxn._-28pXLJ13HOUfP_2pXrEi]
       "Before you start"]]
     [:div.accordion-section-body
      [:div.jfdpfOzslotw2euWgxJNN._1PzvoBkjTI2SFvb5o9YAHW
       (when (:only-use-one-fn? before-start)
         [:p "You can only write " [:strong "one"] " function in the editor."])
       (when-let [required-fns (and (> (count (:required-fns before-start)) 0) (:required-fns before-start))]
         [:p "You need to use these " [:strong "required"] " function(s): " (interpose-fns-names-comp required-fns)])
       (when-let [restricted-fns (and (> (count (:restricted-fns before-start)) 0) (:restricted-fns before-start))]
         [:p "You can't use these " [:strong "restricted"] " function(s): " (interpose-fns-names-comp restricted-fns)])
       (when (:run-pre-tests? before-start)
         [:div
          [:p "Run previous instructions functionality is "
           [:strong "ON"] "."]
          [:p "(e.g. before executing " [:strong "3."] " instruction "
           [:strong "1."] " and " [:strong "2."] " instructions going to be executed to check that they pass or not)"]])]]]))


(defn create-instruction-section
  [sub-instructions]
  (when (seq sub-instructions)
    [:div.accordion-section
     [:div.accordion-section-heading

      [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG
       [:i._3QwoyZ-5nUFRrbC-LsvaCS._2jyOQuyyXLbnU5qEkBUx8S._-28pXLJ13HOUfP_2pXrEi
        {:name "instructions"}]
       "Instructions"]]
     [:div.accordion-section-body
      [:div
       (vec (cons :div (keep-indexed
                         (fn [i sub-instruction]
                           (let [index (+ i 1)
                                 s     (:name sub-instruction)
                                 text  (:text sub-instruction)]
                             [:div
                              [(keyword (str "div#" s "-checkpoint.fcn-checkpoint"))
                               [:b.fcn-checkpoint__number
                                (str index ".")]
                               [:div.fcn-checkpoint__body
                                (vec (cons :div.jfdpfOzslotw2euWgxJNN (parser text)))]
                               [(keyword (str "div#" s "-checkbox.fcn-checkpoint__checkbox"))
                                [(keyword (str "div#" s "-icon.fcn-icon"))]]]])) sub-instructions)))]]]))


(defn create-report-bug-section
  [bug-report]
  [:div.accordion-section
   [:div.accordion-section-heading
    {:data-index "3"}
    [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.support-header
     [:i._3QwoyZ-5nUFRrbC-LsvaCS._2d4F_uhsoeUW39e5KLQ7A_._-28pXLJ13HOUfP_2pXrEi]
     "Report a Bug"]]
   [:div.accordion-section-body
    [:div.accordion-section-body__no-bg.accordion-section-body__padded._1OKgxa6eFiKV7bZx2Cpyve
     [:span]
     [:div.VaiJa78yYYnolwnIsOcUV
      ;{:style "opacity: 1; transform: translateY(0px);"}
      "If you see a bug or any other issue with this page, please report it\n"
      [:a._2gPEYd35kAAQ2razCHgCKk
       (let [href {:href
                   (if-let [email (:email bug-report)]
                     (str "mailto:" email)
                     (:link bug-report))}]
         (if (:link bug-report)
           (assoc href :target "_blank")
           href))
       "here"] "."]]]])


(defn create-main-section
  [data]
  (fn [_]
    [:div#accordion-container
     (create-learn-section (:learn data))
     (create-before-start-section (:before-start data))
     (create-instruction-section (:sub-instructions data))
     (create-report-bug-section (:report-bug-email-or-link data))]))


(defn- open-loading-page
  []
  (reagent/render [(fn [_]
                     [:div._1ExHXGlOxKpCylTzG5QMT6._1huMPl6-RtzoV17I0HmoRf._3dkNckTVR4VPImWXsThMLq._1SsvwN3XXhA0W2Jo_T7CsX
                      [:div.sSQDykDehz7XXlU3XotWJ
                       [:span]
                       [:div.cssload-loader
                        {:style {:width "75px" :height "75px"}}
                        [:div.cssload-inner.cssload-one
                         {:style {:border-bottom "1px solid #34b3a0"}}]
                        [:div.cssload-inner.cssload-two
                         {:style {:border-right "1px solid #34b3a0"}}]
                        [:div.cssload-inner.cssload-three
                         {:style {:border-top "1px solid #34b3a0"}}]]]])] loading-page))


(defn- close-msg-page
  []
  (reagent/render [(fn [_]
                     [:span])] loading-page))


(defn create-close-msg-event
  []
  (fn [_]
    (close-msg-page)))


(defn- open-err-msg-page
  [header-msg msg button-name]
  (reagent/render [(fn [_]
                     [:div._1ExHXGlOxKpCylTzG5QMT6._1huMPl6-RtzoV17I0HmoRf._3dkNckTVR4VPImWXsThMLq._1SsvwN3XXhA0W2Jo_T7CsX
                      [:div.sSQDykDehz7XXlU3XotWJ
                       [:span]
                       [:div._3nBvvU7gkiiXyBTxPuWTB8
                        [:h2 header-msg]
                        [:p msg]
                        [:div._1XRkeRcO7hh1axYhmKiQMM._39K0C3ph1hpmLw6tsBfNnG.GEk9Sbt3OmLggMB2aQtSj
                         [:button#err-msg-button._2fDy3KzGIsY8FHMg74ib-V.Q2qWh46WAtbrJjOvkx6Hq._1mQgyp76JXoCrTvLgR_p-d._1QF8NyeUcBAA6ynUReVvI-
                          {:style {:color "#219d91" :border-color "#219d91"}}
                          button-name]]]]])] loading-page))


(defn render-completed-sub-ins
  [completed-sub-ins]
  (doseq [sub-ins-name completed-sub-ins]
    (render-sub-ins-passed sub-ins-name)))


(defn course-header-comp
  [title course-id]
  (fn []
    [:a._10tr0UBdh7IJTF1w-ahcOa._2Ik8dH0qI7ki4IMCQbG6pZ
     {:href (str "/courses/" course-id)}
     [:i._2s1fAUUBvIvC87s1stozEY.fcn-icon.fcn-icon-arrowPrev]
     [:div title]]))


(defn subject-title-comp
  [subject-index subject-title]
  (fn []
    [:div (str (+ 1 subject-index) ". " subject-title)]))


(defn subject-progress-comp
  [subject-index subject-count]
  (fn []
    [:div (str (+ 1 subject-index) "/" subject-count)]))


(defn activate-back-button
  []
  (do
    (cl/remove (dom/getElement "back-subject-button") "fcn-button--active" "fcn-button--inactive")
    (cl/add (dom/getElement "back-subject-button") "fcn-button--active")
    (reset! back-button-active? true)))


(defn inactivate-back-button []
  (do
    (cl/remove (dom/getElement "back-subject-button") "fcn-button--active" "fcn-button--inactive")
    (cl/add (dom/getElement "back-subject-button") "fcn-button--inactive")
    (reset! back-button-active? false)))


(defn render-back-button
  [d]
  (if (= 0 (:subject-index d) (:sub-chapter-index d) (:chapter-index d))
    (inactivate-back-button)
    (activate-back-button)))


(defn activate-nex-button
  []
  (do
    (cl/remove (dom/getElement "next-subject-button") "fcn-button--active" "fcn-button--inactive")
    (cl/add (dom/getElement "next-subject-button") "fcn-button--active")
    (reset! next-button-active? true)))



(defn inactivate-next-button []
  (do
    (cl/remove (dom/getElement "next-subject-button") "fcn-button--active" "fcn-button--inactive")
    (cl/add (dom/getElement "next-subject-button") "fcn-button--inactive")
    (reset! next-button-active? false)))


(defn render-next-button
  [d]
  (if (or (:done? d) (:no-ins? d) (:skip? d) (:admin? d) (:owner? d))
    (activate-nex-button)
    (inactivate-next-button)))


(defn render-next-and-pre-buttons
  [d]
  (render-back-button d)
  (render-next-button d))


(defn render-common-components-and-persist-fields
  [d]
  (reset! sample-code-count 0)
  (reset! sample-code-maps [])
  (reset! subject-id (:subject-id d))
  (reset! subject-index (:subject-index d))
  (reset! chapter-index (:chapter-index d))
  (reset! done? (:done? d))

  ;;TODO after reset you should render to see changes!!!
  (when-not @pref-loaded?
    (some->> (:font-size d) (reset! font-size))
    (some->> (:wide-size-on? d) (reset! wide-size-screen-on?))
    (some->> (:learn-wide-size-on? d) (reset! learn-wide-size-screen-on?))

    (when @wide-size-screen-on?
      (open-wide-size-screen))

    (set! (-> (dom/getElement "codemirror-container") .-style .-fontSize) (str @font-size "px"))

    (reset! pref-loaded? true))

  (reagent/render [(create-main-section d)] (dom/getElement "accordion-container"))

  (doseq [m @sample-code-maps]
    (reagent/render [(create-code-sample-comp (:form m))] (dom/getElement (:element-id m))))

  (close-full-size-screen)

  (if @learn-wide-size-screen-on?
    (open-learn-wide-size-screen)
    (close-learn-wide-size-screen))

  (reagent/render [(course-header-comp (:title d) (:course-id d))] (dom/getElement "course-header"))
  (reagent/render [(subject-title-comp (:subject-index d) (:subject-title d))] (dom/getElement "subject-title"))
  (reagent/render [(subject-progress-comp (:subject-index d) (:subject-count d))] (dom/getElement "subject-progress"))
  (clear-terminal)
  (render-next-and-pre-buttons d)
  (util/set-event-handler! "onclick" "learn-wide-size-button" (create-learn-wide-size-screen-event)))


(defn- redirect-subject-to-url!
  [d]
  (redirect! (str "course#!/subjects/" (:subject-id d)))
  (set! (.-title js/document) (str (:subject-title d) " - " (:title d) " | Clojurecademy")))


(defn- update-google-analytics
  [d]
  (js/ga "set" "page" (str "/course/subjects/" (:subject-id d)))
  (js/ga "set" "title" (str (:subject-title d) " - " (:title d)))
  (js/ga "send" "pageview"))


(defn render-no-ins-subject-page
  [d]
  (reset! no-ins? true)
  (render-common-components-and-persist-fields d)
  (.setValue @editor-atom "")
  (reset! input "")
  (close-msg-page)
  (redirect-subject-to-url! d)
  (update-google-analytics d)
  (scroll-to-top-instruction))


(defn render-subject-page
  [d]
  (reset! no-ins? false)
  (render-common-components-and-persist-fields d)
  (.setValue @editor-atom (or (:initial-code d) ""))
  (reset! input (:initial-code d))
  (render-completed-sub-ins (:completed-sub-instructions d))
  (close-msg-page)
  (reset! subject-loaded? true)
  (redirect-subject-to-url! d)
  (update-google-analytics d)
  (scroll-to-top-instruction))


(defn render-something-went-wrong
  [msg]
  (open-err-msg-page "Something went wrong!"
                     msg
                     "Close")
  (util/set-event-handler! "onclick" "err-msg-button" (create-close-msg-event)))


(defn handle-error
  [{:keys [status response] :as m}]
  (if (or (= status 401) (= status 403))
    (redirect! "/login")
    (render-something-went-wrong (:error response))))


(defn- start-course
  [course-id]
  (util/ajax :put (str "/courses/" course-id "/start")
             :success (fn [_] (resume (str "/courses/" course-id "/resume")))
             :error handle-error))

(defn create-start-course-event
  [course-id]
  (fn [_]
    (start-course course-id)))


(defn render-course-finished
  []
  (open-err-msg-page "Course finished!"
                     "Congratulations! You've completed this course, if the author of this course adds new content we will inform you.
                     You can work on another courses now!"
                     "Go to Courses")
  (util/set-event-handler! "onclick" "err-msg-button" (create-go-to-courses-event)))


(defn render-not-enrolled
  [course-id]
  (open-err-msg-page "You did not enroll!"
                     "We've noticed you haven't started this course yet! Click below to get started."
                     "Start")
  (util/set-event-handler! "onclick" "err-msg-button" (create-start-course-event course-id)))


(defn render-course-does-not-exist
  []
  (open-err-msg-page "Course does not exist"
                     "The course that you are looking for does not exist, click below to check courses."
                     "Go to Courses")
  (util/set-event-handler! "onclick" "err-msg-button" (create-go-to-courses-event)))


(defn render-not-found
  []
  (open-err-msg-page "Not found!"
                     "The page that you are looking for does not exist."
                     "Go to Courses")
  (util/set-event-handler! "onclick" "err-msg-button" (create-go-to-courses-event)))


(defn render-too-long-input
  []
  (open-err-msg-page "Too long input!"
                     "Your input is too long, you can't evaluate more than 30.000 characters"
                     "Close")
  (util/set-event-handler! "onclick" "err-msg-button" (create-close-msg-event)))


(defn render-pages
  [d]
  (cond
    (:does-not-exists? d)
    (render-course-does-not-exist)

    (:not-enrolled? d)
    (render-not-enrolled (:course-id d))

    (:course-finished? d)
    (render-course-finished)

    (:no-pre-left? d)
    (js/alert "No pre subject left")

    (:no-ins? d)
    (render-no-ins-subject-page d)

    :else
    (render-subject-page d)))


(defn run-code
  []
  (if (> (count @input) 30000)
    (render-too-long-input)
    (when-not @code-running?
      (reset! code-running? true)
      (clear-terminal)
      (show-run-button-loading)
      (util/ajax :put "/eval"
                 :data {:client-code @input :subject-id @subject-id}
                 :success (fn [l]
                            (let [stdout (dom/getElement "console-stdout")
                                  stderr (dom/getElement "console-stderr")]
                              (if (:error l)
                                (do
                                  (open-terminal)
                                  (reagent/render [(fn [_] [:span#console-stdout])] stdout)
                                  (reagent/render [(fn [_] [:span#console-stderr (-> l :exception-msg)])] stderr))
                                (do
                                  (when-not (str/blank? (-> l :code-body :str))
                                    (open-terminal))
                                  (render-eval-results (:sub-ins-tests l) (-> l :code-body :str))
                                  (when (and (not @next-button-active?) (:done? l))
                                    (activate-nex-button)))))
                            (close-run-button-loading))
                 :error (fn [{:keys [status response]}]
                          (render-something-went-wrong (str (:error response) "(Try refreshing the page)"))
                          (close-run-button-loading))
                 :finally (fn [_] (reset! code-running? false))))))


(defn run-repl-code
  []
  (if (> (count @input) 30000)
    (render-too-long-input)
    (when-not @code-running?
      (reset! code-running? true)
      (clear-terminal)
      (show-repl-button-loading)
      (util/ajax :put "/eval-repl"
                 :data {:client-code @input}
                 :success (fn [l]
                            (let [stdout (dom/getElement "console-stdout")
                                  stderr (dom/getElement "console-stderr")]
                              (if (:error l)
                                (do
                                  (open-terminal)
                                  (reagent/render [(fn [_] [:span#console-stdout])] stdout)
                                  (reagent/render [(fn [_] [:span#console-stderr (-> l :exception-msg)])] stderr))
                                (do
                                  (when (or (not (str/blank? (:out-str l))) (not (str/blank? (:err-str l))))
                                    (open-terminal))
                                  (reagent/render [(fn [_] [:span#console-stdout (:out-str l)])] stdout)
                                  (reagent/render [(fn [_] [:span#console-stderr (:err-str l)])] stderr))))
                            (close-repl-button-loading))
                 :error (fn [{:keys [status response]}]
                          (render-something-went-wrong (str (:error response) "(Try refreshing the page)"))
                          (close-repl-button-loading))
                 :finally (fn [_] (reset! code-running? false))))))


(defn create-run-button-event
  []
  (fn [_]
    (run-code)))


(defn create-repl-button-event
  []
  (fn [_]
    (run-repl-code)))


(defn create-next-chapter-rendered-event
  []
  (fn [_]
    (open-loading-page)
    (when @next-button-active?
      (util/ajax :get (str "/subjects/" @subject-id "/next")
                 :success render-pages
                 :error handle-error))))


(defn create-pre-chapter-rendered-event
  []
  (fn [_]
    (when @back-button-active?
      (open-loading-page)
      (util/ajax :get (str "/subjects/" @subject-id "/pre")
                 :success render-pages
                 :error handle-error))))


(defn resume
  [path]
  (util/ajax :get path
             :success render-pages
             :error handle-error))

(defn add-editor-events
  []
  (e/listen (dom/getElement "codemirror-container") e/EventType.KEYDOWN
            (fn [e]
              (when (and (.-altKey e) (= (.-keyCode e) 13))
                (run-code))
              (when (and (or (.-metaKey e) (.-ctrlKey e)) (= (.-keyCode e) 13))
                (run-repl-code))))
  (e/listen (dom/getElement "codemirror-container") e/EventType.MOUSEDOWN
            (fn [e]
              (when (or @wide-size-screen-on? @full-size-screen-on?)
                (close-terminal)))))


(defn add-events
  []
  (util/set-event-handler! "onclick" "wide-size-button" (create-wide-size-screen-event))
  (util/set-event-handler! "onclick" "full-size-button" (create-full-size-event))
  (util/set-event-handler! "onclick" "inc-font-size-button" (change-editor-font-size! inc))
  (util/set-event-handler! "onclick" "dec-font-size-button" (change-editor-font-size! dec))
  (util/set-event-handler! "onclick" "terminal-button" (create-terminal-event))
  (util/set-event-handler! "onclick" "learn-wide-size-button" (create-learn-wide-size-screen-event))
  (util/set-event-handler! "onclick" "run-button-container" (create-run-button-event))
  (util/set-event-handler! "onclick" "repl-button-container" (create-repl-button-event))
  (util/set-event-handler! "onclick" "next-subject-button" (create-next-chapter-rendered-event))
  (util/set-event-handler! "onclick" "back-subject-button" (create-pre-chapter-rendered-event)))


(defn add-routing
  []
  (secretary/set-config! :prefix "#")

  (defroute start-course-route #"/courses/(\d+)/start" [course-id]
            (start-course course-id))

  (defroute resume-course-route #"/courses/(\d+)/resume" [course-id]
            (resume (str "/courses/" course-id "/resume")))

  (defroute resume-course-route #"/sub-chapters/(\d+)/resume" [sub-chapter-id]
            (resume (str "/sub-chapters/" sub-chapter-id "/resume")))

  (defroute course-subject-route #"/subjects/(\d+)" [course-subject-id]
            (when-not @subject-loaded?
              (resume (str "/subjects/" course-subject-id))))

  ;;This has to be last in the routing order!!
  (defroute no-route "*" []
            (render-not-found))

  (let [h (History.)]
    (goog.events/listen h EventType/NAVIGATE #(let [token      (.-token %)
                                                    trim-token (subs token 1)]
                                                (secretary/dispatch! trim-token)))
    (doto h
      (.setEnabled true))))


(defn add-dom-manipulation-stuff
  []
  (observe-editor-dom-changes)
  (.addEventListener js/window "resize" (adjust-editor-size)))

(defn start! []
  (reagent/render [(create-editor-comp)] (dom/getElement "codemirror-container"))
  (add-dom-manipulation-stuff)
  (add-events)
  (add-editor-events)
  (add-routing)
  ;;TODO get it from config.
  (js/ga "create" "UA-103585852-1" "auto"))

(start!)