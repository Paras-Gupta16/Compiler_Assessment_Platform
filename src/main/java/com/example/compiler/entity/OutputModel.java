package com.example.compiler.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class OutputModel {
    private String status;
    private String output;
    private long timeMs;


}
