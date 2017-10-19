(ns clojurecademy.repl.util
  (:require [clojure.string :as str]))


(defn list-gen
  [& args]
  (reverse (reduce #(cons %2 %1) (reverse (first args)) (rest args))))


(defn in?
  [x coll]
  (some #(= x %) coll))


(defn runtime-ex
  [^String message]
  (throw (RuntimeException. message)))


(defn take-while-and-n-more
  [pred n coll]
  (let [[head tail] (split-with pred coll)]
    (concat head (take n tail))))


(defmacro with-out-str-data-map
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*out* s#]
       (let [r# ~@body]
         {:result r#
          :str    (str s#)}))))


(defn form-eval
  [f coll]
  (let [sym f
        f   (resolve f)]
    {:form   (str (cons sym coll))
     :result (apply f coll)}))


(defn macro?
  [f]
  (-> f resolve meta :macro boolean))


(defn get-err-msg
  [ex-msg]
  (cond
    (str/starts-with? ex-msg "EOF while reading")
    (str ex-msg ".Please check your parentheses, might not be opened/closed in correct order.")

    (str/includes? ex-msg "Unable to resolve symbol:")
    (str "Apparently you did not define: "
         (subs ex-msg (+ (count "Unable to resolve symbol:") (str/index-of ex-msg "Unable to resolve symbol:"))
               (str/index-of ex-msg "in this context")))

    (str/ends-with? ex-msg "cannot be cast to clojure.lang.IFn")
    (let [t (-> ex-msg (str/split #"\s+") second (str/split #"\.") last)]
      (str t " is not a function. Your first argument's type is: " t ", it has to be a function."))

    :else
    ex-msg))

(defn client-exception-message
  [^Throwable t]
  (let [t-map  (Throwable->map t)
        via    (first (:via t-map))
        ex-msg (:message via)]
    (format "%s" (get-err-msg ex-msg))))


(defn wrap-code
  [code]
  (str "(\n" code "\n)"))