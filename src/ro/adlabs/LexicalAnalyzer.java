package ro.adlabs;

import ro.adlabs.tokens.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexicalAnalyzer implements Analyzer {

    private List<Token> tokens;
    private Map<Character, Integer> initialStateTransitions = new HashMap<Character, Integer>() {{
        put(' ', States.STATE_INITIAL);
        put('\n', States.STATE_INITIAL);
        put('\r', States.STATE_INITIAL);
        put('\t', States.STATE_INITIAL);
        put(',', States.STATE_COMMA_25);
        put(';', States.STATE_SEMICOLON_26);
        put('(', States.STATE_LPAR_27);
        put(')', States.STATE_RPAR_28);
        put('[', States.STATE_LBRACKET_29);
        put(']', States.STATE_RBRACKET_30);
        put('{', States.STATE_LACC_31);
        put('}', States.STATE_RACC_32);
        put('+', States.STATE_ADD_34);
        put('-', States.STATE_SUB_35);
        put('*', States.STATE_MUL_36);
        put('&', States.STATE_AND_39);
        put('|', States.STATE_OR_41);
        put('!', States.STATE_NOT_43);
        put('<', States.STATE_LESS_46);
        put('>', States.STATE_GREATER_49);
        put('=', States.STATE_ASSIGN_52);
        put('"', States.STATE_STRING_15);
        put('\'', States.STATE_CHAR_20);
        put('/', States.STATE_COMMENT_BLOCK_55);
        put('.', States.STATE_DOT_24);
    }};
    private String sourceCode;
    private Integer index = 0;

    public LexicalAnalyzer(String source) {
        this.sourceCode = source;
        this.tokens = new ArrayList<>();
    }

    @Override
    public String toString() {
        return tokens.toString();
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public boolean analyze() {
        Integer state = States.STATE_INITIAL;
        Integer identifierStartIndex = -1;
        Integer currentLine = 1;

        while (true) {
            Character currentCharacter = null;
            try {
                currentCharacter = sourceCode.charAt(index);
            } catch (IndexOutOfBoundsException e) {}

            switch (state) {
                case States.STATE_INITIAL: // inceput de ID
                    // verific daca caracterul e in map-ul de tranzitii din starea de inceput 0
                    if (initialStateTransitions.containsKey(currentCharacter)) {
                        state = initialStateTransitions.get(currentCharacter);

                        if (currentCharacter == '\n') {
                            currentLine++;
                        }
                    } else {
                        if (identifierStartIndex == -1) {
                            identifierStartIndex = index;
                        }

                        if (isIdentifierStart(currentCharacter)) {

                            state = States.STATE_ID_1;

                        } else if (isStartOfNumber(currentCharacter)) { //is digit..

                            if (numberStartsWithZero(currentCharacter)) {

                                state = States.STATE_NUMERIC_5; // if it starts with 0 then it might be octal or hex

                            } else {

                                state = States.STATE_NUMERIC_3; // number is decimal

                            }
                        }

                    }

                    index++;
                    break;

                case States.STATE_ID_1:
                    if (!isContinuationOfIdentifier(currentCharacter)) {
                        state = States.STATE_ID_2_FINAL;
                        continue;
                    }
                    index++;
                    break;

                case States.STATE_ID_2_FINAL:
                    String identifier = sourceCode.substring(identifierStartIndex, index);
                    identifierStartIndex = -1;

                    String keyword = getKeywordToken(identifier);
                    if(keyword != null) {
                        Token.TokenType token = Token.getTokenTypeForTokenString(keyword);
                        createToken(token, keyword, currentLine);
                    } else {
                        createToken(Token.TokenType.ID, identifier, currentLine);
                    }

                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_NUMERIC_3:
                    if (identifierStartIndex == -1) {
                        identifierStartIndex = index;
                    }

                    if (currentCharacter == '.') {
                        state = States.STATE_NUMERIC_10;
                    } else if (isExponent(currentCharacter)) {
                        state = States.STATE_NUMERIC_9;
                    } else if (!Character.isDigit(currentCharacter)) {
                        state = States.STATE_INT_4_FINAL;
                        continue;
                    }
                    index++;
                    break;

                case States.STATE_INT_4_FINAL:
                    Number intValue = convertStringToIntegerConsideringBase(sourceCode.substring(identifierStartIndex, index));
                    identifierStartIndex = -1;
                    createToken(Token.TokenType.CT_INT, intValue, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_NUMERIC_5:
                    if (identifierStartIndex == -1) {
                        identifierStartIndex = index;
                    }

                    if (currentCharacter == 'x') { // is hex
                        state = States.STATE_NUMERIC_6;
                    } else if (isExponent(currentCharacter)) {
                        state = States.STATE_NUMERIC_9;
                    } else {
                        state = States.STATE_NUMERIC_7;
                    }
                    index++;
                    break;

                case States.STATE_NUMERIC_6:
                    if (!isHexDigit(currentCharacter)) {
                        state = States.STATE_INT_4_FINAL;
                        continue;
                    }
                    index++;
                    break;


                case States.STATE_NUMERIC_7:
                    if (currentCharacter == '.') {
                        state = States.STATE_NUMERIC_10;
                    } else if (currentCharacter == '8' || currentCharacter == '9') {
                        state = States.STATE_NUMERIC_8;
                    } else if (currentCharacter >= '0' && currentCharacter <= '7') { // still octal
                        state = States.STATE_NUMERIC_7;
                    } else if (isExponent(currentCharacter)) {
                        state = States.STATE_NUMERIC_9;
                    } else {
                        state = States.STATE_INT_4_FINAL;
                        continue;
                    }
                    index++;
                    break;

                case States.STATE_NUMERIC_8:
                    if (currentCharacter == '.') {
                        state = States.STATE_NUMERIC_10;
                    } else if (isExponent(currentCharacter)) {
                        state = States.STATE_NUMERIC_9;
                    }
                    index++;
                    break;

                case States.STATE_NUMERIC_9:
                    if (Character.isDigit(currentCharacter)) {
                        state = States.STATE_NUMERIC_14;
                    } else if (currentCharacter == '+' || currentCharacter == '-') {
                        state = States.STATE_NUMERIC_13;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_NUMERIC_10:
                    if (Character.isDigit(currentCharacter)) {
                        state = States.STATE_NUMERIC_11;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_NUMERIC_11:
                    if (isExponent(currentCharacter)) {
                        state = States.STATE_NUMERIC_9;
                    } else if (Character.isDigit(currentCharacter)) {
                        state = States.STATE_NUMERIC_11;
                    } else {
                        state = States.STATE_REAL_12_FINAL;
                        break;
                    }
                    index++;
                    break;

                case States.STATE_REAL_12_FINAL:
                    Number floatValue = convertStringToIntegerConsideringBase(sourceCode.substring(identifierStartIndex, index));
                    identifierStartIndex = -1;
                    createToken(Token.TokenType.CT_REAL, floatValue, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_NUMERIC_13:
                    if (Character.isDigit(currentCharacter)) {
                        state = States.STATE_NUMERIC_14;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_NUMERIC_14:
                    if (!Character.isDigit(currentCharacter)) {
                        state = States.STATE_REAL_12_FINAL;
                        break;
                    }

                    index++;
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_STRING_15:
                    if (identifierStartIndex == -1) {
                        identifierStartIndex = index;
                    }

                    if (currentCharacter == '\\') {
                        state = States.STATE_STRING_16;
                    } else if (currentCharacter != '"') {
                        state = States.STATE_STRING_18;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_STRING_16:
                    if (isEscape(currentCharacter)) {
                        state = States.STATE_STRING_18;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_STRING_18:
                    if (currentCharacter == '"') {
                        state = States.STATE_STRING_19_FINAL;
                        index++;
                    } else {
                        state = States.STATE_STRING_15;
                    }
                    break;

                case States.STATE_STRING_19_FINAL:
                    String theString = replaceEscapedCharacters(sourceCode.substring(identifierStartIndex, index - 1));
                    identifierStartIndex = -1;
                    createToken(Token.TokenType.CT_STRING, theString, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_CHAR_20:
                    if (identifierStartIndex == -1) {
                        identifierStartIndex = index;
                    }

                    if (currentCharacter == '\\') {
                        state = States.STATE_CHAR_17;
                    } else if (currentCharacter != '\'') {
                        state = States.STATE_CHAR_21;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_CHAR_21:
                    if (currentCharacter == '\'') {
                        state = States.STATE_CHAR_23_FINAL;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_CHAR_17:
                    if (isEscape(currentCharacter)) {
                        state = States.STATE_CHAR_22;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_CHAR_22:
                    if (currentCharacter == '\'') {
                        state = States.STATE_CHAR_23_FINAL;
                    } else {
                        cannotHaveAnotherValue(currentCharacter);
                    }
                    index++;
                    break;

                case States.STATE_CHAR_23_FINAL:
                    String theChar = replaceEscapedCharacters(sourceCode.substring(identifierStartIndex, index - 1));
                    identifierStartIndex = -1;
                    createToken(Token.TokenType.CT_CHAR, theChar, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_DOT_24:
                    Character c = '.';
                    createToken(Token.TokenType.DOT, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_COMMA_25:
                    c = ',';
                    createToken(Token.TokenType.COMMA, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_SEMICOLON_26:
                    c = ';';
                    createToken(Token.TokenType.SEMICOLON, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_LPAR_27:
                    c = '(';
                    createToken(Token.TokenType.LPAR, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_RPAR_28:
                    c = ')';
                    createToken(Token.TokenType.RPAR, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_LBRACKET_29:
                    c = '[';
                    createToken(Token.TokenType.LBRACKET, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_RBRACKET_30:
                    c = ']';
                    createToken(Token.TokenType.RBRACKET, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_LACC_31:
                    c = '{';
                    createToken(Token.TokenType.LACC, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_RACC_32:
                    c = '}';
                    createToken(Token.TokenType.RACC, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_ADD_34:
                    c = '+';
                    createToken(Token.TokenType.ADD, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_SUB_35:
                    c = '-';
                    createToken(Token.TokenType.SUB, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_MUL_36:
                    c = '*';
                    createToken(Token.TokenType.MUL, c, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_DIV_37_FINAL:
                    createToken(Token.TokenType.DIV, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_AND_39:
                    if (currentCharacter != '&') {
                        cannotHaveAnotherValue(currentCharacter);
                    }

                    index++;
                    state = States.STATE_AND_40_FINAL;
                    break;

                case States.STATE_AND_40_FINAL:
                    createToken(Token.TokenType.AND, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_OR_41:
                    if (currentCharacter != '|') {
                        cannotHaveAnotherValue(currentCharacter);
                    }

                    index++;
                    state = States.STATE_OR_42_FINAL;
                    break;

                case States.STATE_OR_42_FINAL:
                    createToken(Token.TokenType.OR, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_NOT_43:
                    if (currentCharacter == '=') {
                        state = States.STATE_NOTEQ_45_FINAL;
                        index++;
                    } else {
                        state = States.STATE_NOT_44_FINAL;
                    }
                    break;

                case States.STATE_NOT_44_FINAL:
                    createToken(Token.TokenType.NOT, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_NOTEQ_45_FINAL:
                    createToken(Token.TokenType.NOTEQ, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_LESS_46:
                    if (currentCharacter == '=') {
                        state = States.STATE_LESSEQ_48_FINAL;
                        index++;
                    } else {
                        state = States.STATE_LESS_47_FINAL;
                    }
                    break;

                case States.STATE_LESS_47_FINAL:
                    createToken(Token.TokenType.LESS, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_LESSEQ_48_FINAL:
                    createToken(Token.TokenType.LESSEQ, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_GREATER_49:
                    if (currentCharacter == '=') {
                        state = States.STATE_GREATEREQ_51_FINAL;
                        index++;
                    } else {
                        state = States.STATE_GREATER_50_FINAL;
                    }
                    break;


                case States.STATE_GREATER_50_FINAL:
                    createToken(Token.TokenType.GREATER, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_GREATEREQ_51_FINAL:
                    createToken(Token.TokenType.GREATEREQ, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_ASSIGN_52:
                    if (currentCharacter == '=') {
                        state = States.STATE_EQUAL_54_FINAL;
                        index++;
                    } else {
                        state = States.STATE_ASSIGN_53_FINAL;
                    }
                    break;


                case States.STATE_ASSIGN_53_FINAL:
                    createToken(Token.TokenType.ASSIGN, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_EQUAL_54_FINAL:
                    createToken(Token.TokenType.EQUAL, null, currentLine);
                    state = States.STATE_INITIAL;
                    break;

                case States.STATE_COMMENT_BLOCK_55:
                    if (currentCharacter == '/') { // if the next character is also "/" then we have a LINE COMMENT
                        state = States.STATE_COMMENT_LINE_58;
                    } else if (currentCharacter == '*') { // if the next character is "*" then we have a BLOCK COMMENT
                        state = States.STATE_COMMENT_BLOCK_56;
                    } else {
                        state = States.STATE_DIV_37_FINAL;
                    }
                    index++;
                    break;

                case States.STATE_COMMENT_BLOCK_56:
                    if (currentCharacter == '*') { // might be the ending "*" of the block comment
                        state = States.STATE_COMMENT_BLOCK_57;
                    }
                    index++;
                    break;

                case States.STATE_COMMENT_BLOCK_57:
                    if (currentCharacter == '/') { // end of block comment
                        state = States.STATE_INITIAL;
                    } else if (currentCharacter != '*') { // anything other than "*" or "/" makes it change state back to 56
                        state = States.STATE_COMMENT_BLOCK_56;
                    }
                    index++;
                    break;

                case States.STATE_COMMENT_LINE_58:
                    if (isLineBreak(currentCharacter)) {
                        state = States.STATE_INITIAL;
                    }
                    index++;
                    break;
            }

            if (index > sourceCode.length() || currentCharacter == null) {
                createToken(Token.TokenType.END, null, currentLine);
                state = States.STATE_INITIAL;

                break;
            }
        }

        return true;
    }

    // '\\' [abfnrtv'?"\\0] ;
    private String replaceEscapedCharacters(String substring) {
        return substring.replace("\\\\", "\\")
                .replace("\\a", String.valueOf(((char) 0x07)))
                .replace("\\b", String.valueOf(((char) 0x08)))
                .replace("\\e", String.valueOf(((char) 0x1B)))
                .replace("\\f", String.valueOf(((char) 0x0C)))
                .replace("\\n", String.valueOf(((char) 0x0A)))
                .replace("\\r", String.valueOf(((char) 0x0D)))
                .replace("\\t", String.valueOf(((char) 0x09)))
                .replace("\\v", String.valueOf(((char) 0x0B)))
                .replace("\\\\", String.valueOf(((char) 0x5C)))
                .replace("\\\"", String.valueOf(((char) 0x22)))
                .replace("\\\'", String.valueOf(((char) 0x27)))
                .replace("\\?", String.valueOf(((char) 0x3F)))
                .replace("\\0", String.valueOf(((char) 0x00)));
    }

    private Number convertStringToIntegerConsideringBase(String number) {
        if(number.startsWith("0x")) {
            number = number.replace("0x", "");
            return Long.parseLong(number, 16);
        } else if(number.startsWith("0") && !number.contains(".")) { //octal
            return Long.parseLong(number, 8);
        }

        return Double.parseDouble(number);
    }

    private String getKeywordToken(String id) {
        return Token.getKeywordToken(id);
    }

    private boolean isEscape(Character currentCharacter) {
        return currentCharacter == 'a'
                || currentCharacter == 'b'
                || currentCharacter == 'f'
                || currentCharacter == 'n'
                || currentCharacter == 'r'
                || currentCharacter == 't'
                || currentCharacter == 'v'
                || currentCharacter == '\''
                || currentCharacter == '\"'
                || currentCharacter == '?'
                || currentCharacter == '\\'
                || currentCharacter == '0';
    }

    private void cannotHaveAnotherValue(Character currentCharacter) {
        throw new IllegalStateException("Cannot have another value here: " + currentCharacter);
    }

    private boolean isHexDigit(Character currentCharacter) {
        return Character.isDigit(currentCharacter)
                || (currentCharacter >= 'a' && currentCharacter <= 'f')
                || (currentCharacter >= 'A' && currentCharacter <= 'F');

    }

    private boolean isExponent(Character currentCharacter) {
        return currentCharacter == 'e' || currentCharacter == 'E';
    }

    private void createToken(Token.TokenType id, Object identifier, Integer currentLine) {
        Token tk = new Token(id, identifier, currentLine);
        tokens.add(tk);
    }

    private boolean isContinuationOfIdentifier(Character currentCharacter) {
        return Character.isAlphabetic(currentCharacter) || Character.isDigit(currentCharacter) || currentCharacter == '_';
    }

    private boolean isLineBreak(Character currentCharacter) {
        return currentCharacter == '\n' || currentCharacter == '\r' || currentCharacter == '\0';
    }

    private boolean numberStartsWithZero(Character currentCharacter) {
        return currentCharacter == '0';
    }

    private boolean isStartOfNumber(Character currentCharacter) {
        return Character.isDigit(currentCharacter);
    }

    /**
     * Is it the start of an ID atom?
     *
     * @param currentCharacter
     * @return
     */
    private boolean isIdentifierStart(Character currentCharacter) {
        return Character.isAlphabetic(currentCharacter) || currentCharacter == '_';
    }


    public static class States {
        public static final int STATE_INITIAL = 0;
        public static final int STATE_ID_1 = 1;
        public static final int STATE_ID_2_FINAL = 2;
        public static final int STATE_NUMERIC_3 = 3;
        public static final int STATE_INT_4_FINAL = 4;
        public static final int STATE_NUMERIC_5 = 5;
        public static final int STATE_NUMERIC_6 = 6;
        public static final int STATE_NUMERIC_7 = 7;
        public static final int STATE_NUMERIC_8 = 8;
        public static final int STATE_NUMERIC_9 = 9;
        public static final int STATE_NUMERIC_10 = 10;
        public static final int STATE_NUMERIC_11 = 11;
        public static final int STATE_REAL_12_FINAL = 12;
        public static final int STATE_NUMERIC_13 = 13;
        public static final int STATE_NUMERIC_14 = 14;
        public static final int STATE_STRING_15 = 15;
        public static final int STATE_STRING_16 = 16;
        public static final int STATE_STRING_18 = 18;
        public static final int STATE_STRING_19_FINAL = 19;
        public static final int STATE_CHAR_17 = 17;
        public static final int STATE_CHAR_20 = 20;
        public static final int STATE_CHAR_21 = 21;
        public static final int STATE_CHAR_22 = 22;
        public static final int STATE_CHAR_23_FINAL = 23;
        public static final int STATE_DOT_24 = 24;
        public static final int STATE_COMMA_25 = 25;
        public static final int STATE_SEMICOLON_26 = 26;
        public static final int STATE_LPAR_27 = 27;
        public static final int STATE_RPAR_28 = 28;
        public static final int STATE_LBRACKET_29 = 29;
        public static final int STATE_RBRACKET_30 = 30;
        public static final int STATE_LACC_31 = 31;
        public static final int STATE_RACC_32 = 32;
        public static final int STATE_ADD_34 = 34;
        public static final int STATE_SUB_35 = 35;
        public static final int STATE_MUL_36 = 36;
        public static final int STATE_DIV_37_FINAL = 37;
        public static final int STATE_AND_39 = 39;
        public static final int STATE_AND_40_FINAL = 40;
        public static final int STATE_OR_41 = 41;
        public static final int STATE_OR_42_FINAL = 42;
        public static final int STATE_NOT_43 = 43;
        public static final int STATE_NOT_44_FINAL = 44;
        public static final int STATE_NOTEQ_45_FINAL = 45;
        public static final int STATE_LESS_46 = 46;
        public static final int STATE_LESS_47_FINAL = 47;
        public static final int STATE_LESSEQ_48_FINAL = 48;
        public static final int STATE_GREATER_49 = 49;
        public static final int STATE_GREATER_50_FINAL = 50;
        public static final int STATE_GREATEREQ_51_FINAL = 51;
        public static final int STATE_ASSIGN_52 = 52;
        public static final int STATE_ASSIGN_53_FINAL = 53;
        public static final int STATE_EQUAL_54_FINAL = 54;
        public static final int STATE_COMMENT_BLOCK_55 = 55;
        public static final int STATE_COMMENT_BLOCK_56 = 56;
        public static final int STATE_COMMENT_BLOCK_57 = 57;
        public static final int STATE_COMMENT_LINE_58 = 58;
    }
}