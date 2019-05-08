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
    public static enum TokenType {
            ID, CT_INT, CT_REAL, CT_CHAR, CT_STRING, COMMA, SEMICOLON, LPAR, RPAR, LBRACKET, RBRACKET, LACC, RACC,
        ADD, SUB, MUL, AND, OR, NOT, NOTEQ, LESS, LESSEQ, GREATER, GREATEREQ, ASSIGN, EQUAL, DIV, END
    }

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

}
