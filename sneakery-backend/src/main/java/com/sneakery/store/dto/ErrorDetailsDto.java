package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ErrorDetailsDto {
    private Date timestamp;
    private String message;
    private String details;
}