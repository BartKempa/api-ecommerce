package com.example.apiecommerce.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeProvider {

    public LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }
}
