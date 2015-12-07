# heartbeat

A clojure library that currently makes your Mac (currently only OSX compatible)
play really terrible gabber (yes it's intentionally obnoxious) at a speed
dependent on your machine's load average.

Yes it plays a shitty kick drum/hi-hat sequence which goes faster if you leave
all your tabs open

## Usage

Why would you want to use it? (requires leinengen)

    cd heartbeat
    lein repl #will install all deps and the like
    ...

Then at the repl prompt:

    user=> (use 'heartbeat.core)
    user=> (def update-worker (updatemetro metro bpm)
    user=> (drum-player (metro))
    user=> (buzz-player (metro))

You should be hearing awful, awful noises by now that modulate in speed
depending on how hard your laptop is working... If it's insanely fast you can
lower it or raise it with

    user=> (swap! basebpm - 20)
    user=> (swap! basebpm + 20)

Or set it with

    user=> (reset! basebpm 128)

## License

Copyright © 2015 Matt Carroll

Distributed under the 3 Clause BSD license.
