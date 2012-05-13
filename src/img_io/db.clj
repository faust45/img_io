(ns img_io.db
  (:require [clojure.java.io :as io] [clojure.string :as s])
  (:import java.util.UUID javax.imageio.ImageIO java.awt.Image))


(defn resize
  [file new-file height width]
  (let [img (ImageIO/read file)
        new-img (.getScaledInstance img 300 255 Image/SCALE_DEFAULT)]
    (println (class new-file))
    ;(ImageIO/write new-img "png" new-file)
    (println "in resize")))

(defn save
  [file]
  (let [id (str (UUID/randomUUID))]
    (io/copy (file :tempfile) (io/file (str "uploads/" id)))
    id))

(defn cached-file
  [params]
  (io/file (str "cache/" (s/join '-' params))))

(defn get-cached
  [& params]
  (let [file (cached-file params)]
    (if (.exists file)
      file
      (println "Cache fail"))))

(defn process
  [& params]
  (let [[id height width] params
        file (io/file (str "uploads/" id))]
    (if (.exists file)
      (resize file (cached-file params) height width)
      nil)))

