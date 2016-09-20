(ns phonsole-client.macros.config
  (:require [phonsole-client.macros.env-vars :refer [env-var]]))

(def defaults
  {:server-url "localhost:8080"})


(defmacro config [key]
  (let [value (or (env-var key)
                  (defaults key))]
    (if (nil? value)
      (throw (Exception. (str "No config var found for key" (name key))))
      value)))
