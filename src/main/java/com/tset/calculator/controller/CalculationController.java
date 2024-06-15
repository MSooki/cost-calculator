package com.tset.calculator.controller;

import com.tset.calculator.dto.CalculationRequest;
import com.tset.calculator.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/calculation")
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

        if (request.monthlyContribution() > 0) {
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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleInvalidInput(IllegalArgumentException ex) {
        // just return 400 status
    }
}
