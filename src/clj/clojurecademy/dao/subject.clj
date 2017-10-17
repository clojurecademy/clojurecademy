(ns clojurecademy.dao.subject
  (:require [clojurecademy.dao.db :as db]
            [kezban.core :refer :all]))


(defn find-next-subject-id-by-subject-id
  [t subject-id]
  (when-let [subject (db/entity-as-of t subject-id)]
    (db/q-as-of t '[:find ?s .
                    :in $ ?sub-chapter-id ?index
                    :where
                    [?s :subject/sub-chapter-id ?sub-chapter-id]
                    [?s :subject/index ?index]
                    [?s :subject/active? true]]
                (:db/id (:subject/sub-chapter-id subject)) (+ (:subject/index subject) 1))))


(defn find-pre-subject-id-by-subject-id
  [t subject-id]
  (when-let [subject (db/entity-as-of t subject-id)]
    (db/q-as-of t '[:find ?s .
                    :in $ ?sub-chapter-id ?index
                    :where
                    [?s :subject/sub-chapter-id ?sub-chapter-id]
                    [?s :subject/index ?index]
                    [?s :subject/active? true]]
                (:db/id (:subject/sub-chapter-id subject)) (- (:subject/index subject) 1))))


(defn find-subject-name-by-subject-id
  [t subject-id]
  (db/q-as-of t '[:find ?subject-name .
                  :in $ ?subject-id
                  :where
                  [?subject-id :subject/name ?subject-name]
                  [?subject-id :subject/active? true]]
              subject-id))


(defn find-first-subject-of-course-by-course-id
  [t course-id]
  (some->> (db/q-as-of t '[:find ?sub .
                           :in $ ?course-id
                           :where
                           [?c :chapter/course-id ?course-id]
                           [?c :chapter/index 0]
                           [?c :chapter/active? true]
                           [?s :sub-chapter/chapter-id ?c]
                           [?s :sub-chapter/index 0]
                           [?s :sub-chapter/active? true]
                           [?sub :subject/sub-chapter-id ?s]
                           [?sub :subject/index 0]
                           [?sub :subject/active? true]]
                       course-id)
           (db/entity-as-of t)))


(defn find-first-subject-id-of-sub-chapter-by-sub-chapter-id
  [t sub-chapter-id]
  (db/q-as-of t '[:find ?sub .
                  :in $ ?sub-chapter-id
                  :where
                  [?sub :subject/sub-chapter-id ?sub-chapter-id]
                  [?sub :subject/index 0]
                  [?sub :subject/active? true]]
              sub-chapter-id))


(defn find-first-subject-of-sub-chapter-by-sub-chapter-id
  [t sub-chapter-id]
  (some->> sub-chapter-id
           (find-first-subject-id-of-sub-chapter-by-sub-chapter-id t)
           (db/entity-as-of t)))


(defn find-max-indexed-subject-id-by-sub-chapter-id
  [t sub-chapter-id]
  (let [index-subject-id-set (db/q-as-of t '[:find ?index ?c
                                             :in $ ?sub-chapter-id
                                             :where
                                             [?c :subject/sub-chapter-id ?sub-chapter-id]
                                             [?c :subject/index ?index]
                                             [?c :subject/active? true]]
                                         sub-chapter-id)]
    (->> (reduce (fn [m [k v]]
                   (conj m {:index k :id v})) [] index-subject-id-set)
         (sort-by :index #(compare %2 %1))
         first
         :id)))


(defn find-max-indexed-sub-chapter-id-by-chapter-id
  [t chapter-id]
  (let [index-sub-chapter-id-set (db/q-as-of t '[:find ?index ?c
                                                 :in $ ?chapter-id
                                                 :where
                                                 [?c :sub-chapter/chapter-id ?chapter-id]
                                                 [?c :sub-chapter/index ?index]
                                                 [?c :sub-chapter/active? true]]
                                             chapter-id)]
    (->> (reduce (fn [m [k v]]
                   (conj m {:index k :id v})) [] index-sub-chapter-id-set)
         (sort-by :index #(compare %2 %1))
         first
         :id)))


(defn find-first-subject-id-of-chapter-by-chapter-id
  [t chapter-id]
  (db/q-as-of t '[:find ?sub .
                  :in $ ?chapter-id
                  :where
                  [?sub-ch :sub-chapter/chapter-id ?chapter-id]
                  [?sub-ch :sub-chapter/index 0]
                  [?sub-ch :sub-chapter/active? true]
                  [?sub :subject/sub-chapter-id ?sub-ch]
                  [?sub :subject/index 0]
                  [?sub :subject/active? true]]
              chapter-id))


(defn find-latest-subject-id-of-chapter-by-chapter-id
  [t chapter-id]
  (->> chapter-id
       (find-max-indexed-sub-chapter-id-by-chapter-id t)
       (find-max-indexed-subject-id-by-sub-chapter-id t)))


(defn find-subject-and-childs
  [t subject-id]
  (db/q-as-of t '[:find ?i ?sub-i
                  :in $ ?subject-id
                  :where
                  [?i :instruction/subject-id ?subject-id]
                  [?i :instruction/active? true]
                  [?sub-i :sub-instruction/instruction-id ?i]
                  [?sub-i :sub-instruction/active? true]]
              subject-id))


(defn find-count-of-subjects-by-subject-id
  [t subject-id]
  (db/q-as-of t '[:find (count ?s) .
                  :in $ ?subject-id
                  :where
                  [?subject-id :subject/sub-chapter-id ?sub-chapter-id]
                  [?sub-chapter-id :sub-chapter/name ?name]
                  [?sub-chapter-id :sub-chapter/active? true]
                  [?s :subject/sub-chapter-id ?sub-chapter-id]
                  [?s :subject/active? true]]
              subject-id))


(defn find-subject-index-by-subject-id
  [t subject-id]
  (db/q-as-of t '[:find ?index .
                  :in $ ?subject-id
                  :where
                  [?subject-id :subject/index ?index]
                  [?subject-id :subject/active? true]]
              subject-id))


(defn collect-subject-and-childs
  [t subject-id]
  (let [subject (db/entity-as-of t subject-id)]
    (if-let* [subject-and-childs (find-subject-and-childs t subject-id)
              _ (> (count subject-and-childs) 0)
              instruction-id (ffirst subject-and-childs)
              instruction (db/entity-as-of t instruction-id)
              sub-ins (vec (sort-by :sub-instruction/index (map (fn [[f sub-ins-id]] (db/entity-as-of t sub-ins-id)) subject-and-childs)))]
             {:subject          (db/get-non-qua-and-given-keys subject [:id :name :title :learn :ns])
              :instruction      (db/get-non-qua-and-given-keys instruction [:id :name :run-pre-tests? :initial-code :rule])
              :sub-instructions (mapv #(db/get-non-qua-and-given-keys % [:id :name :index :instruction-text :testing]) sub-ins)}
             {:subject (db/get-non-qua-and-given-keys subject [:id :name :title :learn :ns])})))