package com.example.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
@Slf4j
public class DemoApplication {

    private final static Logger logger = LoggerFactory.getLogger("threadCall");

    public static void main(String[] args) {
        System.setErr(new LoggerInterceptor(System.err));
        System.setOut(new LoggerInterceptor(System.out));

        SpringApplication.run(DemoApplication.class, args);

        for (int i = 0; i < 5; ++i) {
            final int index = i;
            Runnable runnable = () -> {

                final String projectName = String.format("Project %d", index);

                MDC.put("projectName", projectName);

                logThroughStdErr(projectName);
                desyncThreads();
                logThroughLogback(projectName);
                spawnChildThreadAndLog(projectName);
//                MDC.remove("projectName"); optional
            };
            new Thread(runnable).start();
        }
    }

    private static void desyncThreads() {
        Random random = new Random();
        int randInt = random.nextInt(500);
        try {
            Thread.sleep(randInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.warn(String.format("Waited for %d ms", randInt));
    }

    /**
     * Use MDC
     */
    private static void logThroughLogback(String projectName) {
        logger.info("Info from: " + projectName);
        logger.warn("Warn from: " + projectName);
    }

    /**
     * Use LoggerInterceptor
     */
    private static void logThroughStdErr(String projectName) {
        try {
            Integer.parseInt(projectName);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Injecting the project name only works in following cases:
     * 1. You control the thread creation and can manipulate MDC in the child thread
     * 2. You can decide in a LoggerInterceptor which project is logging (in this case you wouldn't need MDC at all)
     */
    private static void spawnChildThreadAndLog(String projectName) {
        Runnable runnable = () -> {
            String msg = String.format("Child thread spawned from %s", projectName);
            logger.info(msg);
            System.out.println(msg);
        };
        new Thread(runnable).start();
    }
}
