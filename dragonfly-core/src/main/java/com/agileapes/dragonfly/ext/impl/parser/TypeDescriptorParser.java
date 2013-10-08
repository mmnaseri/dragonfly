package com.agileapes.dragonfly.ext.impl.parser;

import org.antlr.runtime.*;

import org.antlr.runtime.tree.*;


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
			pushFollow(FOLLOW_typeRest_in_type269);
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


			pushFollow(FOLLOW_type_in_clazz277);
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
					CHILD3=(Token)match(input,CHILD,FOLLOW_CHILD_in_clazz280); 
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
		Token set16=null;
		Token WHITESPACE17=null;
		Token CLOSING18=null;
		Token HAVING19=null;
		Token WHITESPACE20=null;
		Token METHOD21=null;
		Token WHITESPACE22=null;
		Token OPENING23=null;
		Token WHITESPACE24=null;
		Token WHITESPACE26=null;
		Token WHITESPACE28=null;
		Token set29=null;
		Token WHITESPACE30=null;
		Token OPENING31=null;
		Token WHITESPACE32=null;
		Token WHITESPACE34=null;
		Token ANYTHING35=null;
		Token CLOSING36=null;
		Token WHITESPACE37=null;
		Token CLOSING38=null;
		ParserRuleReturnScope annotations4 =null;
		ParserRuleReturnScope clazz6 =null;
		ParserRuleReturnScope clazz7 =null;
		ParserRuleReturnScope clazz14 =null;
		ParserRuleReturnScope annotations25 =null;
		ParserRuleReturnScope clazz27 =null;
		ParserRuleReturnScope methodParameters33 =null;

		Object WHITESPACE5_tree=null;
		Object HAVING8_tree=null;
		Object WHITESPACE9_tree=null;
		Object PROPERTY10_tree=null;
		Object WHITESPACE11_tree=null;
		Object OPENING12_tree=null;
		Object WHITESPACE13_tree=null;
		Object WHITESPACE15_tree=null;
		Object set16_tree=null;
		Object WHITESPACE17_tree=null;
		Object CLOSING18_tree=null;
		Object HAVING19_tree=null;
		Object WHITESPACE20_tree=null;
		Object METHOD21_tree=null;
		Object WHITESPACE22_tree=null;
		Object OPENING23_tree=null;
		Object WHITESPACE24_tree=null;
		Object WHITESPACE26_tree=null;
		Object WHITESPACE28_tree=null;
		Object set29_tree=null;
		Object WHITESPACE30_tree=null;
		Object OPENING31_tree=null;
		Object WHITESPACE32_tree=null;
		Object WHITESPACE34_tree=null;
		Object ANYTHING35_tree=null;
		Object CLOSING36_tree=null;
		Object WHITESPACE37_tree=null;
		Object CLOSING38_tree=null;
		RewriteRuleTokenStream stream_WHITESPACE=new RewriteRuleTokenStream(adaptor,"token WHITESPACE");
		RewriteRuleSubtreeStream stream_annotations=new RewriteRuleSubtreeStream(adaptor,"rule annotations");
		RewriteRuleSubtreeStream stream_clazz=new RewriteRuleSubtreeStream(adaptor,"rule clazz");

		try {
			
			int alt13=4;
			switch ( input.LA(1) ) {
			case ANNOTATION:
				{
				alt13=1;
				}
				break;
			case ID:
			case SOMETHING:
				{
				alt13=2;
				}
				break;
			case HAVING:
				{
				int LA13_3 = input.LA(2);
				if ( (LA13_3==WHITESPACE) ) {
					int LA13_4 = input.LA(3);
					if ( (LA13_4==PROPERTY) ) {
						alt13=3;
					}
					else if ( (LA13_4==METHOD) ) {
						alt13=4;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 4, input);
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
							new NoViableAltException("", 13, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}
			switch (alt13) {
				case 1 :
					
					{
					pushFollow(FOLLOW_annotations_in_selector290);
					annotations4=annotations();
					state._fsp--;

					stream_annotations.add(annotations4.getTree());
					WHITESPACE5=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector292);  
					stream_WHITESPACE.add(WHITESPACE5);

					pushFollow(FOLLOW_clazz_in_selector294);
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
					pushFollow(FOLLOW_clazz_in_selector309);
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


					HAVING8=(Token)match(input,HAVING,FOLLOW_HAVING_in_selector322); 
					WHITESPACE9=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector325); 
					PROPERTY10=(Token)match(input,PROPERTY,FOLLOW_PROPERTY_in_selector328); 
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
							WHITESPACE11=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector331); 
							}
							break;

					}

					OPENING12=(Token)match(input,OPENING,FOLLOW_OPENING_in_selector335); 
					
					int alt3=2;
					int LA3_0 = input.LA(1);
					if ( (LA3_0==WHITESPACE) ) {
						alt3=1;
					}
					switch (alt3) {
						case 1 :
							
							{
							WHITESPACE13=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector338); 
							}
							break;

					}

					pushFollow(FOLLOW_clazz_in_selector342);
					clazz14=clazz();
					state._fsp--;

					adaptor.addChild(root_0, clazz14.getTree());

					WHITESPACE15=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector344); 
					set16=input.LT(1);
					if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
						input.consume();
						adaptor.addChild(root_0, (Object)adaptor.create(set16));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0==WHITESPACE) ) {
						alt4=1;
					}
					switch (alt4) {
						case 1 :
							
							{
							WHITESPACE17=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector353); 
							}
							break;

					}

					CLOSING18=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_selector357); 
					}
					break;
				case 4 :
					
					{
					root_0 = (Object)adaptor.nil();


					HAVING19=(Token)match(input,HAVING,FOLLOW_HAVING_in_selector363); 
					WHITESPACE20=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector366); 
					METHOD21=(Token)match(input,METHOD,FOLLOW_METHOD_in_selector369); 
					METHOD21_tree = (Object)adaptor.create(METHOD21);
					root_0 = (Object)adaptor.becomeRoot(METHOD21_tree, root_0);

					
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0==WHITESPACE) ) {
						alt5=1;
					}
					switch (alt5) {
						case 1 :
							
							{
							WHITESPACE22=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector372); 
							}
							break;

					}

					OPENING23=(Token)match(input,OPENING,FOLLOW_OPENING_in_selector376); 
					
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==WHITESPACE) ) {
						alt6=1;
					}
					switch (alt6) {
						case 1 :
							
							{
							WHITESPACE24=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector379); 
							}
							break;

					}

					
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==ANNOTATION) ) {
						alt7=1;
					}
					switch (alt7) {
						case 1 :
							
							{
							pushFollow(FOLLOW_annotations_in_selector384);
							annotations25=annotations();
							state._fsp--;

							adaptor.addChild(root_0, annotations25.getTree());

							WHITESPACE26=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector386); 
							}
							break;

					}

					pushFollow(FOLLOW_clazz_in_selector391);
					clazz27=clazz();
					state._fsp--;

					adaptor.addChild(root_0, clazz27.getTree());

					WHITESPACE28=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector393); 
					set29=input.LT(1);
					if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
						input.consume();
						adaptor.addChild(root_0, (Object)adaptor.create(set29));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					
					int alt8=2;
					int LA8_0 = input.LA(1);
					if ( (LA8_0==WHITESPACE) ) {
						alt8=1;
					}
					switch (alt8) {
						case 1 :
							
							{
							WHITESPACE30=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector402); 
							}
							break;

					}

					OPENING31=(Token)match(input,OPENING,FOLLOW_OPENING_in_selector406); 
					
					int alt9=2;
					int LA9_0 = input.LA(1);
					if ( (LA9_0==WHITESPACE) ) {
						alt9=1;
					}
					switch (alt9) {
						case 1 :
							
							{
							WHITESPACE32=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector409); 
							}
							break;

					}

					
					int alt11=3;
					switch ( input.LA(1) ) {
					case ID:
					case SOMETHING:
						{
						alt11=1;
						}
						break;
					case ANYTHING:
						{
						alt11=2;
						}
						break;
					case CLOSING:
						{
						alt11=3;
						}
						break;
					default:
						NoViableAltException nvae =
							new NoViableAltException("", 11, 0, input);
						throw nvae;
					}
					switch (alt11) {
						case 1 :
							
							{
							pushFollow(FOLLOW_methodParameters_in_selector414);
							methodParameters33=methodParameters();
							state._fsp--;

							adaptor.addChild(root_0, methodParameters33.getTree());

							
							int alt10=2;
							int LA10_0 = input.LA(1);
							if ( (LA10_0==WHITESPACE) ) {
								alt10=1;
							}
							switch (alt10) {
								case 1 :
									
									{
									WHITESPACE34=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector416); 
									}
									break;

							}

							}
							break;
						case 2 :
							
							{
							ANYTHING35=(Token)match(input,ANYTHING,FOLLOW_ANYTHING_in_selector422); 
							ANYTHING35_tree = (Object)adaptor.create(ANYTHING35);
							adaptor.addChild(root_0, ANYTHING35_tree);

							}
							break;
						case 3 :
							
							{
							}
							break;

					}

					CLOSING36=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_selector427); 
					
					int alt12=2;
					int LA12_0 = input.LA(1);
					if ( (LA12_0==WHITESPACE) ) {
						alt12=1;
					}
					switch (alt12) {
						case 1 :
							
							{
							WHITESPACE37=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector430); 
							}
							break;

					}

					CLOSING38=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_selector434); 
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

		Token WHITESPACE40=null;
		Token COMMA41=null;
		Token WHITESPACE42=null;
		ParserRuleReturnScope clazz39 =null;
		ParserRuleReturnScope clazz43 =null;

		Object WHITESPACE40_tree=null;
		Object COMMA41_tree=null;
		Object WHITESPACE42_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(PARAMS, "PARAMS"), root_0);
			
			
			{
			pushFollow(FOLLOW_clazz_in_methodParameters447);
			clazz39=clazz();
			state._fsp--;

			adaptor.addChild(root_0, clazz39.getTree());

			
			loop16:
			while (true) {
				int alt16=2;
				int LA16_0 = input.LA(1);
				if ( (LA16_0==WHITESPACE) ) {
					int LA16_1 = input.LA(2);
					if ( (LA16_1==COMMA) ) {
						alt16=1;
					}

				}
				else if ( (LA16_0==COMMA) ) {
					alt16=1;
				}

				switch (alt16) {
				case 1 :
					
					{
					
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0==WHITESPACE) ) {
						alt14=1;
					}
					switch (alt14) {
						case 1 :
							
							{
							WHITESPACE40=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_methodParameters450); 
							}
							break;

					}

					COMMA41=(Token)match(input,COMMA,FOLLOW_COMMA_in_methodParameters454); 
					
					int alt15=2;
					int LA15_0 = input.LA(1);
					if ( (LA15_0==WHITESPACE) ) {
						alt15=1;
					}
					switch (alt15) {
						case 1 :
							
							{
							WHITESPACE42=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_methodParameters457); 
							}
							break;

					}

					pushFollow(FOLLOW_clazz_in_methodParameters461);
					clazz43=clazz();
					state._fsp--;

					adaptor.addChild(root_0, clazz43.getTree());

					}
					break;

				default :
					break loop16;
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

		Token set44=null;
		Token SEPARATOR45=null;
		Token set46=null;
		Token ANYTHING47=null;
		Token set48=null;
		Token SEPARATOR49=null;
		Token set50=null;

		Object set44_tree=null;
		Object SEPARATOR45_tree=null;
		Object set46_tree=null;
		Object ANYTHING47_tree=null;
		Object set48_tree=null;
		Object SEPARATOR49_tree=null;
		Object set50_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==ID||LA17_0==SOMETHING) ) {
					int LA17_1 = input.LA(2);
					if ( (LA17_1==SEPARATOR) ) {
						alt17=1;
					}

				}

				switch (alt17) {
				case 1 :
					
					{
					set44=input.LT(1);
					if ( input.LA(1)==ID||input.LA(1)==SOMETHING ) {
						input.consume();
						adaptor.addChild(root_0, (Object)adaptor.create(set44));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					SEPARATOR45=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest484); 
					SEPARATOR45_tree = (Object)adaptor.create(SEPARATOR45);
					adaptor.addChild(root_0, SEPARATOR45_tree);

					}
					break;

				default :
					break loop17;
				}
			}

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
			
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( (LA19_0==ANYTHING) ) {
					alt19=1;
				}

				switch (alt19) {
				case 1 :
					
					{
					ANYTHING47=(Token)match(input,ANYTHING,FOLLOW_ANYTHING_in_typeRest497); 
					ANYTHING47_tree = (Object)adaptor.create(ANYTHING47);
					adaptor.addChild(root_0, ANYTHING47_tree);

					
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
							SEPARATOR49=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest508); 
							SEPARATOR49_tree = (Object)adaptor.create(SEPARATOR49);
							adaptor.addChild(root_0, SEPARATOR49_tree);

							}
							break;

						default :
							break loop18;
						}
					}

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
					}
					break;

				default :
					break loop19;
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

		Token ANNOTATION51=null;
		Token WHITESPACE53=null;
		Token ANNOTATION54=null;
		ParserRuleReturnScope type52 =null;
		ParserRuleReturnScope type55 =null;

		Object ANNOTATION51_tree=null;
		Object WHITESPACE53_tree=null;
		Object ANNOTATION54_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			ANNOTATION51=(Token)match(input,ANNOTATION,FOLLOW_ANNOTATION_in_annotations529); 
			ANNOTATION51_tree = (Object)adaptor.create(ANNOTATION51);
			root_0 = (Object)adaptor.becomeRoot(ANNOTATION51_tree, root_0);

			pushFollow(FOLLOW_type_in_annotations532);
			type52=type();
			state._fsp--;

			adaptor.addChild(root_0, type52.getTree());

			
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==WHITESPACE) ) {
					int LA20_1 = input.LA(2);
					if ( (LA20_1==ANNOTATION) ) {
						alt20=1;
					}

				}

				switch (alt20) {
				case 1 :
					
					{
					WHITESPACE53=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_annotations535); 
					ANNOTATION54=(Token)match(input,ANNOTATION,FOLLOW_ANNOTATION_in_annotations538); 
					pushFollow(FOLLOW_type_in_annotations541);
					type55=type();
					state._fsp--;

					adaptor.addChild(root_0, type55.getTree());

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

		Token WHITESPACE56=null;
		Token WHITESPACE58=null;
		Token EOF59=null;
		ParserRuleReturnScope orExpression57 =null;

		Object WHITESPACE56_tree=null;
		Object WHITESPACE58_tree=null;
		Object EOF59_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==WHITESPACE) ) {
				alt21=1;
			}
			switch (alt21) {
				case 1 :
					
					{
					WHITESPACE56=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_start552); 
					}
					break;

			}

			pushFollow(FOLLOW_orExpression_in_start556);
			orExpression57=orExpression();
			state._fsp--;

			adaptor.addChild(root_0, orExpression57.getTree());

			
			int alt22=2;
			int LA22_0 = input.LA(1);
			if ( (LA22_0==WHITESPACE) ) {
				alt22=1;
			}
			switch (alt22) {
				case 1 :
					
					{
					WHITESPACE58=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_start558); 
					}
					break;

			}

			EOF59=(Token)match(input,EOF,FOLLOW_EOF_in_start562); 
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

		Token WHITESPACE61=null;
		Token OR62=null;
		Token WHITESPACE63=null;
		ParserRuleReturnScope andExpression60 =null;
		ParserRuleReturnScope andExpression64 =null;

		Object WHITESPACE61_tree=null;
		Object OR62_tree=null;
		Object WHITESPACE63_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_andExpression_in_orExpression572);
			andExpression60=andExpression();
			state._fsp--;

			adaptor.addChild(root_0, andExpression60.getTree());

			
			loop23:
			while (true) {
				int alt23=2;
				int LA23_0 = input.LA(1);
				if ( (LA23_0==WHITESPACE) ) {
					int LA23_1 = input.LA(2);
					if ( (LA23_1==OR) ) {
						alt23=1;
					}

				}

				switch (alt23) {
				case 1 :
					
					{
					WHITESPACE61=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_orExpression575); 
					OR62=(Token)match(input,OR,FOLLOW_OR_in_orExpression578); 
					OR62_tree = (Object)adaptor.create(OR62);
					root_0 = (Object)adaptor.becomeRoot(OR62_tree, root_0);

					WHITESPACE63=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_orExpression581); 
					pushFollow(FOLLOW_andExpression_in_orExpression584);
					andExpression64=andExpression();
					state._fsp--;

					adaptor.addChild(root_0, andExpression64.getTree());

					}
					break;

				default :
					break loop23;
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

		Token WHITESPACE66=null;
		Token AND67=null;
		Token WHITESPACE68=null;
		ParserRuleReturnScope booleanExpression65 =null;
		ParserRuleReturnScope booleanExpression69 =null;

		Object WHITESPACE66_tree=null;
		Object AND67_tree=null;
		Object WHITESPACE68_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_booleanExpression_in_andExpression595);
			booleanExpression65=booleanExpression();
			state._fsp--;

			adaptor.addChild(root_0, booleanExpression65.getTree());

			
			loop24:
			while (true) {
				int alt24=2;
				int LA24_0 = input.LA(1);
				if ( (LA24_0==WHITESPACE) ) {
					int LA24_1 = input.LA(2);
					if ( (LA24_1==AND) ) {
						alt24=1;
					}

				}

				switch (alt24) {
				case 1 :
					
					{
					WHITESPACE66=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_andExpression598); 
					AND67=(Token)match(input,AND,FOLLOW_AND_in_andExpression601); 
					AND67_tree = (Object)adaptor.create(AND67);
					root_0 = (Object)adaptor.becomeRoot(AND67_tree, root_0);

					WHITESPACE68=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_andExpression604); 
					pushFollow(FOLLOW_booleanExpression_in_andExpression607);
					booleanExpression69=booleanExpression();
					state._fsp--;

					adaptor.addChild(root_0, booleanExpression69.getTree());

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

		ParserRuleReturnScope expression70 =null;
		ParserRuleReturnScope notExpression71 =null;


		try {
			
			int alt25=2;
			int LA25_0 = input.LA(1);
			if ( (LA25_0==ANNOTATION||(LA25_0 >= HAVING && LA25_0 <= ID)||LA25_0==OPENING||LA25_0==SOMETHING) ) {
				alt25=1;
			}
			else if ( (LA25_0==NOT) ) {
				alt25=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 25, 0, input);
				throw nvae;
			}

			switch (alt25) {
				case 1 :
					
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_expression_in_booleanExpression620);
					expression70=expression();
					state._fsp--;

					adaptor.addChild(root_0, expression70.getTree());

					}
					break;
				case 2 :
					
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_notExpression_in_booleanExpression625);
					notExpression71=notExpression();
					state._fsp--;

					adaptor.addChild(root_0, notExpression71.getTree());

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

		Token NOT72=null;
		Token WHITESPACE73=null;
		ParserRuleReturnScope expression74 =null;

		Object NOT72_tree=null;
		Object WHITESPACE73_tree=null;

		try {
			
			
			{
			root_0 = (Object)adaptor.nil();


			NOT72=(Token)match(input,NOT,FOLLOW_NOT_in_notExpression636); 
			NOT72_tree = (Object)adaptor.create(NOT72);
			root_0 = (Object)adaptor.becomeRoot(NOT72_tree, root_0);

			
			int alt26=2;
			int LA26_0 = input.LA(1);
			if ( (LA26_0==WHITESPACE) ) {
				alt26=1;
			}
			switch (alt26) {
				case 1 :
					
					{
					WHITESPACE73=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_notExpression639); 
					}
					break;

			}

			pushFollow(FOLLOW_expression_in_notExpression643);
			expression74=expression();
			state._fsp--;

			adaptor.addChild(root_0, expression74.getTree());

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

		Token OPENING76=null;
		Token WHITESPACE77=null;
		Token WHITESPACE79=null;
		Token CLOSING80=null;
		ParserRuleReturnScope selector75 =null;
		ParserRuleReturnScope orExpression78 =null;

		Object OPENING76_tree=null;
		Object WHITESPACE77_tree=null;
		Object WHITESPACE79_tree=null;
		Object CLOSING80_tree=null;

		try {
			
			int alt29=2;
			int LA29_0 = input.LA(1);
			if ( (LA29_0==ANNOTATION||(LA29_0 >= HAVING && LA29_0 <= ID)||LA29_0==SOMETHING) ) {
				alt29=1;
			}
			else if ( (LA29_0==OPENING) ) {
				alt29=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 29, 0, input);
				throw nvae;
			}

			switch (alt29) {
				case 1 :
					
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_selector_in_expression654);
					selector75=selector();
					state._fsp--;

					adaptor.addChild(root_0, selector75.getTree());

					}
					break;
				case 2 :
					
					{
					root_0 = (Object)adaptor.nil();


					OPENING76=(Token)match(input,OPENING,FOLLOW_OPENING_in_expression659); 
					
					int alt27=2;
					int LA27_0 = input.LA(1);
					if ( (LA27_0==WHITESPACE) ) {
						alt27=1;
					}
					switch (alt27) {
						case 1 :
							
							{
							WHITESPACE77=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_expression662); 
							}
							break;

					}

					pushFollow(FOLLOW_orExpression_in_expression666);
					orExpression78=orExpression();
					state._fsp--;

					adaptor.addChild(root_0, orExpression78.getTree());

					
					int alt28=2;
					int LA28_0 = input.LA(1);
					if ( (LA28_0==WHITESPACE) ) {
						alt28=1;
					}
					switch (alt28) {
						case 1 :
							
							{
							WHITESPACE79=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_expression668); 
							}
							break;

					}

					CLOSING80=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_expression672); 
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



	public static final BitSet FOLLOW_typeRest_in_type269 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_clazz277 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_CHILD_in_clazz280 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotations_in_selector290 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector292 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_clazz_in_selector294 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_clazz_in_selector309 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HAVING_in_selector322 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector325 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_PROPERTY_in_selector328 = new BitSet(new long[]{0x0000000000404000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector331 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_OPENING_in_selector335 = new BitSet(new long[]{0x0000000000500800L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector338 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_clazz_in_selector342 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector344 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_selector347 = new BitSet(new long[]{0x0000000000400100L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector353 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_CLOSING_in_selector357 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HAVING_in_selector363 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector366 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_METHOD_in_selector369 = new BitSet(new long[]{0x0000000000404000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector372 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_OPENING_in_selector376 = new BitSet(new long[]{0x0000000000500820L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector379 = new BitSet(new long[]{0x0000000000100820L});
	public static final BitSet FOLLOW_annotations_in_selector384 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector386 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_clazz_in_selector391 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector393 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_selector396 = new BitSet(new long[]{0x0000000000404000L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector402 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_OPENING_in_selector406 = new BitSet(new long[]{0x0000000000500940L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector409 = new BitSet(new long[]{0x0000000000100940L});
	public static final BitSet FOLLOW_methodParameters_in_selector414 = new BitSet(new long[]{0x0000000000400100L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector416 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_ANYTHING_in_selector422 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_CLOSING_in_selector427 = new BitSet(new long[]{0x0000000000400100L});
	public static final BitSet FOLLOW_WHITESPACE_in_selector430 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_CLOSING_in_selector434 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_clazz_in_methodParameters447 = new BitSet(new long[]{0x0000000000400202L});
	public static final BitSet FOLLOW_WHITESPACE_in_methodParameters450 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COMMA_in_methodParameters454 = new BitSet(new long[]{0x0000000000500800L});
	public static final BitSet FOLLOW_WHITESPACE_in_methodParameters457 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_clazz_in_methodParameters461 = new BitSet(new long[]{0x0000000000400202L});
	public static final BitSet FOLLOW_set_in_typeRest476 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_SEPARATOR_in_typeRest484 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_typeRest488 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_ANYTHING_in_typeRest497 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_typeRest500 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_SEPARATOR_in_typeRest508 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_typeRest512 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_ANNOTATION_in_annotations529 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_type_in_annotations532 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_WHITESPACE_in_annotations535 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_ANNOTATION_in_annotations538 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_type_in_annotations541 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_WHITESPACE_in_start552 = new BitSet(new long[]{0x0000000000106C20L});
	public static final BitSet FOLLOW_orExpression_in_start556 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_start558 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_start562 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_andExpression_in_orExpression572 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_WHITESPACE_in_orExpression575 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_OR_in_orExpression578 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_orExpression581 = new BitSet(new long[]{0x0000000000106C20L});
	public static final BitSet FOLLOW_andExpression_in_orExpression584 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_booleanExpression_in_andExpression595 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_WHITESPACE_in_andExpression598 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_AND_in_andExpression601 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_WHITESPACE_in_andExpression604 = new BitSet(new long[]{0x0000000000106C20L});
	public static final BitSet FOLLOW_booleanExpression_in_andExpression607 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_expression_in_booleanExpression620 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_notExpression_in_booleanExpression625 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_notExpression636 = new BitSet(new long[]{0x0000000000504C20L});
	public static final BitSet FOLLOW_WHITESPACE_in_notExpression639 = new BitSet(new long[]{0x0000000000104C20L});
	public static final BitSet FOLLOW_expression_in_notExpression643 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_selector_in_expression654 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OPENING_in_expression659 = new BitSet(new long[]{0x0000000000506C20L});
	public static final BitSet FOLLOW_WHITESPACE_in_expression662 = new BitSet(new long[]{0x0000000000106C20L});
	public static final BitSet FOLLOW_orExpression_in_expression666 = new BitSet(new long[]{0x0000000000400100L});
	public static final BitSet FOLLOW_WHITESPACE_in_expression668 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_CLOSING_in_expression672 = new BitSet(new long[]{0x0000000000000002L});
}
