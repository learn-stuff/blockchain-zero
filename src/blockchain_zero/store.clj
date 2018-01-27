(ns blockchain-zero.store
  (:require [taoensso.carmine :as car :refer (wcar)]))

(def server-conn nil)

(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(defn save [name value]
  (wcar* (car/set name value)))

(defn extract [name]
  (wcar* (car/get name)))

