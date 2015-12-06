(ns heartbeat.core
  (:use [overtone.live]
        [overtone.inst.drum]
        [clojure.java.shell :only [sh]]
        [clojure.string :only [split]]))

(defonce basebpm 128.0)

(def metro (metronome basebpm))

(def bpm (atom basebpm))

(defn loadavg
  "Gets load average of the machine"
  []
  (Float. (second (split (:out (sh "sysctl" "-n" "vm.loadavg")) #" "))))

(defn updatemetro
  ""
  [metro, bpm]
  (let [p (promise)]
    {:future (future 
               (let [t (Thread/currentThread)]
                (deliver p t)
                (while true 
                  (let [newbpm (* (loadavg) basebpm)]
                           (Thread/sleep 500)
                           (reset! bpm newbpm)
                           (metro-bpm metro newbpm)
                           (println bpm)
                           ))))
     :thread @p}))

(def update-worker (updatemetro metro bpm))

(:thread update-worker)
(:future update-worker)
;(future-cancel (:future update-worker))

;(stop)

(defn player
  "I don't do a whole lot."
  [beat]
  (prn beat)
  (at (metro beat) (kick))
  (when (= 0 (mod beat 8)) (at (metro (+ 0.5 beat)) (kick)))
  (at (metro (+ 0.5 beat)) (closed-hat))
  (apply-by (metro (inc beat)) #'player (inc beat) [])
  )

(player (metro))
