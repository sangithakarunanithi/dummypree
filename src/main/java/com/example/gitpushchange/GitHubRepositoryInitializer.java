package com.example.gitpushchange;

import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.service.*;
import org.eclipse.egit.github.core.client.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.*;

import java.io.*;
import java.util.*;

public class GitHubRepositoryInitializer {
    public static void main(String[] args) throws IOException {
        // Replace with your GitHub personal access token
        String accessToken = "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";

        // Initialize GitHub client
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(accessToken);
        RepositoryService repositoryService = new RepositoryService(client);
        DataService dataService = new DataService(client);

        // Repository details
        String owner = "sangithakarunanithi";
        String repoName = "new-repo-adding";
        String projectPath = "https://github.com/sangithakarunanithi/"; // Update this to your project path

        // Create a new repository
        Repository repository = new Repository();
        repository.setName(repoName);
        repository.setPrivate(false); // Set to true if you want a private repository
        repository.setDescription("My new repository");

        try {
            repositoryService.createRepository(owner, repository);
            System.out.println("Repository created successfully.");

            // Initialize Git
            Git git = Git.init().setDirectory(new File(projectPath)).call();
            org.eclipse.jgit.lib.Repository localRepo = git.getRepository();

            // Add all files to the index
            git.add().addFilepattern(".").call();

            // Commit the changes
            git.commit().setMessage("Initial commit").call();

            // Get the list of modified files
            Status status = git.status().call();
            Set<String> modifiedFiles = status.getModified();

            // Push modified files to GitHub
            for (String filePath : modifiedFiles) {
              //     dataService.createFile(owner, repoName, filePath, new File(projectPath, filePath), "Initial commit");
                System.out.println("Pushed: " + filePath);
            }

            System.out.println("Project initialized, committed, and modified files pushed to GitHub.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            throw new RuntimeException(e);
        } catch (UnmergedPathsException e) {
            throw new RuntimeException(e);
        } catch (NoFilepatternException e) {
            throw new RuntimeException(e);
        } catch (WrongRepositoryStateException e) {
            throw new RuntimeException(e);
        } catch (ServiceUnavailableException e) {
            throw new RuntimeException(e);
        } catch (ConcurrentRefUpdateException e) {
            throw new RuntimeException(e);
        } catch (AbortedByHookException e) {
            throw new RuntimeException(e);
        } catch (NoMessageException e) {
            throw new RuntimeException(e);
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
