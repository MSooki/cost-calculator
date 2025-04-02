package com.tset.calculator.dto;

public record CalculationRequest(
        float principal,
        float annualInterestRate,
        int timesPerYear,
        int years,
        float monthlyContribution,
        float monthlyContributionIncreaseAnnually,
        float housePrice,
        float annualHousePriceIncrease,
        float mortgageAmount
) {}