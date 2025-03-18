package edu.xyf.middleware.sdk;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class OpenAICodeReview {

    public static void main(String[] args) throws Exception {
        System.out.println("测试执行");

        // 1. 代码检出
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));

        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        StringBuilder diffCode = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = process.waitFor();
        System.out.println("ExitCode with code: " + exitCode);

        System.out.println("评审代码: " + diffCode.toString());
    }

}
