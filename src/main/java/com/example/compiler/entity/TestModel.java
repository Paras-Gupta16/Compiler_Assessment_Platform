package com.example.compiler.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TestModel {
    private String languageVersion;
    private String code;
    private String input;
}
