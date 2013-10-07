package com.agileapes.dragonfly.ext.impl.parser;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;


@SuppressWarnings("all")
public class TypeDescriptorParser extends Parser {
    public static final String[] tokenNames = new String[] {
            "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "ANNOTATION", "ANY", "CHILD",
            "CLOSING", "ID", "NOT", "OPENING", "OR", "PACKAGE", "SELECT", "SEPARATOR",
            "TYPE", "WHITESPACE"
    };
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
    @Override public String getGrammarFileName() { return "/Users/milad/Projects/Java/TypeDescriptor/src/main/resources/TypeDescriptor.g"; }


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
                pushFollow(FOLLOW_typeRest_in_type229);
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


                pushFollow(FOLLOW_type_in_clazz237);
                type2=type();
                state._fsp--;

                adaptor.addChild(root_0, type2.getTree());

                
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( (LA1_0==CHILD) ) {
                    alt1=1;
                }
                else if ( (LA1_0==EOF||LA1_0==CLOSING||LA1_0==WHITESPACE) ) {
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
                        CHILD3=(Token)match(input,CHILD,FOLLOW_CHILD_in_clazz240);
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


    public static class clazzSelector_return extends ParserRuleReturnScope {
        Object tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "clazzSelector"
    
    public final TypeDescriptorParser.clazzSelector_return clazzSelector() throws RecognitionException {
        TypeDescriptorParser.clazzSelector_return retval = new TypeDescriptorParser.clazzSelector_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ANY5=null;
        ParserRuleReturnScope clazz4 =null;

        Object ANY5_tree=null;

        try {
            
            
            {
                root_0 = (Object)adaptor.nil();


                
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( (LA2_0==ANY) ) {
                    int LA2_1 = input.LA(2);
                    if ( (LA2_1==PACKAGE||LA2_1==SEPARATOR) ) {
                        alt2=1;
                    }
                    else if ( (LA2_1==EOF||LA2_1==CLOSING||LA2_1==WHITESPACE) ) {
                        alt2=2;
                    }

                    else {
                        int nvaeMark = input.mark();
                        try {
                            input.consume();
                            NoViableAltException nvae =
                                    new NoViableAltException("", 2, 1, input);
                            throw nvae;
                        } finally {
                            input.rewind(nvaeMark);
                        }
                    }

                }
                else if ( (LA2_0==ID) ) {
                    alt2=1;
                }

                else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 2, 0, input);
                    throw nvae;
                }

                switch (alt2) {
                    case 1 :
                        
                    {
                        pushFollow(FOLLOW_clazz_in_clazzSelector253);
                        clazz4=clazz();
                        state._fsp--;

                        adaptor.addChild(root_0, clazz4.getTree());

                    }
                    break;
                    case 2 :
                        
                    {
                        ANY5=(Token)match(input,ANY,FOLLOW_ANY_in_clazzSelector255);
                        ANY5_tree = (Object)adaptor.create(ANY5);
                        adaptor.addChild(root_0, ANY5_tree);

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
    // $ANTLR end "clazzSelector"


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

        Token WHITESPACE7=null;
        ParserRuleReturnScope annotations6 =null;
        ParserRuleReturnScope clazzSelector8 =null;
        ParserRuleReturnScope clazzSelector9 =null;

        Object WHITESPACE7_tree=null;
        RewriteRuleTokenStream stream_WHITESPACE=new RewriteRuleTokenStream(adaptor,"token WHITESPACE");
        RewriteRuleSubtreeStream stream_annotations=new RewriteRuleSubtreeStream(adaptor,"rule annotations");
        RewriteRuleSubtreeStream stream_clazzSelector=new RewriteRuleSubtreeStream(adaptor,"rule clazzSelector");

        try {
            
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==ANNOTATION) ) {
                alt3=1;
            }
            else if ( (LA3_0==ANY||LA3_0==ID) ) {
                alt3=2;
            }

            else {
                NoViableAltException nvae =
                        new NoViableAltException("", 3, 0, input);
                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    
                {
                    pushFollow(FOLLOW_annotations_in_selector263);
                    annotations6=annotations();
                    state._fsp--;

                    stream_annotations.add(annotations6.getTree());
                    WHITESPACE7=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_selector265);
                    stream_WHITESPACE.add(WHITESPACE7);

                    pushFollow(FOLLOW_clazzSelector_in_selector267);
                    clazzSelector8=clazzSelector();
                    state._fsp--;

                    stream_clazzSelector.add(clazzSelector8.getTree());
                    // AST REWRITE
                    // elements: clazzSelector, annotations
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

                    root_0 = (Object)adaptor.nil();
                    // 50:48: -> ^( SELECT annotations clazzSelector )
                    {
                        
                        {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SELECT, "SELECT"), root_1);
                            adaptor.addChild(root_1, stream_annotations.nextTree());
                            adaptor.addChild(root_1, stream_clazzSelector.nextTree());
                            adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                }
                break;
                case 2 :
                    
                {
                    pushFollow(FOLLOW_clazzSelector_in_selector282);
                    clazzSelector9=clazzSelector();
                    state._fsp--;

                    stream_clazzSelector.add(clazzSelector9.getTree());
                    // AST REWRITE
                    // elements: clazzSelector
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

                    root_0 = (Object)adaptor.nil();
                    // 51:18: -> ^( SELECT clazzSelector )
                    {
                        
                        {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SELECT, "SELECT"), root_1);
                            adaptor.addChild(root_1, stream_clazzSelector.nextTree());
                            adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

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

        Token ANY10=null;
        Token SEPARATOR11=null;
        Token ID12=null;
        Token SEPARATOR13=null;
        Token ID14=null;
        Token ID15=null;
        Token SEPARATOR16=null;
        Token ID17=null;
        Token ID18=null;
        Token SEPARATOR19=null;
        Token ID20=null;
        Token PACKAGE21=null;
        Token ID22=null;
        Token SEPARATOR23=null;
        Token ID24=null;
        Token ANY25=null;
        Token PACKAGE26=null;
        Token ID27=null;
        Token SEPARATOR28=null;
        Token ID29=null;

        Object ANY10_tree=null;
        Object SEPARATOR11_tree=null;
        Object ID12_tree=null;
        Object SEPARATOR13_tree=null;
        Object ID14_tree=null;
        Object ID15_tree=null;
        Object SEPARATOR16_tree=null;
        Object ID17_tree=null;
        Object ID18_tree=null;
        Object SEPARATOR19_tree=null;
        Object ID20_tree=null;
        Object PACKAGE21_tree=null;
        Object ID22_tree=null;
        Object SEPARATOR23_tree=null;
        Object ID24_tree=null;
        Object ANY25_tree=null;
        Object PACKAGE26_tree=null;
        Object ID27_tree=null;
        Object SEPARATOR28_tree=null;
        Object ID29_tree=null;

        try {
            
            int alt9=4;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    
                {
                    root_0 = (Object)adaptor.nil();


                    ANY10=(Token)match(input,ANY,FOLLOW_ANY_in_typeRest303);
                    ANY10_tree = (Object)adaptor.create(ANY10);
                    adaptor.addChild(root_0, ANY10_tree);

                    SEPARATOR11=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest305);
                    SEPARATOR11_tree = (Object)adaptor.create(SEPARATOR11);
                    adaptor.addChild(root_0, SEPARATOR11_tree);

                    ID12=(Token)match(input,ID,FOLLOW_ID_in_typeRest307);
                    ID12_tree = (Object)adaptor.create(ID12);
                    adaptor.addChild(root_0, ID12_tree);

                    
                    loop4:
                    while (true) {
                        int alt4=2;
                        int LA4_0 = input.LA(1);
                        if ( (LA4_0==SEPARATOR) ) {
                            alt4=1;
                        }

                        switch (alt4) {
                            case 1 :
                                
                            {
                                SEPARATOR13=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest310);
                                SEPARATOR13_tree = (Object)adaptor.create(SEPARATOR13);
                                adaptor.addChild(root_0, SEPARATOR13_tree);

                                ID14=(Token)match(input,ID,FOLLOW_ID_in_typeRest312);
                                ID14_tree = (Object)adaptor.create(ID14);
                                adaptor.addChild(root_0, ID14_tree);

                            }
                            break;

                            default :
                                break loop4;
                        }
                    }

                }
                break;
                case 2 :
                    
                {
                    root_0 = (Object)adaptor.nil();


                    ID15=(Token)match(input,ID,FOLLOW_ID_in_typeRest319);
                    ID15_tree = (Object)adaptor.create(ID15);
                    adaptor.addChild(root_0, ID15_tree);

                    
                    loop5:
                    while (true) {
                        int alt5=2;
                        int LA5_0 = input.LA(1);
                        if ( (LA5_0==SEPARATOR) ) {
                            alt5=1;
                        }

                        switch (alt5) {
                            case 1 :
                                
                            {
                                SEPARATOR16=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest322);
                                SEPARATOR16_tree = (Object)adaptor.create(SEPARATOR16);
                                adaptor.addChild(root_0, SEPARATOR16_tree);

                                ID17=(Token)match(input,ID,FOLLOW_ID_in_typeRest324);
                                ID17_tree = (Object)adaptor.create(ID17);
                                adaptor.addChild(root_0, ID17_tree);

                            }
                            break;

                            default :
                                break loop5;
                        }
                    }

                }
                break;
                case 3 :
                    
                {
                    root_0 = (Object)adaptor.nil();


                    ID18=(Token)match(input,ID,FOLLOW_ID_in_typeRest331);
                    ID18_tree = (Object)adaptor.create(ID18);
                    adaptor.addChild(root_0, ID18_tree);

                    
                    loop6:
                    while (true) {
                        int alt6=2;
                        int LA6_0 = input.LA(1);
                        if ( (LA6_0==SEPARATOR) ) {
                            alt6=1;
                        }

                        switch (alt6) {
                            case 1 :
                                
                            {
                                SEPARATOR19=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest334);
                                SEPARATOR19_tree = (Object)adaptor.create(SEPARATOR19);
                                adaptor.addChild(root_0, SEPARATOR19_tree);

                                ID20=(Token)match(input,ID,FOLLOW_ID_in_typeRest336);
                                ID20_tree = (Object)adaptor.create(ID20);
                                adaptor.addChild(root_0, ID20_tree);

                            }
                            break;

                            default :
                                break loop6;
                        }
                    }

                    PACKAGE21=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_typeRest340);
                    PACKAGE21_tree = (Object)adaptor.create(PACKAGE21);
                    adaptor.addChild(root_0, PACKAGE21_tree);

                    ID22=(Token)match(input,ID,FOLLOW_ID_in_typeRest342);
                    ID22_tree = (Object)adaptor.create(ID22);
                    adaptor.addChild(root_0, ID22_tree);

                    
                    loop7:
                    while (true) {
                        int alt7=2;
                        int LA7_0 = input.LA(1);
                        if ( (LA7_0==SEPARATOR) ) {
                            alt7=1;
                        }

                        switch (alt7) {
                            case 1 :
                                
                            {
                                SEPARATOR23=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest345);
                                SEPARATOR23_tree = (Object)adaptor.create(SEPARATOR23);
                                adaptor.addChild(root_0, SEPARATOR23_tree);

                                ID24=(Token)match(input,ID,FOLLOW_ID_in_typeRest347);
                                ID24_tree = (Object)adaptor.create(ID24);
                                adaptor.addChild(root_0, ID24_tree);

                            }
                            break;

                            default :
                                break loop7;
                        }
                    }

                }
                break;
                case 4 :
                    
                {
                    root_0 = (Object)adaptor.nil();


                    ANY25=(Token)match(input,ANY,FOLLOW_ANY_in_typeRest354);
                    ANY25_tree = (Object)adaptor.create(ANY25);
                    adaptor.addChild(root_0, ANY25_tree);

                    PACKAGE26=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_typeRest356);
                    PACKAGE26_tree = (Object)adaptor.create(PACKAGE26);
                    adaptor.addChild(root_0, PACKAGE26_tree);

                    ID27=(Token)match(input,ID,FOLLOW_ID_in_typeRest358);
                    ID27_tree = (Object)adaptor.create(ID27);
                    adaptor.addChild(root_0, ID27_tree);

                    
                    loop8:
                    while (true) {
                        int alt8=2;
                        int LA8_0 = input.LA(1);
                        if ( (LA8_0==SEPARATOR) ) {
                            alt8=1;
                        }

                        switch (alt8) {
                            case 1 :
                                
                            {
                                SEPARATOR28=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_typeRest361);
                                SEPARATOR28_tree = (Object)adaptor.create(SEPARATOR28);
                                adaptor.addChild(root_0, SEPARATOR28_tree);

                                ID29=(Token)match(input,ID,FOLLOW_ID_in_typeRest363);
                                ID29_tree = (Object)adaptor.create(ID29);
                                adaptor.addChild(root_0, ID29_tree);

                            }
                            break;

                            default :
                                break loop8;
                        }
                    }

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

        Token ANNOTATION30=null;
        Token WHITESPACE32=null;
        Token ANNOTATION33=null;
        ParserRuleReturnScope type31 =null;
        ParserRuleReturnScope type34 =null;

        Object ANNOTATION30_tree=null;
        Object WHITESPACE32_tree=null;
        Object ANNOTATION33_tree=null;

        try {
            
            
            {
                root_0 = (Object)adaptor.nil();


                ANNOTATION30=(Token)match(input,ANNOTATION,FOLLOW_ANNOTATION_in_annotations374);
                ANNOTATION30_tree = (Object)adaptor.create(ANNOTATION30);
                root_0 = (Object)adaptor.becomeRoot(ANNOTATION30_tree, root_0);

                pushFollow(FOLLOW_type_in_annotations377);
                type31=type();
                state._fsp--;

                adaptor.addChild(root_0, type31.getTree());

                
                loop10:
                while (true) {
                    int alt10=2;
                    int LA10_0 = input.LA(1);
                    if ( (LA10_0==WHITESPACE) ) {
                        int LA10_1 = input.LA(2);
                        if ( (LA10_1==ANNOTATION) ) {
                            alt10=1;
                        }

                    }

                    switch (alt10) {
                        case 1 :
                            
                        {
                            WHITESPACE32=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_annotations380);
                            ANNOTATION33=(Token)match(input,ANNOTATION,FOLLOW_ANNOTATION_in_annotations383);
                            pushFollow(FOLLOW_type_in_annotations386);
                            type34=type();
                            state._fsp--;

                            adaptor.addChild(root_0, type34.getTree());

                        }
                        break;

                        default :
                            break loop10;
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

        Token WHITESPACE35=null;
        Token WHITESPACE37=null;
        Token EOF38=null;
        ParserRuleReturnScope orExpression36 =null;

        Object WHITESPACE35_tree=null;
        Object WHITESPACE37_tree=null;
        Object EOF38_tree=null;

        try {
            
            
            {
                root_0 = (Object)adaptor.nil();


                
                int alt11=2;
                int LA11_0 = input.LA(1);
                if ( (LA11_0==WHITESPACE) ) {
                    alt11=1;
                }
                switch (alt11) {
                    case 1 :
                        
                    {
                        WHITESPACE35=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_start397);
                    }
                    break;

                }

                pushFollow(FOLLOW_orExpression_in_start401);
                orExpression36=orExpression();
                state._fsp--;

                adaptor.addChild(root_0, orExpression36.getTree());

                
                int alt12=2;
                int LA12_0 = input.LA(1);
                if ( (LA12_0==WHITESPACE) ) {
                    alt12=1;
                }
                switch (alt12) {
                    case 1 :
                        
                    {
                        WHITESPACE37=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_start403);
                    }
                    break;

                }

                EOF38=(Token)match(input,EOF,FOLLOW_EOF_in_start407);
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

        Token WHITESPACE40=null;
        Token OR41=null;
        Token WHITESPACE42=null;
        ParserRuleReturnScope andExpression39 =null;
        ParserRuleReturnScope andExpression43 =null;

        Object WHITESPACE40_tree=null;
        Object OR41_tree=null;
        Object WHITESPACE42_tree=null;

        try {
            
            
            {
                root_0 = (Object)adaptor.nil();


                pushFollow(FOLLOW_andExpression_in_orExpression417);
                andExpression39=andExpression();
                state._fsp--;

                adaptor.addChild(root_0, andExpression39.getTree());

                
                loop13:
                while (true) {
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==WHITESPACE) ) {
                        int LA13_1 = input.LA(2);
                        if ( (LA13_1==OR) ) {
                            alt13=1;
                        }

                    }

                    switch (alt13) {
                        case 1 :
                            
                        {
                            WHITESPACE40=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_orExpression420);
                            OR41=(Token)match(input,OR,FOLLOW_OR_in_orExpression423);
                            OR41_tree = (Object)adaptor.create(OR41);
                            root_0 = (Object)adaptor.becomeRoot(OR41_tree, root_0);

                            WHITESPACE42=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_orExpression426);
                            pushFollow(FOLLOW_andExpression_in_orExpression429);
                            andExpression43=andExpression();
                            state._fsp--;

                            adaptor.addChild(root_0, andExpression43.getTree());

                        }
                        break;

                        default :
                            break loop13;
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

        Token WHITESPACE45=null;
        Token AND46=null;
        Token WHITESPACE47=null;
        ParserRuleReturnScope booleanExpression44 =null;
        ParserRuleReturnScope booleanExpression48 =null;

        Object WHITESPACE45_tree=null;
        Object AND46_tree=null;
        Object WHITESPACE47_tree=null;

        try {
            
            
            {
                root_0 = (Object)adaptor.nil();


                pushFollow(FOLLOW_booleanExpression_in_andExpression440);
                booleanExpression44=booleanExpression();
                state._fsp--;

                adaptor.addChild(root_0, booleanExpression44.getTree());

                
                loop14:
                while (true) {
                    int alt14=2;
                    int LA14_0 = input.LA(1);
                    if ( (LA14_0==WHITESPACE) ) {
                        int LA14_1 = input.LA(2);
                        if ( (LA14_1==AND) ) {
                            alt14=1;
                        }

                    }

                    switch (alt14) {
                        case 1 :
                            
                        {
                            WHITESPACE45=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_andExpression443);
                            AND46=(Token)match(input,AND,FOLLOW_AND_in_andExpression446);
                            AND46_tree = (Object)adaptor.create(AND46);
                            root_0 = (Object)adaptor.becomeRoot(AND46_tree, root_0);

                            WHITESPACE47=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_andExpression449);
                            pushFollow(FOLLOW_booleanExpression_in_andExpression452);
                            booleanExpression48=booleanExpression();
                            state._fsp--;

                            adaptor.addChild(root_0, booleanExpression48.getTree());

                        }
                        break;

                        default :
                            break loop14;
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

        ParserRuleReturnScope expression49 =null;
        ParserRuleReturnScope notExpression50 =null;


        try {
            
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( ((LA15_0 >= ANNOTATION && LA15_0 <= ANY)||LA15_0==ID||LA15_0==OPENING) ) {
                alt15=1;
            }
            else if ( (LA15_0==NOT) ) {
                alt15=2;
            }

            else {
                NoViableAltException nvae =
                        new NoViableAltException("", 15, 0, input);
                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    
                {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_expression_in_booleanExpression465);
                    expression49=expression();
                    state._fsp--;

                    adaptor.addChild(root_0, expression49.getTree());

                }
                break;
                case 2 :
                    
                {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_notExpression_in_booleanExpression470);
                    notExpression50=notExpression();
                    state._fsp--;

                    adaptor.addChild(root_0, notExpression50.getTree());

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

        Token NOT51=null;
        Token WHITESPACE52=null;
        ParserRuleReturnScope expression53 =null;

        Object NOT51_tree=null;
        Object WHITESPACE52_tree=null;

        try {
            
            
            {
                root_0 = (Object)adaptor.nil();


                NOT51=(Token)match(input,NOT,FOLLOW_NOT_in_notExpression481);
                NOT51_tree = (Object)adaptor.create(NOT51);
                root_0 = (Object)adaptor.becomeRoot(NOT51_tree, root_0);

                
                int alt16=2;
                int LA16_0 = input.LA(1);
                if ( (LA16_0==WHITESPACE) ) {
                    alt16=1;
                }
                switch (alt16) {
                    case 1 :
                        
                    {
                        WHITESPACE52=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_notExpression484);
                    }
                    break;

                }

                pushFollow(FOLLOW_expression_in_notExpression488);
                expression53=expression();
                state._fsp--;

                adaptor.addChild(root_0, expression53.getTree());

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

        Token OPENING55=null;
        Token WHITESPACE56=null;
        Token WHITESPACE58=null;
        Token CLOSING59=null;
        ParserRuleReturnScope selector54 =null;
        ParserRuleReturnScope orExpression57 =null;

        Object OPENING55_tree=null;
        Object WHITESPACE56_tree=null;
        Object WHITESPACE58_tree=null;
        Object CLOSING59_tree=null;

        try {
            
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( ((LA19_0 >= ANNOTATION && LA19_0 <= ANY)||LA19_0==ID) ) {
                alt19=1;
            }
            else if ( (LA19_0==OPENING) ) {
                alt19=2;
            }

            else {
                NoViableAltException nvae =
                        new NoViableAltException("", 19, 0, input);
                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    
                {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_selector_in_expression499);
                    selector54=selector();
                    state._fsp--;

                    adaptor.addChild(root_0, selector54.getTree());

                }
                break;
                case 2 :
                    
                {
                    root_0 = (Object)adaptor.nil();


                    OPENING55=(Token)match(input,OPENING,FOLLOW_OPENING_in_expression504);
                    
                    int alt17=2;
                    int LA17_0 = input.LA(1);
                    if ( (LA17_0==WHITESPACE) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            
                        {
                            WHITESPACE56=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_expression507);
                        }
                        break;

                    }

                    pushFollow(FOLLOW_orExpression_in_expression511);
                    orExpression57=orExpression();
                    state._fsp--;

                    adaptor.addChild(root_0, orExpression57.getTree());

                    
                    int alt18=2;
                    int LA18_0 = input.LA(1);
                    if ( (LA18_0==WHITESPACE) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            
                        {
                            WHITESPACE58=(Token)match(input,WHITESPACE,FOLLOW_WHITESPACE_in_expression513);
                        }
                        break;

                    }

                    CLOSING59=(Token)match(input,CLOSING,FOLLOW_CLOSING_in_expression517);
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


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
            "\11\uffff";
    static final String DFA9_eofS =
            "\2\uffff\1\6\5\uffff\1\6";
    static final String DFA9_minS =
            "\1\6\1\15\1\7\2\uffff\1\11\2\uffff\1\7";
    static final String DFA9_maxS =
            "\1\11\1\17\1\21\2\uffff\1\11\2\uffff\1\21";
    static final String DFA9_acceptS =
            "\3\uffff\1\1\1\4\1\uffff\1\2\1\3\1\uffff";
    static final String DFA9_specialS =
            "\11\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\1\2\uffff\1\2",
            "\1\4\1\uffff\1\3",
            "\2\6\4\uffff\1\7\1\uffff\1\5\1\uffff\1\6",
            "",
            "",
            "\1\10",
            "",
            "",
            "\2\6\4\uffff\1\7\1\uffff\1\5\1\uffff\1\6"
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    protected class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        @Override
        public String getDescription() {
            return "53:10: fragment typeRest : ( ANY SEPARATOR ID ( SEPARATOR ID )* | ID ( SEPARATOR ID )* | ID ( SEPARATOR ID )* PACKAGE ID ( SEPARATOR ID )* | ANY PACKAGE ID ( SEPARATOR ID )* );";
        }
    }

    public static final BitSet FOLLOW_typeRest_in_type229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_clazz237 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_CHILD_in_clazz240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_clazz_in_clazzSelector253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_clazzSelector255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_selector263 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_WHITESPACE_in_selector265 = new BitSet(new long[]{0x0000000000000240L});
    public static final BitSet FOLLOW_clazzSelector_in_selector267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_clazzSelector_in_selector282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_typeRest303 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_SEPARATOR_in_typeRest305 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_typeRest307 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_SEPARATOR_in_typeRest310 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_typeRest312 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ID_in_typeRest319 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_SEPARATOR_in_typeRest322 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_typeRest324 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ID_in_typeRest331 = new BitSet(new long[]{0x000000000000A000L});
    public static final BitSet FOLLOW_SEPARATOR_in_typeRest334 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_typeRest336 = new BitSet(new long[]{0x000000000000A000L});
    public static final BitSet FOLLOW_PACKAGE_in_typeRest340 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_typeRest342 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_SEPARATOR_in_typeRest345 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_typeRest347 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ANY_in_typeRest354 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_PACKAGE_in_typeRest356 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_typeRest358 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_SEPARATOR_in_typeRest361 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_typeRest363 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ANNOTATION_in_annotations374 = new BitSet(new long[]{0x0000000000000240L});
    public static final BitSet FOLLOW_type_in_annotations377 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_WHITESPACE_in_annotations380 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ANNOTATION_in_annotations383 = new BitSet(new long[]{0x0000000000000240L});
    public static final BitSet FOLLOW_type_in_annotations386 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_WHITESPACE_in_start397 = new BitSet(new long[]{0x0000000000000E60L});
    public static final BitSet FOLLOW_orExpression_in_start401 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_WHITESPACE_in_start403 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_start407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andExpression_in_orExpression417 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_WHITESPACE_in_orExpression420 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_OR_in_orExpression423 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_WHITESPACE_in_orExpression426 = new BitSet(new long[]{0x0000000000000E60L});
    public static final BitSet FOLLOW_andExpression_in_orExpression429 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_booleanExpression_in_andExpression440 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_WHITESPACE_in_andExpression443 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_AND_in_andExpression446 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_WHITESPACE_in_andExpression449 = new BitSet(new long[]{0x0000000000000E60L});
    public static final BitSet FOLLOW_booleanExpression_in_andExpression452 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_expression_in_booleanExpression465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notExpression_in_booleanExpression470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notExpression481 = new BitSet(new long[]{0x0000000000020A60L});
    public static final BitSet FOLLOW_WHITESPACE_in_notExpression484 = new BitSet(new long[]{0x0000000000000A60L});
    public static final BitSet FOLLOW_expression_in_notExpression488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_expression499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPENING_in_expression504 = new BitSet(new long[]{0x0000000000020E60L});
    public static final BitSet FOLLOW_WHITESPACE_in_expression507 = new BitSet(new long[]{0x0000000000000E60L});
    public static final BitSet FOLLOW_orExpression_in_expression511 = new BitSet(new long[]{0x0000000000020100L});
    public static final BitSet FOLLOW_WHITESPACE_in_expression513 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_CLOSING_in_expression517 = new BitSet(new long[]{0x0000000000000002L});
}