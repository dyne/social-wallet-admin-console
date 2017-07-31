(ns social-wallet-admin-console.core
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [clojure.string :as string]
            [clojure.walk :refer :all]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [freecoin-lib.app :as app]
            [freecoin-lib.core :as core]
            [freecoin-lib.db.wallet :as wallet]
            [freecoin-lib.db.storage :as storage]
            [incanter.core :refer :all]
            [gorilla-repl.table :refer :all]
            [clojure.contrib.humanize :refer :all]
            )
  (:gen-class)
  )

(def ctx (atom (app/start {})))


(defn get-wallet []
  (storage/get-wallet-store (get-in @ctx [:backend :stores-m])))
(defn get-confirmations []
  (storage/get-confirmation-store (get-in @ctx [:backend :stores])))
;; (defn get-transactions []
;;   (storage/get-transaction-store (get-in @ctx [:backend :stores])))
;; (defn get-tags []
;;   (storage/get-tag-store (get-in @ctx [:backend :stores])))


(defn view-table
  "# Formats a dataset into an HTML table

Facilitate the view of a dataset (`arg1`) in the console"
  [data]
  (table-view (map vals (:rows data))
              :columns (:column-names data)))

(defn list-participants
  "# List of participants in this social wallet

  `arg-1` *optional* map to select only rows containing value at
  column.  Example: `{:name 'bernard'}`

  `returns` a dataset ready for further transformations"
  ([] (list-participants {}))
  ([query] (-> (get-wallet) (wallet/query query) to-dataset)))

(defn list-transactions
  "# List of transactions in this social wallet

  `arg-1` *optional* map to select only rows containing value at
  column.

  `returns` a dataset ready for further transformations"
  ([] (list-transactions {}))
  ([query] (-> (:backend @ctx) (core/list-transactions query) to-dataset)))

(defn list-tags
  "# List of tags in this social wallet

  `arg-1` *optional* map to select only rows containing value at column.

  `returns` a dataset ready for further transformations" 
  ([] (list-tags {}))
  ([query] (-> (:backend @ctx) (core/list-tags query) to-dataset)))

  
