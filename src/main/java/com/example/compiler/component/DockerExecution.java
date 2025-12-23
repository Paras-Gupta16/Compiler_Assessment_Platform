package com.example.compiler.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import com.example.compiler.entity.OutputModel;
import com.example.compiler.entity.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class DockerExecution {

    @Autowired
    private Docker_Pull_Image dockerPullImage;

    public OutputModel run(TestModel testModel) throws IOException {
        // to get language version and type;
        String []rawVersion = testModel.getLanguageVersion().split(" ");
        String languageName = rawVersion[0];
        String languageVersion = rawVersion[1];

        // start timer
        long startTime = System.currentTimeMillis();
        //code file name
        Path tempDir = Files.createTempDirectory("runner-");
        try{
            String fileName = switch(languageName){
                case "java"->"Main.java";
                case "py"->"main.py";
                case "c"->"main.c";
                case "cpp"-> "main.cpp";
                default -> throw new RuntimeException("Unsupported Language");
            };
            Files.writeString(tempDir.resolve(fileName),testModel.getCode());
            Files.writeString(tempDir.resolve("input.txt"),testModel.getInput() != null ? testModel.getInput() : "");
            //select docker image
            String imageName = switch(languageName){
                case "java" -> "eclipse-temurin:"+languageVersion;
                case "py" -> "python:"+languageVersion;
                case "c","cpp" -> "gcc:"+languageVersion;
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
                case "cpp"->
                         "g++ main.cpp -o main && ./main < input.txt";
                default -> throw new RuntimeException("Unsupported language");
            };

            // run docker command
            dockerPullImage.PullImage(imageName);
            Process process = new ProcessBuilder(
                    "docker","run","--rm",
                    "--network=none",
                    "--memory=256m",
                    "--cpus=0.5",
                    "-v",tempDir.toAbsolutePath()+":/app",
                    "-w", "/app",
                    imageName,
                    "sh", "-c", command)
                    .start();
            StringBuffer str = new StringBuffer();
            Thread outputThread = new Thread(()-> {
                try {
                    str.append(read(process.getInputStream()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Thread errorThread = new Thread(()->{
                try {
                    str.append(read(process.getErrorStream()));
                } catch (
                        Exception e) {
                    throw new RuntimeException(e);
                }
            });
            outputThread.start();
            errorThread.start();

            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if(!finished){
                process.destroyForcibly();
                return new OutputModel("Time Limit","Execution Stop",10*10);
            }
            outputThread.join(1000);
            errorThread.join(1000);
            long endTime = System.currentTimeMillis();
            return new OutputModel("Ok",str.toString(),endTime-startTime);

        }catch(Exception e){
            return new OutputModel("Error",e.getMessage(),0);
        }finally {
            try (java.util.stream.Stream<Path> walk = Files.walk(tempDir)) {
                walk.sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
            } catch (java.io.IOException e) {
                log.error("Failed to delete temp directory: {}", tempDir);
            }
        }
    }
    public String read(InputStream inputStream)throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer str = new StringBuffer();
        String line;
        while((line=bufferedReader.readLine())!=null){
            str.append(line).append("\n");

        }
        return str.toString();
    }
}
