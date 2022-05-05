(ns jarl.builtins.numbers
  (:require [jarl.builtins.utils :refer [check-args]]
            [clojure.set :as set])
  (:import (se.fylling.jarl UndefinedException)))

(defn- possibly-int
  "present e.g. 1.0 as 1"
  [num]
  (if (zero? (mod num 1))
    (int num)
    num))

(defn builtin-plus
  "Implementation of plus built-in"
  {:builtin "plus" :args-types ["number" "number"]}
  [a b]
  (check-args (meta #'builtin-plus) a b)
  (possibly-int (+ a b)))

(defn builtin-minus
  "Implementation of minus built-in"
  {:builtin "minus" :args-types ["number" "number"]}
  [a b]
  ; Disable args check until we have a way to express union types
  ; (check-args (meta #'builtin-minus) a b)
  (if (and (set? a) (set? b))
    (set/difference a b)
    (possibly-int (- a b))))

(defn builtin-mul
  "Implementation of mul built-in"
  {:builtin "mul" :args-types ["number" "number"]}
  [a b]
  (check-args (meta #'builtin-mul) a b)
  (possibly-int (* a b)))

(defn builtin-div
  "Implementation of div built-in"
  {:builtin "div" :args-types ["number" "number"]}
  [a b]
  (check-args (meta #'builtin-div) a b)
  (if (zero? b)
    (throw (UndefinedException. "div: divide by zero"))
    (possibly-int (double (/ a b)))))

(defn builtin-rem
  "Implementation of rem built-in"
  {:builtin "rem" :args-types ["number" "number"]}
  [a b]
  (check-args (meta #'builtin-rem) a b)
  (let [res (rem a b)]
    (if (zero? (mod res 1))
      (possibly-int res)
      ; OPA returns undefined for rem resulting in floating point numbers
      (throw (UndefinedException. "remainder is not an integer")))))

(defn builtin-round
  "Implementation of round built-in"
  {:builtin "round" :args-types ["number"]}
  [x]
  (check-args (meta #'builtin-round) x)
  (cond
    (int? x) x
    (double? x) (Math/round ^Double x)
    (float? x) (Math/round ^Float x)))

(defn builtin-ceil
  "Implementation of ceil built-in"
  {:builtin "ceil" :args-types ["number"]}
  [x]
  (check-args (meta #'builtin-ceil) x)
  (cond
    (int? x) x
    (double? x) (Math/round (Math/ceil ^Double x))
    (float? x) (Math/round (Math/ceil ^Float x))))

(defn builtin-floor
  "Implementation of floor built-in"
  {:builtin "floor" :args-types ["number"]}
  [x]
  (check-args (meta #'builtin-floor) x)
  (cond
    (int? x) x
    (double? x) (Math/round (Math/floor ^Double x))
    (float? x) (Math/round (Math/floor ^Float x))))

(defn builtin-abs
  "Implementation of abs built-in"
  {:builtin "abs" :args-types ["number"]}
  [x]
  (check-args (meta #'builtin-abs) x)
  ; Clojure 1.11 has `abs` in core, but lein clj-kondo seems to depend on an older version, which fails
  ; on calling abs as "unresolved". I've asked the maintainers to have that bumped, and will replace this once fixed.
  (let [abs (fn [n] (max n (-' n)))]
    (abs x)))

(defn builtin-numbers-range
  "Implementation of numbers.range built-in"
  {:builtin "numbers.range" :args-types ["number" "number"]}
  [a b]
  (check-args (meta #'builtin-numbers-range) a b)
  (if (> a b)
    (rseq (vec (range b (inc a))))
    (vec (range a (inc b)))))
