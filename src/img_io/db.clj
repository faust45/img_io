(ns img_io.db
  (:require [clojure.java.io :as io] [clojure.string :as s])
  (:import java.util.UUID javax.imageio.ImageIO java.awt.Image java.awt.image.BufferedImage))

(defn get-buff-img
  [img]
  (let [w  (.getWidth img) h (.getWidth img)
        bi (BufferedImage. w h BufferedImage/TYPE_INT_RGB)
        graphics (.getGraphics bi)]
    (.drawImage graphics img 0 0 nil)
    (.dispose graphics)
    (println "in get buf")
    (println w h)
    bi))

(defn resize
  [file new-file height width]
  (let [img (ImageIO/read file)
        new-img (.getScaledInstance img height width Image/SCALE_DEFAULT)]
    (ImageIO/write (get-buff-img new-img) "png" new-file))
  new-file)

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
        h (read-string (or height "200"))
        w (read-string (or width "200"))
        file (io/file (str "uploads/" id))]
    (if (.exists file)
      (resize file (cached-file params) h w)
      nil)))

