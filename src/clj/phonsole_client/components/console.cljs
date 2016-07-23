(ns phonsole-client.components.console
  (:require [reagent.dom :as rdom]
            [re-frame.core :refer [dispatch]]
            [phonsole-client.components.connection-status :refer [connection-status]]))

(defn console-output [output]
  [:div {:class "output"}
   (-> (vec output)
       (conj ">_")
       (->>
        (map #(identity [:p {:class "line"} %]))))])

(defn console [client]
  (println (:disconnected? client))
  [:div {:class "console"}
   [:div {:class "card"}
    (console-output (:output client))
    [:div {:class "card-content"}
     [:p {:class "card-title"}
      [:span {:class "margin-horizontal"} (:client-id client)]
      (connection-status (not (:disconnected? client)))]]
    [:div {:class "card-action"}
     (when (:disconnected? client)
       [:button {:class "btn btn-flat waves-purple"
                 :on-click #(dispatch [:remove-console (:client-id client)])}
        "Close"])
     [:span {:class "fill"}]
     [:i {:class "material-icons icon-action"
          :on-click #(-> %
                         .-target
                         .-parentElement
                         .-parentElement
                         .webkitRequestFullscreen)}
      "open_with"]]]
   ])
