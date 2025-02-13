package com.reactiveprogramming.reactive_api.controller;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class VideoStreamingController {

    private static final String VIDEO_FILE_PATH = "videos/sample.mp4"; // adjust path accordingly

    @GetMapping(value = "/video", produces = "video/mp4")
    public Mono<ResponseEntity<Flux<DataBuffer>>> streamVideo(@RequestHeader HttpHeaders headers) {
        try {
            Path videoPath = Paths.get(VIDEO_FILE_PATH);
            long fileSize = videoPath.toFile().length();

            // Parse the Range header if present.
            List<HttpRange> httpRanges = headers.getRange();
            long start = 0;
            long end = fileSize - 1;
            if (httpRanges != null && !httpRanges.isEmpty()) {
                HttpRange range = httpRanges.get(0);
                start = range.getRangeStart(fileSize);
                end = range.getRangeEnd(fileSize);
            }
            long contentLength = end - start + 1;

            // Create a Flux that reads the video file in non-blocking way.
            Flux<DataBuffer> dataBufferFlux = DataBufferUtils.readByteChannel(
                    () -> java.nio.channels.AsynchronousFileChannel.open(videoPath),
                    org.springframework.core.io.buffer.DefaultDataBufferFactory.sharedInstance,
                    4096)
                // Skip bytes before the start offset
                .skipUntil(buffer -> {
                    // NOTE: For a production implementation, you must adjust the buffer
                    // to start exactly at the byte offset. Here we assume start is 0 for simplicity.
                    return start == 0;
                })
                // For demonstration, we stream the entire file.
                .takeUntil(buffer -> false);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "video/mp4");
            responseHeaders.add("Accept-Ranges", "bytes");
            responseHeaders.add("Content-Length", String.valueOf(contentLength));
            responseHeaders.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);

            HttpStatus status = (httpRanges != null && !httpRanges.isEmpty()) ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;

            return Mono.just(ResponseEntity.status(status)
                    .headers(responseHeaders)
                    .body(dataBufferFlux));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }
}
