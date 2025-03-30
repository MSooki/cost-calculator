package com.tset.calculator.controller;

import com.tset.calculator.dto.CalculationRequest;
import com.tset.calculator.dto.CalculationResult;
import com.tset.calculator.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculation")
@CrossOrigin(origins = {"http://localhost:3000", "https://cost-calculator-production.up.railway.app", "https://savematic.pro/"})
public class CalculationController {

    @Autowired
    private CalculationService calculationService;

    @PostMapping("/calculate")
    public CalculationResult calculation(@RequestBody CalculationRequest request) {
        if (request.principal() < 0 || request.annualInterestRate() < 0 || request.timesPerYear() < 0 || request.years() < 0) {
            throw new IllegalArgumentException("Invalid input values");
        }

        if (request.housePrice() > 0 && request.annualHousePriceIncrease() > 0) {
            if (request.monthlyContribution() > 0) {
                return calculationService.calculate(
                        request.principal(),
                        request.annualInterestRate(),
                        request.timesPerYear(),
                        request.years(),
                        request.monthlyContribution(),
                        request.housePrice(),
                        request.annualHousePriceIncrease()
                );
            } else {
                throw new IllegalArgumentException("Monthly contribution is required when calculating house affordability.");
            }
        }
        else if (request.monthlyContribution() > 0) {
            return calculationService.calculate(
                    request.principal(),
                    request.annualInterestRate(),
                    request.timesPerYear(),
                    request.years(),
                    request.monthlyContribution()
            );
        } else {
            return calculationService.calculate(
                    request.principal(),
                    request.annualInterestRate(),
                    request.timesPerYear(),
                    request.years()
            );
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleInvalidInput(IllegalArgumentException ex) {
        // just return 400 status
    }
}
