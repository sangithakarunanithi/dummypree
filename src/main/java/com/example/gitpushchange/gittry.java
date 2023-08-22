package com.example.gitpushchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class gittry {

    public static void main(String[] args) {
        try {
            // GitHub API endpoints
            String apiUrl = "https://api.github.com/user/repos";
            String accessToken = "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"; // Replace with your access token

            // Repository details
            String repoName = "try1"; // Replace with your desired repository name
            String projectPath = "C:\\Users\\Admin\\Downloads\\gitpushchange\\gitpushchange"; // Replace with your project path

            // Step 1: Create a new repository
            createGitHubRepository(apiUrl, accessToken, repoName);

            // Step 2: Initialize a Git repository locally
            initializeLocalGitRepo(repoName);

            // Step 3: Push the project to the new repository
            pushProjectToGitHub(projectPath, repoName);

            System.out.println("Repository creation and project push completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createGitHubRepository(String apiUrl, String accessToken, String repoName) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = "{\"name\":\"" + repoName + "\"}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 201) {
            System.out.println("Repository created successfully.");
        } else {
            throw new IOException("Failed to create repository. Response code: " + responseCode);
        }

        connection.disconnect();
    }

    private static void initializeLocalGitRepo(String repoName) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("git", "init");
        builder.directory(new File(repoName));
        Process process = builder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Failed to initialize local Git repository.");
        }
        System.out.println("Local Git repository initialized.");
    }

    private static void pushProjectToGitHub(String projectPath, String repoName) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("git", "add", ".");
        builder.directory(new File(projectPath));
        Process process = builder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Failed to add files to Git index.");
        }

        builder = new ProcessBuilder("git", "commit", "-m", "Initial commit");
        builder.directory(new File(projectPath));
        process = builder.start();
        exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Failed to commit files to Git repository.");
        }

        String remoteUrl = "https://github.com/sangithakarunanithi/" + repoName + ".git"; // Replace with your GitHub username
        builder = new ProcessBuilder("git", "remote", "add", "origin", remoteUrl);
        builder.directory(new File(projectPath));
        process = builder.start();
        exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Failed to add remote origin.");
        }

        builder = new ProcessBuilder("git", "push", "-u", "origin", "master");
        builder.directory(new File(projectPath));
        process = builder.start();
        exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Failed to push files to GitHub repository.");
        }

        System.out.println("Project pushed to GitHub repository.");
    }
}

