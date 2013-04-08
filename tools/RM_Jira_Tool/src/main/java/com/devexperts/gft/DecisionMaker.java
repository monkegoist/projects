package com.devexperts.gft;

import com.google.common.base.Joiner;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DecisionMaker {

    private static final Logger log = Logger.getLogger(DecisionMaker.class);

    private static final int NUM_THREADS = 4; // number of threads that will be checking projects

    public static void main(String[] args) {

        log.info("Starting application...");

        // 1. Read configuration
        Configuration configuration = Configuration.getInstance();

        // 2. Authenticate
        Authenticator authenticator = new Authenticator(configuration);
        DefaultHttpClient httpClient;
        try {
            httpClient = authenticator.authenticate();
        } catch (Exception e) {
            log.error("Failed to login to Jira!!!");
            throw new RuntimeException(e);
        }

        // 3. Process
        ConcurrentMap<JiraProject, Boolean> results = new ConcurrentHashMap<JiraProject, Boolean>();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future> tasks = new ArrayList<Future>();
        for (JiraProject jiraProject : configuration.getProjects()) {
            tasks.add(
                    executor.submit(new ProjectChecker(
                            configuration,
                            jiraProject,
                            results,
                            httpClient
                    ))
            );
        }
        executor.shutdown();

        for (Future task : tasks) {
            try {
                task.get(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error("Failed to get information for project", e);
                task.cancel(true);
            } catch (ExecutionException e) {
                log.error("Failed to get information for project", e);
            } catch (TimeoutException e) {
                log.error("Timeout exception occurred", e);
            }
        }

        // 4. Output results
        log.info("Results of processing:");
        for (JiraProject project : results.keySet()) {
            log.info(project.getName() + ": " + results.get(project));
        }

        // 5. Save results
        if (results.values().contains(true)) {
            PrintWriter writer = null;
            try {
                File file = new File(configuration.getResultsFile());
                file.delete();
                writer = new PrintWriter(new FileWriter(file));
                List<String> list = new ArrayList<String>();
                for (JiraProject project : results.keySet()) {
                    if (results.get(project))
                        list.add(project.getName());
                }
                writer.print(Joiner.on(", ").join(list));
            } catch (IOException e) {
                log.error("Failed to save results to a file!");
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }
}