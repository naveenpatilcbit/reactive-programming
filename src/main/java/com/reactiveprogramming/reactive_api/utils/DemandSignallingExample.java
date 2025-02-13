package com.reactiveprogramming.reactive_api.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.BaseSubscriber;
import org.reactivestreams.Subscription;

/*
 * In many real-world scenarios, you don't want to create custom subscribers by overriding 
 * hookOnSubscribe or hookOnNext just to control demand. Instead, you can use built-in operators 
 * that handle demand (or backpressure) for you. One common example is the limitRate operator in Reactor, which requests data in batches (or "chunks") from upstream.


 */
public class DemandSignallingExample {
	public static void main(String[] args) {
		// Create a Flux that emits numbers from 1 to 10.
		Flux<Integer> flux = Flux.range(1, 10);

		flux.subscribe(new BaseSubscriber<Integer>() {
			@Override
			protected void hookOnSubscribe(Subscription subscription) {
				// When subscribing, request 2 items initially.
				System.out.println("Subscribed! Requesting 2 items...");
				request(2);
			}

			@Override
			protected void hookOnNext(Integer value) {
				// Process each received value.
				System.out.println("Received: " + value);
				// After processing, request one more item.
				request(1);
			}
		});
	}
}
