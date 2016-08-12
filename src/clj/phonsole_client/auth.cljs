(ns phonsole-client.auth
  (:require [cljsjs.auth0-lock]
            [phonsole-client.local-storage :as ls]))

(def lock (js/Auth0Lock. "Qs4Ep1x2dZDZjiRuaZfys2JqEkRf9XVD" "dwmartin41.eu.auth0.com"))
(def user-token-key :user-token)
(def profile-key :user-details)

(defn show-lock! []
  (print "showing lock")
  (.show lock #js {:closable false
                   :responseType "token"}))

(defn parse-id-token []
  "gets the id token from the hash params of the current url"
  (if-let [hash  (->> js/window
                      .-location
                      .-hash
                      (.parseHash lock))] 
    (aget hash "id_token")))

(defn get-token []
  (ls/get user-token-key))

(defn set-token! [token]
  (ls/set! user-token-key token)
  token)

(defn get-profile [token callback]
  (.getProfile lock token (fn [err js-profile]
                            (let [profile (js->clj js-profile :keywordize-keys true)]
                              (callback err profile)))))

(defn log-out! []
  (doseq [name (->> [user-token-key profile-key]
                    (map name))]
    (ls/delete name)))



