(ns com.breezeehr.datomic.reporter.stackdriver
  (:import (com.google.cloud.monitoring.v3 MetricServiceClient)
           (com.google.monitoring.v3 ProjectName TimeInterval TypedValue Point TimeSeries TimeSeries$Builder CreateTimeSeriesRequest)
           (com.google.api MonitoredResource Metric MetricDescriptor)
           (com.google.protobuf.util Timestamps)
           (com.google.cloud ServiceOptions)))


(defn make-metric-descriptor [prefix t labels]
  (-> (Metric/newBuilder)
      (.setType (str "custom.googleapis.com/datomic/" prefix "/" t))
      (.putAllLabels labels)
      (.build)))

(defn make-gke-resource  [project_id]
  (-> (MonitoredResource/newBuilder)
      (.setType "k8s_container")
      (.putAllLabels {"project_id",
                      project_id
                      #_"cluster_name"
                      #_(System/getenv "CLUSTER_NAME")
                      "namespace_name"
                      (System/getenv "POD_NAMESPACE")
                      "pod_name"
                      (System/getenv "HOSTNAME")
                      "container_name"
                      (System/getenv "CONTAINER_NAME")})
      (.build)))

(defn make-monitored-resource [project_id]
  (-> (MonitoredResource/newBuilder)
      (.setType "global")
      (.putAllLabels {"project_id",
                      project_id})
      (.build)))

(defn typed-val [val]
  (-> (TypedValue/newBuilder)
      (.setDoubleValue val)
      (.build)))

(defn point [interval value]
  (-> (Point/newBuilder)
      (.setInterval interval)
      (.setValue value)
      (.build)))

(defn time-series [metric resource point]
  (-> (TimeSeries/newBuilder)
      (.setMetric metric)
      ^TimeSeries$Builder (.setResource resource)
      (.addPoints point)
      (.build))
  )

(defn report-datomic-metrics-to-stackdriver [prefix metrics]
  (let [mclient (MetricServiceClient/create)
        project-id
                (ServiceOptions/getDefaultProjectId)
        project-name (ProjectName/of project-id)
        res     (make-gke-resource project-id)
        interval (-> (TimeInterval/newBuilder)
                     (.setEndTime (Timestamps/fromMillis (System/currentTimeMillis)))
                     (.build))]
    (try
      (-> (CreateTimeSeriesRequest/newBuilder)
          (.setName (str project-name))
          (.addAllTimeSeries
            (->> metrics
                 (reduce
                   (fn [acc [metric-name value]]
                     (if (map? value)
                       (reduce-kv
                         (fn [inner-acc sub-metric-name sub-metric-value]
                           (conj! inner-acc
                             (time-series
                               (make-metric-descriptor
                                 prefix (str (name metric-name) "-" (name sub-metric-name)) {})
                               res
                               (point interval (typed-val sub-metric-value)))))
                         acc
                         value)
                       (conj! acc
                         (time-series
                           (make-metric-descriptor
                             prefix (name metric-name) {})
                           res
                           (point interval (typed-val value))))))
                   (transient []))
                 (persistent!)))
          (.build)
          (->>
            (.createTimeSeries mclient)))
      (finally (.close mclient)))))

(defn transactor [metrics]
  (report-datomic-metrics-to-stackdriver "transactor" metrics)
  )

(defn peer [metrics]
  (report-datomic-metrics-to-stackdriver "peer" metrics))


(defn delete-metrics []
  (let [mclient (MetricServiceClient/create)
        project-id
                (ServiceOptions/getDefaultProjectId)
        project-name (ProjectName/of project-id)]

    (try
      (run!
        (fn [^MetricDescriptor x]
          (prn (.deleteMetricDescriptor mclient (.getName x)))

          )
        (into []
          (filter (fn [x] (.startsWith (.getType x) "custom.googleapis.com/datomic")))
          (.iterateAll
            (.listMetricDescriptors
              mclient
              project-name))))
      (finally (.close mclient)))))
