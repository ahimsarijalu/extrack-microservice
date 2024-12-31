package com.ahimsarijalu.fund_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FundNotFoundException extends RuntimeException {
    public FundNotFoundException(String id) {
        super("Fund " + id + " Not Found");
    }
}
