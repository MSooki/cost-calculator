package com.tset.calculator.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CalculationService {

    private final ConcurrentHashMap<String, Double> results = new ConcurrentHashMap<>();

    @Async
    public void calculate(String requestId, float principal, float annualInterestRate, int timesPerYear, int years) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        double rate = annualInterestRate / 100;
        double A = principal * Math.pow(1 + rate / timesPerYear, timesPerYear * years);
        results.put(requestId, A);
    }

    @Async
    public void calculate(String requestId, float principal, float annualInterestRate, int timesPerYear, int years, float monthlyContribution) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Compound interest formula with regular contribution: A = P(1 + r/n)^(nt) + C(((1 + r/n)^(nt) - 1) / (r/n))
        double rate = annualInterestRate / 100;
        double pow = Math.pow(1 + rate / timesPerYear, timesPerYear * years);
        double A = principal * pow;
        A += (monthlyContribution * ((pow - 1) / (rate / timesPerYear)));

        results.put(requestId, A);
    }

    public double getResult(String requestId) {
        return results.get(requestId);
    }
}
