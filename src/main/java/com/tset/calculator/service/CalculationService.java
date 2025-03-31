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

    public CalculationResult calculate(float principal, float annualInterestRate, int timesPerYear, int years,
                                       float monthlyContribution, double monthlyContributionIncreaseAnnually) {
        double rate = annualInterestRate / 100.0;
        double contributionIncreaseFactor = 1.0 + (monthlyContributionIncreaseAnnually / 100.0);

        double currentBalance = principal;
        double currentMonthlyContribution = monthlyContribution;
        double totalContribution = 0;

        for (int y = 0; y < years; y++) {
            double quarterlyContribution = currentMonthlyContribution * 3;
            for (int q = 0; q < 4; q++) {
                currentBalance += quarterlyContribution;
                totalContribution += quarterlyContribution;
                double periodsPerQuarter = (double) timesPerYear / 4.0;
                currentBalance *= Math.pow(1 + rate / timesPerYear, periodsPerQuarter);
            }
            currentMonthlyContribution *= contributionIncreaseFactor; // Increase for next year
        }

        double interestEarned = currentBalance - principal - totalContribution;
        // Ensure CalculationResult constructor uses double
        return new CalculationResult(currentBalance, principal, interestEarned, totalContribution, -1, -1);
    }

    public CalculationResult calculate(float principal, float annualInterestRate, int timesPerYear, int years,
                                       float monthlyContribution, double monthlyContributionIncreaseAnnually,
                                       float housePrice, float annualHousePriceIncrease) {
        double rate = annualInterestRate / 100.0;
        double contributionIncreaseFactor = 1.0 + (monthlyContributionIncreaseAnnually / 100.0);
        double currentBalance = principal;
        double currentMonthlyCont = monthlyContribution;
        double totalContribution = 0;

        for (int y = 0; y < years; y++) {
            double quarterlyContribution = currentMonthlyCont * 3;
            for (int q = 0; q < 4; q++) {
                currentBalance += quarterlyContribution;
                totalContribution += quarterlyContribution;
                double periodsPerQuarter = (double) timesPerYear / 4.0;
                currentBalance *= Math.pow(1 + rate / timesPerYear, periodsPerQuarter);
            }
            currentMonthlyCont *= contributionIncreaseFactor; // Increase for next year
        }
        double interestEarned = currentBalance - principal - totalContribution;
        // --- End of final balance calculation ---

        double housePriceIncreaseRate = annualHousePriceIncrease / 100.0;
        double housePriceAtTheEndOfPeriod = housePrice * Math.pow(1 + housePriceIncreaseRate, years);

        int yearsToBuy = calculateYears(principal, annualInterestRate, timesPerYear,
                monthlyContribution, monthlyContributionIncreaseAnnually, // Pass new param
                housePrice, annualHousePriceIncrease);

        // Ensure CalculationResult constructor uses double
        return new CalculationResult(currentBalance, principal, interestEarned, totalContribution, yearsToBuy, housePriceAtTheEndOfPeriod);
    }

    private int calculateYears(float principal, float annualInterestRate, int timesPerYear,
                               float initialMonthlyContribution, double monthlyContributionIncreaseAnnually, float initialHousePrice, float annualHousePriceIncrease) {
        double rate = annualInterestRate / 100.0;
        double houseRate = annualHousePriceIncrease / 100.0;
        double contributionIncreaseFactor = 1.0 + (monthlyContributionIncreaseAnnually / 100.0); // New factor

        double currentSavings = principal;
        double currentHousePrice = initialHousePrice;
        double currentMonthlyContribution = initialMonthlyContribution; // Start with initial
        int year = 0;

        double previousGap = Double.MAX_VALUE;
        int increasingGapYears = 0;
        final int MAX_CONSECUTIVE_INCREASING_GAP_YEARS = 5;
        final int MAX_SIMULATION_YEARS = 100; // Safety break

        if (currentSavings >= currentHousePrice) return 0;

        while (currentSavings < currentHousePrice) {
            year++;
            if (year > MAX_SIMULATION_YEARS) return -1; // Safety break

            // Use the contribution amount for the *current* year
            double quarterlyContribution = currentMonthlyContribution * 3;
            for (int q = 0; q < 4; q++) {
                currentSavings += quarterlyContribution;
                double periodsPerQuarter = (double) timesPerYear / 4.0;
                currentSavings *= Math.pow(1 + rate / timesPerYear, periodsPerQuarter);
            }

            currentHousePrice *= (1 + houseRate); // Increase house price for next comparison

            // Increase contribution amount for the *next* year's calculation
            currentMonthlyContribution *= contributionIncreaseFactor;

            // Check affordability and gap analysis (logic remains similar)
            if (currentSavings >= currentHousePrice) break;

            double currentGap = currentHousePrice - currentSavings;
            if (currentGap > previousGap && year > 1) {
                increasingGapYears++;
            } else {
                increasingGapYears = 0;
            }
            if (increasingGapYears >= MAX_CONSECUTIVE_INCREASING_GAP_YEARS) {
                return -1; // Unaffordable
            }
            previousGap = currentGap;
        }

        return year;
    }
}
