(ns blockchain-zero.transactions
  (:require [blockchain-zero.blockchain :as bc]))

(def transactions (atom []))

(def buffer-size 5)

(defn add-transaction
  [value]
  (swap! transactions conj value)
  (if (= buffer-size (count @transactions))
    (do
      (bc/add-transactions @transactions)
      (reset! transactions []))))
