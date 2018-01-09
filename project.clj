(defproject org.dyne/social-wallet-admin-console "0.1.0-SNAPSHOT"
  :description "Interactive admin console (REPL) for the social wallet"
  :url "https://freecoin.dyne.org"

  :license {:author "Dyne.org Foundation"
            :email "foundation@dyne.org"
            :year 2017
            :key "gpl-3.0"}

  :dependencies ^:replace [[org.clojure/clojure "1.8.0"]
                           [org.clojure/data.json "0.2.6"]
                           [org.clojure/data.csv "0.1.4"]
                           [clj-http "3.6.1"]
                           [cheshire "5.7.1"]
                           [clojure-humanize "0.2.2"]

                           ;; freecoin deps
                           [org.clojars.dyne/freecoin-lib "0.3.0-SNAPSHOT"]
                           [org.clojars.dyne/auxiliary "0.1.0"]
                           ;; [org.clojars.dyne/clj-openssh-keygen "0.1.0"]

                           ;; logging done right with slf4j
                           [com.taoensso/timbre "4.10.0"]
                           [com.fzakaria/slf4j-timbre "0.3.7"]
                           [org.slf4j/slf4j-api "1.7.25"]
                           [org.slf4j/log4j-over-slf4j "1.7.25"]
                           [org.slf4j/jul-to-slf4j "1.7.25"]
                           [org.slf4j/jcl-over-slf4j "1.7.25"]

                           ;; graphical visualization
                           [incanter/incanter-core "1.5.7" :upgrade :incanter]

                           ;; gorilla-repl deps
                           [http-kit "2.2.0"]
                           [compojure "1.6.0"]
                           [ring/ring-json "0.4.0"]
                           ;; [ch.qos.logback/logback-classic "1.2.3"]
                           [gorilla-renderable "2.0.0"]
                           [gorilla-plot "0.1.4"]
                           [javax.servlet/servlet-api "2.5"]
                           [grimradical/clj-semver "0.3.0" :exclusions [org.clojure/clojure]]
                           [cider/cider-nrepl "0.15.0"]
                           [org.clojure/tools.nrepl "0.2.13"]]

  :pedantic? :warn

  :jvm-opts ["-Djava.security.egd=file:/dev/random"
             ;; use a proper random source (install haveged)

             "-XX:-OmitStackTraceInFastThrow"
             ;; prevent JVM exceptions without stack trace
             ]

  :env [[:base-url "http://localhost:8990"]
        [:email-config "email-conf.edn"]
        [:secure "false"]
        [:admin-email "sender@mail.com"]
        [:ttl-password-recovery "1800"]]

  :source-paths ["src"]
  :resource-paths ["resources"]
  :template-additions ["ws/index.clj"]
  :main ^:skip-aot gorilla-repl.core
  :profiles {:dev {:plugins [[lein-marginalia "0.9.0"]]}
             :uberjar {:aot [gorilla-repl.core social-wallet-admin-console.core]}}
  :target-path "target/%s"
  )
