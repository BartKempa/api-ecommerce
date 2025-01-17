package com.example.apiecommerce.domain;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataTimeProvider {

    public LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }
}
