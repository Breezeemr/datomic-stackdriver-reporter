(defproject com.breezeehr/datomic-stackdriver-reporter "0.1.0"
  :description "A tiny clojure library that reports datomic metrics to statsd."
  :url "http://github.com/appcanary/datomic-statsd-reporter"
  :license {:name "Apache License Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[com.google.cloud/google-cloud-monitoring "2.2.1" :exclusions [com.google.guava/guava]]
                 [com.google.cloud/google-cloud-core "1.94.8" :exclusions [com.google.guava/guava]]]
  :profiles
  {:provided
   {:dependencies
    [[org.clojure/clojure "1.10.1"]
     [com.google.guava/guava "30.1-jre"]
     #_[javax.xml.bind/jaxb-api "2.3.1"]
     #_[com.sun.xml.bind/jaxb-core "2.3.0"]
     #_[com.sun.xml.bind/jaxb-impl "2.3.1"]
     ]}
   :uberjar
   {:uberjar-name "stackdriver-reporter.jar"}})
