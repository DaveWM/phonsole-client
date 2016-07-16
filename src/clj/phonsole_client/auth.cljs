(ns phonsole-client.auth
  (:require [cljsjs.auth0-lock]))

(def lock (js/Auth0Lock. "Qs4Ep1x2dZDZjiRuaZfys2JqEkRf9XVD" "dwmartin41.eu.auth0.com"))
(def user-token-key :user-token)
(def profile-key :user-details)

(defn show-lock! []
  (print "showing lock")
  (.show lock #js {:closable false
                   :callbackURL "http://localhost:8000/"
                   :responseType "token"}))

(defn parse-id-token []
  "gets the id token from the hash params of the current url"
  (if-let [hash  (->> js/window
                      .-location
                      .-hash
                      (.parseHash lock))]
    (.-id_token hash)))

(defn get-token []
  (.getItem js/localStorage (name user-token-key)))

(defn set-token! [token]
  (.setItem js/localStorage (name user-token-key) token)
  token)

(defn get-profile [token callback]
  (.getProfile lock token (fn [err profile]
                            (.setItem js/localStorage (name profile-key) profile)
                            (callback err (js->clj profile :keywordize-keys true)))))

(defn log-out! []
  (->> [user-token-key profile-key]
       (map name)
       (map #(.removeItem js/localStorage %))))



