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
            [clojure.contrib.humanize :as h]
            [auxiliary.core :refer :all])
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

(defn get-type      [data] (get-in data [:meta :type]))

(defn participants? [attr] (= attr :participants))
(defn transactions? [attr] (= attr :transactions))
(defn tags?         [attr] (= attr :tags))

(defn view-table
  "# Formats a dataset into an HTML table

Facilitate the view of a dataset (`arg1`) in the console"
  [data] {:pre [(dataset? data)]}

  (if (get-in data [:meta :human])
    (let [d (branch-on
             (get-type data)
             participants? data

             transactions? ($ [:time-ago :from-id :to-id :quantity :tags] data)

             tags? data
             :else data)]
      (table-view (map vals (:rows d))
                  :columns (:column-names d)))

    (table-view (map vals (:rows data))
                :columns (:column-names data))))


(defn humanize
  "# Converts the values of a dataset to a form that is easily read by
  humans"
  [data] {:pre [(dataset? data)]}
  (branch-on (get-type data)

             participants? data

             transactions?
             (with-data data
               (add-derived-column :time-ago [:timestamp] h/datetime)
               (add-derived-column :quantity [:amount] h/intword)
               (update-in $data [:meta] assoc :human true))

             tags? data

             :else data))

(defn list-participants
  "# List of participants in this social wallet

  `arg-1` *optional* map to select only rows containing value at
  column.  Example: `{:name 'bernard'}`

  `returns` a dataset ready for further transformations"
  ([] (list-participants {}))
  ([query] {:pre [(map? query)] :post [(dataset? %)]}
   (assoc
    (-> (get-wallet) (wallet/query query) to-dataset)
    :meta {:type :participants})))

(defn list-transactions
  "# List of transactions in this social wallet

  `arg-1` *optional* map to select only rows containing value at
  column.

  `returns` a dataset ready for further transformations"
  ([] (list-transactions {}))
  ([query] {:pre [(map? query)] :post [(dataset? %)]}
   (with-data (-> (:backend @ctx) (core/list-transactions query) to-dataset)
     (assoc
      (->> (add-derived-column :time-ago [:timestamp] h/datetime)
           (add-derived-column :quantity [:amount] h/intword))
      :meta {:type :transactions}))))

(defn list-tags
  "# List of tags in this social wallet

  `arg-1` *optional* map to select only rows containing value at column.

  `returns` a dataset ready for further transformations"
  ([] (list-tags {}))
  ([query] {:pre [(map? query)]}
   (assoc
    (-> (:backend @ctx) (core/list-tags query) to-dataset)
    :meta {:type :tags})))
