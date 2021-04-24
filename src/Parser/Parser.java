package Parser;

import Lexer.Lexer;
import Lexer.Token.ArithmeticOperation;
import Lexer.Token.Token;
import Lexer.Token.TokenType;
import Parser.Nodes.*;

import java.util.List;

/**
 *
 */
public class Parser {
    // ATTRIBUTES
    private final List<Token> tokens;
    private int currentPosition;
    private Token currentToken;

    // CONSTRUCTOR
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        currentPosition = -1;
        currentToken = null;
        advance();
    }

    // HELPER METHODS
    /** Goes to the next token in the list of tokens */
    private void advance() {
        currentPosition += 1;

        if (currentPosition < tokens.size()) {
            currentToken = tokens.get(currentPosition);
        }
    }

    /** Parses each token to different terms/expressions to create an AST (abstract syntax tree) */
    private Node parse() {

        // TODO: refactor this for other syntax types, the following if-else WILL NOT WORK with other types
        // For example, check if it's the only one, assert that it is a number node
        if (tokens.size() == 1) { // if there is just 1 number then create a number node
            return getAtom();
        }
        else {
            return getExpression();
        }
    }

    /** Returns node type for given token */
    private Node getAtom() {
        Token token = currentToken;

        switch (token.getType()) {
            case INTEGER:
            case FLOAT:
                advance();
                return new NumberNode(token);

            // TODO: fix for other types of syntax (right now only arithmetic works)
            // Operator : Should not begin with operator, return error
            case OPERATOR:
                // Still need to define nodes for these
            case EQUAL:

            case VARIABLE:
            case IDENTIFIER:
            case VARIABLE_INSTANTIATION:
                // return new VariableAccessNode(token);

            case COMMENT_START:
            case COMMENT_END:
            case NONE:
            case UNEXPECTED:
        }

        // TODO: fix
        return null;
    }

    // TODO: Simplify DRY code for getTerm() and getExpression()
    private Node getTerm() {
        NumberNode leftFactor = (NumberNode) getAtom();
        ArithmeticOperationNode term = new ArithmeticOperationNode();
        ArithmeticOperation operation = ArithmeticOperation.getArithmeticOperation(currentToken.getValue());
        boolean inIf = false;

        // TODO: this code looks so bad... uh//// fix????????????????????????????????????????????
        // check if the current token is still an add or subtract token
        if (operation.equals(ArithmeticOperation.ADD) || operation.equals(ArithmeticOperation.SUBTRACT)) {
            inIf = true;
            Token operatorToken = currentToken;
            advance();

            // get the factor to add/subtract by
            NumberNode rightFactor = (NumberNode) getAtom();
            // create a new term
            term = new ArithmeticOperationNode(leftFactor, operatorToken, rightFactor);
        }
        if(inIf) {
            return term;
        }
        // if it didn't, then we can just return the term we already made
        else {
            return leftFactor;
        }
    }

    private Node getExpression() {
       // Check for variable instantiation
        if (currentToken.getType().equals(TokenType.VARIABLE)) {
            advance();
            if(!currentToken.getType().equals(TokenType.IDENTIFIER)) {
                return new Node(new Token(TokenType.NONE)); // failure
            }
            else {
                Token varName = currentToken;
                advance();
                if(!currentToken.getType().equals(TokenType.VARIABLE_INSTANTIATION)) {
                    return new Node(new Token(TokenType.NONE)); // failure
                }
                else {

                }
            }
        }

        // Check for arithmetic expression
        Node leftTerm = getTerm();

        ExpressionNode expression = new ExpressionNode();
        ArithmeticOperation operation = ArithmeticOperation.getArithmeticOperation(currentToken.getValue());
        boolean inWhile = false;

        // TODO: simplify condition, DRY ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^66 ╰（‵□′）╯（︶^︶）( ˘︹˘ )
        // check if the current token is still a multiply or divide token
        if (operation.equals(ArithmeticOperation.MULTIPLY) || operation.equals(ArithmeticOperation.DIVIDE)) {
            inWhile = true;
            Token operatorToken = currentToken;
            advance();

            // get the factor to multiply/divide by
            NumberNode rightFactor = (NumberNode) getAtom();
            // create a new expression
            expression = new ExpressionNode(leftTerm, operatorToken, rightFactor);
        }

        // TODO: fix for error
        // if the while loop was entered, it means there was more to read, and an expression was made
        if(inWhile) {
            return expression;
        }
        // if it didn't, then we can just return the term we already made
        else {
            return leftTerm;
        }
    }

    // TODO: simplify, put run method in one place
    public static Node run(String code) {
        // Generate tokens
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.makeTokens();

        // Generate AST
        Parser parser = new Parser(tokens);
        //System.out.println(parser.parse());
        return parser.parse();
    }
}
