(ns phonsole-client.subscriptions.consoles
  (:require [re-frame.core :refer [register-handler register-sub]]
            [phonsole-client.utils :refer [update-keys]])
  (:require-macros [reagent.ratom :refer [reaction]])
  (:import [goog.date DateTime]))

(defn connected [db [_ {:keys [connected-clients]}]]
  (update db :connected-consoles #(merge
                                   (update-keys (fn [client] (assoc client :disconnected? true)) %)
                                   connected-clients)))

(defn remove-console [db [_ id]]
  (-> db
      (update :connected-consoles dissoc id)
      (update :console-output dissoc id)))

(def output-limit 2500)

(defn output [db [_ {:keys [output sender]}]]
  (update-in db [:console-output (:client-id sender)] #(->> (conj % {:text output :timestamp (-> (DateTime.)
                                                                                                 (.getTime))})
                                                            (take-last output-limit)
                                                            vec)))

(defn connected-consoles [db [_]]
  (reaction (->> (:connected-consoles @db)
                 (update-keys
                  (fn [client id] (assoc client :output (get-in @db [:console-output id])))))))
