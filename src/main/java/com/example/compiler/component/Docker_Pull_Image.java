package com.example.compiler.component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Docker_Pull_Image {
    public void PullImage(String imageName) throws Exception {
        try{
                log.info("Ensuring Docker Image exists: {}", imageName);
                Process process = new ProcessBuilder("docker", "pull", imageName)
                        .redirectErrorStream(true)
                        .start();
            String output = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));
                boolean finished = process.waitFor(2, TimeUnit.MINUTES);

                if (!finished || process.exitValue() != 0) {
                    throw new RuntimeException("Docker pull failed or timed out"+output);
                }
        }catch(Exception e){
            throw new Exception("Failed to Fetch Docker Image: " + imageName + " - " + e.getMessage());
        }
    }
}
