package com.tset.calculator.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CalculationService {

    private final ConcurrentHashMap<Integer, Double> results = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Integer> resultYears = new ConcurrentHashMap<>();

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

    @Async
    public void calculate(int requestId, float principal, float annualInterestRate, int timesPerYear, float monthlyContribution, float housePrice, float annualHousePriceIncrease) {
        double rate = annualInterestRate / 100;
        double houseRate = annualHousePriceIncrease / 100;

        double A = principal;
        double currentHousePrice = housePrice;
        int year = 0;

        double previousGap = Double.MAX_VALUE; // Start with a very large gap

        while (A < currentHousePrice) {
            year++;

            // Compound the investment
            A *= Math.pow(1 + rate / timesPerYear, timesPerYear);
            for (int i = 1; i <= 12; i++) {
                A += monthlyContribution * Math.pow(1 + rate / timesPerYear, timesPerYear * ((12 - i + 1) / 12.0));
            }

            // Increase the house price
            currentHousePrice *= (1 + houseRate);

            // Calculate the new gap
            double currentGap = currentHousePrice - A;

            // If the gap is increasing, it will never be affordable
            if (currentGap > previousGap) {
                resultYears.put(requestId, -1); // Special value: -1 means "never affordable"
                results.put(requestId, A);
                return;
            }

            previousGap = currentGap; // Update the previous gap
        }

        results.put(requestId, A);
        resultYears.put(requestId, year);
    }

    public Integer getResultYear(int requestId) {
        return resultYears.get(requestId);
    }

    public double getResult(int requestId) {
        return results.get(requestId);
    }
}
