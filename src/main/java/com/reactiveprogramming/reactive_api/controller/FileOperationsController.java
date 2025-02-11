package com.reactiveprogramming.reactive_api.controller;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactiveprogramming.reactive_api.utils.FileUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class FileOperationsController {

	private static final int CHUNK_SIZE = 1024;

	@GetMapping(value = "/generate_file", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<String> genearateFile() {
		WebClient webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com") // Sample public API
				.build();

		Flux<String> dataMap = webClient.get().uri("/posts") // Example endpoint returning JSON array
				.retrieve().bodyToFlux(String.class);

		Flux<String> testdata = dataMap.flatMap(item -> {
			return Mono.fromCallable(() -> {
				Files.writeString(Path.of("output.txt"), item + System.lineSeparator(), StandardOpenOption.CREATE,
						StandardOpenOption.APPEND);
				return item;
			}).subscribeOn(Schedulers.boundedElastic());
		});
		testdata.subscribe(item -> System.out.println(item));

		return Mono.just("success");
	}

	@GetMapping(value = "/generate_file", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<String> copyFiles() {

		String inputPath = "input.txt";
		// Path to the destination (output) file
		String outputPath = "output.txt";

		// 1. Open file channels in try-with-resources so they auto-close
		try (AsynchronousFileChannel inChannel = AsynchronousFileChannel.open(Paths.get(inputPath),
				StandardOpenOption.READ);
				AsynchronousFileChannel outChannel = AsynchronousFileChannel.open(Paths.get(outputPath),
						StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
			// 2. Create a Flux of ByteBuffer that reads the file in chunks
			Flux<ByteBuffer> readFlux = FileUtils.readFileAsFlux(inChannel, CHUNK_SIZE);

			// 3. Write each chunk to the output file in order
			// - concatMap ensures sequential writes (one after another)
			// - if you used flatMap, writes could interleave or race
			Mono<Void> copyPipeline = readFlux.concatMap(buffer -> FileUtils.writeFileAsync(outChannel, buffer)).then(); // completes
																															// when
																															// the
																															// whole
																															// Flux
																															// is
																															// done

			// 4. Trigger the pipeline (we block here just so the demo app can finish)
			copyPipeline.block();

			System.out.println("File copy completed (reactive, non-blocking NIO).");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Mono.just("success");
	}

	@GetMapping(value = "/read_upload", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<String> readAndPost() {

		String inputPath = "input.txt";

		WebClient webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com") // Sample public API
				.build();

		try {
			Flux.fromStream(Files.lines(Path.of(inputPath))).subscribeOn(Schedulers.boundedElastic())
					.subscribe(line -> webClient.post().uri("/posts").bodyValue(line)          // send the JSON as the request body
		                    .retrieve()
		                    .bodyToMono(String.class)); // expecting a String response; adjust if needed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Mono.just("success");

	}

}
