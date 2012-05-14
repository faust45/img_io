(ns img_io.core
  (:gen-class)
  (:require [ring.util.response :as resp]
            [pantomime.media :as mt]
            [img_io.db :as db]
            [clojure.java.io :as io]
            [compojure.handler :as handler])
  (:import java.util.UUID)
  (:use ring.adapter.jetty
        img_io.reload
        cheshire.core
        pantomime.mime
        [compojure.core]
        ring.middleware.multipart-params
        ring.middleware.params))
  
(def ^:const limit-size 10000000)

(defn board []
  (resp/file-response "board.html" {:root "public"}))

(defn get-img
  [args]
  (let [[id & params] args]
    (if (empty? params)
      (db/get-original id)
      (or (apply db/get-cached args)
          (apply db/process args)))))

(defn serv-img
  [& args]
  (if-let [file (get-img (remove nil? args))]
    (resp/response file)
    (resp/not-found "Sorry")))

(defn img-content?
  [file]
  (mt/image? (mime-type-of file)))


(defn upload-file
  [{:keys [tempfile size]}]
  (if (and (< size limit-size) (img-content? tempfile))
    (let [id (db/save tempfile)] 
      (resp/response id))
    (board)))

(defn list-imgs
  []
  (let [names (map #(.getName %) (.listFiles (io/file "uploads/")))]
    (resp/response (generate-string names))))

(defroutes public-routes
   (GET "/" [] (board))
   (GET "/public/:path" [path] (resp/file-response path {:root "public/"}))
   (GET "/all" [] (list-imgs))
   (GET "/img/:id" [id height width is-rounded] (serv-img id height width is-rounded))
   (POST "/upload" [file] (upload-file file)))

(def app
  (-> (handler/site public-routes)))
      ;(wrap-reload 'img_io.core)))
    
(defn -main [& args]
  (run-jetty #'app {:port 8080}))


