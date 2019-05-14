package ro.adlabs;

import ro.adlabs.exceptions.InvalidSyntaxException;
import ro.adlabs.tokens.Token;

import java.util.List;

public class SyntacticAnalyzer implements Analyzer {

    private List<Token> tokens;

    private Integer currentIndex;

    public SyntacticAnalyzer(List<Token> tokenList) {
        tokens = tokenList;
        currentIndex = 0;
    }

    /**
     * TODO: Implement this
     *
     * @return
     */
    @Override
    public boolean analyze() {
        while (isStructureDeclaration() || isFunctionDeclaration() || isVariableDeclaration()) ;
        if (nextTokenIs(Token.TokenType.END)) {
            return true;
        }

        return false;
    }

    private boolean isStructureDeclaration() {
        Integer localIndex = currentIndex;

        if (nextTokenIs(Token.TokenType.STRUCT, currentIndex++)) {
            if (nextTokenIs(Token.TokenType.ID, currentIndex++)) {
                if (nextTokenIs(Token.TokenType.LACC, currentIndex++)) {
                    while (isVariableDeclaration()) ;
                    if (nextTokenIs(Token.TokenType.RACC, currentIndex++)) {
                        if (nextTokenIs(Token.TokenType.SEMICOLON, currentIndex++)) {
                            return true;
                        }
                    }
                }
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean isFunctionDeclaration() {
        Integer localIndex = currentIndex;

        boolean typeBase = false;
        if (consumeTypeBase()) {
            if(nextTokenIs(Token.TokenType.MUL, currentIndex)) {
                currentIndex++;
            }
            typeBase = true;
        }

        if(!typeBase) {
            currentIndex = localIndex;
            nextTokenIs(Token.TokenType.VOID, currentIndex++);
        }

        if (nextTokenIs(Token.TokenType.ID, currentIndex++)) {
            if (nextTokenIs(Token.TokenType.LPAR, currentIndex++)) {

                if (consumeFunctionArgument()) {
                    while (nextTokenIs(Token.TokenType.COMMA, currentIndex++)) {
                        if (consumeFunctionArgument()) {

                        } else {
                            throw new InvalidSyntaxException();
                        }
                    }
                }

                if (nextTokenIs(Token.TokenType.RPAR, currentIndex++)) {
                    if (consumeStatementCompound()) {
                        return true;
                    }
                }
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean isVariableDeclaration() {
        Integer localIndex = currentIndex;

        if (consumeTypeBase()) {
            if (nextTokenIs(Token.TokenType.ID, currentIndex++)) {
                consumeArrayDeclaration();
                while (nextTokenIs(Token.TokenType.COMMA, currentIndex)) {
                    currentIndex++;
                    if (nextTokenIs(Token.TokenType.ID, currentIndex++)) {
                        consumeArrayDeclaration();
                    }
                }

                if (nextTokenIs(Token.TokenType.SEMICOLON, currentIndex++)) {
                    return true;
                }

            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeStatementCompound() {
        Integer localIndex = currentIndex;

        if (nextTokenIs(Token.TokenType.LACC, currentIndex++)) {
            while (isVariableDeclaration() || consumeStatement()) {
                if (nextTokenIs(Token.TokenType.RACC, currentIndex++)) {
                    return true;
                }
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeStatement() {
        Integer localIndex = currentIndex;
        if (consumeStatementCompound()) {
            return true;
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.IF, currentIndex++)) {
            if (nextTokenIs(Token.TokenType.LPAR, currentIndex++)) {
                if (consumeExpression()) {
                    if (nextTokenIs(Token.TokenType.RPAR, currentIndex++)) {
                        if (consumeStatement()) {
                            if (nextTokenIs(Token.TokenType.ELSE, currentIndex++)) {
                                if (!consumeStatement()) {
                                    throw new InvalidSyntaxException();
                                }
                            }

                            return true;
                        }
                    }
                }
            }
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.WHILE, currentIndex++)) {
            if (nextTokenIs(Token.TokenType.LPAR, currentIndex++)) {
                if (consumeExpression()) {
                    if (nextTokenIs(Token.TokenType.RPAR, currentIndex++)) {
                        if (consumeStatement()) {
                            return true;
                        }
                    }
                }
            }
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.FOR, currentIndex++)) {
            if (nextTokenIs(Token.TokenType.LPAR, currentIndex++)) {
                consumeExpression();

                if (nextTokenIs(Token.TokenType.SEMICOLON, currentIndex++)) {
                    consumeExpression();

                    if (nextTokenIs(Token.TokenType.SEMICOLON, currentIndex++)) {
                        consumeExpression();

                        if (nextTokenIs(Token.TokenType.RPAR, currentIndex++)) {
                            if (consumeStatement()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        currentIndex = localIndex;
        if(nextTokenIs(Token.TokenType.BREAK, currentIndex++)) {
            if(nextTokenIs(Token.TokenType.SEMICOLON, currentIndex++)) {
                return true;
            }
        }

        currentIndex = localIndex;
        if(nextTokenIs(Token.TokenType.RETURN, currentIndex++)) {
            consumeExpression();

            if(nextTokenIs(Token.TokenType.SEMICOLON, currentIndex++)) {
                return true;
            }
        }

        currentIndex = localIndex;
        consumeExpression();
        if(nextTokenIs(Token.TokenType.SEMICOLON, currentIndex++)) {
            return true;
        }


        currentIndex = localIndex;
        return false;
    }

    private boolean consumeFunctionArgument() {
        Integer localIndex = currentIndex;

        if (consumeTypeBase()) {
            if (nextTokenIs(Token.TokenType.ID, currentIndex++)) {
                consumeArrayDeclaration();

                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeTypeBase() {
        Integer localIndex = currentIndex;
        Token currentToken = getToken(currentIndex++);

        if (currentToken.is(Token.TokenType.INT) || currentToken.is(Token.TokenType.DOUBLE) || currentToken.is(Token.TokenType.CHAR)) {
            return true;
        }

        if (currentToken.is(Token.TokenType.STRUCT)) {
            Token nextToken = getToken(currentIndex);
            return nextToken.is(Token.TokenType.ID);

        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeTypeName() {
        Integer localIndex = currentIndex;

        if (consumeTypeBase()) {
            consumeArrayDeclaration();
        }

        currentIndex = localIndex;
        return false;
    }

    /**
     * TODO: Implement this
     *
     * @return
     */
    private boolean consumeArrayDeclaration() {
        Integer localIndex = currentIndex;

        if (nextTokenIs(Token.TokenType.LBRACKET, currentIndex++)) {
            consumeExpression();
            if (nextTokenIs(Token.TokenType.RBRACKET, currentIndex++)) {
                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpression() {
        return consumeExpressionAssign();
    }

    private boolean consumeExpressionAssign() {
        Integer localIndex = currentIndex;

        if (consumeExpressionUnary()) {
            if (nextTokenIs(Token.TokenType.ASSIGN, currentIndex++)) {
                if (consumeExpressionAssign()) {
                    return true;
                }
            }
        }

        boolean result = consumeExpressionOr();
        if (result) {
            return result;
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionOr() {
        Integer localIndex = currentIndex;

        if (consumeExpressionAnd()) {
            if (consumeExpressionOr1()) {
                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionOr1() {
        if (nextTokenIs(Token.TokenType.OR, currentIndex++)) {
            if (consumeExpressionAnd()) {
                if (consumeExpressionOr1()) {
                    return true;
                }
            }
        }

        return true;
    }

    private boolean consumeExpressionAnd() {
        Integer localIndex = currentIndex;

        if (consumeExpressionEq()) {
            if (consumeExpressionAnd1()) {
                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionAnd1() {
        if (nextTokenIs(Token.TokenType.AND, currentIndex++)) {
            if (consumeExpressionEq()) {
                if (consumeExpressionAnd1()) {
                    return true;
                }
            }
        }

        return true;
    }

    private boolean consumeExpressionEq() {
        Integer localIndex = currentIndex;

        if (consumeExpressionRel()) {
            if (consumeExpressionEq1()) {
                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionEq1() {
        if (nextTokenIs(Token.TokenType.EQUAL) || nextTokenIs(Token.TokenType.NOTEQ)) {
            currentIndex++;

            if (consumeExpressionRel()) {
                if (consumeExpressionEq1()) {
                    return true;
                }
            }
        }

        return true;
    }

    private boolean consumeExpressionRel() {
        Integer localIndex = currentIndex;

        if (consumeExpressionAdd()) {
            if (consumeExpressionRel1()) {
                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionRel1() {
        if (nextTokenIs(Token.TokenType.LESS) || nextTokenIs(Token.TokenType.LESSEQ)
                || nextTokenIs(Token.TokenType.GREATER) || nextTokenIs(Token.TokenType.GREATEREQ)) {
            currentIndex++;

            if (consumeExpressionAdd()) {
                if (consumeExpressionRel1()) {
                    return true;
                }
            }
        }

        return true;
    }

    private boolean consumeExpressionAdd() {
        Integer localIndex = currentIndex;

        if (consumeExpressionMul()) {
            if (consumeExpressionAdd1()) {
                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionAdd1() {
        if (nextTokenIs(Token.TokenType.ADD) || nextTokenIs(Token.TokenType.SUB)) {
            currentIndex++;

            if (consumeExpressionMul()) {
                if (consumeExpressionAdd1()) {
                    return true;
                }
            }
        }

        return true;
    }

    private boolean consumeExpressionMul() {
        Integer localIndex = currentIndex;

        if (consumeExpressionCast()) {
            if (consumeExpressionMul1()) {
                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionMul1() {
        if (nextTokenIs(Token.TokenType.LESS) || nextTokenIs(Token.TokenType.LESSEQ)) {
            currentIndex++;

            if (consumeExpressionCast()) {
                if (consumeExpressionMul1()) {
                    return true;
                }
            }
        }

        return true;
    }

    private boolean consumeExpressionCast() {
        Integer localIndex = currentIndex;

        if (nextTokenIs(Token.TokenType.LPAR, currentIndex++)) {
            if (consumeTypeName()) {
                if (nextTokenIs(Token.TokenType.RPAR, currentIndex++)) {
                    if (consumeExpressionCast()) {
                        return true;
                    }
                }
            }
        }

        currentIndex = localIndex;

        if (consumeExpressionUnary()) {
            return true;
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionUnary() {
        Integer localIndex = currentIndex;

        if (nextTokenIs(Token.TokenType.SUB) || nextTokenIs(Token.TokenType.NOT)) {
            if (consumeExpressionUnary()) {
                return true;
            }
        }


        boolean result = consumeExpressionPostfix();
        if (result) {
            return result;
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionPostfix() {
        Integer localIndex = currentIndex;

        if(consumeExpressionPrimary()) {
            if(consumeExpressionPostfix1()) {
                return true;
            }
        }

        currentIndex = localIndex;
        return false;
    }

    private boolean consumeExpressionPostfix1() {
        Integer localIndex = currentIndex;

        if (nextTokenIs(Token.TokenType.LBRACKET, currentIndex++)) {
            if (consumeExpression()) {
                if (nextTokenIs(Token.TokenType.RBRACKET, currentIndex++)) {
                    if(consumeExpressionPostfix1()) {
                        return true;
                    }
                }
            }
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.DOT, currentIndex++)) {
            if (nextTokenIs(Token.TokenType.ID, currentIndex++)) {
                if(consumeExpressionPostfix1()) {
                    return true;
                }
            }
        }

        return true;
    }

    private boolean consumeExpressionPrimary() {
        Integer localIndex = currentIndex;

        if (nextTokenIs(Token.TokenType.ID, currentIndex++)) {

            if (nextTokenIs(Token.TokenType.LPAR, currentIndex++)) {
                if (consumeExpression()) {
                    while (nextTokenIs(Token.TokenType.COMMA, currentIndex)) {
                        currentIndex++;

                        if (!consumeExpression()) {
                            throw new InvalidSyntaxException();
                        }
                    }

                    return true;
                }

                if (nextTokenIs(Token.TokenType.RPAR, currentIndex++)) {
                    return true;
                }

                throw new InvalidSyntaxException();
            }

            return true;
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.CT_INT, currentIndex++)) {
            return true;
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.CT_REAL, currentIndex++)) {
            return true;
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.CT_CHAR, currentIndex++)) {
            return true;
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.CT_STRING, currentIndex++)) {
            return true;
        }

        currentIndex = localIndex;
        if (nextTokenIs(Token.TokenType.LPAR)) {
            if (consumeExpression()) {
                if (nextTokenIs(Token.TokenType.RPAR)) {
                    return true;
                }
            }
        }


        currentIndex = localIndex;
        return false;
    }


    private boolean nextTokenIs(Token.TokenType token, Integer index) {
        return getToken(index).is(token);
    }

    private boolean nextTokenIs(Token.TokenType token) {
        return nextTokenIs(token, currentIndex);
    }


    private Token getToken(Integer index) {
        if (tokens.size() > index) {
            return tokens.get(index);
        }

        return new Token();
    }
}
