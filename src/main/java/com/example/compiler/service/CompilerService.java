package com.example.compiler.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.example.compiler.component.DockerExecution;
import com.example.compiler.entity.OutputModel;
import com.example.compiler.entity.TestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CompilerService {
    @Autowired
    private DockerExecution dockerExecution;

    @Async("compiler")
    public CompletableFuture<OutputModel> runService(TestModel testModel) throws IOException {
        OutputModel outputModel = dockerExecution.run(testModel);
        return CompletableFuture.completedFuture(outputModel);
    }
}
