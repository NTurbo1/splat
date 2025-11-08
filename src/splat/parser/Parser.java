package splat.parser;

import java.util.ArrayList;
import java.util.List;

import splat.lexer.Token;
import splat.parser.elements.*;
import splat.lang.Operations;
import splat.lang.Types;
import splat.lang.Keywords;

public class Parser {

	private List<Token> tokens;
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Compares the next token to an expected value, and throws
	 * an exception if they don't match.  This removes the front-most
	 * (next) token  
	 * 
	 * @param expected value of the next token
	 * @throws ParseException if the actual token doesn't match what 
	 * 			was expected
	 */
	private void checkNext(String expected) throws ParseException {

		Token tok = tokens.remove(0);
		
		if (!tok.getValue().equals(expected)) {
			throw new ParseException("Expected '"+ expected + "', got '" 
					+ tok.getValue()+ "'.", tok);
		}
	}

	/**
	 * Compares the next token to an expected value, and if they match,
     * then it returns true and removes the front-most (next) token.
     * If they don't match, then it just returns false without removing
     * anything.
	 * 
	 * @param expected value of the next token
	 */
	private boolean checkNextIfMatch(String expected) {

		Token tok = tokens.get(0);
		
		if (tok.getValue().equals(expected)) {
            tokens.remove(0);
            return true;
		}

        return false;
	}
	
	/**
	 * Returns a boolean indicating whether or not the next token matches
	 * the expected String value.  This does not remove the token from the
	 * token list.
	 * 
	 * @param expected value of the next token
	 * @return true iff the token value matches the expected string
	 */
	private boolean peekNext(String expected) {
		return tokens.get(0).getValue().equals(expected);
	}
	
	/**
	 * Returns a boolean indicating whether or not the token directly after
	 * the front most token matches the expected String value.  This does 
	 * not remove any tokens from the token list.
	 * 
	 * @param expected value of the token directly after the next token
	 * @return true iff the value matches the expected string
	 */
	private boolean peekTwoAhead(String expected) {
		return tokens.get(1).getValue().equals(expected);
	}
	
	
	/*
	 *  <program> ::= program <decls> begin <stmts> end ;
	 */
	public ProgramAST parse() throws ParseException {
		
		try {
			// Needed for 'program' token position info
			Token startTok = tokens.get(0);
			
			checkNext("program");
			
			List<Declaration> decls = parseDecls();
			
			checkNext("begin");
			
			List<Statement> stmts = parseStmts();
			
			checkNext("end");
			checkNext(";");
	
			return new ProgramAST(decls, stmts, startTok);
			
		// This might happen if we do a tokens.get(), and nothing is there!
		} catch (IndexOutOfBoundsException ex) {
			
			throw new ParseException("Unexpectedly reached the end of file.", -1, -1);
		}
	}
	
	/*
	 *  <decls> ::= (  <decl>  )*
	 */
	private List<Declaration> parseDecls() throws ParseException {
		
		List<Declaration> decls = new ArrayList<Declaration>();
		
		while (!peekNext("begin")) {
			Declaration decl = parseDecl();
			decls.add(decl);
		}
		
		return decls;
	}
	
	/*
	 * <decl> ::= <var-decl> | <func-decl>
	 */
	private Declaration parseDecl() throws ParseException {

		if (peekTwoAhead(":")) {
			return parseVarDecl();
		} else if (peekTwoAhead("(")) {
			return parseFuncDecl();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Declaration expected", tok);
		}
	}

	/*
	 * <var-decl> ::= <label> : <type> ;
	 */
	private VariableDecl parseVarDecl() throws ParseException {
        Token varLabelTok = tokens.remove(0);
        verifyLabel(varLabelTok);

        Token delimTok = tokens.remove(0);
        if (!delimTok.getValue().equals(":")) {
            throw new ParseException("You probably forgot ':' after the variable label.", delimTok);
        }

        Token typeTok = tokens.remove(0);
        Types.verifyVarType(typeTok);

        Token endSemiCol = tokens.remove(0);
        if (!endSemiCol.getValue().equals(";")) {
            throw new ParseException("Expected ';' after variable declaration.", endSemiCol);
        }

        VariableDecl varDecl = new VariableDecl(varLabelTok, varLabelTok.getValue(), typeTok.getValue());

        return varDecl;
	}
	
	/*
	 * <func-decl> ::= <label> ( <params> ) : <ret-type> is 
	 * 						<loc-var-decls> begin <stmts> end ;
	 */
	private FunctionDecl parseFuncDecl() throws ParseException {
        Token funcLabelTok = tokens.remove(0);
        verifyLabel(funcLabelTok);

        Token firstParanthTok = tokens.remove(0);
        if (!firstParanthTok.getValue().equals("(")) {
            throw new ParseException("Expected '(' after the function label.", firstParanthTok);
        }

        List<FuncParamDecl> params = parseFuncParams();

        Token secondParanthTok = tokens.remove(0);
        if (!secondParanthTok.getValue().equals(")")) {
            throw new ParseException("Expected ')' after the function parameters.", secondParanthTok);
        }

        Token delimTok = tokens.remove(0);
        if (!delimTok.getValue().equals(":")) {
            throw new ParseException(
                    "You probably forgot ':' before the function return type.", 
                    delimTok);
        }

        Token returnTypeTok = tokens.remove(0);
        Types.verifyFuncReturnType(returnTypeTok);

        Token isKeyword = tokens.remove(0);
        if (!isKeyword.getValue().equals("is")) {
            throw new ParseException("Expected keyword 'is' after the function return type.", isKeyword);
        }

        List<VariableDecl> localVarDecls = parseLocalVariableDecls();

        checkNext("begin");

        List<Statement> statements = parseStmts();

        Token endTok = tokens.remove(0);
        if (!endTok.getValue().equals("end")) {
            throw new ParseException("Expected keyword 'end' after the function body.", endTok);
        }

        Token endSemiCol = tokens.remove(0);
        if (!endSemiCol.getValue().equals(";")) {
            throw new ParseException("Expected ';' at the end of the function declaration.", endSemiCol);
        }

        FunctionDecl funcDecl = new FunctionDecl(
                funcLabelTok,
                funcLabelTok.getValue(),
                params,
                returnTypeTok.getValue(),
                localVarDecls,
                statements);

		return funcDecl;
	}

    /*
     * <param> ::= <label> : <type>
     */
    private FuncParamDecl parseFuncParam() throws ParseException {
        Token paramLabelTok = tokens.remove(0);
        verifyLabel(paramLabelTok);

        Token delimTok = tokens.remove(0);
        if (!delimTok.getValue().equals(":")) {
            throw new ParseException(
                    "You probably forgot ':' after the function parameter label.", 
                    delimTok);
        }

        Token typeTok = tokens.remove(0);
        Types.verifyVarType(typeTok);

        FuncParamDecl paramDecl = new FuncParamDecl(
                paramLabelTok, paramLabelTok.getValue(), typeTok.getValue());

        return paramDecl;
    }

    /*
     * <params> ::= <param> ( , <param> )* | ɛ
     */
    private List<FuncParamDecl> parseFuncParams() throws ParseException {
        List<FuncParamDecl> params = new ArrayList<FuncParamDecl>();
        while (!peekNext(")")) {
            FuncParamDecl param = parseFuncParam();
            params.add(param);
            if (!checkNextIfMatch(",")) {
                break;
            }
        }

        return params;
    }

    /*
     * <loc-var-decls> ::= ( <var-decl> )*
     */
    private List<VariableDecl> parseLocalVariableDecls() throws ParseException {
        List<VariableDecl> varDecls = new ArrayList<VariableDecl>();
        while (!peekNext("begin")) {
            VariableDecl varDecl = parseVarDecl();
            varDecls.add(varDecl);
        }

        return varDecls;
    }
	
	/*
	 * <stmts> ::= (  <stmt>  )*
	 */
	private List<Statement> parseStmts() throws ParseException {
        List<Statement> statements = new ArrayList<Statement>();
        while(!peekNext("end") && !peekNext("else")) {
            Statement stmt = parseStmt();
            statements.add(stmt);
        }

		return statements;
	}

    /*
     * <stmt> ::= <label> := <expr> ;
                | while <expr> do <stmts> end while ;
                | if <expr> then <stmts> else <stmts> end if ;
                | if <expr> then <stmts> end if ;
                | <label> ( <args> ) ;
                | print <expr> ;
                | print_line ;
                | return <expr> ;
                | return ;
     */
    private Statement parseStmt() throws ParseException {
        Token tok = tokens.get(0);
        switch (tok.getValue()) {
            case "while":
                return parseWhileLoopStmt();
            case "if":
                return parseIfElseStmt();
            case "print":
                return parsePrintStmt();
            case "print_line":
                return parsePrintLineStmt();
            case "return":
                return parseReturnStmt();
            default:
                if (peekTwoAhead(":=")) {
                    return parseLabelAssignmentStmt();
                }
                return parseLabelArgsStmt();
        }
    }

    /*
     * while <expr> do <stmts> end while ;
     */
    private WhileLoopStatement parseWhileLoopStmt() throws ParseException {
        Token whileKeywordTok = tokens.remove(0);
        if (!whileKeywordTok.getValue().equals("while")) {
            throw new ParseException("No 'while' keyword before the while loop.", whileKeywordTok);
        }

        Expression expr = parseExpression();
        Token doTok = tokens.remove(0);
        if (!doTok.getValue().equals("do")) {
            throw new ParseException(
                    "Expected 'do' keyword after the binary operation expression.", 
                    doTok);
        }

        List<Statement> stmts = parseStmts();

        checkNext("end");
        checkNext("while");
        checkNext(";");

        WhileLoopStatement wls = new WhileLoopStatement(whileKeywordTok, expr, stmts);

        return wls;
    }

    /*
     * if <expr> then <stmts> else <stmts> end if ; 
     * | if <expr> then <stmts> end if ;
     */
    private IfElseStatement parseIfElseStmt() throws ParseException {
        Token ifTok = tokens.remove(0);
        if (!ifTok.getValue().equals("if")) {
            throw new ParseException("Expected 'if' keyword.", ifTok);
        }
        
        Expression expr = parseExpression();
        Token thenTok = tokens.remove(0);
        if (!thenTok.getValue().equals("then")) {
            throw new ParseException(
                    "Expected 'then' keyword after the binary operation expression.", 
                    thenTok);
        }

        List<Statement> stmts = parseStmts();
        List<Statement> elseStmts = null;

        Token nextKeywordTok = tokens.remove(0);
        String nextKeyword = nextKeywordTok.getValue();
        if (nextKeyword.equals("else")) {
            elseStmts = parseStmts();
            checkNext("end");
            checkNext("if");
            checkNext(";");
        } else if (nextKeyword.equals("end")) {
            checkNext("if");
            checkNext(";");
        } else {
            throw new ParseException(
                    "Expected 'else' or 'end' after the if statement body.", nextKeywordTok);
        }

        IfElseStatement ifElseStmt = new IfElseStatement(ifTok, expr, stmts, elseStmts);

        return ifElseStmt;
    }

    /*
     * print <expr> ;
     */
    private Statement parsePrintStmt() throws ParseException {
        Token printTok = tokens.remove(0);
        if (!printTok.getValue().equals("print")) {
            throw new ParseException("Expected 'print' keyword.", printTok);
        }
        Expression expr = parseExpression();
        checkNext(";");

        PrintStatement printStmt = new PrintStatement(printTok, expr);

        return printStmt;
    }

    /*
     * print_line ;
     */
    private Statement parsePrintLineStmt() throws ParseException {
        Token printLineTok = tokens.remove(0);
        if (!printLineTok.getValue().equals("print_line")) {
            throw new ParseException("Expected 'print_line' keyword.", printLineTok);
        }
        checkNext(";");

        Statement stmt = new PrintLineStatement(printLineTok);

        return stmt;
    }

    /*
     * return <expr> ; | return ;
     */
    private Statement parseReturnStmt() throws ParseException {
        Token returnTok = tokens.remove(0);
        if (!returnTok.getValue().equals("return")) {
            throw new ParseException("Expected 'return' keyword.", returnTok);
        }

        Expression expr = null;
        if (!peekNext(";")) {
            expr = parseExpression();
        }
        checkNext(";");

        ReturnStatement stmt = new ReturnStatement(returnTok, expr);
        return stmt;
    }

    private LabelAssignmentStatement parseLabelAssignmentStmt() throws ParseException {
        Token labelTok = tokens.remove(0);
        verifyLabel(labelTok);
        checkNext(":=");
        Expression expr = parseExpression();
        checkNext(";");

        LabelAssignmentStatement las = new LabelAssignmentStatement(labelTok, labelTok.getValue(), expr);
        return las;
    }

    /*
     * <expr> ::= ( <expr> <bin-op> <expr> )
                | ( <unary-op> <expr> )
                | <label> ( <args> )
                | <label>
                | <literal>
     */
    private Expression parseExpression() throws ParseException {
        if (peekNext("(")) { // operation expression
            Token tok = tokens.get(1);
            String tokValue = tok.getValue();
            if (Operations.UNARY_OPERATORS.values().contains(tokValue)) {
                return parseUnaryOpExpression();
            }

            return parseBinaryOpExpression();
        } else if (peekTwoAhead("(")) {
            return parseLabelArgsExpr();
        } else {
            return parseLabelOrLiteral();
        }
    }

    private Expression parseLabelOrLiteral() throws ParseException {
        ParseException labelEx = null;
        ParseException literalEx = null;
        LabelExpression labelExpr = null;
        Literal literal = null;

        Token tok = tokens.remove(0);
        try {
            verifyLabel(tok);
            labelExpr = new LabelExpression(tok);
        } catch (ParseException ex) {
            labelEx = ex;
        }

        try {
            literal = new Literal(tok);
        } catch (ParseException ex) {
            literalEx = ex;
        }

        // Note: priority is given to literal
        if (literalEx == null) {
            return literal;
        } else if (labelEx == null) {
            return labelExpr;
        } else {
            throw literalEx;
        }
    }

    /*
     * ( <expr> <bin-op> <expr> )
     */
    private BinaryOpExpression parseBinaryOpExpression() throws ParseException {
        Token startParenthTok = tokens.remove(0);
        if (!startParenthTok.getValue().equals("(")) {
            throw new ParseException(
                    "Expected '(' before a binary operation expression.", startParenthTok);
        }

        Expression leftExpr = parseExpression();
        Token opTok = tokens.remove(0);
        String operator = opTok.getValue();
        if (!Operations.BINARY_OPERATORS.values().contains(operator)) {
            throw new ParseException("Unknown binary operator: " + operator, opTok);
        }
        Expression rightExpr = parseExpression();

        Token lastParenthTok = tokens.remove(0);
        if (!lastParenthTok.getValue().equals(")")) {
            throw new ParseException(
                    "Expected ')' after a binary operation expression.", lastParenthTok);
        }

        BinaryOpExpression binOpExpression = new BinaryOpExpression(
                startParenthTok, leftExpr, rightExpr, operator);

        return binOpExpression;
    }

    /*
     * ( <unary-op> <expr> )
     */
    private UnaryOpExpression parseUnaryOpExpression() throws ParseException {
        Token startParenthTok = tokens.remove(0);
        if (!startParenthTok.getValue().equals("(")) {
            throw new ParseException(
                    "Expected '(' before a unary operation expression.", startParenthTok);
        }

        Token opTok = tokens.remove(0);
        String operator = opTok.getValue();
        if (!Operations.UNARY_OPERATORS.values().contains(operator)) {
            throw new ParseException("Unknown unary operator: " + operator, opTok);
        }
        Expression rightExpr = parseExpression();

        Token lastParenthTok = tokens.remove(0);
        if (!lastParenthTok.getValue().equals(")")) {
            throw new ParseException(
                    "Expected ')' after a unary operation expression.", lastParenthTok);
        }

        UnaryOpExpression unaryExpr = new UnaryOpExpression(startParenthTok, rightExpr, operator);

        return unaryExpr;
    }

    /*
     * <label> ( <args> ) ;
     *
     * <args> ::= <expr> ( , <expr> )* | ɛ
     */
    private LabelArgsStatement parseLabelArgsStmt() throws ParseException {
        Token tok = tokens.get(0); // just for the initialization later.
        LabelArgsExpression labelArgsExpr = parseLabelArgsExpr();
        checkNext(";");

        LabelArgsStatement stmt = new LabelArgsStatement(
                tok, labelArgsExpr.getLabel(), labelArgsExpr.getArgs());

        return stmt;
    }

    /*
     * <label> ( <args> )
     *
     * <args> ::= <expr> ( , <expr> )* | ɛ
     */
    private LabelArgsExpression parseLabelArgsExpr() throws ParseException {
        Token labelTok = tokens.remove(0);
        verifyLabel(labelTok);

        checkNext("(");

        List<Expression> args = parseArgs();

        checkNext(")");

        LabelArgsExpression labelArgsExpr = new LabelArgsExpression(labelTok, labelTok.getValue(), args);

        return labelArgsExpr;
    }

    private List<Expression> parseArgs() throws ParseException {
        List<Expression> exprs = new ArrayList<Expression>();

        while(!peekNext(")")) {
            Expression expr = parseExpression();
            exprs.add(expr);

            if (!checkNextIfMatch(",")) {
                break;
            }
        }

        return exprs;
    }

    /*
     * <label> ::= ...sequence of alphanumeric characters and underscore, not starting with a digit,
     *              which are not keywords...
     */
    private void verifyLabel(Token tok) throws ParseException {
        String tokValue = tok.getValue();
        if (Character.isDigit(tokValue.charAt(0))) {
            throw new ParseException("Variable label can't start with a digit!", tok);
        }
        if (Keywords.RESERVED_WORDS.contains(tokValue)) {
            throw new ParseException("Reserved word " + tokValue + " can't be used as a label!", tok);
        }
    }
}
