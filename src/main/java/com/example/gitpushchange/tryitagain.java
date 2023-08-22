package com.example.gitpushchange;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class tryitagain {

        public static void main(String[] args) {
            String accessToken = "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";
            String repoName = "create a -repo-name";
           // String projectPath = "C:\\Users\\Admin\\Downloads\\gitpushchange\\gitpushchange";

            String projectPath = "C:\\Users\\Admin\\Downloads\\gitpushchange\\gitpushchange";

            try {
                // Step 1: Create a new repository using GitHub API
                HttpClient httpClient = HttpClients.createDefault();
                HttpPost request = new HttpPost("https://api.github.com/user/repos");
                request.addHeader("Authorization", "Bearer " + accessToken);
                StringEntity params = new StringEntity("{\"name\": \"" + repoName + "\"}");
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);

                // Step 2: Clone the newly created repository
                Git.cloneRepository()
                    .setURI("https://github.com/sangithakarunanithi/" + repoName + ".git")
                    .setDirectory(new File(projectPath))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, ""))
                    .call();

                // Step 3: Commit and push your project to the new repository
                try (Repository repository = Git.open(new File(projectPath + "/.git")).getRepository()) {
                    Git git = new Git(repository);
                    git.add().addFilepattern(".").call();
                    git.commit().setMessage("Initial commit").call();
                    git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, "")).call();
                } catch (GitAPIException e) {
                    e.printStackTrace();
                }
            } catch (IOException | GitAPIException e) {
                e.printStackTrace();
            }
        }
    }

