package com.example.gitpushchange;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GitHubAPIExample {
    public static void main(String[] args) {
        String accessToken = "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"; // Replace with your GitHub personal access token
        String owner = "sangithakarunanithi"; // Replace with your GitHub username
        String repoName = "FinalRepoDemo"; // Replace with the desired repository name
        String fileName = "sample.txt";
        String commitMessage = "Initial commit";

        try {
            // Create a new repository
            createRepository(owner, repoName, accessToken);

            // Add a file to the repository
            String content = "Hello, GitHub!";
            addFileToRepository(owner, repoName, fileName, content, accessToken);

            // Commit and push the changes
            commitAndPush(owner, repoName, fileName, commitMessage, accessToken);

            System.out.println("Changes committed and pushed successfully.");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void createRepository(String owner, String repoName, String accessToken) throws IOException, JSONException {
        String apiUrl = "https://api.github.com/user/repos";
        JSONObject requestBody = new JSONObject()
            .put("name", repoName)
            .put("private", false); // Set to true if you want a private repository

        sendPostRequest(apiUrl, requestBody.toString(), accessToken);
    }

    public static void addFileToRepository(String owner, String repoName, String fileName, String content, String accessToken) throws IOException, JSONException {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repoName + "/contents/" + fileName;
        JSONObject requestBody = new JSONObject()
            .put("path", fileName)
            .put("message", "Add " + fileName)
            .put("content", java.util.Base64.getEncoder().encodeToString(content.getBytes()));

        sendPutRequest(apiUrl, requestBody.toString(), accessToken);
    }

    public static void commitAndPush(String owner, String repoName, String fileName, String commitMessage, String accessToken) throws IOException, JSONException {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repoName + "/contents/" + fileName;
        JSONObject requestBody = new JSONObject()
            .put("path", fileName)
            .put("message", commitMessage)
            .put("content", "bXkgbmV3IGZpbGUgY29udGVudHM=") // Base64-encoded "my new file contents"
            .put("sha", getFileSha(owner, repoName, fileName, accessToken));

        sendPutRequest(apiUrl, requestBody.toString(), accessToken);
    }

    public static String getFileSha(String owner, String repoName, String fileName, String accessToken) throws IOException, JSONException {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repoName + "/contents/" + fileName;
        String response = sendGetRequest(apiUrl, accessToken);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("sha");
    }

    public static void sendPostRequest(String apiUrl, String requestBody, String accessToken) throws IOException {
        sendHttpRequest(apiUrl, "POST", requestBody, accessToken);
    }

    public static void sendPutRequest(String apiUrl, String requestBody, String accessToken) throws IOException {
        sendHttpRequest(apiUrl, "PUT", requestBody, accessToken);
    }

    public static String sendGetRequest(String apiUrl, String accessToken) throws IOException {
        return sendHttpRequest(apiUrl, "GET", null, accessToken);
    }

    public static String sendHttpRequest(String apiUrl, String method, String requestBody, String accessToken) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (requestBody != null) {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int responseCode = connection.getResponseCode();
        String response;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine);
            }
            response = responseBuilder.toString();
        }

        if (responseCode >= 200 && responseCode < 300) {
            return response;
        } else {
            throw new IOException("Request failed with HTTP error code: " + responseCode + "\nResponse: " + response);
        }
    }
}
