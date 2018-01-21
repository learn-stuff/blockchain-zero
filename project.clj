(defproject blockchain-zero "0.1.0-SNAPSHOT"
  :description "Simple blockchain concept"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :key "mit"
            :year 2015}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.0"]
                 [ring/ring-json "0.4.0"]
                 [digest "1.4.6"]
                 [clj-time "0.14.0"]
                 [http-kit "2.2.0"]]
  :main blockchain-zero.core)
