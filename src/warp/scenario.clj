(ns warp.scenario
  (:require [clojure.string :as str]
            [warp.watcher   :as watcher]))

(defn ensure-sequential
  [x]
  (if (or (nil? x) (sequential? x)) x [x]))

(defn interpol
  ([input args not-found]
   (when (string? input)
     (let [clean   #(str/replace % #"(\{\{|\}\})" "")
           args    (assoc (into {} (map-indexed #(vector (str %1) %2)
                                                (ensure-sequential args)))
                          "*" (str/join " " args))
           extract (fn [k] (or (get args (clean k))
                               (if (fn? not-found)
                                 (not-found k)
                                 not-found)))]
       (str/replace input #"\{\{[0-9*]+\}\}" extract))))
  ([input args]
   (interpol input args "")))

(defn prepare-command
  [args {:keys [literal type] :as cmd}]
  (if (nil? cmd)
    (throw (ex-info "invalid command for scenario" {}))
    (case type
      :ping    cmd
      :sleep   cmd
      :service (if literal
                 cmd
                 (-> cmd
                     (update :service interpol args "NOSERVICE")
                     (update :action interpol args "status")))
      :shell   (if literal
                 cmd
                 (-> cmd
                     (update :shell interpol args)
                     (update :cwd interpol args)))
      (throw (ex-info "invalid command type" {:command cmd})))))

(defn prepare-matcher
  [args {:keys [type] :as matcher}]
  (if (nil? matcher)
    {:type "none"}
    (case type
      :all  matcher
      :none matcher
      :host (update matcher :host (partial interpol args))
      :fact (update matcher :value (partial interpol args))
      :not  (update matcher :clause (partial prepare-matcher args))
      :or   (update matcher :clauses (mapv (partial prepare-matcher args)))
      :and  (update matcher :clauses (mapv (partial prepare-matcher args)))
      (throw (ex-info "invalid matcher" {:matcher matcher :status 400})))))

(defn prepare-raw
  [raw profile matchargs args]
  (let [profiles (:profiles raw)
        profile  (when profile (or (get profiles (keyword profile))
                                   (throw (ex-info "invalid profile" {}))))
        scenario (merge raw profile)]
    (-> scenario
        (update :matcher  (partial prepare-matcher matchargs))
        (update :commands (partial mapv (partial prepare-command args))))))

(defn fetch
  [{:keys [watcher]} id {:keys [profile matchargs args]}]
  (prepare-raw (watcher/by-id watcher id) profile matchargs args))
