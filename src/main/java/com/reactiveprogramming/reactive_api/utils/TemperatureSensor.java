package com.reactiveprogramming.reactive_api.utils;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TemperatureSensor {

    private final Random random = new Random();

    public interface TemperatureListener {
        void onTemperature(double temperature);
        void onError(Throwable t);
    }

    // Simulate starting the sensor and sending temperature updates
    public void start(TemperatureListener listener) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                // Simulate reading a temperature value between 20 and 30 degrees
                double temperature = 20 + random.nextDouble() * 10;
                listener.onTemperature(temperature);
            } catch (Exception e) {
                listener.onError(e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}

