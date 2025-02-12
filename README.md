# reactive-programming

Synchronous vs. Asynchronous
Synchronous Generation

Definition:
In synchronous generation, the event-producing code runs on the same thread that is executing the reactive stream. Each event is produced one at a time, and the production of the next event waits until the current one is processed.
Flux.generate:
Behavior:
The generator function is invoked in a loop on the same thread. Each call to sink.next() is executed sequentially, and you produce exactly one signal per invocation.
Implication:
This is a pull-based model where the next value is only produced after the previous one has been handled. It’s best for scenarios where you have a deterministic, iterative process.
Example Context:
Generating a sequence of numbers or a Fibonacci sequence in a controlled, step-by-step fashion.
Asynchronous Generation

Definition:
In asynchronous generation, event production is decoupled from the processing thread. Events can be pushed into the stream from different threads or in response to external triggers (like callbacks or timers), without waiting for the subscriber’s demand.
Flux.create:
Behavior:
The sink provided in Flux.create allows events to be emitted from any thread. It supports multiple signals per callback and is suitable for integrating with asynchronous, callback-based sources.
Implication:
This is a push-based model. Events are emitted as soon as they occur, regardless of whether the subscriber has requested them immediately. This requires proper handling of backpressure.
Example Context:
Integrating with external systems like sensors or network callbacks where events occur independently of the subscriber's control.
Flux.push:
Behavior:
Similar to Flux.create, but optimized for single-threaded asynchronous scenarios. It does not have the overhead of thread-safety mechanisms because it's assumed that emissions occur on one thread.
Implication:
It provides an efficient way to push events asynchronously when you know that only one thread will be involved.
Example Context:
Handling UI events where events (like button clicks) are produced on the UI thread and pushed into the reactive stream.
