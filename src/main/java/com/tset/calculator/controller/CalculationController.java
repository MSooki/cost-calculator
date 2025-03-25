package com.tset.calculator.controller;

import com.tset.calculator.dto.CalculationRequest;
import com.tset.calculator.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/calculation")
@CrossOrigin(origins = {"http://localhost:3000", "https://cost-calculator-production.up.railway.app"})
public class CalculationController {

    @Autowired
    private CalculationService calculationService;
    private final AtomicInteger counterId = new AtomicInteger(0);

    @PostMapping("/initiate")
    public int initiateCalculation(@RequestBody CalculationRequest request) {
        if (request.principal() <= 0 || request.annualInterestRate() <= 0 || request.timesPerYear() <= 0 || request.years() <= 0) {
            throw new IllegalArgumentException("Invalid input values");
        }

        int requestId = counterId.incrementAndGet();

        if (request.housePrice() > 0 && request.annualHousePriceIncrease() > 0) {
            if (request.monthlyContribution() > 0) {
                calculationService.calculate(requestId, request.principal(), request.annualInterestRate(),
                        request.timesPerYear(), request.monthlyContribution(), request.housePrice(), request.annualHousePriceIncrease());
            } else {
                throw new IllegalArgumentException("Monthly contribution is required when calculating house affordability.");
            }
        }
        else if (request.monthlyContribution() > 0) {
            calculationService.calculate(requestId, request.principal(), request.annualInterestRate(),
                    request.timesPerYear(), request.years(), request.monthlyContribution());
        } else {
            calculationService.calculate(requestId, request.principal(), request.annualInterestRate(),
                    request.timesPerYear(), request.years());
        }

        return requestId;
    }

    @GetMapping("/result/{requestId}")
    public Float getResult(@PathVariable int requestId) {
        return (float) calculationService.getResult(requestId);
    }

    @GetMapping("/result/year/{requestId}")
    public Integer getResultYear(@PathVariable int requestId) {
        return calculationService.getResultYear(requestId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleInvalidInput(IllegalArgumentException ex) {
        // just return 400 status
    }
}
