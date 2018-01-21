(ns blockchain-zero.core
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json
             :refer [wrap-json-params
                     wrap-json-response]]
            [ring.util.response :refer [response]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [blockchain-zero.blockchain :as bc]
            [blockchain-zero.data :as bc-data]))

(defroutes handlers
  (GET "/last_blocks/:number{[0-9]+}" [number]
       (response (bc/take-last-blocks (Integer/parseInt number))))
  (POST "/add_data" request
        (if-let [data (get-in request [:params "data"])]
          (do
            (bc-data/add-data data)
            {:status 200})
          {:status 422}))
  (route/not-found ""))

(def app
  (-> handlers
      wrap-params
      wrap-json-response
      wrap-json-params))

(defn -main [& args]
  (run-server app {:port 8080})
  (println "Server started on port 8080"))
