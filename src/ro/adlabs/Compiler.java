package ro.adlabs;

import ro.adlabs.logging.Log;
import ro.adlabs.tokens.Token;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Compiler {

    private String sourceCode;

    private LexicalAnalyzer lexicalAnalyzer;
    private SyntacticAnalyzer syntacticAnalyzer;

    public Compiler(String sourcePath) throws IOException {
        File sourceFile = new File(sourcePath);
        sourceCode = getSourceCode(sourceFile);

        lexicalAnalyzer = new LexicalAnalyzer(sourceCode);
    }

    public boolean compile() {
        boolean codeIsValid = lexicalAnalyzer.analyze();
        if(!codeIsValid) {
            Log.error("Code is not lexically correct");
        }

        syntacticAnalyzer = new SyntacticAnalyzer(lexicalAnalyzer.getTokens());
        boolean syntacticallyCorrect = syntacticAnalyzer.analyze();
        printTokens();

        return true;
    }

    private void printTokens() {
        for(Token tk : lexicalAnalyzer.getTokens()) {
            System.out.println(tk);
        }
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
