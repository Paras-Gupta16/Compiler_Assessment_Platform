package com.example.compiler.controller;

import java.util.concurrent.CompletableFuture;

import com.example.compiler.entity.TestModel;
import com.example.compiler.service.CompilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/code")
public class CompilerModel {

    @Autowired
    private CompilerService compilerService;

    @PostMapping("/run")
    public CompletableFuture<ResponseEntity<?>> run(@RequestBody
                                                    TestModel testModel){
        try{
            return compilerService.runService(testModel).thenApply(ResponseEntity::ok);
        }catch (Exception e){
            return CompletableFuture.completedFuture(e.getMessage()).thenApply(ResponseEntity::ok);
        }
    }
}
