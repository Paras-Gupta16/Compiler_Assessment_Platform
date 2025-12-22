package com.example.compiler.component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.example.compiler.entity.OutputModel;
import com.example.compiler.entity.TestModel;
import org.springframework.stereotype.Component;


@Component
public class DockerExecution {
    public OutputModel run(TestModel testModel){
        // to get language version and type;
        String []rawVersion = testModel.getLanguageVersion().split(" ");
        String languageName = rawVersion[0];
        String languageVersion = rawVersion[1];

        // start timer
        long startTime = System.currentTimeMillis();
        try{
            //code file name
            Path tempDir = Files.createTempDirectory("runner-");
            String fileName = switch(languageName){
                case "java"->"Main.java";
                case "py"->"main.py";
                case "c"->"main.c";
                default -> throw new RuntimeException("Unsupported Language");
            };
            Files.writeString(tempDir.resolve(fileName),testModel.getCode());

            //select docker image
            String imageName = switch(languageName){
                case "java" -> "openjdk:17";
                case "py" -> "python:3.10";
                case "c" -> "gcc:11";
                default -> throw new RuntimeException("Unsupported Language");
            };
            // build command
            String command = switch (languageName) {
                case "java" ->
                        "javac Main.java && java Main < input.txt";
                case "py" ->
                        "python main.py < input.txt";
                case "c" ->
                        "gcc main.c -o main && ./main < input.txt";
                default -> throw new RuntimeException("Unsupported language");
            };

            // run docker command
            Process process = new ProcessBuilder(
                    "docker","run","--rm",
                    "--network=none","v",tempDir.toAbsolutePath()+":/app",
                    "-w", "/app",
                    imageName,
                    "sh", "-c", command).start();
            process.waitFor();
            String output = read(process.getInputStream())+process.getErrorStream();
            long endTime = System.currentTimeMillis();
            return new OutputModel("OK",output,endTime-startTime);

        }catch(Exception e){
            return new OutputModel("Error",e.getMessage(),0);
        }
    }
    public String read(InputStream inputStream)throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer str = new StringBuffer();
        String line;
        while((line=bufferedReader.readLine())!=null){
            str.append(line);
            str.append("\n");
        }
        return str.toString();
    }
}
