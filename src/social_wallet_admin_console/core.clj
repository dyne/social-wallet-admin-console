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
            [gorilla-repl.table :refer :all]
            [clojure.contrib.humanize :refer :all]
            )
  (:gen-class)
  )

(def ctx (atom (app/start {})))

(defn view-table [dati]
  (table-view (map vals dati)
              :columns (some keys dati)))


(defn get-wallet []
  (storage/get-wallet-store (get-in @ctx [:backend :stores-m])))
(defn get-confirmations []
  (storage/get-confirmation-store (get-in @ctx [:backend :stores])))
;; (defn get-transactions []
;;   (storage/get-transaction-store (get-in @ctx [:backend :stores])))
;; (defn get-tags []
;;   (storage/get-tag-store (get-in @ctx [:backend :stores])))

(defn list-participants
  "list of participants in this wallet"
  ([] (list-participants {}))
  ([query] (-> (get-wallet) (wallet/query query) view-table)))

(defn list-transactions
  "list of transactions"
  ([] (list-transactions {}))
  ([query] (-> (:backend @ctx) (core/list-transactions query) view-table)))

(defn list-tags
  "list of tags"
  ([] (list-tags {}))
  ([query] (-> (:backend @ctx) (core/list-tags query) view-table)))

  
