(ns clojurecademy.dao.db
  (:require [datomic.api :as d]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [cpath-clj.core :as cp]
            [clojurecademy.util.config :as conf]
            [clojurecademy.util.logging :as log])
  (:import (java.util Date UUID)
           (java.io File)
           (java.nio.file.attribute FileAttribute)
           (org.apache.commons.io FileUtils)
           (java.nio.file Files)))


(def conn (atom nil))

(def conn-locked? (atom false))


(defn entity->map
  [e]
  (assoc (into {} e) :db/id (:db/id e)))


(defn get-non-qua-and-given-keys
  [m keyseq]
  (select-keys (into {} (map (fn [[k v]]
                               [(keyword (name k)) v]) (entity->map m))) keyseq))


(defn query-seq
  [result]
  (-> result vec flatten seq))

(defn entity
  [e-id]
  (d/touch (d/entity (d/db @conn) e-id)))

(defn entity-as-of
  [^Long t e-id]
  (if t
    (d/touch (d/entity (d/as-of (d/db @conn) (Date. t)) e-id))
    (d/touch (d/entity (d/db @conn) e-id))))

(defn q
  [query & params]
  (apply d/q (concat [query] [(d/db @conn)] params)))

(defn q-as-of
  [^Long t query & params]
  (if t
    (apply d/q (concat [query] [(d/as-of (d/db @conn) (Date. t))] params))
    (apply d/q (concat [query] [(d/db @conn)] params))))

(defn temp-id
  []
  (d/tempid :db.part/user))

(defn transact
  [& entity-maps]
  @(d/transact
     @conn
     (vec entity-maps)))

(defn persist-and-return-entity-id
  ([entity]
   (persist-and-return-entity-id (d/tempid :db.part/user) entity))
  ([temp-id entity]
   (let [tx        (transact (merge {:db/id temp-id} entity))
         entity-id (d/resolve-tempid (:db-after tx) (:tempids tx) temp-id)]
     entity-id)))

(defn find-course-id-by-course-name-and-owner
  [name owner]
  (q '[:find ?e .
       :in $ ?name ?owner
       :where
       [?e :course/name ?name]
       [?e :course/owner ?owner]]
     name owner))

(defn find-chapter-id
  [course-id name]
  (q '[:find ?e .
       :in $ ?course-id ?name
       :where
       [?e :chapter/course-id ?course-id]
       [?e :chapter/name ?name]]
     course-id name))

(defn find-sub-chapter-id
  [course-id chapter-id name]
  (q '[:find ?e .
       :in $ ?course-id ?chapter-id ?name
       :where
       [?e :sub-chapter/course-id ?course-id]
       [?e :sub-chapter/chapter-id ?chapter-id]
       [?e :sub-chapter/name ?name]]
     course-id chapter-id name))

(defn find-subject-id
  [course-id sub-chapter-id name]
  (q '[:find ?e .
       :in $ ?course-id ?sub-chapter-id ?name
       :where
       [?e :subject/course-id ?course-id]
       [?e :subject/sub-chapter-id ?sub-chapter-id]
       [?e :subject/name ?name]]
     course-id sub-chapter-id name))

(defn find-instruction-id
  [course-id subject-id name]
  (q '[:find ?e .
       :in $ ?course-id ?subject-id ?name
       :where
       [?e :instruction/course-id ?course-id]
       [?e :instruction/subject-id ?subject-id]
       [?e :instruction/name ?name]]
     course-id subject-id name))

(defn find-sub-instruction-id
  [course-id instruction-id name]
  (q '[:find ?e .
       :in $ ?course-id ?instruction-id ?name
       :where
       [?e :sub-instruction/course-id ?course-id]
       [?e :sub-instruction/instruction-id ?instruction-id]
       [?e :sub-instruction/name ?name]]
     course-id instruction-id name))

(defn create-keyword-active
  [k]
  (keyword (-> (apply str (drop 1 (str k)))
               (str "/" "active?"))))

(defn deactive-entities
  [k entities]
  @(d/transact
     @conn
     (mapv (fn [v]
             {:db/id                    v
              (create-keyword-active k) false}) entities)))

(defn deactivate-chapters
  [course-id chapters]
  (let [entities   (q '[:find ?e
                        :in $ ?course-id [?name ...]
                        :where
                        [?e :chapter/course-id ?course-id]
                        [?e :chapter/name ?name]]
                      course-id chapters)
        f-entities (apply concat entities)]
    (deactive-entities :chapter f-entities)
    f-entities))

(defn deactivate-sub-chapters
  [course-id chapter-id sub-chapters]
  (let [entities   (q '[:find ?e
                        :in $ ?course-id ?chapter-id [?name ...]
                        :where
                        [?e :sub-chapter/course-id ?course-id]
                        [?e :sub-chapter/chapter-id ?chapter-id]
                        [?e :sub-chapter/name ?name]]
                      course-id chapter-id sub-chapters)
        f-entities (apply concat entities)]
    (deactive-entities :sub-chapter f-entities)
    f-entities))

(defn deactivate-subjects
  [course-id sub-chapter-id subjects]
  (let [entities   (q '[:find ?e
                        :in $ ?course-id ?sub-chapter-id [?name ...]
                        :where
                        [?e :subject/course-id ?course-id]
                        [?e :subject/sub-chapter-id ?sub-chapter-id]
                        [?e :subject/name ?name]]
                      course-id sub-chapter-id subjects)
        f-entities (apply concat entities)]
    (deactive-entities :subject f-entities)
    f-entities))

(defn upsert-course
  [course owner]
  (if-let [course-id (find-course-id-by-course-name-and-owner (-> course :manifest :name str) owner)]
    (do
      (transact {:db/id                           course-id
                 :course/title                    (-> course :manifest :title)
                 :course/short-description        (-> course :manifest :short-description)
                 :course/long-description         (-> course :manifest :long-description)
                 :course/skip?                    (-> course :manifest :skip?)
                 :course/report-bug-email-or-link (-> course :manifest :report-bug-email-or-link)
                 :course/who-is-this-course-for   (-> course :manifest :who-is-this-course-for)})
      course-id)
    (persist-and-return-entity-id {:course/name                     (-> course :manifest :name str)
                                   :course/active?                  true
                                   :course/title                    (-> course :manifest :title)
                                   :course/short-description        (-> course :manifest :short-description)
                                   :course/long-description         (-> course :manifest :long-description)
                                   :course/skip?                    (-> course :manifest :skip?)
                                   :course/report-bug-email-or-link (-> course :manifest :report-bug-email-or-link)
                                   :course/created-time             (System/currentTimeMillis)
                                   :course/who-is-this-course-for   (-> course :manifest :who-is-this-course-for)})))

(defn upsert-chapter
  [course-id chapter]
  (if-let [chapter-id (find-chapter-id course-id (str (:name chapter)))]
    (do
      (transact {:db/id           chapter-id
                 :chapter/title   (:title chapter)
                 :chapter/active? true
                 :chapter/index   (:index chapter)})
      chapter-id)
    (persist-and-return-entity-id {:chapter/name      (str (:name chapter))
                                   :chapter/active?   true
                                   :chapter/title     (:title chapter)
                                   :chapter/index     (:index chapter)
                                   :chapter/course-id course-id})))

(defn upsert-sub-chapter
  [course-id chapter-id sub-chapter]
  (if-let [sub-chapter-id (find-sub-chapter-id course-id chapter-id (str (:name sub-chapter)))]
    (do
      (transact {:db/id               sub-chapter-id
                 :sub-chapter/title   (:title sub-chapter)
                 :sub-chapter/active? true
                 :sub-chapter/index   (:index sub-chapter)})
      sub-chapter-id)
    (persist-and-return-entity-id {:sub-chapter/name       (str (:name sub-chapter))
                                   :sub-chapter/active?    true
                                   :sub-chapter/title      (:title sub-chapter)
                                   :sub-chapter/index      (:index sub-chapter)
                                   :sub-chapter/chapter-id chapter-id
                                   :sub-chapter/course-id  course-id})))

(defn upsert-subject
  [course-id sub-chapter-id subject]
  (if-let [subject-id (find-subject-id course-id sub-chapter-id (str (:name subject)))]
    (do
      (transact {:db/id           subject-id
                 :subject/active? true
                 :subject/title   (:title subject)
                 :subject/index   (:index subject)
                 :subject/learn   (str (:learn subject))
                 :subject/ns      (str (:ns subject))})
      subject-id)
    (persist-and-return-entity-id {:subject/title          (:title subject)
                                   :subject/active?        true
                                   :subject/index          (:index subject)
                                   :subject/name           (str (:name subject))
                                   :subject/learn          (str (:learn subject))
                                   :subject/ns             (str (:ns subject))
                                   :subject/course-id      course-id
                                   :subject/sub-chapter-id sub-chapter-id})))

(defn upsert-instruction
  [course-id subject-id instruction]
  (if-let [instruction-id (find-instruction-id course-id subject-id (str (:name instruction)))]
    (do
      (transact {:db/id                      instruction-id
                 :instruction/active?        true
                 :instruction/run-pre-tests? (:run-pre-tests? instruction)
                 :instruction/initial-code   (str (:initial-code instruction))
                 :instruction/rule           (str (:rule instruction))})
      instruction-id)
    (persist-and-return-entity-id {:instruction/name           (str (:name instruction))
                                   :instruction/active?        true
                                   :instruction/run-pre-tests? (:run-pre-tests? instruction)
                                   :instruction/initial-code   (str (:initial-code instruction))
                                   :instruction/rule           (str (:rule instruction))
                                   :instruction/course-id      course-id
                                   :instruction/subject-id     subject-id})))

(defn upsert-sub-instruction
  [course-id instruction-id sub-instruction]
  (if-let [sub-instruction-id (find-sub-instruction-id course-id instruction-id (str (:name sub-instruction)))]
    (do
      (transact {:db/id                            sub-instruction-id
                 :sub-instruction/index            (:index sub-instruction)
                 :sub-instruction/active?          true
                 :sub-instruction/instruction-text (str (:instruction-text sub-instruction))
                 :sub-instruction/testing          (str (:testing sub-instruction))})
      sub-instruction-id)
    (persist-and-return-entity-id {:sub-instruction/name             (str (:name sub-instruction))
                                   :sub-instruction/active?          true
                                   :sub-instruction/index            (:index sub-instruction)
                                   :sub-instruction/instruction-text (str (:instruction-text sub-instruction))
                                   :sub-instruction/testing          (str (:testing sub-instruction))
                                   :sub-instruction/course-id        course-id
                                   :sub-instruction/instruction-id   instruction-id})))

(defn find-chapters
  [course-id]
  (q '[:find ?name ?e
       :in $ ?course-id
       :where
       [?e :chapter/name ?name]
       [?e :chapter/course-id ?course-id]
       [?e :chapter/active? true]]
     course-id))

(defn find-sub-chapters
  [course-id chapter-id]
  (q '[:find ?name ?e
       :in $ ?course-id ?chapter-id
       :where
       [?e :sub-chapter/name ?name]
       [?e :sub-chapter/course-id ?course-id]
       [?e :sub-chapter/chapter-id ?chapter-id]
       [?e :sub-chapter/active? true]]
     course-id chapter-id))

(defn find-subjects
  [course-id sub-chapter-id]
  (q '[:find ?name ?e
       :in $ ?course-id ?sub-chapter-id
       :where
       [?e :subject/name ?name]
       [?e :subject/course-id ?course-id]
       [?e :subject/sub-chapter-id ?sub-chapter-id]
       [?e :subject/active? true]]
     course-id sub-chapter-id))

(defn find-sub-instructions
  [course-id instruction-id]
  (q '[:find ?name ?e
       :in $ ?course-id ?instruction-id
       :where
       [?e :sub-instruction/name ?name]
       [?e :sub-instruction/course-id ?course-id]
       [?e :sub-instruction/instruction-id ?instruction-id]
       [?e :sub-instruction/active? true]]
     course-id instruction-id))

(defn deactivate-sub-instructions
  [course-id instruction-id sub-instructions]
  (let [entities (q '[:find ?e
                      :in $ ?course-id ?instruction-id [?name ...]
                      :where
                      [?e :sub-instruction/course-id ?course-id]
                      [?e :sub-instruction/instruction-id ?instruction-id]
                      [?e :sub-instruction/name ?name]]
                    course-id instruction-id sub-instructions)]
    (deactive-entities :sub-instruction (apply concat entities))))


(defn deactivate-instructions
  [course-id sub-chapter-id retracted-subjects]
  (let [entities   (q '[:find ?i
                        :in $ ?course-id ?sub-chapter-id [?name ...]
                        :where
                        [?e :subject/course-id ?course-id]
                        [?e :subject/sub-chapter-id ?sub-chapter-id]
                        [?e :subject/name ?name]
                        [?e :subject/active? false]
                        [?i :instruction/active? true]
                        [?i :instruction/subject-id ?e]]
                      course-id sub-chapter-id retracted-subjects)
        f-entities (apply concat entities)]
    (deactive-entities :instruction f-entities)
    f-entities))

(defn indexed-map
  [m]
  (keep-indexed #(merge {:index %1} %2) m))

(defn get-new-names
  [maps]
  (set (map #(str (:name %)) maps)))

(defn get-old-names
  [form]
  (set (keys (into {} form))))

(defn find-deactivated-names
  [old-form new-form]
  (set/difference (get-old-names old-form) (get-new-names new-form)))

(defn deactivate-sub-chapters*
  [chapter-id]
  (let [entities   (q '[:find ?e
                        :in $ ?chapter-id
                        :where
                        [?e :sub-chapter/chapter-id ?chapter-id]
                        [?e :sub-chapter/active? true]]
                      chapter-id)
        f-entities (apply concat entities)]
    (deactive-entities :sub-chapter f-entities)
    f-entities))

(defn deactivate-subjects*
  [sub-chapter-id]
  (let [entities   (q '[:find ?e
                        :in $ ?sub-chapter-id
                        :where
                        [?e :subject/sub-chapter-id ?sub-chapter-id]
                        [?e :subject/active? true]]
                      sub-chapter-id)
        f-entities (apply concat entities)]
    (deactive-entities :subject f-entities)
    f-entities))

(defn deactivate-instructions*
  [subject-id]
  (let [entities   (q '[:find ?e
                        :in $ ?subject-id
                        :where
                        [?e :instruction/subject-id ?subject-id]
                        [?e :instruction/active? true]]
                      subject-id)
        f-entities (apply concat entities)]
    (deactive-entities :instruction f-entities)
    f-entities))

(defn deactivate-sub-instructions*
  [instruction-id]
  (let [entities   (q '[:find ?e
                        :in $ ?instruction-id
                        :where
                        [?e :sub-instruction/instruction-id ?instruction-id]
                        [?e :sub-instruction/active? true]]
                      instruction-id)
        f-entities (apply concat entities)]
    (deactive-entities :sub-instruction f-entities)))

(defn deactivate-instruction
  [course-id subject-id]
  (when-let [instruction-id (q '[:find ?e .
                                 :in $ ?course-id ?subject-id
                                 :where
                                 [?e :instruction/course-id ?course-id]
                                 [?e :instruction/subject-id ?subject-id]
                                 [?e :instruction/name ?name]
                                 [?e :instruction/active? true]]
                               course-id subject-id)]
    @(d/transact
       @conn
       [{:db/id               instruction-id
         :instruction/active? false}])))

(defn deactivate-instruction-childs
  [deactivated-instructions-ids]
  (doseq [instruction-id deactivated-instructions-ids]
    (deactivate-sub-instructions* instruction-id)))

(defn deactivate-subject-childs
  [deactivated-subjects-ids]
  (doseq [subject-id deactivated-subjects-ids]
    (let [deactivated-instructions (deactivate-instructions* subject-id)]
      (deactivate-instruction-childs deactivated-instructions))))

(defn deactivate-sub-chapter-childs
  [deactivated-sub-chapters-ids]
  (doseq [sub-chapter-id deactivated-sub-chapters-ids]
    (let [deactivated-subjects (deactivate-subjects* sub-chapter-id)]
      (deactivate-subject-childs deactivated-subjects))))

(defn deactivate-chapter-childs
  [deactivated-chapters-ids]
  (doseq [chapter-id deactivated-chapters-ids]
    (let [deactivated-sub-chapters (deactivate-sub-chapters* chapter-id)]
      (deactivate-sub-chapter-childs deactivated-sub-chapters))))

(defn persist-sub-instructions
  [course-id instruction-id sub-instructions]
  (doseq [sub-instruction sub-instructions]
    (upsert-sub-instruction course-id instruction-id sub-instruction)))

(defn persist-instruction
  [course-id subject-id instruction]
  (let [instruction-id             (upsert-instruction course-id subject-id instruction)
        sub-instructions           (indexed-map (:sub-instructions instruction))
        retracted-sub-instructions (find-deactivated-names (find-sub-instructions course-id instruction-id) sub-instructions)]
    (deactivate-sub-instructions course-id instruction-id retracted-sub-instructions)
    (persist-sub-instructions course-id instruction-id sub-instructions)))

(defn persist-subjects
  [course-id sub-chapter-id subjects retracted-subjects]
  (doseq [subject subjects]
    (let [subject-id                  (upsert-subject course-id sub-chapter-id subject)
          instruction                 (:instruction subject)
          deactivated-instruction-ids (deactivate-instructions course-id sub-chapter-id retracted-subjects)]
      (deactivate-instruction-childs deactivated-instruction-ids)
      (if instruction
        (persist-instruction course-id subject-id instruction)
        (deactivate-instruction course-id subject-id)))))

(defn persist-sub-chapters
  [course-id chapter-id sub-chapters]
  (doseq [sub-chapter sub-chapters]
    (let [sub-chapter-id          (upsert-sub-chapter course-id chapter-id sub-chapter)
          subjects                (indexed-map (:subjects sub-chapter))
          retracted-subjects      (find-deactivated-names (find-subjects course-id sub-chapter-id) subjects)
          deactivated-subject-ids (deactivate-subjects course-id sub-chapter-id retracted-subjects)]
      (deactivate-subject-childs deactivated-subject-ids)
      (persist-subjects course-id sub-chapter-id subjects retracted-subjects))))

(defn persist-chapters
  [course-id chapters]
  (doseq [chapter chapters]
    (let [chapter-id                  (upsert-chapter course-id chapter)
          sub-chapters                (indexed-map (:sub-chapters chapter))
          retracted-sub-chapters      (find-deactivated-names (find-sub-chapters course-id chapter-id) sub-chapters)
          deactivated-sub-chapter-ids (deactivate-sub-chapters course-id chapter-id retracted-sub-chapters)]
      (deactivate-sub-chapter-childs deactivated-sub-chapter-ids)
      (persist-sub-chapters course-id chapter-id sub-chapters))))

(defn persist-course
  [course-map owner]
  (let [course-id               (upsert-course course-map owner)
        chapters                (indexed-map (:chapters course-map))
        retracted-chapters      (find-deactivated-names (find-chapters course-id) chapters)
        deactivated-chapter-ids (deactivate-chapters course-id retracted-chapters)]
    (deactivate-chapter-childs deactivated-chapter-ids)
    (persist-chapters course-id chapters)
    course-id))


(defn- collect-schema
  []
  (flatten (reduce
             (fn [v [_ uris]]
               (->> uris first slurp read-string (conj v)))
             []
             (cp/resources (io/resource "schema")))))


(defn establish-conn
  []
  (try
    (when-not @conn-locked?
      (reset! conn-locked? true)
      (d/create-database (conf/get :db-uri))
      (reset! conn (d/connect (conf/get :db-uri))))
    (catch Throwable t
      (log/error "Could not establish db conn." t))
    (finally
      (reset! conn-locked? false))))


(defn create-db!
  []
  (establish-conn)
  @(d/transact
     @conn
     (collect-schema)))