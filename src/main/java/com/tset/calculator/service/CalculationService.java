package com.tset.calculator.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CalculationService {

    private ConcurrentHashMap<String, Float> results = new ConcurrentHashMap<>();

    @Async
    public void calculate(String requestId, float principal, float annualInterestRate, int timesPerYear, int years) {
        try {
            // Simulate a long calculation
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Compound interest formula: A = P(1 + r/n)^(nt)
        float rate = annualInterestRate / 100;
        float A = (float) (principal * Math.pow(1 + rate / timesPerYear, timesPerYear * years));
        results.put(requestId, A);
    }

    public Float getResult(String requestId) {
        return results.get(requestId);
    }
}
