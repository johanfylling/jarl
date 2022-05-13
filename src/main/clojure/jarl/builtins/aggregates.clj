(ns jarl.builtins.aggregates
  (:require [jarl.builtins.utils :refer [possibly-int typed-seq undefined-ex]]
            [jarl.builtins.comparison :refer [rego-compare]]))

(defn builtin-count
  "Implementation of count built-in"
  {:builtin "count" :args-types ["any"]}
  [x]
  (count x))

(defn builtin-sum
  "Implementation of sum built-in"
  {:builtin "sum" :args-types ["any"]}
  [coll]
  (typed-seq coll #{"number"})
  (possibly-int (apply + coll)))

(defn builtin-product
  "Implementation of product built-in"
  {:builtin "product" :args-types ["any"]}
  [coll]
  (typed-seq coll #{"number"})
  (possibly-int (apply * coll)))

(defn builtin-max
  "Implementation of max built-in"
  {:builtin "max" :args-types ["any"]}
  [coll]
  (if (empty? coll)
    (throw (undefined-ex "max on empty collection"))
    (last (sort rego-compare coll))))

(defn builtin-min
  "Implementation of min built-in"
  {:builtin "min" :args-types ["any"]}
  [coll]
  (if (empty? coll)
    (throw (undefined-ex "min on empty collection"))
    (first (sort rego-compare coll))))

(defn builtin-sort
  "Implementation of sort built-in"
  {:builtin "sort" :args-types ["any"]}
  [coll]
  (sort rego-compare coll))