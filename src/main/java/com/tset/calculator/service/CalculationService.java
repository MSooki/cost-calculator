package com.tset.calculator.service;

import com.tset.calculator.dto.CalculationResult;
import org.springframework.stereotype.Service;

@Service
public class CalculationService {

    public CalculationResult calculate(float principal, float annualInterestRate, int timesPerYear, int years) {
        double rate = annualInterestRate / 100;
        double A = principal * Math.pow(1 + rate / timesPerYear, timesPerYear * years);
        double interestEarned = A - principal;
        return new CalculationResult(A, principal, interestEarned, 0, -1, -1);
    }

    public CalculationResult calculate(float principal, float annualInterestRate, int timesPerYear, int years, float monthlyContribution) {
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
        double totalContribution = monthlyContribution * 12 * years;
        double interestEarned = A - principal - totalContribution;
        return new CalculationResult(A, principal, interestEarned, totalContribution, -1, -1);
    }

    public CalculationResult calculate(float principal, float annualInterestRate, int timesPerYear, int years,
                                       float monthlyContribution, float housePrice, float annualHousePriceIncrease) {
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
        double totalContribution = monthlyContribution * 12 * years;
        double interestEarned = A - principal - totalContribution;
        double housePriceAtTheEndOfPeriod = housePrice * Math.pow(1 + annualHousePriceIncrease/100, years);
        int yearsToBuy = calculateYears(principal, annualInterestRate, timesPerYear, monthlyContribution, housePrice, annualHousePriceIncrease);

        return new CalculationResult(A, principal, interestEarned, totalContribution, yearsToBuy, housePriceAtTheEndOfPeriod);
    }

    private int calculateYears(float principal, float annualInterestRate, int timesPerYear,
                                       float monthlyContribution, float housePrice, float annualHousePriceIncrease) {
        double rate = annualInterestRate / 100;
        double houseRate = annualHousePriceIncrease / 100;

        double A = principal;
        double currentHousePrice = housePrice;
        int year = 0;

        double previousGap = Double.MAX_VALUE;
        int increasingGapYears = 0; // Track consecutive years where gap increases
        final int MAX_UNAFFORDABLE_YEARS = 5; // If the gap keeps increasing for 5 years, assume it's unaffordable

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

            // If the gap increases, track it
            if (currentGap > previousGap) {
                increasingGapYears++;
            } else {
                increasingGapYears = 0; // Reset counter if the gap decreases
            }

            // If the gap has increased for 5 consecutive years, assume affordability is impossible
            if (increasingGapYears >= MAX_UNAFFORDABLE_YEARS) {
                return year;
            }

            previousGap = currentGap;
        }

        return year;
    }
}
