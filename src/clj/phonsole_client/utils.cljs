(ns phonsole-client.utils)

(defn update-keys [f m]
  (->> (map (fn [[k v]] [k (f v k)]) m)
       (into {})))
