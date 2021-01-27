(ns app.ui.root
  (:require
    [fulcro.client.dom :as dom :refer [div ul li p h3]]
    [fulcro.client.primitives :as prim :refer [defsc]]
    [app.model.user :as user]
    [app.ui.components :as comp]
    [taoensso.timbre :as log]))

(defsc Person [this {:keys [person/name person/age]} {:keys [onDelete]}]
  {:query [:person/name :person/age]
   :initial-state (fn [{:keys [name age] :as params}]
                    {:person/name name :person/age age})}
  (dom/li
   (dom/h5 (str name " (age: " age ")") (dom/button {:onClick #(onDelete name)} "X"))))

(def ui-person (prim/factory Person {:keyfn :person/name}))

(defsc PersonList [this {:keys [person-list/label person-list/people]}]
  {:query [:person-list/label {:person-list/people (prim/get-query Person)}]
   :initial-state (fn [{:keys [label]}]
                    {:person-list/label label
                     :person-list/people (if (= label "Friends")
                                           [(prim/get-initial-state Person {:name "Sally" :age 32})
                                            (prim/get-initial-state Person {:name "Joe" :age 22})]
                                           [(prim/get-initial-state Person {:name "Fred" :age 11})
                                            (prim/get-initial-state Person {:name "Bobby" :age 55})])})}
  (let [delete-person (fn [name] (println label "asked to delete" name))]
    (dom/div
     (dom/h4 label)
     (dom/ul
      (map (fn [p] (ui-person (prim/computed p {:onDelete delete-person}))) people)))))

(def ui-person-list (prim/factory PersonList))

(defsc Root [this {:keys [friends enemies]}]
  {:query [{:friends (prim/get-query PersonList)}
           {:enemies (prim/get-query PersonList)}]
   :initial-state (fn [_] {:friends (prim/get-initial-state PersonList {:label "Friends"})
                           :enemies (prim/get-initial-state PersonList {:label "Enemies"})})}
  (dom/div
   (ui-person-list friends)
   (ui-person-list enemies)))
