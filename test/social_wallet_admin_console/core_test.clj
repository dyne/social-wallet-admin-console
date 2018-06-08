(ns social-wallet-admin-console.core-test
  (:require [midje.sweet :refer :all]
            [social-wallet-admin-console.core :refer :all]
            [gorilla-repl.core :as gc]
            [taoensso.timbre :as log]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [hickory.core :as html]
            [clojure.string :as str]))

(against-background [(before :contents (gc/run-gorilla-server {:port 8990}))
                     (after :contents (gc/stop-gorilla-server))]
                    (facts "Check that the repl is accessible"
                           (:status @(http/get "http://127.0.0.1:8990/index.html")) => 200
                           (-> @(http/get "http://127.0.0.1:8990/gorilla-files")
                               :body
                               (json/read-str)
                               (get "files")
                               (as-> files (some #{"ws/query.clj"} files))) => truthy
                           (let [admin-worksheet @(http/get "http://127.0.0.1:60045/load?worksheet-filename=ws%2Fadmin.clj" :body html/parse html/as-hiccup)
                                 query-worksheet @(http/get "http://127.0.0.1:60045/load?worksheet-filename=ws%2Fquery.clj" :body html/parse html/as-hickory)]
                             (-> admin-worksheet json/read-str
                                 (get "worksheet-data")
                                 (str/includes?  "(empty-db)")) => true
                             (-> query-worksheet json/read-str
                                 (get "worksheet-data")
                                 (str/includes? "(-> (list-transactions {:account-id \"brenningham@pirates.net\"}) view-table)")) => true)))
