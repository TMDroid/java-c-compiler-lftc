package ro.adlabs.tokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*

typedef struct _Token{
    int code;//(numele)
    union{
        char *text;
        long int i;// folosit pentru CT_INT, CT_CHAR
        double r;// folosit pentru CT_REAL
    };
    int line; // linia din fisierul de intrare
    struct _Token *next; // inlantuire la urmatorul AL
}Token;


 */
public class Token {
    private static final Map<TokenType, String> tokenStrings = new HashMap<TokenType, String>() {{
        put(TokenType.ID, "ID");
        put(TokenType.CT_INT, "CT_INT");
        put(TokenType.CT_REAL, "CT_REAL");
        put(TokenType.CT_CHAR, "CT_CHAR");
        put(TokenType.CT_STRING, "CT_STRING");
        put(TokenType.DOT, "DOT");
        put(TokenType.COMMA, "COMMA");
        put(TokenType.SEMICOLON, "SEMICOLON");
        put(TokenType.LPAR, "LPAR");
        put(TokenType.RPAR, "RPAR");
        put(TokenType.LBRACKET, "LBRACKET");
        put(TokenType.RBRACKET, "RBRACKET");
        put(TokenType.LACC, "LACC");
        put(TokenType.RACC, "RACC");
        put(TokenType.ADD, "ADD");
        put(TokenType.SUB, "SUB");
        put(TokenType.MUL, "MUL");
        put(TokenType.AND, "AND");
        put(TokenType.OR, "OR");
        put(TokenType.NOT, "NOT");
        put(TokenType.NOTEQ, "NOTEQ");
        put(TokenType.LESS, "LESS");
        put(TokenType.LESSEQ, "LESSEQ");
        put(TokenType.GREATER, "GREATER");
        put(TokenType.GREATEREQ, "GREATEREQ");
        put(TokenType.ASSIGN, "ASSIGN");
        put(TokenType.EQUAL, "EQUAL");
        put(TokenType.DIV, "DIV");
        put(TokenType.END, "END");
        put(TokenType.BREAK, "BREAK");
        put(TokenType.CHAR, "CHAR");
        put(TokenType.DOUBLE, "DOUBLE");
        put(TokenType.ELSE, "ELSE");
        put(TokenType.FOR, "FOR");
        put(TokenType.IF, "IF");
        put(TokenType.INT, "INT");
        put(TokenType.RETURN, "RETURN");
        put(TokenType.STRUCT, "STRUCT");
        put(TokenType.VOID, "VOID");
        put(TokenType.WHILE, "WHILE");
    }};
    protected TokenType code;
    protected Object value;
    protected Integer line;

    public Token(TokenType code, Object value, Integer line) {
        this.code = code;
        this.value = value;
        this.line = line;
    }

    public Token() {
        this.code = TokenType.END;
        this.value = null;
        this.line = -1;
    }

    public static String getKeywordToken(String code) {
        List<String> keywordsLowerCase = new ArrayList<String>() {{
            add("break");
            add("char");
            add("double");
            add("else");
            add("for");
            add("if");
            add("int");
            add("return");
            add("struct");
            add("void");
            add("while");
        }};

        if (keywordsLowerCase.contains(code.toLowerCase())) {
            return keywordsLowerCase.get(keywordsLowerCase.indexOf(code.toLowerCase())).toUpperCase();
        }

        return null;
    }

    public static Map<TokenType, String> getTokenStrings() {
        return tokenStrings;
    }

    public static TokenType getTokenTypeForTokenString(String tokenString) {
        for (Map.Entry<TokenType, String> entry : tokenStrings.entrySet()) {
            if (entry.getValue().equals(tokenString)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public boolean is(TokenType tokenType) {
        return this.code == tokenType;
    }

    public TokenType getCode() {
        return code;
    }

    public Integer getLine() {
        return line;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        String payload = String.format("Line %d => %s", line, getTokenString());
        if(isSomeFormOfConstact()) {
            payload = String.format("%s: %s", payload, String.valueOf(value));
        }

        return payload;
    }

    private boolean isSomeFormOfConstact() {
        return code == TokenType.CT_CHAR || code == TokenType.CT_INT || code == TokenType.ID
                || code == TokenType.CT_REAL || code == TokenType.CT_STRING;
    }

    public String getTokenString() {
        if (tokenStrings.containsKey(code)) {
            return tokenStrings.get(code);
        }

        return "";
    }

    public static enum TokenType {
        ID, CT_INT, CT_REAL, CT_CHAR, CT_STRING, DOT, COMMA, SEMICOLON, LPAR, RPAR, LBRACKET, RBRACKET, LACC, RACC,
        ADD, SUB, MUL, AND, OR, NOT, NOTEQ, LESS, LESSEQ, GREATER, GREATEREQ, ASSIGN, EQUAL, DIV, END,
        BREAK, CHAR, DOUBLE, ELSE, FOR, IF, INT, RETURN, STRUCT, VOID, WHILE
    }
}
