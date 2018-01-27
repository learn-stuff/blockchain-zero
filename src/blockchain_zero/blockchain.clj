(ns blockchain-zero.blockchain
  (:require [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [digest]
            [blockchain-zero.store :as store]))

(defrecord Block [hash prev_hash ts tx])

(def blockchain (atom {}))
(def last-hash (atom "0"))

(defn start []
  (add-watch blockchain :blockchain-watcher
    (fn [_ _ _ new-state]
      (store/save "blockchain/chain" new-state)))

  (add-watch last-hash :last-hash-watcher
             (fn [_ _ _ new-state]
               (store/save "blockchain/hash" new-state)))

  (if-let [chain (store/extract "blockchain/chain")]
    (reset! blockchain chain))

  (if-let [hash (store/extract "blockchain/hash")]
    (reset! last-hash hash)))

(defn tx->str [value]
  (apply str (map value [:from :to :amount])))

(defn make-block
  [prev-hash txs]
  (let [ts (tc/to-long (time/now))
        txs-string (apply str (map tx->str txs))
        digest-sum (str prev-hash txs-string ts)
        block-hash (digest/sha-256 digest-sum)]
    (->Block block-hash prev-hash ts txs)))

(defn add-data-to-blockchain
  [blockchain txs]
  (let [new-block (make-block @last-hash txs)
        new-block-hash (:hash new-block)]
    (swap! blockchain assoc new-block-hash new-block)
    (reset! last-hash new-block-hash)))

(defn add-transactions-to-blockchain
  [blockchain txs]
  (let [new-block (make-block @last-hash txs)
        new-block-hash (:hash new-block)]
    (swap! blockchain assoc new-block-hash new-block)
    (reset! last-hash new-block-hash)))

(def add-data (partial add-data-to-blockchain blockchain))
(def add-transactions (partial add-transactions-to-blockchain blockchain))

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
               (:prev_hash next-block))))))
