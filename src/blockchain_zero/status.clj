(ns blockchain-zero.status
  (:require [blockchain-zero.blockchain :as bc]
            [blockchain-zero.neighbors :as neighbors]))

(def id 93)
(def node-name "ROFLMAO")
(def ip "http://192.168.44.93:8080")

(defn get-status []
  {:id id
   :name node-name
   :last_hash @bc/last-hash
   :ip ip
   :neighbors (keys @neighbors/neighbors)})
