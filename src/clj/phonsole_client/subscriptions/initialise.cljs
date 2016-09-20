(ns phonsole-client.subscriptions.initialise
  (:require [re-frame.core :refer [register-handler dispatch]]
            [phonsole-client.auth :as auth :refer [parse-id-token set-token! get-profile show-lock!]]
            [phonsole-client.push-events :refer [start-listening!]]
            [phonsole-client.local-storage :as ls]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! >! chan to-chan put!]])
  (:require-macros [phonsole-client.macros.config :refer [config]]
                   [cljs.core.async.macros :refer [go]]))


(defonce initial-state {:connected-clients []
                        :page nil})
(def localstorage-keys [auth/user-token-key auth/profile-key])

(defn check-token-valid [token]
  "Checks whether the given token is still valid by pinging the /auth endpoint on the server. Returns a channel."
  (let [out (chan)]
    (go
      (if token
        (let [request (http/get (str (.-protocol js/location) "//" (config :server-url) "/auth")
                                {:headers {"Authorization" (str "Bearer " token)}})
              response (<! request)]
          (println "auth response" response)
          (put! out (= (:status response) 200)))
        (put! out false)))
    out))

; TODO: re-write this bit, maybe using a transducer pipeline
(defn handle-auth [id-token]
  (go
    (if-let [token-valid? (<! (check-token-valid id-token))]
      (do  (set-token! id-token)
           (get-profile id-token (fn [err profile]
                                   (when (and (not err) profile)
                                     (dispatch [:profile-retrieved profile]))))
           (start-listening! id-token))
      (show-lock!))))

(defn initialise
  [db _]
  (println "init")
  (let [result  (->> localstorage-keys
                     (reduce (fn [result key] (assoc result
                                                     key
                                                     (ls/get key)))
                             db)
                     (merge initial-state))]
    (handle-auth (or (parse-id-token)
                     (auth/user-token-key result)))
    result))
