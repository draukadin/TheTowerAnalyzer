package com.pphi.tower.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String id) {
        super("Report not found: " + id);
    }
}
