package ro.adlabs;

import ro.adlabs.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Compiler {

    private String sourceCode;

    private LexicalAnalyzer analyzer;

    public Compiler(String sourcePath) throws IOException {
        File sourceFile = new File(sourcePath);
        sourceCode = getSourceCode(sourceFile);

        analyzer = new LexicalAnalyzer(sourceCode);
    }

    public boolean compile() {
        boolean codeIsValid = analyzer.analyze();
        if(!codeIsValid) {
            Log.error("Code is not lexically correct");
        }

        System.out.println(sourceCode);

        return true;
    }

    private String getSourceCode(File source) throws IOException {
        FileInputStream fis = new FileInputStream(source);

        byte[] bytes = new byte[fis.available()];
        boolean success = fis.read(bytes) > 0;

        if(!success) {
            Log.error("Cannot read from file");
        }

        return new String(bytes);
    }

}
