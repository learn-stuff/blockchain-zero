(ns blockchain-zero.blockchain
  (:require [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [digest]))

(defrecord Block [previous_block_hash rows timestamp block_hash])

(def blockchain (atom {}))
(def last-hash (atom "0"))

(defn make-block
 [previous-block-hash rows]
  (let [timestamp (tc/to-long (time/now))
        rows-string (apply str rows)
        digest-sum (str previous-block-hash rows-string timestamp)
        block-hash (digest/sha-256 digest-sum)]
    (->Block previous-block-hash rows timestamp block-hash)))

(defn add-data-to-blockchain
  [blockchain rows]
  (let [new-block (make-block @last-hash rows)
        new-block-hash (:block_hash new-block)]
    (swap! blockchain assoc new-block-hash new-block)
    (reset! last-hash new-block-hash)))

(def add-data (partial add-data-to-blockchain blockchain))

(defn take-last-blocks
  [n]
  (loop [blocks []
         count n
         hash @last-hash]
    (let [next-block (get @blockchain hash)]
      (if (or (= count 0) (= hash "0"))
        (reverse blocks)
        (recur (conj blocks next-block)
               (dec count)
               (:previous_block_hash next-block))))))
