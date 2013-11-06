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
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;


@SuppressWarnings("all")
public class TypeDescriptorParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "ANNOTATION", "ANYTHING", 
		"CHILD", "CLOSING", "COMMA", "HAVING", "ID", "METHOD", "NOT", "OPENING", 
		"OR", "PARAMS", "PROPERTY", "SELECT", "SEPARATOR", "SOMETHING", "TYPE", 
		"WHITESPACE"
	};
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
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public TypeDescriptorParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public TypeDescriptorParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return TypeDescriptorParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/milad/Projects/Java/dragonfly/dragonfly-core/src/main/resources/grammar/TypeDescriptor.g"; }


	public static class type_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "type"
	
	public final TypeDescriptorParser.type_return type() throws RecognitionException {
		TypeDescriptorParser.type_return retval = new TypeDescriptorParser.type_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope typeRest1 =null;


		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(TYPE, "TYPE"), root_0);
			pushFollow(FOLLOW_typeRest_in_type283);
			typeRest1=typeRest();
			state._fsp--;

			adaptor.addChild(root_0, typeRest1.getTree());

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "type"


	public static class clazz_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "clazz"
	
	public final TypeDescriptorParser.clazz_return clazz() throws RecognitionException {
		TypeDescriptorParser.clazz_return retval = new TypeDescriptorParser.clazz_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token CHILD3=null;
		ParserRuleReturnScope type2 =null;

		Object CHILD3_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_type_in_clazz291);
			type2=type();
			state._fsp--;

			adaptor.addChild(root_0, type2.getTree());

			
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0==CHILD) ) {
				alt1=1;
			}
			else if ( (LA1_0==EOF||(LA1_0 >= CLOSING && LA1_0 <= COMMA)||LA1_0==WHITESPACE) ) {
				alt1=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}

			switch (alt1) {
				case 1 :
					
					{
					CHILD3=(Token)match(input,CHILD,FOLLOW_CHILD_in_clazz294); 
					CHILD3_tree = (Object)adaptor.create(CHILD3);
					adaptor.addChild(root_0, CHILD3_tree);

					}
					break;
				case 2 :
					
					{
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "clazz"


	public static class selector_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "selector"
	
	public final TypeDescriptorParser.selector_return selector() throws RecognitionException {
		TypeDescriptorParser.selector_return retval = new TypeDescriptorParser.selector_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token WHITESPACE5=null;
		Token HAVING8=null;
		Token WHITESPACE9=null;
		Token PROPERTY10=null;
		Token WHITESPACE11=null;
		Token OPENING12=null;
		Token WHITESPACE13=null;
		Token WHITESPACE15=null;
		Token WHITESPACE17=null;
		Token set18=null;
		Token WHITESPACE19=null;
		Token CLOSING20=null;
		Token HAVING21=null;
		Token WHITESPACE22=null;
		Token METHOD23=null;
		Token WHITESPACE24=null;
		Token OPENING25=null;
		Token WHITESPACE26=null;
		Token WHITESPACE28=null;
		Token WHITESPACE30=null;
		Token set31=null;
		Token WHITESPACE32=null;
		Token OPENING33=null;
		Token WHITESPACE34=null;
		Token WHITESPACE36=null;
		Token ANYTHING37=null;
		Token CLOSING38=null;
		Token WHITESPACE39=null;
		Token CLOSING40=null;
		ParserRuleReturnScope annotations4 =null;
		ParserRuleReturnScope clazz6 =null;
		ParserRuleReturnScope clazz7 =null;
		ParserRuleReturnScope annotations14 =null;
		ParserRuleReturnScope clazz16 =null;
		ParserRuleReturnScope annotations27 =null;
		ParserRuleReturnScope clazz29 =null;
		ParserRuleReturnScope methodParameters35 =null;

		Object WHITESPACE5_tree=null;
		Object HAVING8_tree=null;
		Object WHITESPACE9_tree=null;
		Object PROPERTY10_tree=null;
		Object WHITESPACE11_tree=null;
		Object OPENING12_tree=null;
		Object WHITESPACE13_tree=null;
		Object WHITESPACE15_tree=null;
		Object WHITESPACE17_tree=null;
		Object set18_tree=null;
		Object WHITESPACE19_tree=null;
		Object CLOSING20_tree=null;
		Object HAVING21_tree=null;
		Object WHITESPACE22_tree=null;
		Object METHOD23_tree=null;
		Object WHITESPACE24_tree=null;
		Object OPENING25_tree=null;
		Object WHITESPACE26_tree=null;
		Object WHITESPACE28_tree=null;
		Object WHITESPACE30_tree=null;
		Object set31_tree=null;
		Object WHITESPACE32_tree=null;
		Object OPENING33_tree=null;
		Object WHITESPACE34_tree=null;
		Object WHITESPACE36_tree=null;
		Object ANYTHING37_tree=null;
		Object CLOSING38_tree=null;
		Object WHITESPACE39_tree=null;
		Object CLOSING40_tree=null;
		RewriteRuleTokenStream stream_WHITESPACE=new RewriteRuleTokenStream(adaptor,"token WHITESPACE");
		RewriteRuleSubtreeStream stream_annotations=new RewriteRuleSubtreeStream(adaptor,"rule annotations");
		RewriteRuleSubtreeStream stream_clazz=new RewriteRuleSubtreeStream(adaptor,"rule clazz");

		try {
			
			int alt14=4;
			switch ( input.LA(1) ) {
			case ANNOTATION:
				{
				alt14=1;
				}
				break;
			case ID:
			case SOMETHING:
				{
				alt14=2;
				}
				break;
			case HAVING:
				{
				int LA14_3 = input.LA(2);
				if ( (LA14_3==WHITESPACE) ) {
					int LA14_4 = input.LA(3);
					if ( (LA14_4==PROPERTY) ) {
						alt14=3;
					}
					else if ( (LA14_4==METHOD) ) {
						alt14=4;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 14, 4, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 14, 0, input);
				throw nvae;
			}
			switch (alt14) {
				case 1 :
					
					{
					pushFollow(FOLLOW_annotations_in_selector304);
					annotations4=annotations();
					state._fsp--;

					stream_annotations.add(annotations4.getTree());
					WHITESPACE5=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector306);  
					stream_WHITESPACE.add(WHITESPACE5);

					pushFollow(FOLLOW_clazz_in_selector308);
					clazz6=clazz();
					state._fsp--;

					stream_clazz.add(clazz6.getTree());
					// AST REWRITE
					// elements: annotations, clazz
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 57:40: -> ^( SELECT annotations clazz )
					{
						
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SELECT, "SELECT"), root_1);
						adaptor.addChild(root_1, stream_annotations.nextTree());
						adaptor.addChild(root_1, stream_clazz.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					
					{
					pushFollow(FOLLOW_clazz_in_selector323);
					clazz7=clazz();
					state._fsp--;

					stream_clazz.add(clazz7.getTree());
					// AST REWRITE
					// elements: clazz
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 58:10: -> ^( SELECT clazz )
					{
						
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SELECT, "SELECT"), root_1);
						adaptor.addChild(root_1, stream_clazz.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 3 :
					
					{
					root_0 = (Object)adaptor.nil();


					HAVING8=(Token)match(input,HAVING,FOLLOW_HAVING_in_selector336); 
					WHITESPACE9=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector339); 
					PROPERTY10=(Token)match(input,PROPERTY,FOLLOW_PROPERTY_in_selector342); 
					PROPERTY10_tree = (Object)adaptor.create(PROPERTY10);
					root_0 = (Object)adaptor.becomeRoot(PROPERTY10_tree, root_0);

					
					int alt2=2;
					int LA2_0 = input.LA(1);
					if ( (LA2_0==WHITESPACE) ) {
						alt2=1;
					}
					switch (alt2) {
						case 1 :
							
							{
							WHITESPACE11=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector345); 
							}
							break;

					}

					OPENING12=(Token)match(input,OPENING,FOLLOW_OPENING_in_selector349); 
					
					int alt3=2;
					int LA3_0 = input.LA(1);
					if ( (LA3_0==WHITESPACE) ) {
						alt3=1;
					}
					switch (alt3) {
						case 1 :
							
							{
							WHITESPACE13=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector352); 
							}
							break;

					}

					
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0==ANNOTATION) ) {
						alt4=1;
					}
					switch (alt4) {
						case 1 :
							
							{
							pushFollow(FOLLOW_annotations_in_selector357);
							annotations14=annotations();
							state._fsp--;

							adaptor.addChild(root_0, annotations14.getTree());

							WHITESPACE15=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector359); 
							}
							break;

					}

					pushFollow(FOLLOW_clazz_in_selector364);
					clazz16=clazz();
					state._fsp--;

					adaptor.addChild(root_0, clazz16.getTree());

					WHITESPACE17=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector366); 
					set18=input.LT(1);
					if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
						input.consume();
						adaptor.addChild(root_0, (Object)adaptor.create(set18));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0==WHITESPACE) ) {
						alt5=1;
					}
					switch (alt5) {
						case 1 :
							
							{
							WHITESPACE19=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector375); 
							}
							break;

					}

					CLOSING20=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_selector379); 
					}
					break;
				case 4 :
					
					{
					root_0 = (Object)adaptor.nil();


					HAVING21=(Token)match(input,HAVING,FOLLOW_HAVING_in_selector385); 
					WHITESPACE22=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector388); 
					METHOD23=(Token)match(input,METHOD,FOLLOW_METHOD_in_selector391); 
					METHOD23_tree = (Object)adaptor.create(METHOD23);
					root_0 = (Object)adaptor.becomeRoot(METHOD23_tree, root_0);

					
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==WHITESPACE) ) {
						alt6=1;
					}
					switch (alt6) {
						case 1 :
							
							{
							WHITESPACE24=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector394); 
							}
							break;

					}

					OPENING25=(Token)match(input,OPENING,FOLLOW_OPENING_in_selector398); 
					
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==WHITESPACE) ) {
						alt7=1;
					}
					switch (alt7) {
						case 1 :
							
							{
							WHITESPACE26=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector401); 
							}
							break;

					}

					
					int alt8=2;
					int LA8_0 = input.LA(1);
					if ( (LA8_0==ANNOTATION) ) {
						alt8=1;
					}
					switch (alt8) {
						case 1 :
							
							{
							pushFollow(FOLLOW_annotations_in_selector406);
							annotations27=annotations();
							state._fsp--;

							adaptor.addChild(root_0, annotations27.getTree());

							WHITESPACE28=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector408); 
							}
							break;

					}

					pushFollow(FOLLOW_clazz_in_selector413);
					clazz29=clazz();
					state._fsp--;

					adaptor.addChild(root_0, clazz29.getTree());

					WHITESPACE30=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector415); 
					set31=input.LT(1);
					if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
						input.consume();
						adaptor.addChild(root_0, (Object)adaptor.create(set31));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					
					int alt9=2;
					int LA9_0 = input.LA(1);
					if ( (LA9_0==WHITESPACE) ) {
						alt9=1;
					}
					switch (alt9) {
						case 1 :
							
							{
							WHITESPACE32=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector424); 
							}
							break;

					}

					OPENING33=(Token)match(input,OPENING,FOLLOW_OPENING_in_selector428); 
					
					int alt10=2;
					int LA10_0 = input.LA(1);
					if ( (LA10_0==WHITESPACE) ) {
						alt10=1;
					}
					switch (alt10) {
						case 1 :
							
							{
							WHITESPACE34=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector431); 
							}
							break;

					}

					
					int alt12=3;
					switch ( input.LA(1) ) {
					case ID:
					case SOMETHING:
						{
						alt12=1;
						}
						break;
					case ANYTHING:
						{
						alt12=2;
						}
						break;
					case CLOSING:
						{
						alt12=3;
						}
						break;
					default:
						NoViableAltException nvae =
							new NoViableAltException("", 12, 0, input);
						throw nvae;
					}
					switch (alt12) {
						case 1 :
							
							{
							pushFollow(FOLLOW_methodParameters_in_selector436);
							methodParameters35=methodParameters();
							state._fsp--;

							adaptor.addChild(root_0, methodParameters35.getTree());

							
							int alt11=2;
							int LA11_0 = input.LA(1);
							if ( (LA11_0==WHITESPACE) ) {
								alt11=1;
							}
							switch (alt11) {
								case 1 :
									
									{
									WHITESPACE36=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector438); 
									}
									break;

							}

							}
							break;
						case 2 :
							
							{
							ANYTHING37=(Token)match(input,ANYTHING,FOLLOW_ANYTHING_in_selector444); 
							ANYTHING37_tree = (Object)adaptor.create(ANYTHING37);
							adaptor.addChild(root_0, ANYTHING37_tree);

							}
							break;
						case 3 :
							
							{
							}
							break;

					}

					CLOSING38=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_selector449); 
					
					int alt13=2;
					int LA13_0 = input.LA(1);
					if ( (LA13_0==WHITESPACE) ) {
						alt13=1;
					}
					switch (alt13) {
						case 1 :
							
							{
							WHITESPACE39=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector452); 
							}
							break;

					}

					CLOSING40=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_selector456); 
					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "selector"


	public static class methodParameters_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "methodParameters"
	
	public final TypeDescriptorParser.methodParameters_return methodParameters() throws RecognitionException {
		TypeDescriptorParser.methodParameters_return retval = new TypeDescriptorParser.methodParameters_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token WHITESPACE42=null;
		Token COMMA43=null;
		Token WHITESPACE44=null;
		ParserRuleReturnScope clazz41 =null;
		ParserRuleReturnScope clazz45 =null;

		Object WHITESPACE42_tree=null;
		Object COMMA43_tree=null;
		Object WHITESPACE44_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(PARAMS, "PARAMS"), root_0);
			
			
			{
			pushFollow(FOLLOW_clazz_in_methodParameters469);
			clazz41=clazz();
			state._fsp--;

			adaptor.addChild(root_0, clazz41.getTree());

			
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==WHITESPACE) ) {
					int LA17_1 = input.LA(2);
					if ( (LA17_1==COMMA) ) {
						alt17=1;
					}

				}
				else if ( (LA17_0==COMMA) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					
					{
					
					int alt15=2;
					int LA15_0 = input.LA(1);
					if ( (LA15_0==WHITESPACE) ) {
						alt15=1;
					}
					switch (alt15) {
						case 1 :
							
							{
							WHITESPACE42=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_methodParameters472); 
							}
							break;

					}

					COMMA43=(Token)match(input,COMMA,FOLLOW_COMMA_in_methodParameters476); 
					
					int alt16=2;
					int LA16_0 = input.LA(1);
					if ( (LA16_0==WHITESPACE) ) {
						alt16=1;
					}
					switch (alt16) {
						case 1 :
							
							{
							WHITESPACE44=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_methodParameters479); 
							}
							break;

					}

					pushFollow(FOLLOW_clazz_in_methodParameters483);
					clazz45=clazz();
					state._fsp--;

					adaptor.addChild(root_0, clazz45.getTree());

					}
					break;

				default :
					break loop17;
				}
			}

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "methodParameters"


	public static class typeRest_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "typeRest"
	
	public final TypeDescriptorParser.typeRest_return typeRest() throws RecognitionException {
		TypeDescriptorParser.typeRest_return retval = new TypeDescriptorParser.typeRest_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set46=null;
		Token SEPARATOR47=null;
		Token set48=null;
		Token ANYTHING49=null;
		Token set50=null;
		Token SEPARATOR51=null;
		Token set52=null;

		Object set46_tree=null;
		Object SEPARATOR47_tree=null;
		Object set48_tree=null;
		Object ANYTHING49_tree=null;
		Object set50_tree=null;
		Object SEPARATOR51_tree=null;
		Object set52_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( (LA18_0==ID||LA18_0==SOMETHING) ) {
					int LA18_1 = input.LA(2);
					if ( (LA18_1==SEPARATOR) ) {
						alt18=1;
					}

				}

				switch (alt18) {
				case 1 :
					
					{
					set46=input.LT(1);
					if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
						input.consume();
						adaptor.addChild(root_0, (Object)adaptor.create(set46));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					SEPARATOR47=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest506); 
					SEPARATOR47_tree = (Object)adaptor.create(SEPARATOR47);
					adaptor.addChild(root_0, SEPARATOR47_tree);

					}
					break;

				default :
					break loop18;
				}
			}

			set48=input.LT(1);
			if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
				input.consume();
				adaptor.addChild(root_0, (Object)adaptor.create(set48));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==ANYTHING) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					
					{
					ANYTHING49=(Token)match(input,ANYTHING,FOLLOW_ANYTHING_in_typeRest519); 
					ANYTHING49_tree = (Object)adaptor.create(ANYTHING49);
					adaptor.addChild(root_0, ANYTHING49_tree);

					
					loop19:
					while (true) {
						int alt19=2;
						int LA19_0 = input.LA(1);
						if ( (LA19_0==ID||LA19_0==SOMETHING) ) {
							int LA19_1 = input.LA(2);
							if ( (LA19_1==SEPARATOR) ) {
								alt19=1;
							}

						}

						switch (alt19) {
						case 1 :
							
							{
							set50=input.LT(1);
							if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
								input.consume();
								adaptor.addChild(root_0, (Object)adaptor.create(set50));
								state.errorRecovery=false;
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								throw mse;
							}
							SEPARATOR51=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest530); 
							SEPARATOR51_tree = (Object)adaptor.create(SEPARATOR51);
							adaptor.addChild(root_0, SEPARATOR51_tree);

							}
							break;

						default :
							break loop19;
						}
					}

					set52=input.LT(1);
					if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
						input.consume();
						adaptor.addChild(root_0, (Object)adaptor.create(set52));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;

				default :
					break loop20;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "typeRest"


	public static class annotations_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "annotations"
	
	public final TypeDescriptorParser.annotations_return annotations() throws RecognitionException {
		TypeDescriptorParser.annotations_return retval = new TypeDescriptorParser.annotations_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ANNOTATION53=null;
		Token WHITESPACE55=null;
		Token ANNOTATION56=null;
		ParserRuleReturnScope type54 =null;
		ParserRuleReturnScope type57 =null;

		Object ANNOTATION53_tree=null;
		Object WHITESPACE55_tree=null;
		Object ANNOTATION56_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			ANNOTATION53=(Token)match(input,ANNOTATION,FOLLOW_ANNOTATION_in_annotations551); 
			ANNOTATION53_tree = (Object)adaptor.create(ANNOTATION53);
			root_0 = (Object)adaptor.becomeRoot(ANNOTATION53_tree, root_0);

			pushFollow(FOLLOW_type_in_annotations554);
			type54=type();
			state._fsp--;

			adaptor.addChild(root_0, type54.getTree());

			
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==WHITESPACE) ) {
					int LA21_1 = input.LA(2);
					if ( (LA21_1==ANNOTATION) ) {
						alt21=1;
					}

				}

				switch (alt21) {
				case 1 :
					
					{
					WHITESPACE55=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_annotations557); 
					ANNOTATION56=(Token)match(input,ANNOTATION,FOLLOW_ANNOTATION_in_annotations560); 
					pushFollow(FOLLOW_type_in_annotations563);
					type57=type();
					state._fsp--;

					adaptor.addChild(root_0, type57.getTree());

					}
					break;

				default :
					break loop21;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "annotations"


	public static class start_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "start"
	
	public final TypeDescriptorParser.start_return start() throws RecognitionException {
		TypeDescriptorParser.start_return retval = new TypeDescriptorParser.start_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token WHITESPACE58=null;
		Token WHITESPACE60=null;
		Token EOF61=null;
		ParserRuleReturnScope orExpression59 =null;

		Object WHITESPACE58_tree=null;
		Object WHITESPACE60_tree=null;
		Object EOF61_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			
			int alt22=2;
			int LA22_0 = input.LA(1);
			if ( (LA22_0==WHITESPACE) ) {
				alt22=1;
			}
			switch (alt22) {
				case 1 :
					
					{
					WHITESPACE58=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_start574); 
					}
					break;

			}

			pushFollow(FOLLOW_orExpression_in_start578);
			orExpression59=orExpression();
			state._fsp--;

			adaptor.addChild(root_0, orExpression59.getTree());

			
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==WHITESPACE) ) {
				alt23=1;
			}
			switch (alt23) {
				case 1 :
					
					{
					WHITESPACE60=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_start580); 
					}
					break;

			}

			EOF61=(Token)match(input,EOF,FOLLOW_EOF_in_start584); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "start"


	public static class orExpression_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "orExpression"
	
	public final TypeDescriptorParser.orExpression_return orExpression() throws RecognitionException {
		TypeDescriptorParser.orExpression_return retval = new TypeDescriptorParser.orExpression_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token WHITESPACE63=null;
		Token OR64=null;
		Token WHITESPACE65=null;
		ParserRuleReturnScope andExpression62 =null;
		ParserRuleReturnScope andExpression66 =null;

		Object WHITESPACE63_tree=null;
		Object OR64_tree=null;
		Object WHITESPACE65_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_andExpression_in_orExpression594);
			andExpression62=andExpression();
			state._fsp--;

			adaptor.addChild(root_0, andExpression62.getTree());

			
			loop24:
			while (true) {
				int alt24=2;
				int LA24_0 = input.LA(1);
				if ( (LA24_0==WHITESPACE) ) {
					int LA24_1 = input.LA(2);
					if ( (LA24_1==OR) ) {
						alt24=1;
					}

				}

				switch (alt24) {
				case 1 :
					
					{
					WHITESPACE63=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_orExpression597); 
					OR64=(Token)match(input,OR,FOLLOW_OR_in_orExpression600); 
					OR64_tree = (Object)adaptor.create(OR64);
					root_0 = (Object)adaptor.becomeRoot(OR64_tree, root_0);

					WHITESPACE65=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_orExpression603); 
					pushFollow(FOLLOW_andExpression_in_orExpression606);
					andExpression66=andExpression();
					state._fsp--;

					adaptor.addChild(root_0, andExpression66.getTree());

					}
					break;

				default :
					break loop24;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "orExpression"


	public static class andExpression_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "andExpression"
	
	public final TypeDescriptorParser.andExpression_return andExpression() throws RecognitionException {
		TypeDescriptorParser.andExpression_return retval = new TypeDescriptorParser.andExpression_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token WHITESPACE68=null;
		Token AND69=null;
		Token WHITESPACE70=null;
		ParserRuleReturnScope booleanExpression67 =null;
		ParserRuleReturnScope booleanExpression71 =null;

		Object WHITESPACE68_tree=null;
		Object AND69_tree=null;
		Object WHITESPACE70_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_booleanExpression_in_andExpression617);
			booleanExpression67=booleanExpression();
			state._fsp--;

			adaptor.addChild(root_0, booleanExpression67.getTree());

			
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( (LA25_0==WHITESPACE) ) {
					int LA25_1 = input.LA(2);
					if ( (LA25_1==AND) ) {
						alt25=1;
					}

				}

				switch (alt25) {
				case 1 :
					
					{
					WHITESPACE68=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_andExpression620); 
					AND69=(Token)match(input,AND,FOLLOW_AND_in_andExpression623); 
					AND69_tree = (Object)adaptor.create(AND69);
					root_0 = (Object)adaptor.becomeRoot(AND69_tree, root_0);

					WHITESPACE70=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_andExpression626); 
					pushFollow(FOLLOW_booleanExpression_in_andExpression629);
					booleanExpression71=booleanExpression();
					state._fsp--;

					adaptor.addChild(root_0, booleanExpression71.getTree());

					}
					break;

				default :
					break loop25;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "andExpression"


	public static class booleanExpression_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "booleanExpression"
	
	public final TypeDescriptorParser.booleanExpression_return booleanExpression() throws RecognitionException {
		TypeDescriptorParser.booleanExpression_return retval = new TypeDescriptorParser.booleanExpression_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope expression72 =null;
		ParserRuleReturnScope notExpression73 =null;


		try {
			
			int alt26=2;
			int LA26_0 = input.LA(1);
			if ( (LA26_0==ANNOTATION||(LA26_0 >= HAVING && LA26_0 <= ID)||LA26_0==OPENING||LA26_0==SOMETHING) ) {
				alt26=1;
			}
			else if ( (LA26_0==NOT) ) {
				alt26=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 26, 0, input);
				throw nvae;
			}

			switch (alt26) {
				case 1 :
					
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_expression_in_booleanExpression642);
					expression72=expression();
					state._fsp--;

					adaptor.addChild(root_0, expression72.getTree());

					}
					break;
				case 2 :
					
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_notExpression_in_booleanExpression647);
					notExpression73=notExpression();
					state._fsp--;

					adaptor.addChild(root_0, notExpression73.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "booleanExpression"


	public static class notExpression_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "notExpression"
	
	public final TypeDescriptorParser.notExpression_return notExpression() throws RecognitionException {
		TypeDescriptorParser.notExpression_return retval = new TypeDescriptorParser.notExpression_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NOT74=null;
		Token WHITESPACE75=null;
		ParserRuleReturnScope expression76 =null;

		Object NOT74_tree=null;
		Object WHITESPACE75_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			NOT74=(Token)match(input,NOT,FOLLOW_NOT_in_notExpression658); 
			NOT74_tree = (Object)adaptor.create(NOT74);
			root_0 = (Object)adaptor.becomeRoot(NOT74_tree, root_0);

			
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==WHITESPACE) ) {
				alt27=1;
			}
			switch (alt27) {
				case 1 :
					
					{
					WHITESPACE75=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_notExpression661); 
					}
					break;

			}

			pushFollow(FOLLOW_expression_in_notExpression665);
			expression76=expression();
			state._fsp--;

			adaptor.addChild(root_0, expression76.getTree());

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "notExpression"


	public static class expression_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "expression"
	
	public final TypeDescriptorParser.expression_return expression() throws RecognitionException {
		TypeDescriptorParser.expression_return retval = new TypeDescriptorParser.expression_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OPENING78=null;
		Token WHITESPACE79=null;
		Token WHITESPACE81=null;
		Token CLOSING82=null;
		ParserRuleReturnScope selector77 =null;
		ParserRuleReturnScope orExpression80 =null;

		Object OPENING78_tree=null;
		Object WHITESPACE79_tree=null;
		Object WHITESPACE81_tree=null;
		Object CLOSING82_tree=null;

		try {
			
			int alt30=2;
			int LA30_0 = input.LA(1);
			if ( (LA30_0==ANNOTATION||(LA30_0 >= HAVING && LA30_0 <= ID)||LA30_0==SOMETHING) ) {
				alt30=1;
			}
			else if ( (LA30_0==OPENING) ) {
				alt30=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 30, 0, input);
				throw nvae;
			}

			switch (alt30) {
				case 1 :
					
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_selector_in_expression676);
					selector77=selector();
					state._fsp--;

					adaptor.addChild(root_0, selector77.getTree());

					}
					break;
				case 2 :
					
					{
					root_0 = (Object)adaptor.nil();


					OPENING78=(Token)match(input,OPENING,FOLLOW_OPENING_in_expression681); 
					
					int alt28=2;
					int LA28_0 = input.LA(1);
					if ( (LA28_0==WHITESPACE) ) {
						alt28=1;
					}
					switch (alt28) {
						case 1 :
							
							{
							WHITESPACE79=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_expression684); 
							}
							break;

					}

					pushFollow(FOLLOW_orExpression_in_expression688);
					orExpression80=orExpression();
					state._fsp--;

					adaptor.addChild(root_0, orExpression80.getTree());

					
					int alt29=2;
					int LA29_0 = input.LA(1);
					if ( (LA29_0==WHITESPACE) ) {
						alt29=1;
					}
					switch (alt29) {
						case 1 :
							
							{
							WHITESPACE81=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_expression690); 
							}
							break;

					}

					CLOSING82=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_expression694); 
					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expression"

	// Delegated rules



	public static final BitSet FOLLOW_typeRest_in_type283 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_clazz291 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_CHILD_in_clazz294 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotations_in_selector304 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector306 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_clazz_in_selector308 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_clazz_in_selector323 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HAVING_in_selector336 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector339 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_PROPERTY_in_selector342 = new BitSet(new long[]{0x0000000000404000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector345 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_OPENING_in_selector349 = new BitSet(new long[]{0x0000000000500820L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector352 = new BitSet(new long[]{0x0000000000100820L});
	public static final BitSet FOLLOW_annotations_in_selector357 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector359 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_clazz_in_selector364 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector366 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_selector369 = new BitSet(new long[]{0x0000000000400100L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector375 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_CLOSING_in_selector379 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HAVING_in_selector385 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector388 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_METHOD_in_selector391 = new BitSet(new long[]{0x0000000000404000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector394 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_OPENING_in_selector398 = new BitSet(new long[]{0x0000000000500820L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector401 = new BitSet(new long[]{0x0000000000100820L});
	public static final BitSet FOLLOW_annotations_in_selector406 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector408 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_clazz_in_selector413 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector415 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_selector418 = new BitSet(new long[]{0x0000000000404000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector424 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_OPENING_in_selector428 = new BitSet(new long[]{0x0000000000500940L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector431 = new BitSet(new long[]{0x0000000000100940L});
	public static final BitSet FOLLOW_methodParameters_in_selector436 = new BitSet(new long[]{0x0000000000400100L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector438 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_ANYTHING_in_selector444 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_CLOSING_in_selector449 = new BitSet(new long[]{0x0000000000400100L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector452 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_CLOSING_in_selector456 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_clazz_in_methodParameters469 = new BitSet(new long[]{0x0000000000400202L});
	public static final BitSet FOLLOW_WHITESPACE_in_methodParameters472 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COMMA_in_methodParameters476 = new BitSet(new long[]{0x0000000000500800L});
	public static final BitSet FOLLOW_WHITESPACE_in_methodParameters479 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_clazz_in_methodParameters483 = new BitSet(new long[]{0x0000000000400202L});
	public static final BitSet FOLLOW_set_in_typeRest498 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_SEPARATOR_in_typeRest506 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_typeRest510 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_ANYTHING_in_typeRest519 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_typeRest522 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_SEPARATOR_in_typeRest530 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_typeRest534 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_ANNOTATION_in_annotations551 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_type_in_annotations554 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_WHITESPACE_in_annotations557 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_ANNOTATION_in_annotations560 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_type_in_annotations563 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_WHITESPACE_in_start574 = new BitSet(new long[]{0x0000000000106C20L});
	public static final BitSet FOLLOW_orExpression_in_start578 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_start580 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_start584 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_andExpression_in_orExpression594 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_WHITESPACE_in_orExpression597 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_OR_in_orExpression600 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_orExpression603 = new BitSet(new long[]{0x0000000000106C20L});
	public static final BitSet FOLLOW_andExpression_in_orExpression606 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_booleanExpression_in_andExpression617 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_WHITESPACE_in_andExpression620 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_AND_in_andExpression623 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_andExpression626 = new BitSet(new long[]{0x0000000000106C20L});
	public static final BitSet FOLLOW_booleanExpression_in_andExpression629 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_expression_in_booleanExpression642 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_notExpression_in_booleanExpression647 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_notExpression658 = new BitSet(new long[]{0x0000000000504C20L});
	public static final BitSet FOLLOW_WHITESPACE_in_notExpression661 = new BitSet(new long[]{0x0000000000104C20L});
	public static final BitSet FOLLOW_expression_in_notExpression665 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_selector_in_expression676 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OPENING_in_expression681 = new BitSet(new long[]{0x0000000000506C20L});
	public static final BitSet FOLLOW_WHITESPACE_in_expression684 = new BitSet(new long[]{0x0000000000106C20L});
	public static final BitSet FOLLOW_orExpression_in_expression688 = new BitSet(new long[]{0x0000000000400100L});
	public static final BitSet FOLLOW_WHITESPACE_in_expression690 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_CLOSING_in_expression694 = new BitSet(new long[]{0x0000000000000002L});
}
