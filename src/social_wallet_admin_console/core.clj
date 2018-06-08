(ns social-wallet-admin-console.core
  (:require [social-wallet-admin-console.dataset :refer :all]
            [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [freecoin-lib.app :as app]
            [freecoin-lib.core :as core]
            [freecoin-lib.db.wallet :as wallet]
            [just-auth.core :as auth]
            ;; TODO: shoul;d not have to access db level from outside the project
            [just-auth.db.just-auth :as auth-db]
            [incanter.core :as incanter]
            [gorilla-repl.table :refer [table-view]]
            [clojure.contrib.humanize :as h]
            [auxiliary.core :refer :all]
            [taoensso.timbre :as log]
            [clj-storage.core :as storage])
  (:gen-class))

(def emails (atom []))
(defonce ctx (atom {}))

;; TODO schema
(defn start [config]
  (log/merge-config! {:level (keyword (or (:log-level config) "debug"))
                      ;; #{:trace :debug :info :warn :error :fatal :report}

                      ;; Control log filtering by
                      ;; namespaces/patterns. Useful for turning off
                      ;; logging in noisy libraries, etc.:
                      :ns-whitelist  ["social-wallet-api.*"
                                      "freecoin-lib.*"
                                      "clj-storage.*"]
                      :ns-blacklist  ["org.eclipse.jetty.*"
                                      "org.mongodb.driver.*"]})
  (reset! ctx
   (let [fc-lib (app/start {})
         auth-lib {:auth (-> (:db fc-lib)
                             auth-db/create-auth-stores
                             (auth/new-stub-email-based-authentication emails))}]
     (conj fc-lib auth-lib))))

(defn stop []
  (when-not (nil? ctx)
    (app/disconnect-mongo ctx)
    (reset! ctx nil)))

(defn view-table
  "# Formats a dataset into an HTML table

Facilitate the view of a dataset (`arg1`) in the console"
  [data] {:pre [(incanter/dataset? data)]}

  (if (get-in data [:meta :human])
    (let [d (branch-on data
             participants? data

             transactions? (incanter/$ [:time-ago :from-id :to-id :quantity :tags] data)

             tags? data
             :else data)]
      (table-view (map vals (:rows d))
                  :columns (:column-names d)))

    (table-view (map vals (:rows data))
                :columns (:column-names data))))


(defn humanize
  "# Converts the values of a dataset to a form that is easily read by
  humans"
  [data] {:pre [(incanter/dataset? data)]}
  (branch-on data

             participants? data

             transactions?
             (incanter/with-data data
               (incanter/add-derived-column :time-ago [:timestamp] h/datetime)
               (incanter/add-derived-column :quantity [:amount] h/intword)
               (update-in incanter/$data [:meta] assoc :human true))

             tags? data

             :else data))

(defn create-participant
  "# Create a new participant in this social wallet

  `arg-1` map of information containing:
  {:name      name of participant
   :email     email of participant
   :password  password for account
   :2fa       second factor auth config
   :othername other names}"
  [{:keys [name email]}]
  (try
    (auth/sign-up (:auth @ctx) name email "xxx" {} "")
    (catch Exception e
      (print (str "ERROR: " (.getMessage e))))))

(defn create-wallet
  [{:keys [name email]}]
  (try
    (wallet/new-empty-wallet!
        (-> @ctx :backend :stores-m :wallet-store)
      (:backend @ctx) name email)
    (catch Exception e
      (print (str "ERROR: " (.getMessage e))))))

(defn create-transaction
  "# Create a new transaction between participants of this social wallet

  `arg-1` map of information containing:
  {:from    email of sender
   :amount  amount of units to send
   :to      email of recipient
   :tags    [array of tags]}"
  [{:keys [from amount to tags]}]
  (try
    ;; TODO: Test if participant exists
    (core/create-transaction
     (:backend @ctx) from
     amount to {:tags tags})
    (catch Exception e
      (print (str "ERROR: " (.getMessage e))))))

(defn list-participants
  "# List of participants in this social wallet

  `arg-1` *optional* map to select only rows containing value at
  column.  Example: `{:name 'bernard'}`

  `returns` a dataset ready for further transformations"
  ([] (list-participants {}))
  ([query] {:pre [(map? query)] :post [(incanter/dataset? %)]}
   (assoc
    (-> @ctx :auth 
        (auth/list-accounts {})
        (as-> accounts (map #(dissoc % :password :activated) accounts))
        incanter/to-dataset)
    :meta {:type :participants})))

(defn list-transactions
  "# List of transactions in this social wallet

  `arg-1` *optional* map to select only rows containing value at
  column.

  `returns` a dataset ready for further transformations"
  ([] (list-transactions {}))

  ([query] {:pre [(map? query)] :post [(incanter/dataset? %)]}

   (incanter/with-data (-> (:backend @ctx) (core/list-transactions query) incanter/to-dataset)
     (assoc
      (->> (incanter/add-derived-column :time-ago [:timestamp] h/datetime))
      :meta {:type :transactions}))))

(defn list-transactions-plain
  ([] (list-transactions-plain {}))
  ([query] {:pre [(map? query)] }
   (core/list-transactions (:backend @ctx) query)))

(defn counts-per-tag []
  )

(defn list-tags
  "# List of tags in this social wallet

  `arg-1` *optional* map to select only rows containing value at column.

  `returns` a dataset ready for further transformations"
  ([] (list-tags {}))
  ([query] {:pre [(map? query)]}
   (assoc
    (-> (:backend @ctx) (core/list-tags query) incanter/to-dataset)
    :meta {:type :tags})))

(defn empty-db
  "# Meant for administrative purposes and during development

  `arg-1` *optional* vector with specific db collections to be emptied

   `returns` a message of which collections were emptied"
  ([] (empty-db (merge (-> @ctx :backend :stores-m)
                       (-> @ctx :auth :account-activator (dissoc :emails))
                       (-> @ctx :auth :password-recoverer (dissoc :emails)))))
  ([collections]
   (dorun (map #(storage/delete-all! %) (vals collections)))
   (log/info "Emptied " (keys collections))))
