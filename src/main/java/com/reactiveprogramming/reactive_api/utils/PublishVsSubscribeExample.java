package com.reactiveprogramming.reactive_api.utils;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/* subscribeOn always affects the source (upstream):
No matter where you place it in the chain, it determines which scheduler is used to subscribe to (or start) the source.
If you place it at the beginning or the end of your chain, it still forces the subscription (and everything upstream of the first operator) to run on its scheduler.
publishOn is position-sensitive:
It switches the execution context for all operators downstream of its position.
Changing its position changes which parts of your chain run on the new scheduler. 

* Key Takeaways
subscribeOn is “sticky” for the subscription process:
It always affects the upstream source no matter where it appears in the chain.
publishOn changes the thread for operators that come after it:
Its position in the chain is critical. Operators upstream of a publishOn will remain unaffected by it.
*
*/

public class PublishVsSubscribeExample {
	public static void main(String[] args) throws InterruptedException {
		Flux.range(1, 5)
				// This operation will run on the thread specified by subscribeOn.
				.map(i -> {
					System.out.println("Map 1 (subscribeOn): " + i + " on thread: " + Thread.currentThread().getName());
					return i;
				})
				// subscribeOn sets the scheduler for the source and upstream operations.
				.subscribeOn(Schedulers.boundedElastic())
				// publishOn switches the thread for downstream operations.
				.publishOn(Schedulers.parallel()).map(i -> {
					System.out.println("Map 2 (publishOn): " + i + " on thread: " + Thread.currentThread().getName());
					return i;
				}).subscribe(i -> System.out
						.println("Subscriber got: " + i + " on thread: " + Thread.currentThread().getName()));

		// Sleep to allow asynchronous operations to complete.
		Thread.sleep(1000);
	}
}
