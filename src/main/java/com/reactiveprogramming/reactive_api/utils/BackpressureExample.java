package com.reactiveprogramming.reactive_api.utils;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * If the publisher produces items faster than the consumer can process them, operators can buffer the excess items, drop them, 
 * or apply other strategies (like sampling). Reactor offers operators such as:
onBackpressureBuffer()
onBackpressureDrop()
onBackpressureLatest()
 * 
 */
public class BackpressureExample {
	public static void main(String[] args) throws InterruptedException {
		Flux.range(1, 1000)
				// Buffer up to 100 items; if more items arrive, drop them and log the dropped
				// items.
				.onBackpressureBuffer(100, dropped -> System.out.println("Dropped: " + dropped))
				// Use a parallel scheduler and limit prefetch to 50 items.
				.publishOn(Schedulers.parallel(), 50).subscribe(i -> {
					try {
						// Simulate a slow consumer (processing each item takes 100ms)
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					System.out.println("Processed: " + i + ", Thread: " + Thread.currentThread().getName());
				});

		// Keep the application alive to see the output
		Thread.sleep(15000);
	}
}
