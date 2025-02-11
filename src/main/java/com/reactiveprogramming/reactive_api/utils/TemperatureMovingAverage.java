package com.reactiveprogramming.reactive_api.utils;

import java.time.Duration;
import java.util.List;

import reactor.core.publisher.Flux;

public class TemperatureMovingAverage {

	// Wrap the sensor's callback-based API into a Flux using Flux.create
	public static Flux<Double> createTemperatureFlux(TemperatureSensor sensor) {
		return Flux.create(sink -> {
			sensor.start(new TemperatureSensor.TemperatureListener() {
				@Override
				public void onTemperature(double temperature) {
					// Use the sink to push new temperature readings into the Flux
					sink.next(temperature);
				}

				@Override
				public void onError(Throwable t) {
					// Propagate errors to the Flux
					sink.error(t);
				}
			});
		});
	}

	public static void main(String[] args) {
		TemperatureSensor sensor = new TemperatureSensor();
		Flux<Double> temperatureFlux = createTemperatureFlux(sensor);

		// Option 1: Non-overlapping window every 5 minutes
		temperatureFlux.buffer(Duration.ofMinutes(5)).map(TemperatureMovingAverage::calculateAverage)
				.subscribe(avg -> System.out.println("5-minute average (non-overlapping): " + avg));

		// Option 2: Sliding window moving average
		// This will update the average every 30 seconds with the last 5 minutes of
		// data.
		temperatureFlux.buffer(Duration.ofMinutes(5), Duration.ofSeconds(30))
				.map(TemperatureMovingAverage::calculateAverage)
				.subscribe(avg -> System.out.println("Sliding 5-minute average (updated every 30 sec): " + avg));

		// Keep the application running (for demonstration purposes, 10 minutes here)
		try {
			Thread.sleep(Duration.ofMinutes(10).toMillis());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	// Utility method to calculate the average from a list of Double values.
	private static double calculateAverage(List<Double> temperatures) {
		if (temperatures.isEmpty()) {
			return 0;
		}
		double sum = temperatures.stream().mapToDouble(Double::doubleValue).sum();
		return sum / temperatures.size();
	}
}
