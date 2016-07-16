(ns phonsole-client.components.console
  (:require [reagent.dom :as rdom]))

(defn console-output [output]
  [:div {:class "output"}
   (-> output
       (conj ">_")
       (->>
        (map #(identity [:p {:class "line"} %]))))])

(defn console [client output]
  [:div {:class "console card"}
   (console-output output)
   [:div {:class "card-content"}
    [:p {:class "card-title"} (:client-id client)]]
   [:div {:class "card-action"}
    [:i {:class "material-icons icon-action"
         :on-click #(-> %
                        .-target
                        .-parentElement
                        .-parentElement
                        .webkitRequestFullscreen)}
     "open_with"]]
   ])
