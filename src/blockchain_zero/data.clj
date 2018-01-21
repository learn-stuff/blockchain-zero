(ns blockchain-zero.data
  (:require [blockchain-zero.blockchain :as bc]))

(def data (atom []))

(def buffer-size 5)

(defn add-data
  [value]
  (swap! data conj value)
  (if (== buffer-size (count @data))
    (do
      (bc/add-data @data)
      (reset! data []))))
