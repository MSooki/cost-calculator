package com.tset.calculator.service;

import com.tset.calculator.dto.CalculationResult;
import org.springframework.stereotype.Service;

@Service
public class CalculationService {

    public CalculationResult calculate(float principal, float annualInterestRate, int timesPerYear, int years) {
        double rate = annualInterestRate / 100;
        double A = principal * Math.pow(1 + rate / timesPerYear, timesPerYear * years);
        double interestEarned = A - principal;
        return new CalculationResult(A, principal, interestEarned, 0, -1, -1, -1);
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
        return new CalculationResult(currentBalance, principal, interestEarned, totalContribution, -1, -1, -1);
    }

    /**
     * Calculates compound interest with increasing monthly contributions and determines
     * affordability against an increasing house price, optionally considering a maximum mortgage amount
     * that also increases annually. Includes final mortgage amount in the result.
     */
    public CalculationResult calculate(double principal, double annualInterestRate, int timesPerYear, int years,
                                       double monthlyContribution, double monthlyContributionIncreaseAnnually,
                                       double housePrice, double annualHousePriceIncrease,
                                       double maxMortgageAmount) {

        if (principal < 0 || annualInterestRate < 0 || timesPerYear <= 0 || years < 0 || monthlyContribution < 0 || monthlyContributionIncreaseAnnually < 0 || housePrice <= 0 || annualHousePriceIncrease < 0 || maxMortgageAmount < 0) {
            return new CalculationResult(0, principal, 0, 0, -1, 0, 0);
        }

        double rate = annualInterestRate / 100.0;
        double increaseFactor = 1.0 + (monthlyContributionIncreaseAnnually / 100.0);
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
            currentMonthlyCont *= increaseFactor;
        }
        double interestEarned = currentBalance - principal - totalContribution;

        double housePriceIncreaseRate = annualHousePriceIncrease / 100.0;
        double housePriceAtTheEndOfPeriod = housePrice * Math.pow(1 + housePriceIncreaseRate, years);

        double mortgageAtTheEndOfPeriod = 0.0;
        if (maxMortgageAmount > 0) {
            mortgageAtTheEndOfPeriod = maxMortgageAmount * Math.pow(increaseFactor, years);
        }


        int yearsToBuy = calculateYears(principal, annualInterestRate, timesPerYear,
                monthlyContribution, monthlyContributionIncreaseAnnually,
                housePrice, annualHousePriceIncrease,
                maxMortgageAmount);

        return new CalculationResult(
                currentBalance,
                principal,
                interestEarned,
                totalContribution,
                yearsToBuy,
                housePriceAtTheEndOfPeriod,
                mortgageAtTheEndOfPeriod
        );
    }

    /**
     * Helper method to calculate the number of years needed to afford the house,
     * considering increasing contributions, increasing house price, and optionally an
     * increasing maximum mortgage amount.
     */
    private int calculateYears(double principal, double annualInterestRate, int timesPerYear,
                               double initialMonthlyContribution, double monthlyContributionIncreaseAnnually,
                               double initialHousePrice, double annualHousePriceIncrease,
                               double initialMaxMortgageAmount) {

        if (principal < 0 || annualInterestRate < 0 || timesPerYear <= 0 || initialMonthlyContribution < 0 || monthlyContributionIncreaseAnnually < 0 || initialHousePrice <= 0 || annualHousePriceIncrease < 0 || initialMaxMortgageAmount < 0) {
            return -1;
        }

        double rate = annualInterestRate / 100.0;
        double houseRate = annualHousePriceIncrease / 100.0;
        double increaseFactor = 1.0 + (monthlyContributionIncreaseAnnually / 100.0);

        double currentSavings = principal;
        double currentHousePrice = initialHousePrice;
        double currentMonthlyContribution = initialMonthlyContribution;
        boolean useMortgage = initialMaxMortgageAmount > 0;
        double currentMaxMortgageAmount = useMortgage ? initialMaxMortgageAmount : 0.0;

        int year = 0;

        double previousGap = Double.MAX_VALUE;
        int increasingGapYears = 0;
        final int MAX_CONSECUTIVE_INCREASING_GAP_YEARS = 5;
        final int MAX_SIMULATION_YEARS = 100;

        if (currentSavings + currentMaxMortgageAmount >= currentHousePrice) {
            return 0;
        }

        // Simulation loop
        while (currentSavings + currentMaxMortgageAmount < currentHousePrice) {
            year++;
            if (year > MAX_SIMULATION_YEARS) return -1; // Safety break

            // --- Simulate savings growth for one year ---
            double quarterlyContribution = currentMonthlyContribution * 3;
            for (int q = 0; q < 4; q++) {
                currentSavings += quarterlyContribution;
                double periodsPerQuarter = (double) timesPerYear / 4.0;
                currentSavings *= Math.pow(1 + rate / timesPerYear, periodsPerQuarter);
            }
            // --- End of year's savings simulation ---

            // --- Update values for the start of the NEXT year ---
            currentHousePrice *= (1 + houseRate);
            currentMonthlyContribution *= increaseFactor;
            if (useMortgage) {
                currentMaxMortgageAmount *= increaseFactor;
            }

            // --- Check affordability and convergence ---
            // Re-check affordability *after* all values for the year are updated
            if (currentSavings + currentMaxMortgageAmount >= currentHousePrice) {
                break; // Afford the house at the end of this year
            }

            // Calculate gap considering potential mortgage
            double affordableAmount = currentSavings + currentMaxMortgageAmount;
            double currentGap = currentHousePrice - affordableAmount;

            // Check if the gap is widening persistently
            if (currentGap > previousGap && year > 1) { // Check after year 1
                increasingGapYears++;
            } else {
                increasingGapYears = 0; // Reset counter if gap shrinks or stays same
            }

            if (increasingGapYears >= MAX_CONSECUTIVE_INCREASING_GAP_YEARS) {
                return -1; // Unaffordable condition met
            }
            previousGap = currentGap;
        }

        return year;
    }
}
