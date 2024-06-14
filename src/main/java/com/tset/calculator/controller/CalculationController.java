package com.tset.calculator.controller;

import com.tset.calculator.dto.*;
import com.tset.calculator.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/calculation")
public class CalculationController {

    @Autowired
    private CalculationService calculationService;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateCalculation(@RequestBody CalculationRequest request) {
        if (request.principal() <= 0 || request.annualInterestRate() <= 0 || request.timesPerYear() <= 0 || request.years() <= 0) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid input values."));
        }

        String requestId = UUID.randomUUID().toString();
        if (request.monthlyContribution() > 0) {
            calculationService.calculate(requestId, request.principal(), request.annualInterestRate(),
                    request.timesPerYear(), request.years(), request.monthlyContribution());
        } else {
            calculationService.calculate(requestId, request.principal(), request.annualInterestRate(),
                    request.timesPerYear(), request.years());
        }

        return ResponseEntity.ok(new CalculationResponse(requestId));
    }

    @GetMapping("/result/{requestId}")
    public ResponseEntity<?> getResult(@PathVariable String requestId) {
        Float result = (float) calculationService.getResult(requestId);
        if (result == null) {
            return ResponseEntity.accepted().body(new MessageResponse("Result not ready yet"));
        }
        return ResponseEntity.ok(new ResultResponse(requestId, result));
    }
}
