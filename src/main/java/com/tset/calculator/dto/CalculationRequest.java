package com.tset.calculator.dto;

public record CalculationRequest(
        double principal,
        double annualInterestRate,
        int timesPerYear,
        int years,
        double monthlyContribution,
        double monthlyContributionIncreaseAnnually,
        double housePrice,
        double annualHousePriceIncrease,
        double mortgageAmount,
        double mortgageIncreaseAnnually
) {}