(ns heartbeat.core-test
  (:require [clojure.test :refer :all]
            [heartbeat.core :refer :all]))

(deftest updatemetro-worker-can-be-killed
  (testing "When a metro update worker is created, it have it's future 
           referenced and cancelled"
    (let [worker (updatemetro metro bpm)]
    (is (true? (future-cancel (:future worker)))))))
