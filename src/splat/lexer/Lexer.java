package splat.lexer;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.lang.StringBuilder;

public class Lexer {
    private final InputStream iStream;

    private int line = 1;
    private int column = 0;
    private int currByte = -1;
    private boolean eof = false;

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "program", "begin", "end", "if", "then", "else", "while", "do", "print", "print_line",
        "return", "Integer", "Boolean", "String", "true", "false", "is", "void"
    ));

	public Lexer(File progFile) {
        try {
            this.iStream = new BufferedInputStream(new FileInputStream(progFile));
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException(fnfe);
        }
	}

    public List<Token> tokenize() throws LexException {
        List<Token> tokens = new ArrayList<>();

        try {
            advance();

            while (!eof) {
                skipWhitespace();

                if (eof) break;

                char ch = (char) currByte;

                if (Character.isLetter(ch) || ch == '_') {
                    tokens.add(readIdentifierOrKeyword());
                } else if (Character.isDigit(ch)) {
                    tokens.add(readNumber());
                } else if (ch == '"') {
                    tokens.add(readString());
                } else {
                    tokens.add(readSymbol());
                }
            }

            iStream.close();
            return tokens;

        } catch (IOException e) {
            throw new LexException("I/O error while reading input: " + e.getMessage(), line, column);
        }
    }

    private void advance() throws IOException {
        currByte = iStream.read();
        if (currByte == -1) {
            eof = true;
        } else {
            if (currByte == '\n') {
                line++;
                column = 0;
            } else {
                column++;
            }
        }
    }

    private void skipWhitespace() throws IOException {
        while (!eof && Character.isWhitespace((char) currByte)) {
            advance();
        }
    }

    private Token readIdentifierOrKeyword() throws IOException {
        StringBuilder sb = new StringBuilder();
        int startCol = column;

        while (!eof && (Character.isLetterOrDigit((char) currByte) || currByte == '_')) {
            sb.append((char) currByte);
            advance();
        }

        String value = sb.toString();
        return new Token(value, line, startCol);
    }

    private Token readNumber() throws IOException, LexException {
        StringBuilder sb = new StringBuilder();
        int startCol = column;

        while (!eof && Character.isDigit((char) currByte)) {
            sb.append((char) currByte);
            advance();
        }

        // Disallow letters immediately after digits
        if (!eof && (Character.isLetter((char) currByte) || currByte == '_')) {
            throw new LexException("Invalid numeric literal", line, column);
        }

        return new Token(sb.toString(), line, startCol);
    }

    private Token readString() throws IOException, LexException {
        StringBuilder sb = new StringBuilder();
        int startCol = column;

        advance(); // skip opening quote

        while (!eof && currByte != '"') {
            char ch = (char) currByte;
            if (ch == '\\' || ch == '\n' || ch == '\r') {
                throw new LexException("Invalid character in string literal", line, column);
            }
            sb.append(ch);
            advance();
        }

        if (eof) {
            throw new LexException("Unterminated string literal", line, startCol);
        }

        advance(); // skip closing quote
        return new Token("\"" + sb.toString() + "\"", line, startCol);
    }

    private Token readSymbol() throws IOException, LexException {
        int startCol = column;
        char ch = (char) currByte;
        String value = String.valueOf(ch);

        switch (ch) {
            case ':':
                advance();
                if (currByte == '=') { advance(); return new Token(":=", line, startCol); }
                return new Token(":", line, startCol);
            case '>':
                advance();
                if (currByte == '=') { advance(); return new Token(">=", line, startCol); }
                return new Token(">", line, startCol);
            case '<':
                advance();
                if (currByte == '=') { advance(); return new Token("<=", line, startCol); }
                return new Token("<", line, startCol);
            case '=':
                advance();
                if (currByte == '=') { advance(); return new Token("==", line, startCol); }
                throw new LexException("Unexpected '=' (did you mean '=='?)", line, startCol);
            case '+': case '-': case '*': case '/': case '%':
            case '(': case ')': case ',': case ';':
                advance();
                return new Token(value, line, startCol);
            default:
                throw new LexException("Unexpected character: '" + ch + "'", line, column);
        }
    }
}
