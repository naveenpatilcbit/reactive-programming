package com.reactiveprogramming.reactive_api.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FluxCreationExamples {
	public static void main(String[] args) {

		// 1. Flux.just()
		// Creates a Flux that emits the specified items.
		Flux<String> fluxJust = Flux.just("A", "B", "C");
		fluxJust.subscribe(item -> System.out.println("just: " + item));

		// 2. Flux.fromIterable()
		// Creates a Flux from an Iterable (like a List).
		List<String> list = Arrays.asList("D", "E", "F");
		Flux<String> fluxFromIterable = Flux.fromIterable(list);
		fluxFromIterable.subscribe(item -> System.out.println("fromIterable: " + item));

		// 3. Flux.fromArray()
		// Creates a Flux from an array.
		String[] array = { "G", "H", "I" };
		Flux<String> fluxFromArray = Flux.fromArray(array);
		fluxFromArray.subscribe(item -> System.out.println("fromArray: " + item));

		// 4. Flux.range()
		// Emits a range of sequential integers.
		Flux<Integer> fluxRange = Flux.range(1, 5); // emits 1, 2, 3, 4, 5
		fluxRange.subscribe(item -> System.out.println("range: " + item));

		// 5. Flux.interval()
		// Emits increasing long values at specified intervals.
		// Note: This Flux is infinite unless limited.
		Flux<Long> fluxInterval = Flux.interval(Duration.ofSeconds(1));
		fluxInterval.take(3) // Taking only the first 3 emissions for demonstration.
				.subscribe(item -> System.out.println("interval: " + item));

		// 6. Flux.generate()
		// Programmatically generates signals one-by-one.
		Flux<String> fluxGenerate = Flux.generate(sink -> {
			sink.next("Generated Value");
			sink.complete(); // Complete the Flux after emitting a value.
		});
		fluxGenerate.subscribe(item -> System.out.println("generate: " + item));

		// 7. Flux.create()
		// Creates a Flux in an asynchronous manner, supporting multiple threads.
		Flux<String> fluxCreate = Flux.create(sink -> {
			sink.next("Created Value 1");
			sink.next("Created Value 2");
			sink.complete();
		});
		fluxCreate.subscribe(item -> System.out.println("create: " + item));

		// 8. Flux.push()
		// Similar to create(), but is intended for single-threaded producers.
		Flux<String> fluxPush = Flux.push(sink -> {
			sink.next("Pushed Value");
			sink.complete();
		});
		fluxPush.subscribe(item -> System.out.println("push: " + item));

		// 9. Flux.defer()
		// Defers the creation of a Flux until subscription time.
		Flux<String> fluxDefer = Flux.defer(() -> Flux.just("Deferred Value 1", "Deferred Value 2"));
		fluxDefer.subscribe(item -> System.out.println("defer: " + item));

		// 10. Mono.just(...).flux()
		// Converts a Mono into a Flux.
		Flux<String> fluxFromMono = Mono.just("Mono to Flux").flux();
		fluxFromMono.subscribe(item -> System.out.println("fromMono: " + item));

		// 11. Flux.empty()
		// Creates an empty Flux that completes immediately.
		Flux<String> fluxEmpty = Flux.empty();
		fluxEmpty.subscribe(item -> System.out.println("This will not be printed"),
				error -> System.err.println("Error: " + error), () -> System.out.println("empty: Flux completed"));

		// 12. Flux.error()
		// Creates a Flux that terminates with an error.
		Flux<String> fluxError = Flux.error(new RuntimeException("Error occurred"));
		fluxError.subscribe(item -> System.out.println("Value: " + item),
				error -> System.err.println("error: " + error.getMessage()));

		// 13. Flux.fromCallable()
		// Creates a Flux from a Callable, which is useful for deferring execution.
		/* Flux<String> fluxCallable = Flux.fromCallable(() -> "Callable Value");
		fluxCallable.subscribe(item -> System.out.println("fromCallable: " + item)); */

		// 14. Flux.fromStream()
		// Creates a Flux from a Java Stream.
		Stream<String> stream = Stream.of("Stream Value 1", "Stream Value 2");
		Flux<String> fluxFromStream = Flux.fromStream(stream);
		fluxFromStream.subscribe(item -> System.out.println("fromStream: " + item));

		// Note: Some Flux types like interval operate asynchronously.
		// You might need to wait or block in a main method if you want to see their
		// output.
		try {
			Thread.sleep(4000); // Allow time for async operations (like interval) to complete.
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
