package com.reactiveprogramming.reactive_api.controller;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {

	@GetMapping("/hello")
	public Mono<String> sayHello() {
		// "Mono.just" creates a publisher with a single "Hello, World!" message
		return Mono.just("Hello, World!");
	}

	@GetMapping(value = "/users", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> getUsers() {
		// "Mono.just" creates a publisher with a single "Hello, World!" message
		return Flux.just("Apple", "banana", "Grapes", "Mango").delayElements(Duration.ofMillis(2000));
	}

	@GetMapping(value = "/posts", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> getPosts() {

		// 1. Create a WebClient instance (point to any test API)
		WebClient webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com") // Sample public API
				.build();

		return webClient.get().uri("/posts") // Example endpoint returning JSON array
				.retrieve().bodyToFlux(String.class).delayElements(Duration.ofMillis(2000));

	}

	
}
