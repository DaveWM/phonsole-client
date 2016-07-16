(ns phonsole-client.macros.register-sub-functions)

(defmacro register-subs [& sub-funcs]
  `(do
     ~@(map (fn [sub] `(re-frame.core/register-sub ~(keyword (name sub)) ~sub)) sub-funcs)))

(defmacro register-handlers [& handler-funcs]
  `(do
     ~@(map (fn [handler] `(re-frame.core/register-handler ~@(if (vector? handler)
                                                               `(~(keyword (name (first handler)) (name (second handler))) ~(second handler))
                                                               `(~(keyword (name handler)) ~handler))))
            handler-funcs)))
