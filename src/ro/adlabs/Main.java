package ro.adlabs;

import ro.adlabs.logging.Log;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length < 1) {
            Log.error("C source file not specified");
        }

        Compiler c = new Compiler(args[0]);
        c.compile();
    }
}
