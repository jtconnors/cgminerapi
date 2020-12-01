package com.jtconnors.cgminerapi;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Util {
    
    /**
     * Return the stack trace associacted with an {@code Exception}
     * in {@code String} form
     *
     * @param e the {@code Excpetion}
     * @return the Exception's stack trace in {@code String} form
     */
    public static String exceptionStackTraceToString(Throwable e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();    
    }
}