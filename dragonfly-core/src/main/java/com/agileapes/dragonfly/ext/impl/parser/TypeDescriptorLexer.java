package com.agileapes.dragonfly.ext.impl.parser;

import org.antlr.runtime.*;

@SuppressWarnings("all")
public class TypeDescriptorLexer extends Lexer {
    public static final int EOF=-1;
    public static final int AND=4;
    public static final int ANNOTATION=5;
    public static final int ANY=6;
    public static final int CHILD=7;
    public static final int CLOSING=8;
    public static final int ID=9;
    public static final int NOT=10;
    public static final int OPENING=11;
    public static final int OR=12;
    public static final int PACKAGE=13;
    public static final int SELECT=14;
    public static final int SEPARATOR=15;
    public static final int TYPE=16;
    public static final int WHITESPACE=17;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public TypeDescriptorLexer() {}
    public TypeDescriptorLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public TypeDescriptorLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    @Override public String getGrammarFileName() { return "/Users/milad/Projects/Java/TypeDescriptor/src/main/resources/TypeDescriptor.g"; }

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                
                int cnt1=0;
                loop1:
                while (true) {
                    int alt1=2;
                    int LA1_0 = input.LA(1);
                    if ( ((LA1_0 >= '\t' && LA1_0 <= '\n')||LA1_0=='\r'||LA1_0==' ') ) {
                        alt1=1;
                    }

                    switch (alt1) {
                        case 1 :
                            
                        {
                            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                                input.consume();
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;
                            }
                        }
                        break;

                        default :
                            if ( cnt1 >= 1 ) break loop1;
                            EarlyExitException eee = new EarlyExitException(1, input);
                            throw eee;
                    }
                    cnt1++;
                }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match("&&");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match("||");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "OPENING"
    public final void mOPENING() throws RecognitionException {
        try {
            int _type = OPENING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match('(');
            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "OPENING"

    // $ANTLR start "CLOSING"
    public final void mCLOSING() throws RecognitionException {
        try {
            int _type = CLOSING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match(')');
            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "CLOSING"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match('!');
            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match('*');
            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "ANY"

    // $ANTLR start "CHILD"
    public final void mCHILD() throws RecognitionException {
        try {
            int _type = CHILD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match('+');
            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "CHILD"

    // $ANTLR start "SEPARATOR"
    public final void mSEPARATOR() throws RecognitionException {
        try {
            int _type = SEPARATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match('.');
            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "SEPARATOR"

    // $ANTLR start "PACKAGE"
    public final void mPACKAGE() throws RecognitionException {
        try {
            int _type = PACKAGE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match("..");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "PACKAGE"

    // $ANTLR start "ANNOTATION"
    public final void mANNOTATION() throws RecognitionException {
        try {
            int _type = ANNOTATION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match('@');
            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "ANNOTATION"

    // $ANTLR start "TYPE"
    public final void mTYPE() throws RecognitionException {
        try {
            int _type = TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match("TYPE");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "SELECT"
    public final void mSELECT() throws RecognitionException {
        try {
            int _type = SELECT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                match("SELECT");

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "SELECT"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            
            
            {
                if ( input.LA(1)=='$'||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                    input.consume();
                }
                else {
                    MismatchedSetException mse = new MismatchedSetException(null,input);
                    recover(mse);
                    throw mse;
                }
                
                loop2:
                while (true) {
                    int alt2=2;
                    int LA2_0 = input.LA(1);
                    if ( (LA2_0=='$'||(LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'Z')||LA2_0=='_'||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
                        alt2=1;
                    }

                    switch (alt2) {
                        case 1 :
                            
                        {
                            if ( input.LA(1)=='$'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                                input.consume();
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;
                            }
                        }
                        break;

                        default :
                            break loop2;
                    }
                }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
            // do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    @Override
    public void mTokens() throws RecognitionException {
        
        int alt3=14;
        switch ( input.LA(1) ) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
            {
                alt3=1;
            }
            break;
            case '&':
            {
                alt3=2;
            }
            break;
            case '|':
            {
                alt3=3;
            }
            break;
            case '(':
            {
                alt3=4;
            }
            break;
            case ')':
            {
                alt3=5;
            }
            break;
            case '!':
            {
                alt3=6;
            }
            break;
            case '*':
            {
                alt3=7;
            }
            break;
            case '+':
            {
                alt3=8;
            }
            break;
            case '.':
            {
                int LA3_9 = input.LA(2);
                if ( (LA3_9=='.') ) {
                    alt3=10;
                }

                else {
                    alt3=9;
                }

            }
            break;
            case '@':
            {
                alt3=11;
            }
            break;
            case 'T':
            {
                int LA3_11 = input.LA(2);
                if ( (LA3_11=='Y') ) {
                    int LA3_16 = input.LA(3);
                    if ( (LA3_16=='P') ) {
                        int LA3_18 = input.LA(4);
                        if ( (LA3_18=='E') ) {
                            int LA3_20 = input.LA(5);
                            if ( (LA3_20=='$'||(LA3_20 >= '0' && LA3_20 <= '9')||(LA3_20 >= 'A' && LA3_20 <= 'Z')||LA3_20=='_'||(LA3_20 >= 'a' && LA3_20 <= 'z')) ) {
                                alt3=14;
                            }

                            else {
                                alt3=12;
                            }

                        }

                        else {
                            alt3=14;
                        }

                    }

                    else {
                        alt3=14;
                    }

                }

                else {
                    alt3=14;
                }

            }
            break;
            case 'S':
            {
                int LA3_12 = input.LA(2);
                if ( (LA3_12=='E') ) {
                    int LA3_17 = input.LA(3);
                    if ( (LA3_17=='L') ) {
                        int LA3_19 = input.LA(4);
                        if ( (LA3_19=='E') ) {
                            int LA3_21 = input.LA(5);
                            if ( (LA3_21=='C') ) {
                                int LA3_23 = input.LA(6);
                                if ( (LA3_23=='T') ) {
                                    int LA3_24 = input.LA(7);
                                    if ( (LA3_24=='$'||(LA3_24 >= '0' && LA3_24 <= '9')||(LA3_24 >= 'A' && LA3_24 <= 'Z')||LA3_24=='_'||(LA3_24 >= 'a' && LA3_24 <= 'z')) ) {
                                        alt3=14;
                                    }

                                    else {
                                        alt3=13;
                                    }

                                }

                                else {
                                    alt3=14;
                                }

                            }

                            else {
                                alt3=14;
                            }

                        }

                        else {
                            alt3=14;
                        }

                    }

                    else {
                        alt3=14;
                    }

                }

                else {
                    alt3=14;
                }

            }
            break;
            case '$':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            {
                alt3=14;
            }
            break;
            default:
                NoViableAltException nvae =
                        new NoViableAltException("", 3, 0, input);
                throw nvae;
        }
        switch (alt3) {
            case 1 :
                
            {
                mWHITESPACE();

            }
            break;
            case 2 :
                
            {
                mAND();

            }
            break;
            case 3 :
                
            {
                mOR();

            }
            break;
            case 4 :
                
            {
                mOPENING();

            }
            break;
            case 5 :
                
            {
                mCLOSING();

            }
            break;
            case 6 :
                
            {
                mNOT();

            }
            break;
            case 7 :
                
            {
                mANY();

            }
            break;
            case 8 :
                
            {
                mCHILD();

            }
            break;
            case 9 :
                
            {
                mSEPARATOR();

            }
            break;
            case 10 :
                
            {
                mPACKAGE();

            }
            break;
            case 11 :
                
            {
                mANNOTATION();

            }
            break;
            case 12 :
                
            {
                mTYPE();

            }
            break;
            case 13 :
                
            {
                mSELECT();

            }
            break;
            case 14 :
                
            {
                mID();

            }
            break;

        }
    }



}
