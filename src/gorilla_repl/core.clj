;;;; This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
;;;;
;;;; gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.

(ns gorilla-repl.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [org.httpkit.server :as server]
            [gorilla-repl.nrepl :as nrepl]
            [gorilla-repl.websocket-relay :as ws-relay]
            [gorilla-repl.renderer :as renderer] ;; this is needed to bring the render implementations into scope
            [gorilla-repl.version :as version]
            [gorilla-repl.handle :as handle]
            [clojure.set :as set]
            [clojure.java.io :as io])
  (:gen-class))

(defonce server (atom nil))

;; the combined routes - we serve up everything in the "public" directory of resources under "/".
;; The REPL traffic is handled in the websocket-transport ns.
(defroutes app-routes
  (GET "/load" [] (handle/wrap-api-handler handle/load-worksheet))
  (POST "/save" [] (handle/wrap-api-handler handle/save))
  (GET "/gorilla-files" [] (handle/wrap-api-handler handle/gorilla-files))
  (GET "/config" [] (handle/wrap-api-handler handle/config))
  (GET "/repl" [] ws-relay/ring-handler)
  (route/resources "/" {:root "gorilla-repl-client"})
  (route/resources "/social-wallet-admin-console"  {:root "social-wallet-admin-console"})
  (route/files "/project-files" {:root "."}))


(defn run-gorilla-server
  [conf]
  ;; get configuration information from parameters
  (let [version (or (:version conf) "develop")
        webapp-requested-port (or (:port conf) 0)
        ip (or (:ip conf) "127.0.0.1")
        nrepl-requested-port (or (:nrepl-port conf) 0)  ;; auto-select port if none requested
        nrepl-port-file (io/file (or (:nrepl-port-file conf) ".nrepl-port"))
        gorilla-port-file (io/file (or (:gorilla-port-file conf) ".gorilla-port"))
        project (or (:project conf) "no project")
        keymap (or (:keymap (:gorilla-options conf)) {})
        _ (handle/update-excludes (fn [x] (set/union x (:load-scan-exclude (:gorilla-options conf)))))]
    ;; app startup
    (println "Gorilla-REPL:" version)
    ;; build config information for client
    (handle/set-config :project project)
    (handle/set-config :keymap keymap)
    ;; check for updates
    ;; (version/check-for-update version)  ;; runs asynchronously
    ;; first startup nREPL
    (nrepl/start-and-connect nrepl-requested-port nrepl-port-file)
    ;; and then the webserver
    (let [s (server/run-server #'app-routes {:port webapp-requested-port :join? false :ip ip :max-body 500000000})
          _ (reset! server s)
          webapp-port (:local-port (meta s))]
      (spit (doto gorilla-port-file .deleteOnExit) webapp-port)
      (println (str "Running at http://" ip ":" webapp-port "/index.html"))
      ;; (if (not (.exists (io/as-file "id_rsa"))) ;; TODO: hardcoded key, may use the one in ~/.ssh
      ;;   (let [kp (ssh/generate-key-pair)]
      ;;     (ssh/write-key-pair kp "id_rsa")))
      ;; (println (str "SSH Public key: " (slurp "id_rsa.pub")))
      (println "Ctrl+C to exit."))))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn -main
  [& args]
  (run-gorilla-server {:port 8990}))
