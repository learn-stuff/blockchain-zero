(ns blockchain-zero.blockchain
  (:require [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [digest]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [blockchain-zero.store :as store]
            [blockchain-zero.neighbors :as neighbors]))

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

(defn vec->blockchain [value]
  (reduce (fn [acc v] (assoc acc (:hash v) v))
          {}
          value))

(defn make-block
  [prev-hash txs]
  (let [ts (tc/to-long (time/now))
        txs-string (apply str (map tx->str txs))
        digest-sum (str prev-hash txs-string ts)
        block-hash (digest/sha-256 digest-sum)]
    (->Block block-hash prev-hash ts txs)))

(defn add-block-to-blockchain
  [block-hash block]
  (println (json/generate-string block))
  (swap! blockchain assoc block-hash block)
  (reset! last-hash block-hash)
  (doseq [ngbr (vals @neighbors/neighbors)]
    (let [url (:url ngbr)]
      (try
        (client/post
          (str url "/blockchain/receive_update")
          {:body (json/generate-string {:sender_id 93 :block block})
           :conn-timeout 1000})
        (catch Exception e (str "caught exception: " (.getMessage e)))))))

(defn add-transactions
  [txs]
  (let [new-block (make-block @last-hash txs)
        new-block-hash (:hash new-block)]
    (add-block-to-blockchain new-block-hash new-block)))

(defn take-last-blocks
  [n]
  (loop [blocks []
         count n
         hash @last-hash]
    (let [next-block (get @blockchain hash)]
      (if (or (= count 0) (= hash "0") (= hash 0))
        (reverse blocks)
        (recur (conj blocks next-block)
               (dec count)
               (:prev_hash next-block))))))

(defn take-all-blocks []
  (vals @blockchain))

(defn receive-update
  [{sender-id :sender_id block :block}]
  (cond
    (= @last-hash (:prev_hash block))
    (add-block-to-blockchain (:hash block) block)))

(defn sync-with-neighbors []
  (if-let [ngbr (first (vals @neighbors/neighbors))]
    (let [url (:url ngbr)
          response (client/get (str url "/management/sync"), {:as :json})
          chain (:body response)]
      (reset! blockchain (vec->blockchain chain))
      (reset! last-hash (:hash (last chain))))))
