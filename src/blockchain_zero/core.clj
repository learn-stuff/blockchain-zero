(ns blockchain-zero.core
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json
             :refer [wrap-json-params
                     wrap-json-response]]
            [ring.util.response :refer [response]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [blockchain-zero.blockchain :as bc]
            [blockchain-zero.transactions :as txs]))

(defroutes handlers
  (GET "/last_blocks/:count{[0-9]+}" [count]
       (response (bc/take-last-blocks (Integer/parseInt count))))
  (POST "/management/add_transaction" request
        (if-let [transaction (:params request)]
          (do
            (txs/add-transaction transaction)
            {:status 200})
          {:status 422}))
  (route/not-found ""))

(def app
  (-> handlers
      wrap-keyword-params
      wrap-params
      wrap-json-response
      wrap-json-params))

(defn -main [& args]
  (run-server app {:port 8080})
  (bc/start)
  (println "Server started on port 8080"))
