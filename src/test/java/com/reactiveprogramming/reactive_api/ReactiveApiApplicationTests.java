package com.reactiveprogramming.reactive_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.core.publisher.Flux;

@SpringBootTest
class ReactiveApiApplicationTests {

	@Test
	void contextLoads() {
	}

	void flux_test() {
		Flux<String> streamData = Flux.just("a", "b", "c");
	
		streamData.subscribe(value -> System.out.println("Received: " + value), // onNext
				error -> System.err.println("Error: " + error), // onError
				() -> System.out.println("Stream Completed!"));
	}

}
