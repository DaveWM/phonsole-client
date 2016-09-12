(ns phonsole-client.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe register-sub dispatch dispatch-sync register-handler]]
            [phonsole-client.routing :as routing]
            [phonsole-client.subscriptions.register :refer [register]]
            [phonsole-client.components.connection-status :refer [connection-status]]
            [goog.events :as events])
  (:require-macros [reagent.ratom :refer [reaction]]))

(enable-console-print!)

(println register)
(register)

(defn loading []
  [:p "Loading..."])

(defn root-component []
  (let [page (subscribe [:page])
        user (subscribe [:user-details])
        connection-state (subscribe [:connection-state])
        show-avatar (r/atom true)]
    (fn []
      [:div
       [:nav {:class "purple"}
        [:a {:class "brand-logo"
             :href "/"}
         "Phonsole"
         [:img {:class "icon" :src "images/phonsole.svg"}]]
        [:ul {:class "controls"}
         (connection-status (:open? @connection-state))
         [:span {:class "user-info"}
          (when @show-avatar
            [:img {:src (:picture @user) :class "circle responsive-img user-icon" :on-error #(reset! show-avatar false)}])
          [:span (:name @user)]]
         [:button {:class "btn purple lighten-2"
                   :on-click #(dispatch [:log-out])} "Log out"]
         [:a {:href "/help"}
          [:i {:class "material-icons"} "help_outline"]]]]
       [:div {:class "container"}
        [(or @page loading)]]
       ])))

(if (aget js/navigator "serviceWorker")
  (-> js/navigator
      .-serviceWorker
      (.register "./serviceWorker.js" (clj->js {:scope "./"}))
      (.then #(println "service worker registered"))))

(defn main []
  (routing/start!)
  (dispatch-sync [:initialise])
  (r/render [root-component]
            (js/document.getElementById "app")))

(defn on-js-reload []
  (routing/stop!)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (main)
 )
(main)

