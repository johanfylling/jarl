(ns jarl.parser
  (:require [clojure.data.json :as json])
  (:require [clojure.tools.logging :as log])
  (:require [clojure.string :as str])
  (:require [jarl.builtins.registry :as builtins])
  (:require [jarl.eval :as eval]))

(declare make-block)
(declare make-blocks)
(declare make-stmts)

(defn make-ArrayAppendStmt [{:strs [array value]}]
  (log/debug "making ArrayAppendStmt stmt")
  (fn [state]
    (eval/eval-ArrayAppendStmt array value state)))

(defn make-AssignIntStmt [{:strs [value target]}]
  (log/debug "making AssignIntStmt stmt")
  (fn [state]
    (eval/eval-AssignIntStmt value target state)))

(defn make-AssignVarStmt [{:strs [target source]}]
  (log/debug "making AssignVarStmt stmt")
  (fn [state]
    (eval/eval-AssignVarStmt source target state)))

(defn make-AssignVarOnceStmt [{:strs [target source]}]
  (log/debug "making AssignVarOnceStmt stmt")
  (fn [state]
    (eval/eval-AssignVarOnceStmt source target state)))

(defn make-BlockStmt [{:strs [blocks] :as stmt-info}]
  (log/debug "making BlockStmt stmt")
  (log/tracef "info: %s" stmt-info)
  (fn [state]
    (eval/eval-BlockStmt (make-blocks blocks) state)))

(defn make-BreakStmt [{:strs [index]}]
  (log/debug "making BreakStmt stmt")
  (fn [state]
    (eval/eval-BreakStmt index state)))

(defn make-CallDynamicStmt [{:strs [result path args]}]
  (log/debug "making CallDynamicStmt stmt")
  (fn [state]
    (eval/eval-CallDynamicStmt result path args state)))

(defn make-CallStmt [{:strs [result func args]}]
  (log/debug "making CallStmt stmt")
  (fn [state]
    (eval/eval-CallStmt result func args state)))

(defn make-DotStmt [{:strs [target source key]}]
  (log/debug "making DotStmt stmt")
  (fn [state]
    (eval/eval-DotStmt source key target state)))

(defn make-EqualStmt [{:strs [a b]}]
  (log/debug "making EqualStmt stmt")
  (fn [state]
    (eval/eval-EqualStmt a b state)))

(defn make-IsArrayStmt [{:strs [source]}]
  (log/debug "making IsArrayStmt stmt")
  (fn [state]
    (eval/eval-IsArrayStmt source state)))

(defn make-IsDefinedStmt [{:strs [source]}]
  (log/debug "making IsDefinedStmt stmt")
  (fn [state]
    (eval/eval-IsDefinedStmt source state)))

(defn make-IsObjectStmt [{:strs [source]}]
  (log/debug "making IsObjectStmt stmt")
  (fn [state]
    (eval/eval-IsObjectStmt source state)))

(defn make-IsUndefinedStmt [{:strs [source]}]
  (log/debug "making IsUndefinedStmt stmt")
  (fn [state]
    (eval/eval-IsUndefinedStmt source state)))

(defn make-LenStmt [{:strs [source target]}]
  (log/debug "making LenStmt stmt")
  (fn [state]
    (eval/eval-LenStmt source target state)))

(defn make-MakeNumberRefStmt [{:strs [target] index "Index"}] ; NOTE: 'Index' is capitalized
  (log/debug "making MakeNumberRefStmt stmt")
  (fn [state]
    (eval/eval-MakeNumberRefStmt index target state)))

(defn make-MakeArrayStmt [{:strs [target] :as stmt-info}]
  (log/debugf "making MakeArrayStmt stmt: %s" stmt-info)
  (fn [state]
    (eval/eval-MakeArrayStmt target state)))

(defn make-MakeNullStmt [{:strs [target] :as stmt-info}]
  (log/debugf "making MakeNullStmt stmt: %s" stmt-info)
  (fn [state]
    (eval/eval-MakeNullStmt target state)))

(defn make-MakeNumberIntStmt [{:strs [value target] :as stmt-info}]
  (log/debugf "making MakeNumberIntStmt stmt: %s" stmt-info)
  (fn [state]
    (eval/eval-MakeNumberIntStmt value target state)))

(defn make-MakeObjectStmt [{:strs [target] :as stmt-info}]
  (log/debugf "making MakeObjectStmt stmt: %s" stmt-info)
  (fn [state]
    (eval/eval-MakeObjectStmt target state)))

(defn make-MakeSetStmt [{:strs [target] :as stmt-info}]
  (log/debugf "making MakeSetStmt stmt: %s" stmt-info)
  (fn [state]
    (eval/eval-MakeSetStmt target state)))

(defn make-NopStmt [stmt-info]
  (log/debugf "making NopStmt stmt: %s" stmt-info)
  (fn [state]
    (eval/eval-NopStmt state)))

(defn make-NotEqualStmt [{:strs [a b]}]
  (log/debug "making NotEqualStmt stmt")
  (fn [state]
    (eval/eval-NotEqualStmt a b state)))

(defn make-NotStmt [{:strs [block]}]
  (log/debug "making NotStmt stmt")
  (fn [state]
    (eval/eval-NotStmt (make-stmts (get block "stmts")) state)))

(defn make-ObjectInsertOnceStmt [{:strs [key value object]}]
  (log/debug "making ObjectInsertOnceStmt stmt")
  (fn [state]
    (eval/eval-ObjectInsertOnceStmt key value object state)))

(defn make-ObjectInsertStmt [{:strs [key value object]}]
  (log/debug "making ObjectInsertStmt stmt")
  (fn [state]
    (eval/eval-ObjectInsertStmt key value object state)))

(defn make-ObjectMergeStmt [{:strs [a b target]}]
  (log/debug "making ObjectMergeStmt stmt")
  (fn [state]
    (eval/eval-ObjectMergeStmt a b target state)))

(defn make-ResetLocalStmt [{:strs [target]}]
  (log/debug "making ResetLocalStmt stmt")
  (fn [state]
    (eval/eval-ResetLocalStmt target state)))

(defn make-ResultSetAddStmt [{:strs [value]}]
  (log/debug "making ResultSetAddStmt stmt")
  (fn [state]
    (eval/eval-ResultSetAddStmt value state)))

(defn make-ReturnLocalStmt [_]
  (log/debug "making ReturnLocalStmt stmt")
  (fn [state]
    (eval/eval-ReturnLocalStmt state)))

(defn make-SetAddStmt [{:strs [set value]}]
  (log/debug "making SetAddStmt stmt")
  (fn [state]
    (eval/eval-SetAddStmt set value state)))

(defn make-ScanStmt [{:strs [source key value block]}]
  (log/debug "making ScanStmt stmt")
  (fn [state]
    (eval/eval-ScanStmt source key value (make-block block) state)))

(defn make-stmt [{:strs [type] :as stmt-info}]
  (log/debugf "making stmt: %s" stmt-info)
  (let [{stmt-info "stmt"} stmt-info
        stmt (case type
               "ArrayAppendStmt" (make-ArrayAppendStmt stmt-info)
               "AssignIntStmt" (make-AssignIntStmt stmt-info)
               "AssignVarOnceStmt" (make-AssignVarOnceStmt stmt-info)
               "AssignVarStmt" (make-AssignVarStmt stmt-info)
               "BlockStmt" (make-BlockStmt stmt-info)
               "BreakStmt" (make-BreakStmt stmt-info)
               "CallDynamicStmt" (make-CallDynamicStmt stmt-info)
               "CallStmt" (make-CallStmt stmt-info)
               "DotStmt" (make-DotStmt stmt-info)
               "EqualStmt" (make-EqualStmt stmt-info)
               "IsArrayStmt" (make-IsArrayStmt stmt-info)
               "IsDefinedStmt" (make-IsDefinedStmt stmt-info)
               "IsObjectStmt" (make-IsObjectStmt stmt-info)
               "IsUndefinedStmt" (make-IsUndefinedStmt stmt-info)
               "LenStmt" (make-LenStmt stmt-info)
               "MakeArrayStmt" (make-MakeArrayStmt stmt-info)
               "MakeNullStmt" (make-MakeNullStmt stmt-info)
               "MakeNumberIntStmt" (make-MakeNumberIntStmt stmt-info)
               "MakeNumberRefStmt" (make-MakeNumberRefStmt stmt-info)
               "MakeObjectStmt" (make-MakeObjectStmt stmt-info)
               "MakeSetStmt" (make-MakeSetStmt stmt-info)
               "NopStmt" (make-NopStmt stmt-info)
               "NotEqualStmt" (make-NotEqualStmt stmt-info)
               "NotStmt" (make-NotStmt stmt-info)
               "ObjectInsertOnceStmt" (make-ObjectInsertOnceStmt stmt-info)
               "ObjectInsertStmt" (make-ObjectInsertStmt stmt-info)
               "ObjectMergeStmt" (make-ObjectMergeStmt stmt-info)
               "ResetLocalStmt" (make-ResetLocalStmt stmt-info)
               "ResultSetAddStmt" (make-ResultSetAddStmt stmt-info)
               "ReturnLocalStmt" (make-ReturnLocalStmt stmt-info)
               "ScanStmt" (make-ScanStmt stmt-info)
               "SetAddStmt" (make-SetAddStmt stmt-info)
               ;"WithStmt"
               (throw (Exception. (format "%s statement type not implemented" type))))]
    (fn [state]
      (eval/eval-stmt type stmt state))))

(defn make-stmts [stmts-info]
  (log/debug "making stmts")
  (let [stmts (doall (for [stmt-info stmts-info]
                       (make-stmt stmt-info)))]
    (fn [state]
      (eval/eval-stmts stmts state))))

(defn make-block [{:strs [stmts]}]
  (log/debug "making block")
  (fn [state]
    (eval/eval-block (make-stmts stmts) state)))

(defn make-blocks [blocks-info]
  (log/debug "making blocks")
  (let [blocks (doall (for [block-info blocks-info]
                        (make-block block-info)))]
    (fn [state]
      (eval/eval-blocks blocks state))))

; the data document seems to be expected to be a hierarchy of maps resembling the entry-point path (plan name).
(defn data-from-plan-info [{plan-name "name"}]
  (if (pos? (count plan-name))
    (loop [components (reverse (str/split plan-name #"/"))
           result {}]
      (if (empty? components)
        result
        (recur (next components) {(first components) result})))
    {}))

(defn make-data [plan-info data]
  (merge data (data-from-plan-info plan-info)))

(defn- align-result-set [result-set]
  (json/read-str (json/write-str result-set)))

(defn make-plan [{:strs [name] blocks-info "blocks" :as plan-info}]
  (log/debugf "making plan '%s'" name)
  (log/tracef "plan: %s" plan-info)
  (let [blocks (make-blocks blocks-info)]
    ; return a [name fn] pair
    [name (fn [info data input]
            (let [state (assoc info :local {0 input
                                            1 (make-data plan-info data)})]
              (log/debugf "Plan - executing '%s'" name)
              (let [state (blocks state)
                    result-set (get state :result-set)]
                (log/debugf "Plan - result-set: %s" result-set)
                (align-result-set result-set))))]))

(defn make-plans [{:strs [plans]}]
  (log/debug "making plans")
  (into-array (doall (for [plan-info plans]
                       (make-plan plan-info)))))

(defn make-func [{:strs [name path] return-index "return" blocks-info "blocks"}]
  (let [blocks (make-blocks blocks-info)]
    (log/debugf "making func <%s>" name)
    [name path (fn [state]
                 (eval/eval-func name return-index blocks state))]))

(defn make-funcs [funcs-info]
  (log/debug "making funcs")
  (loop [func-infos (get funcs-info "funcs")
         func-map {}]
    (if (empty? func-infos)
      func-map
      (let [[name path func] (make-func (first func-infos))
            func-map (assoc func-map name func)             ; bind function to name
            func-map (assoc func-map path func)]            ; bind function to path
        (recur (next func-infos) func-map)))))

(defn make-builtin-func [{:strs [name]}]
  (let [builtin-func (builtins/get-builtin name)]
    (log/debugf "making built-in func <%s>" name)
    (if (nil? builtin-func)
      (throw (Exception. (format "unknown function '%s'" name)))
      [name (fn [state]
              (eval/eval-builtin-func name builtin-func state))])))

(defn make-builtin-funcs [builtin-funcs-info]
  (log/debug "making built-in funcs")
  (loop [func-infos builtin-funcs-info
         func-map {}]
    (if (empty? func-infos)
      func-map
      (let [[name func] (make-builtin-func (first func-infos))]
        (recur (next func-infos) (assoc func-map name func))))))

(defn parse
  "Parses the incoming Intermediate Representation map"
  [{:strs [static plans funcs]}]
  (let [builtin-funcs-info (get static "builtin_funcs")]
    {:plans         (make-plans plans)
     :funcs         (make-funcs funcs)
     :builtin-funcs (make-builtin-funcs builtin-funcs-info)
     :static        static}))

(defn parse-json
  "Parses the incoming string"
  [str] (parse (json/read-str str)))

(defn parse-file
  "Reads and parses the incoming file"
  [file]
  (log/infof "Parsing file '%s'" file)
  (parse-json (slurp file)))
