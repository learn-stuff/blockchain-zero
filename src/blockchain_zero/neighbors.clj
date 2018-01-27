(ns blockchain-zero.neighbors
  (:require [blockchain-zero.store :as store]))

;; TODO: neighbor as record

(def neighbors (atom {}))

(defn start []
  (add-watch neighbors :neighbors-watcher
    (fn [_ _ _ new-state]
      (store/save "blockchain/neighbors" new-state)))

  (if-let [nbrs (store/extract "blockchain/neighbors")]
    (reset! neighbors nbrs)))

(defn add-neighbor
  [value]
  (let [id (:id value)]
    (when-not (get @neighbors id)
      (swap! neighbors assoc id value))))
