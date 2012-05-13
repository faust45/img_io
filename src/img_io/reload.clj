(ns img_io.reload
  )

(defn wrap-reload
  [handler & [nspace]]
  (fn [request]
     (require nspace :reload-all)
     (handler request)))
