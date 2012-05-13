(ns img_io.core
  (:gen-class)
  (:require [ring.util.response :as resp]
            [img_io.db :as db]
            [clojure.java.io :as io]
            [compojure.handler :as handler])
  (:import java.util.UUID)
  (:use ring.adapter.jetty
        img_io.reload
        [compojure.core]
        ring.middleware.multipart-params
        ring.middleware.params))
  

(defn handler [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World from Ring"})

(defn board []
  (resp/file-response "board.html" {:root "public"}))

(defn get-img
  [args]
  (or (apply db/get-cached args)
      (apply db/process args)))

(defn serv-img
  [& args]
  (if-let [file (get-img (remove nil? args))]
    (resp/response file)
    (resp/not-found "Sorry")))

(defn upload-file
  [file]
  (let [id (db/save file)] 
    (resp/response id)))

(defroutes public-routes
   (GET "/" [] (board))
   (GET "/img/:id" [id height width is-rounded] (serv-img id height width is-rounded))
   (POST "/upload" [file] (upload-file file)))

(def app
  (-> (handler/site public-routes)
      ))
    
(defn -main [& args]
  (run-jetty #'app {:port 8080}))


