(ns phonsole-client.subscriptions.initialise
  (:require [re-frame.core :refer [register-handler dispatch]]
            [phonsole-client.auth :as auth :refer [parse-id-token set-token! get-profile show-lock!]]
            [phonsole-client.push-events :refer [start-listening!]]))


(defonce initial-state {:connected-clients []
                        :page nil})
(def localstorage-keys [auth/user-token-key auth/profile-key])
              
(defn initialise
  [db _]
  (println "init")
  (let [result  (->> localstorage-keys
                     (reduce (fn [result key] (assoc result
                                                     key
                                                     (.getItem js/localStorage key)))
                             db)
                     (merge initial-state))]
    (if-let [id-token (or (auth/user-token-key result)
                          (parse-id-token))] 
      (do  (set-token! id-token)
           (get-profile id-token (fn [err profile]
                                   (if profile 
                                     (dispatch [:profile-retrieved profile]))))
           (start-listening! id-token))
      (show-lock!))
    result))
