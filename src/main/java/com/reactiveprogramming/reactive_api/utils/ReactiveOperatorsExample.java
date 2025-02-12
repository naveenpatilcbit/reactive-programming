package com.reactiveprogramming.reactive_api.utils;

import reactor.core.publisher.Flux;

public class ReactiveOperatorsExample {
    public static void main(String[] args) {
        // 1. Creation Operator: Create a Flux of numbers from 1 to 10.
        Flux<Integer> numbers = Flux.range(1, 10);
        
        // 2. Transformation Operator: Map each number to its square.
        Flux<Integer> squares = numbers.map(n -> n * n);
        
        // 3. Filtering Operator: Keep only odd squares.
        Flux<Integer> oddSquares = squares.filter(square -> square % 2 != 0);
        
        // 4. Combination Operator: Merge oddSquares with another Flux of numbers.
        Flux<Integer> moreNumbers = Flux.range(11, 5);
        Flux<Integer> combined = Flux.merge(oddSquares, moreNumbers);
        
        // 5. Error Handling Operator: Trigger an error for a specific value, then handle it.
        Flux<Integer> errorHandled = combined
            .map(n -> {
                if (n == 16) throw new RuntimeException("Error at 16");
                return n;
            })
            .onErrorReturn(-1);
        
        // 6. Aggregation Operator: Reduce the numbers to their sum.
        errorHandled.reduce(0, Integer::sum)
            .subscribe(sum -> System.out.println("Total Sum: " + sum));
        
        // 7. Utility Operator: Side-effect logging using doOnNext.
        numbers.doOnNext(n -> System.out.println("Number: " + n))
            .subscribe();
        
        // 8. Windowing/Buffering Operator: Buffer numbers into lists of 3 items.
        numbers.buffer(3)
            .subscribe(buffer -> System.out.println("Buffered: " + buffer));
    }
}

