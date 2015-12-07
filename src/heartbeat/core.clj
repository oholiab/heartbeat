(ns heartbeat.core
  (:use [overtone.live]
        [overtone.inst.drum]
        [clojure.java.shell :only [sh]]
        [clojure.string :only [split]]))

(def basebpm (atom 128.0))

(def metro (metronome @basebpm))

(def bpm (atom @basebpm))

(defn loadavg
  "Gets load average of the machine"
  []
  (Float. (second (split (:out (sh "sysctl" "-n" "vm.loadavg")) #" "))))

(defn updatemetro
  "Updates the metronome to use the bpm adjusted by the current load average"
  [metro, bpm]
  (let [p (promise)]
    {:future (future 
               (let [t (Thread/currentThread)]
                (deliver p t)
                (while true 
                  (let [newbpm (* (loadavg) @basebpm)]
                           (Thread/sleep 500)
                           (reset! bpm newbpm)
                           (metro-bpm metro newbpm)
                           ))))
     :thread @p}))

;(def update-worker (updatemetro metro bpm))

;(:thread update-worker)
;(:future update-worker)
;(future-cancel (:future update-worker))

;(stop)
(definst testy [freq 440 length 3 amp 1 rate 6]
  (* amp
     (line:kr 1 0 length FREE)
     (saw (+ freq (sin-osc:kr rate))))
  )

(inst-fx! testy fx-distortion2)

(defn buzz-player
  "Synth loop"
  [beat]
  (let [freq 50]
    (at (metro beat) (testy freq 0.5 5 0))
    (when (= 0 (mod beat 8)) (at (metro (+ 0.5 beat)) (testy freq 0.5 5 0))))
  (apply-by (metro (inc beat)) #'buzz-player (inc beat) [])
  )

(defn drum-player
  "Drum program loop suitable for live editing"
  [beat]
  (doseq [x [0 1]]
    (at (metro (+ (* x 0.5) beat)) (closed-hat))
    )
  (at (metro beat) (kick))
  (when (= 0 (mod beat 8)) (at (metro (+ 0.5 beat)) (kick)))
  (at (metro (+ 0.5 beat)) (open-hat))
  (apply-by (metro (inc beat)) #'drum-player (inc beat) [])
  )

;(reset! basebpm 200)

;(drum-player (metro))
;(buzz-player (metro))
