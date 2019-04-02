package ro.adlabs.logging;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.PrintStream;

public class Log {

    private enum Level {
        INFO, WARNING, ERROR
    }

    private static Log instance;

    private static Log getInstance() {
        if(instance == null) {
            instance = new Log();
        }

        return instance;
    }

    private Log() { }

    private void log(String message, Level logLevel) {
        PrintStream ps;
        if(logLevel == Level.ERROR) {
            ps = System.err;
        } else if(logLevel == Level.INFO) {
            ps = System.out;
        } else if(logLevel == Level.WARNING) {
            ps = System.out;
        } else {
            ps = System.err;
        }

        ps.println(message);
    }

    private void log(String message) {
        log(message, Level.INFO);
    }

    public static void error(String message) {
        getInstance().log(message, Level.ERROR);

        System.exit(-1);
    }

    public static void info(String message) {
        getInstance().log(message, Level.INFO);
    }

    public static void warning(String message) {
        getInstance().log(message, Level.WARNING);
    }



}
