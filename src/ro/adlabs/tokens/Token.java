package ro.adlabs.tokens;

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
    protected TokenType code;
    protected Object value;
    protected Integer line;

    public Token(TokenType code, Object value, Integer line) {
        this.code = code;
        this.value = value;
        this.line = line;
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
        String payload = "";
        if (code == TokenType.CT_CHAR || code == TokenType.CT_INT || code == TokenType.ID
                || code == TokenType.CT_REAL || code == TokenType.CT_STRING) {
            payload = String.format(":%s", String.valueOf(value));
        }

        return getTokenString() + payload;
    }

    private String getTokenString() {
        /*
        ID, CT_INT, CT_REAL, CT_CHAR, CT_STRING, COMMA, SEMICOLON, LPAR, RPAR, LBRACKET, RBRACKET, LACC, RACC,
        ADD, SUB, MUL, AND, OR, NOT, NOTEQ, LESS, LESSEQ, GREATER, GREATEREQ, ASSIGN, EQUAL, DIV, END
         */

        switch (code) {
            case ID:
                return "ID";
            case CT_INT:
                return "CT_INT";
            case CT_REAL:
                return "CT_REAL";
            case CT_CHAR:
                return "CT_CHAR";
            case CT_STRING:
                return "CT_STRING";
            case COMMA:
                return "COMMA";
            case SEMICOLON:
                return "SEMICOLON";
            case LPAR:
                return "LPAR";
            case RPAR:
                return "RPAR";
            case LBRACKET:
                return "LBRACKET";
            case RBRACKET:
                return "RBRACKET";
            case LACC:
                return "LACC";
            case RACC:
                return "RACC";
            case ADD:
                return "ADD";
            case SUB:
                return "SUB";
            case MUL:
                return "MUL";
            case AND:
                return "AND";
            case OR:
                return "OR";
            case NOT:
                return "NOT";
            case NOTEQ:
                return "NOTEQ";
            case LESS:
                return "LESS";
            case LESSEQ:
                return "LESSEQ";
            case GREATER:
                return "GREATER";
            case GREATEREQ:
                return "GREATEREQ";
            case ASSIGN:
                return "ASSIGN";
            case EQUAL:
                return "EQUAL";
            case DIV:
                return "DIV";
            case END:
                return "END";
        }

        return "";
    }

    public static enum TokenType {
        ID, CT_INT, CT_REAL, CT_CHAR, CT_STRING, COMMA, SEMICOLON, LPAR, RPAR, LBRACKET, RBRACKET, LACC, RACC,
        ADD, SUB, MUL, AND, OR, NOT, NOTEQ, LESS, LESSEQ, GREATER, GREATEREQ, ASSIGN, EQUAL, DIV, END
    }
}
