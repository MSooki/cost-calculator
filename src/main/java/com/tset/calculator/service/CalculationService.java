package com.tset.calculator.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CalculationService {

    private final ConcurrentHashMap<Integer, Double> results = new ConcurrentHashMap<>();

    @Async
    public void calculate(int requestId, float principal, float annualInterestRate, int timesPerYear, int years) {
        double rate = annualInterestRate / 100;
        double A = principal * Math.pow(1 + rate / timesPerYear, timesPerYear * years);
        results.put(requestId, A);
    }

    @Async
    public void calculate(int requestId, float principal, float annualInterestRate, int timesPerYear, int years, float monthlyContribution) {
        double rate = annualInterestRate / 100;
        double compoundFactor = Math.pow(1 + rate / timesPerYear, timesPerYear * years);

        // Calculate the compound interest on the principal
        double A = principal * compoundFactor;

        // Calculate the future value of each monthly contribution
        double contributionFutureValue = 0.0;
        for (int i = 1; i <= years * 12; i++) {
            contributionFutureValue += monthlyContribution * Math.pow(1 + rate / timesPerYear, timesPerYear * ((years * 12 - i + 1) / 12.0));
        }

        A += contributionFutureValue;
        results.put(requestId, A);
    }

    public double getResult(int requestId) {
        return results.get(requestId);
    }
}
