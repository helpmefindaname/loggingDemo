package com.example.logging.interceptor;

import org.slf4j.MDC;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Interceptor for standard output/error
 */
public class LoggerInterceptor extends PrintStream {

    public LoggerInterceptor(OutputStream out) {
        super(out);
    }

    //TODO override the other println methods (avoid print methods)

    @Override
    public void println(String x) {
        final String projectName = MDC.get("projectName");
        if (projectName != null) {
            final String msg = String.format("%s: %s", projectName, x);
            super.println(msg);
            return;
        }
        super.println(x);
    }

    @Override
    public void println(Object x) {
        final String projectName = MDC.get("projectName");
        if (projectName != null) {
            final String msg = String.format("%s: %s", projectName, x);
            super.println(msg);
            return;
        }
        super.println(x);
    }
}
