(ns phonsole-client.components.console
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch]]
            [phonsole-client.components.connection-status :refer [connection-status]]
            [clojure.string :as str]
            [goog.string :as gstring]))

(defn colour [colour]
  {:style {:color colour}})

(defn background [colour]
  {:style {:background-color colour}})

(defn convert-index-map [to-convert transform starting-index]
  (->> to-convert
       (map
        (fn [[offset item-vec]]
          (map-indexed (fn [i item] [(+ i starting-index offset) (transform item)]) item-vec)))
       (into {})))

(def ansi-codes
  (let [colours {0 ["black" "red" "green" "yellow" "blue" "darkmagenta" "cyan" "lightgrey"]
                 60 ["darkgrey" "lightpink" "lightgreen" "lightyellow" "lightblue" "magenta" "lightcyan" "white"]}]
    (merge {
            1 {:style {:font-weight "bold"}}
            2 {:style {:opacity 0.5}}
            4 {:style {:text-decoration "underline"}}
            8 {:style {:visibility "hidden"}}
            0 {:style {:font-weight "normal" :opacity 1 :text-decoration "none" :visibility "visible"}
               :class "default-colour default-background"}
            21 {:style {:font-weight "normal"}}
            22 {:style {:opacity 1}}
            24 {:style {:text-decoration "none"}}
            28 {:style {:visibility "visible"}}

            39 {:class "default-colour"}
            49 {:class "default-background"}
            }
           (convert-index-map colours colour 30)
           (convert-index-map colours background 40))))

(defn line-part-to-hiccup [part]
  (let [[match escape-code text] (re-find #"\u001B\[(\d*)m(.*)" part)
             number (if (empty? escape-code)
                      0
                      (js/parseInt escape-code))]
    (if match
      [:span (or (get ansi-codes number) {}) text]
      ; if no match for escape code, part must be all text
      [:span part])))

(defn get-hiccup-parts [line]
  (let [[first & rest] (str/split line #"(?=\u001B\[\d*m)")]
    (->> rest
         (cons first)
         (map line-part-to-hiccup))))

(defn escape-line [line]
  (-> line
      (str/replace "\t" "&nbsp;&nbsp;")
      (str/replace " " "&nbsp;")
      (gstring/unescapeEntities)
      (str/replace #"\u001B\[\d*m$" "")))

(defn line-to-hiccup [line]
  (->> (escape-line line)
       get-hiccup-parts
       reverse
       (reduce (fn [hiccup part] (conj part hiccup)))))

(defn console-output-line [line]
  [:p {:class "line"} (line-to-hiccup line)])

(defn console-output [output]
  [:div {:class "output"}
   (-> (vec output)
       (conj {:text ">_" :timestamp -1})
       (->> (map-indexed (fn [idx {:keys [text timestamp]}]
                           ^{:key (str timestamp "::" (if-not (str/blank? text) text idx))}
                           [console-output-line text]))))])

(defn scroll-to-bottom [elem]
  (set! (.-scrollTop elem) (.-scrollHeight elem)))

(defn console [client]
  (let [tailing (r/atom true)]
    (r/create-class
     {:render (fn [this]
                (let [{:keys [output client-id disconnected?]} (r/props this)]
                  [:div {:class "console"}
                   [:div {:class "card" :on-double-click #(do (.webkitExitFullscreen js/document))}
                    [:div {:class "output-container"
                           :on-scroll (fn [ev]
                                        (let [elem (.-currentTarget ev)]
                                          (reset! tailing (<= (.-scrollHeight elem) (+ (.-scrollTop elem) (.-offsetHeight elem))))))}
                     [console-output output]]
                    [:div {:class "card-secondary-content"}
                     [:div {:class "card-content"}
                      [:p {:class "card-title"}
                       [:span {:class "margin-horizontal"} client-id]
                       (connection-status (not disconnected?))]]
                     [:div {:class "card-action"}
                      (when disconnected?
                        [:button {:class "btn btn-flat"
                                  :on-click #(dispatch [:remove-console client-id])}
                         "Close"])
                      [:span {:class "fill"}]
                      (when (not @tailing)
                        [:i {:class "material-icons icon-action"
                             :on-click #(-> (r/dom-node this)
                                            (.querySelector ".output-container")
                                            scroll-to-bottom)}
                         "fast_forward"])
                      [:i {:class "material-icons icon-action full-screen"
                           :on-click #(-> (r/dom-node this)
                                          .webkitRequestFullscreen)}
                       "open_with"]]]]
                   ]))
      :component-did-update (fn [this]
                              (let [elem (-> (r/dom-node this)
                                             (.querySelector ".output-container"))]
                                (when @tailing (scroll-to-bottom elem))))})))
