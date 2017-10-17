(ns clojurecademy.dao.sub-instruction
  (:require [datomic.api :as d]
            [clojurecademy.dao.db :as db]))


(defn find-sub-ins-ids-by-course-id
  [t course-id]
  (db/query-seq (db/q-as-of t '[:find ?sub-instruction-id
                                :in $ ?course-id
                                :where
                                [?course-id :course/released? true]
                                [?course-id :course/active? true]
                                [?sub-instruction-id :sub-instruction/active? true]
                                [?sub-instruction-id :sub-instruction/course-id ?course-id]]
                            course-id)))


(defn find-sub-ins-ids-by-sub-chapter-id-and-course-id
  [t sub-chapter-id]
  (db/query-seq (db/q-as-of t '[:find ?sub-ins
                                :in $ ?sub-chapter-id
                                :where
                                [?s :subject/sub-chapter-id ?sub-chapter-id]
                                [?s :subject/active? true]
                                [?i :instruction/subject-id ?s]
                                [?i :instruction/active? true]
                                [?sub-ins :sub-instruction/instruction-id ?i]
                                [?sub-ins :sub-instruction/active? true]]
                            sub-chapter-id)))