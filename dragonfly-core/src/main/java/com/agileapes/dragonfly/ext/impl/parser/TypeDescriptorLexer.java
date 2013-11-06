/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.ext.impl.parser;

import org.antlr.runtime.*;

@SuppressWarnings("all")
public class TypeDescriptorLexer extends Lexer {
	public static final int EOF=-1;
	public static final int AND=4;
	public static final int ANNOTATION=5;
	public static final int ANYTHING=6;
	public static final int CHILD=7;
	public static final int CLOSING=8;
	public static final int COMMA=9;
	public static final int HAVING=10;
	public static final int ID=11;
	public static final int METHOD=12;
	public static final int NOT=13;
	public static final int OPENING=14;
	public static final int OR=15;
	public static final int PARAMS=16;
	public static final int PROPERTY=17;
	public static final int SELECT=18;
	public static final int SEPARATOR=19;
	public static final int SOMETHING=20;
	public static final int TYPE=21;
	public static final int WHITESPACE=22;

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
	@Override public String getGrammarFileName() { return "/Users/milad/Projects/Java/dragonfly/dragonfly-core/src/main/resources/grammar/TypeDescriptor.g"; }

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

	// $ANTLR start "SOMETHING"
	public final void mSOMETHING() throws RecognitionException {
		try {
			int _type = SOMETHING;
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
	// $ANTLR end "SOMETHING"

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

	// $ANTLR start "ANYTHING"
	public final void mANYTHING() throws RecognitionException {
		try {
			int _type = ANYTHING;
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
	// $ANTLR end "ANYTHING"

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

	// $ANTLR start "HAVING"
	public final void mHAVING() throws RecognitionException {
		try {
			int _type = HAVING;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			
			
			{
			match("having"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HAVING"

	// $ANTLR start "PROPERTY"
	public final void mPROPERTY() throws RecognitionException {
		try {
			int _type = PROPERTY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			
			
			{
			match("property"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PROPERTY"

	// $ANTLR start "METHOD"
	public final void mMETHOD() throws RecognitionException {
		try {
			int _type = METHOD;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			
			
			{
			match("method"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "METHOD"

	// $ANTLR start "PARAMS"
	public final void mPARAMS() throws RecognitionException {
		try {
			int _type = PARAMS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			
			
			{
			match("PARAMS"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PARAMS"

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

	// $ANTLR start "COMMA"
	public final void mCOMMA() throws RecognitionException {
		try {
			int _type = COMMA;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			
			
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMA"

	@Override
	public void mTokens() throws RecognitionException {
		
		int alt3=19;
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
				int LA3_21 = input.LA(3);
				if ( (LA3_21=='P') ) {
					int LA3_27 = input.LA(4);
					if ( (LA3_27=='E') ) {
						int LA3_33 = input.LA(5);
						if ( (LA3_33=='$'||(LA3_33 >= '0' && LA3_33 <= '9')||(LA3_33 >= 'A' && LA3_33 <= 'Z')||LA3_33=='_'||(LA3_33 >= 'a' && LA3_33 <= 'z')) ) {
							alt3=18;
						}

						else {
							alt3=12;
						}

					}

					else {
						alt3=18;
					}

				}

				else {
					alt3=18;
				}

			}

			else {
				alt3=18;
			}

			}
			break;
		case 'S':
			{
			int LA3_12 = input.LA(2);
			if ( (LA3_12=='E') ) {
				int LA3_22 = input.LA(3);
				if ( (LA3_22=='L') ) {
					int LA3_28 = input.LA(4);
					if ( (LA3_28=='E') ) {
						int LA3_34 = input.LA(5);
						if ( (LA3_34=='C') ) {
							int LA3_40 = input.LA(6);
							if ( (LA3_40=='T') ) {
								int LA3_45 = input.LA(7);
								if ( (LA3_45=='$'||(LA3_45 >= '0' && LA3_45 <= '9')||(LA3_45 >= 'A' && LA3_45 <= 'Z')||LA3_45=='_'||(LA3_45 >= 'a' && LA3_45 <= 'z')) ) {
									alt3=18;
								}

								else {
									alt3=13;
								}

							}

							else {
								alt3=18;
							}

						}

						else {
							alt3=18;
						}

					}

					else {
						alt3=18;
					}

				}

				else {
					alt3=18;
				}

			}

			else {
				alt3=18;
			}

			}
			break;
		case 'h':
			{
			int LA3_13 = input.LA(2);
			if ( (LA3_13=='a') ) {
				int LA3_23 = input.LA(3);
				if ( (LA3_23=='v') ) {
					int LA3_29 = input.LA(4);
					if ( (LA3_29=='i') ) {
						int LA3_35 = input.LA(5);
						if ( (LA3_35=='n') ) {
							int LA3_41 = input.LA(6);
							if ( (LA3_41=='g') ) {
								int LA3_46 = input.LA(7);
								if ( (LA3_46=='$'||(LA3_46 >= '0' && LA3_46 <= '9')||(LA3_46 >= 'A' && LA3_46 <= 'Z')||LA3_46=='_'||(LA3_46 >= 'a' && LA3_46 <= 'z')) ) {
									alt3=18;
								}

								else {
									alt3=14;
								}

							}

							else {
								alt3=18;
							}

						}

						else {
							alt3=18;
						}

					}

					else {
						alt3=18;
					}

				}

				else {
					alt3=18;
				}

			}

			else {
				alt3=18;
			}

			}
			break;
		case 'p':
			{
			int LA3_14 = input.LA(2);
			if ( (LA3_14=='r') ) {
				int LA3_24 = input.LA(3);
				if ( (LA3_24=='o') ) {
					int LA3_30 = input.LA(4);
					if ( (LA3_30=='p') ) {
						int LA3_36 = input.LA(5);
						if ( (LA3_36=='e') ) {
							int LA3_42 = input.LA(6);
							if ( (LA3_42=='r') ) {
								int LA3_47 = input.LA(7);
								if ( (LA3_47=='t') ) {
									int LA3_52 = input.LA(8);
									if ( (LA3_52=='y') ) {
										int LA3_55 = input.LA(9);
										if ( (LA3_55=='$'||(LA3_55 >= '0' && LA3_55 <= '9')||(LA3_55 >= 'A' && LA3_55 <= 'Z')||LA3_55=='_'||(LA3_55 >= 'a' && LA3_55 <= 'z')) ) {
											alt3=18;
										}

										else {
											alt3=15;
										}

									}

									else {
										alt3=18;
									}

								}

								else {
									alt3=18;
								}

							}

							else {
								alt3=18;
							}

						}

						else {
							alt3=18;
						}

					}

					else {
						alt3=18;
					}

				}

				else {
					alt3=18;
				}

			}

			else {
				alt3=18;
			}

			}
			break;
		case 'm':
			{
			int LA3_15 = input.LA(2);
			if ( (LA3_15=='e') ) {
				int LA3_25 = input.LA(3);
				if ( (LA3_25=='t') ) {
					int LA3_31 = input.LA(4);
					if ( (LA3_31=='h') ) {
						int LA3_37 = input.LA(5);
						if ( (LA3_37=='o') ) {
							int LA3_43 = input.LA(6);
							if ( (LA3_43=='d') ) {
								int LA3_48 = input.LA(7);
								if ( (LA3_48=='$'||(LA3_48 >= '0' && LA3_48 <= '9')||(LA3_48 >= 'A' && LA3_48 <= 'Z')||LA3_48=='_'||(LA3_48 >= 'a' && LA3_48 <= 'z')) ) {
									alt3=18;
								}

								else {
									alt3=16;
								}

							}

							else {
								alt3=18;
							}

						}

						else {
							alt3=18;
						}

					}

					else {
						alt3=18;
					}

				}

				else {
					alt3=18;
				}

			}

			else {
				alt3=18;
			}

			}
			break;
		case 'P':
			{
			int LA3_16 = input.LA(2);
			if ( (LA3_16=='A') ) {
				int LA3_26 = input.LA(3);
				if ( (LA3_26=='R') ) {
					int LA3_32 = input.LA(4);
					if ( (LA3_32=='A') ) {
						int LA3_38 = input.LA(5);
						if ( (LA3_38=='M') ) {
							int LA3_44 = input.LA(6);
							if ( (LA3_44=='S') ) {
								int LA3_49 = input.LA(7);
								if ( (LA3_49=='$'||(LA3_49 >= '0' && LA3_49 <= '9')||(LA3_49 >= 'A' && LA3_49 <= 'Z')||LA3_49=='_'||(LA3_49 >= 'a' && LA3_49 <= 'z')) ) {
									alt3=18;
								}

								else {
									alt3=17;
								}

							}

							else {
								alt3=18;
							}

						}

						else {
							alt3=18;
						}

					}

					else {
						alt3=18;
					}

				}

				else {
					alt3=18;
				}

			}

			else {
				alt3=18;
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
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'n':
		case 'o':
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
			alt3=18;
			}
			break;
		case ',':
			{
			alt3=19;
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
				mSOMETHING(); 

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
				mANYTHING(); 

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
				mHAVING(); 

				}
				break;
			case 15 :
				
				{
				mPROPERTY(); 

				}
				break;
			case 16 :
				
				{
				mMETHOD(); 

				}
				break;
			case 17 :
				
				{
				mPARAMS(); 

				}
				break;
			case 18 :
				
				{
				mID(); 

				}
				break;
			case 19 :
				
				{
				mCOMMA(); 

				}
				break;

		}
	}



}
