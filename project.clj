(defproject com.breezeehr/datomic-stackdriver-reporter "0.1.0"
  :description "A tiny clojure library that reports datomic metrics to statsd."
  :url "http://github.com/appcanary/datomic-statsd-reporter"
  :license {:name "Apache License Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[com.google.cloud/google-cloud-monitoring "1.90.0"]]
  :profiles
  {:provided
         {:dependencies
          [[org.clojure/clojure "1.10.1"]
           #_[javax.xml.bind/jaxb-api "2.3.1"]
           #_[com.sun.xml.bind/jaxb-core "2.3.0"]
           #_[com.sun.xml.bind/jaxb-impl "2.3.1"]
           ]}
   :test {:plugins [[lein-test-out "0.3.1"]]}})
