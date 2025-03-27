package com.tset.calculator.dto;

public record CalculationResult (
        double totalInvestmentValue,
        double initialInvestment,
        double interestEarned,
        double totalContribution,
        double years,
        double housePriceAtTheEndOfPeriod
){}
