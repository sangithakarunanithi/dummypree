package com.example.gitpushchange;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GitHubCommitAndPush {
    public static void main(String[] args) {
        String username = "sangithakarunanithi";
        String token = "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"; // Generate one in your GitHub account settings
        String repoOwner = "sangithakarunanithi";
        String repoName = "OwnRepo";
        String branch = "main"; // Change to your desired branch
        String commitMessage = "Initial commit"; // Change to your commit message

        try {
            // Base64 encode the authentication credentials
            String auth = username + ":" + token;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            // Create and send a commit request
            URL url = new URL("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/git/refs/heads/" + branch);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("okay");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Extract the latest commit SHA
                String latestCommitSHA = response.toString().split("\"sha\":\"")[1].split("\"")[0];

                // Create a new commit object
                String newCommit = "{\"message\":\"" + commitMessage + "\",\"tree\":\"" + latestCommitSHA + "\"}";

                // Create and send a commit request
                URL commitUrl = new URL("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/git/commits");
                HttpURLConnection commitConn = (HttpURLConnection) commitUrl.openConnection();
                commitConn.setRequestMethod("POST");
                commitConn.setRequestProperty("Authorization", "Basic " + encodedAuth);
                commitConn.setRequestProperty("Content-Type", "application/json");
                commitConn.setDoOutput(true);

                commitConn.getOutputStream().write(newCommit.getBytes(StandardCharsets.UTF_8));

                int commitResponseCode = commitConn.getResponseCode();
                if (commitResponseCode == HttpURLConnection.HTTP_CREATED) {
                    System.out.println("Commit created successfully");

                    // Update the reference to point to the new commit
                    String newCommitSHA = new BufferedReader(new InputStreamReader(commitConn.getInputStream())).readLine();
                    URL updateRefUrl = new URL("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/git/refs/heads/" + branch);
                    HttpURLConnection updateRefConn = (HttpURLConnection) updateRefUrl.openConnection();
                    updateRefConn.setRequestMethod("PATCH");
                    updateRefConn.setRequestProperty("Authorization", "Basic " + encodedAuth);
                    updateRefConn.setRequestProperty("Content-Type", "application/json");
                    updateRefConn.setDoOutput(true);

                    String newRef = "{\"sha\":\"" + newCommitSHA + "\"}";
                    updateRefConn.getOutputStream().write(newRef.getBytes(StandardCharsets.UTF_8));

                    int updateRefResponseCode = updateRefConn.getResponseCode();
                    if (updateRefResponseCode == HttpURLConnection.HTTP_OK) {
                        System.out.println("Reference updated successfully");

                        // Push changes to the repository
                        URL pushUrl = new URL("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/git/refs/heads/" + branch + "/update");
                        HttpURLConnection pushConn = (HttpURLConnection) pushUrl.openConnection();
                        pushConn.setRequestMethod("POST");
                        pushConn.setRequestProperty("Authorization", "Basic " + encodedAuth);
                        pushConn.setDoOutput(true);

                        int pushResponseCode = pushConn.getResponseCode();
                        if (pushResponseCode == HttpURLConnection.HTTP_CREATED) {
                            System.out.println("Pushed changes successfully");
                        } else {
                            System.out.println("Error pushing changes: " + pushResponseCode);
                        }
                    } else {
                        System.out.println("Error updating reference: " + updateRefResponseCode);
                    }
                } else {
                    System.out.println("Error creating commit: " + commitResponseCode);
                }
            } else {
                System.out.println("Error getting latest commit: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
