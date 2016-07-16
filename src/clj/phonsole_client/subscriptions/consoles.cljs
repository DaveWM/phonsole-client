(ns phonsole-client.subscriptions.consoles
  (:require [re-frame.core :refer [register-handler register-sub]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn connected [db [_ {:keys [connected-clients]}]]
  (assoc db :connected-consoles connected-clients))

(defn output [db [_ {:keys [output sender]}]]
  (update-in db [:console-output (:client-id sender)] #(conj % output)))

(defn connected-consoles [db [_]]
  (reaction (->> (:connected-consoles @db)
                 (map (fn [console]
                        (assoc console :output (get-in @db [:console-output (:client-id console)])))))))

