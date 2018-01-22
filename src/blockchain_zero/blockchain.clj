(ns blockchain-zero.blockchain
  (:require [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [digest]))

(defrecord Block [previous_block_hash
                  rows
                  timestamp
                  block_hash])

(def blockchain (atom []))

(defn make-block
 [previous-block-hash rows]
  (let [timestamp (tc/to-long (time/now))
        rows-string (apply str rows)
        digest-sum (str previous-block-hash rows-string timestamp)
        block-hash (digest/sha-256 digest-sum)]
    (->Block previous-block-hash rows timestamp block-hash)))

(defn previous-block-hash
  [blockchain]
  (let [chain @blockchain
        first-block? (zero? (count chain))]
    (if first-block? "0" (:block_hash (last chain)))))

(defn add-data-to-blockchain
  [blockchain rows]
  (let [previous-hash (previous-block-hash blockchain)]
    (swap! blockchain conj (make-block previous-hash rows))))

(def add-data (partial add-data-to-blockchain blockchain))

(defn take-last-blocks
  [n]
  (take-last n @blockchain))
