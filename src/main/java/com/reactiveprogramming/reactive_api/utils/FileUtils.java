package com.reactiveprogramming.reactive_api.utils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FileUtils {

	/**
	 * Read the file in chunks using AsynchronousFileChannel, emitting a Flux of
	 * ByteBuffers. We keep reading until we reach the end of the file (indicated by
	 * read result == -1).
	 */
	public static Flux<ByteBuffer> readFileAsFlux(AsynchronousFileChannel channel, int chunkSize) {
		return Flux.create(sink -> {
			// We'll track the current file position in an AtomicLong
			AtomicLong position = new AtomicLong(0);

			// A recursive method to schedule each next read
			class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
				@Override
				public void completed(Integer bytesRead, ByteBuffer buffer) {
					if (bytesRead == -1) {
						// End of file
						sink.complete();
						return;
					}

					// Prepare buffer to be emitted
					buffer.flip();
					sink.next(buffer);

					// Schedule next chunk read
					long newPos = position.addAndGet(bytesRead);
					ByteBuffer nextBuffer = ByteBuffer.allocate(chunkSize);
					channel.read(nextBuffer, newPos, nextBuffer, this);
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					sink.error(exc);
				}
			}

			// Start the first read
			ByteBuffer firstBuffer = ByteBuffer.allocate(chunkSize);
			channel.read(firstBuffer, position.get(), firstBuffer, new ReadHandler());
		});
	}

	/**
	 * Write a ByteBuffer to the given AsynchronousFileChannel at the current
	 * position, returning a Mono<Void> that completes when the write finishes.
	 */
	private static final AtomicLong writePosition = new AtomicLong(0);

	public static Mono<Void> writeFileAsync(AsynchronousFileChannel channel, ByteBuffer buffer) {
		return Mono.create(sink -> {
			// Current position to write
			long pos = writePosition.getAndAdd(buffer.remaining());

			channel.write(buffer, pos, null, new CompletionHandler<Integer, Void>() {
				@Override
				public void completed(Integer result, Void attachment) {
					// Signal success
					sink.success();
				}

				@Override
				public void failed(Throwable exc, Void attachment) {
					sink.error(exc);
				}
			});
		});
	}

}
