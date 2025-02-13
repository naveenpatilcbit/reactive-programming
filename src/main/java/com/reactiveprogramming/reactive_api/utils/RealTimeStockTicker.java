package com.reactiveprogramming.reactive_api.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class RealTimeStockTicker {

    public static void main(String[] args) throws InterruptedException {
        // Create a stream that triggers an API call every second.
        // The API call is simulated by the callApi() method.
        Flux<Double> priceStream = Flux.interval(Duration.ofSeconds(1))
            .flatMap(tick -> callApi())
            // share() multicasts the stream so multiple subscribers can use the same source.
            .share();

        // 1-Minute Moving Average: 60 seconds window, updated every second.
        priceStream
            .buffer(Duration.ofMinutes(1), Duration.ofSeconds(1))
            .map(RealTimeStockTicker::calculateAverage)
            .subscribe(avg -> System.out.println("1 Min MA: " + avg));

        // 5-Minute Moving Average: 5 minutes window, updated every second.
        priceStream
            .buffer(Duration.ofMinutes(5), Duration.ofSeconds(1))
            .map(RealTimeStockTicker::calculateAverage)
            .subscribe(avg -> System.out.println("5 Min MA: " + avg));

        // 1-Hour Moving Average: 1 hour window, updated every second.
        priceStream
            .buffer(Duration.ofHours(1), Duration.ofSeconds(1))
            .map(RealTimeStockTicker::calculateAverage)
            .subscribe(avg -> System.out.println("1 Hr MA: " + avg));

        // Keep the application running long enough to observe the moving averages.
        Thread.sleep(Duration.ofMinutes(65).toMillis());
    }

    /**
     * Simulates an API call that returns a stock price.
     * Here we generate a random price between 100 and 150.
     */
    private static Mono<Double> callApi() {
        double price = 100 + new Random().nextDouble() * 50;
        // Simulate network latency of 100 milliseconds.
        return Mono.just(price).delayElement(Duration.ofMillis(100));
    }

    /**
     * Calculates the average of a list of Double values.
     */
    private static double calculateAverage(List<Double> prices) {
        return prices.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
    }
}
