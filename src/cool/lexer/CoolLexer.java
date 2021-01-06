// Generated from C:/Users/ungur/Desktop/CPL_tema2/src/cool/lexer\CoolLexer.g4 by ANTLR 4.8

    package cool.lexer;	

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CoolLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ERROR=1, CLASS=2, INHERITS=3, IF=4, THEN=5, ELSE=6, FI=7, LET=8, IN=9, 
		WHILE=10, LOOP=11, POOL=12, CASE=13, ESAC=14, OF=15, NEW=16, ISVOID=17, 
		NOT=18, BOOL=19, SEMICOLON=20, COLON=21, COMMA=22, LPAREN=23, RPAREN=24, 
		LBRACE=25, RBRACE=26, PLUS=27, MINUS=28, MULT=29, DIV=30, EQUAL=31, LT=32, 
		LE=33, ASSIGN=34, AT=35, DOT=36, CASE_THEN=37, TILDE=38, STRING=39, LINE_COMMENT=40, 
		UNMATCHED_COMMENT_BLOCK=41, BLOCK_COMMENT=42, TYPE=43, ID=44, INT=45, 
		WS=46, UNKNOWN_CHAR=47;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"CLASS", "INHERITS", "IF", "THEN", "ELSE", "FI", "LET", "IN", "WHILE", 
			"LOOP", "POOL", "CASE", "ESAC", "OF", "NEW", "ISVOID", "NOT", "BOOL", 
			"SEMICOLON", "COLON", "COMMA", "LPAREN", "RPAREN", "LBRACE", "RBRACE", 
			"PLUS", "MINUS", "MULT", "DIV", "EQUAL", "LT", "LE", "ASSIGN", "AT", 
			"DOT", "CASE_THEN", "TILDE", "NEWLINE", "ESCAPED", "QUOTE", "STRING", 
			"LINE_COMMENT", "UNMATCHED_COMMENT_BLOCK", "BLOCK_COMMENT", "UPPERCASE", 
			"LOWERCASE", "LETTER", "DIGIT", "TYPE", "ID", "INT", "WS", "UNKNOWN_CHAR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'class'", "'inherits'", "'if'", "'then'", "'else'", "'fi'", 
			"'let'", "'in'", "'while'", "'loop'", "'pool'", "'case'", "'esac'", "'of'", 
			"'new'", "'isvoid'", "'not'", null, "';'", "':'", "','", "'('", "')'", 
			"'{'", "'}'", "'+'", "'-'", "'*'", "'/'", "'='", "'<'", "'<='", "'<-'", 
			"'@'", "'.'", "'=>'", "'~'", null, null, "'*)'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ERROR", "CLASS", "INHERITS", "IF", "THEN", "ELSE", "FI", "LET", 
			"IN", "WHILE", "LOOP", "POOL", "CASE", "ESAC", "OF", "NEW", "ISVOID", 
			"NOT", "BOOL", "SEMICOLON", "COLON", "COMMA", "LPAREN", "RPAREN", "LBRACE", 
			"RBRACE", "PLUS", "MINUS", "MULT", "DIV", "EQUAL", "LT", "LE", "ASSIGN", 
			"AT", "DOT", "CASE_THEN", "TILDE", "STRING", "LINE_COMMENT", "UNMATCHED_COMMENT_BLOCK", 
			"BLOCK_COMMENT", "TYPE", "ID", "INT", "WS", "UNKNOWN_CHAR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	    int stringLen = 0;
	    private void raiseError(String msg) {
	        setText(msg);
	        setType(ERROR);
	    }

	    /**
	     * Strips the string of quotation marks, manages all escaped sequences
	     * replaces escaped characters such as newlines and tabs to their conterpart character,
	     * manages effective newlines, as well as strips any 'unnecessary' escaped character.
	     * @param str String to format and parse
	     * @return String the managed and formatted string
	     */
	    private String formatString(String str) {
	        return str.substring(1, getText().length()-1).replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t").replaceAll("\\\\([^bf])", "$1");
	    }


	public CoolLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CoolLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 40:
			STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 42:
			UNMATCHED_COMMENT_BLOCK_action((RuleContext)_localctx, actionIndex);
			break;
		case 43:
			BLOCK_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 52:
			UNKNOWN_CHAR_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 ++stringLen; 
			break;
		case 1:
			 raiseError("\"String contains null character\""); 
			break;
		case 2:
			 ++stringLen; 
			break;
		case 3:
			 raiseError("EOF in string constant"); stringLen = 0; 
			break;
		case 4:
			 raiseError("Unterminated string constant"); 
			break;
		case 5:
			 setText(formatString(getText())); if (stringLen > 1024) raiseError("String constant too long"); stringLen = 0; 
			break;
		}
	}
	private void UNMATCHED_COMMENT_BLOCK_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 6:
			 raiseError("Unmatched *)"); 
			break;
		}
	}
	private void BLOCK_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 7:
			 skip(); 
			break;
		case 8:
			 raiseError("EOF in comment"); 
			break;
		}
	}
	private void UNKNOWN_CHAR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 9:
			 raiseError("Invalid character: " + getText()); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\61\u016d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3"+
		"\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3"+
		"\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3"+
		"\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3"+
		"\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\5\23\u00c9\n\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30"+
		"\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37"+
		"\3\37\3 \3 \3!\3!\3!\3\"\3\"\3\"\3#\3#\3$\3$\3%\3%\3%\3&\3&\3\'\5\'\u00f5"+
		"\n\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\5(\u0103\n(\3)\3)\3*\3*\3*"+
		"\3*\3*\3*\3*\3*\3*\7*\u0110\n*\f*\16*\u0113\13*\3*\3*\3*\3*\3*\3*\3*\3"+
		"*\5*\u011d\n*\3+\3+\3+\3+\7+\u0123\n+\f+\16+\u0126\13+\3+\3+\5+\u012a"+
		"\n+\3+\3+\3,\3,\3,\3,\3,\3-\3-\3-\3-\3-\7-\u0138\n-\f-\16-\u013b\13-\3"+
		"-\3-\3-\3-\3-\3-\5-\u0143\n-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62"+
		"\3\62\3\62\7\62\u0151\n\62\f\62\16\62\u0154\13\62\3\63\3\63\3\63\3\63"+
		"\7\63\u015a\n\63\f\63\16\63\u015d\13\63\3\64\6\64\u0160\n\64\r\64\16\64"+
		"\u0161\3\65\6\65\u0165\n\65\r\65\16\65\u0166\3\65\3\65\3\66\3\66\3\66"+
		"\5\u0111\u0124\u0139\2\67\3\4\5\5\7\6\t\7\13\b\r\t\17\n\21\13\23\f\25"+
		"\r\27\16\31\17\33\20\35\21\37\22!\23#\24%\25\'\26)\27+\30-\31/\32\61\33"+
		"\63\34\65\35\67\369\37; =!?\"A#C$E%G&I\'K(M\2O\2Q\2S)U*W+Y,[\2]\2_\2a"+
		"\2c-e.g/i\60k\61\3\2\7\3\2C\\\3\2c|\4\2C\\c|\3\2\62;\5\2\13\f\16\17\""+
		"\"\2\u017e\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2"+
		"\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y"+
		"\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\3m\3\2"+
		"\2\2\5s\3\2\2\2\7|\3\2\2\2\t\177\3\2\2\2\13\u0084\3\2\2\2\r\u0089\3\2"+
		"\2\2\17\u008c\3\2\2\2\21\u0090\3\2\2\2\23\u0093\3\2\2\2\25\u0099\3\2\2"+
		"\2\27\u009e\3\2\2\2\31\u00a3\3\2\2\2\33\u00a8\3\2\2\2\35\u00ad\3\2\2\2"+
		"\37\u00b0\3\2\2\2!\u00b4\3\2\2\2#\u00bb\3\2\2\2%\u00c8\3\2\2\2\'\u00ca"+
		"\3\2\2\2)\u00cc\3\2\2\2+\u00ce\3\2\2\2-\u00d0\3\2\2\2/\u00d2\3\2\2\2\61"+
		"\u00d4\3\2\2\2\63\u00d6\3\2\2\2\65\u00d8\3\2\2\2\67\u00da\3\2\2\29\u00dc"+
		"\3\2\2\2;\u00de\3\2\2\2=\u00e0\3\2\2\2?\u00e2\3\2\2\2A\u00e4\3\2\2\2C"+
		"\u00e7\3\2\2\2E\u00ea\3\2\2\2G\u00ec\3\2\2\2I\u00ee\3\2\2\2K\u00f1\3\2"+
		"\2\2M\u00f4\3\2\2\2O\u0102\3\2\2\2Q\u0104\3\2\2\2S\u0106\3\2\2\2U\u011e"+
		"\3\2\2\2W\u012d\3\2\2\2Y\u0132\3\2\2\2[\u0144\3\2\2\2]\u0146\3\2\2\2_"+
		"\u0148\3\2\2\2a\u014a\3\2\2\2c\u014c\3\2\2\2e\u0155\3\2\2\2g\u015f\3\2"+
		"\2\2i\u0164\3\2\2\2k\u016a\3\2\2\2mn\7e\2\2no\7n\2\2op\7c\2\2pq\7u\2\2"+
		"qr\7u\2\2r\4\3\2\2\2st\7k\2\2tu\7p\2\2uv\7j\2\2vw\7g\2\2wx\7t\2\2xy\7"+
		"k\2\2yz\7v\2\2z{\7u\2\2{\6\3\2\2\2|}\7k\2\2}~\7h\2\2~\b\3\2\2\2\177\u0080"+
		"\7v\2\2\u0080\u0081\7j\2\2\u0081\u0082\7g\2\2\u0082\u0083\7p\2\2\u0083"+
		"\n\3\2\2\2\u0084\u0085\7g\2\2\u0085\u0086\7n\2\2\u0086\u0087\7u\2\2\u0087"+
		"\u0088\7g\2\2\u0088\f\3\2\2\2\u0089\u008a\7h\2\2\u008a\u008b\7k\2\2\u008b"+
		"\16\3\2\2\2\u008c\u008d\7n\2\2\u008d\u008e\7g\2\2\u008e\u008f\7v\2\2\u008f"+
		"\20\3\2\2\2\u0090\u0091\7k\2\2\u0091\u0092\7p\2\2\u0092\22\3\2\2\2\u0093"+
		"\u0094\7y\2\2\u0094\u0095\7j\2\2\u0095\u0096\7k\2\2\u0096\u0097\7n\2\2"+
		"\u0097\u0098\7g\2\2\u0098\24\3\2\2\2\u0099\u009a\7n\2\2\u009a\u009b\7"+
		"q\2\2\u009b\u009c\7q\2\2\u009c\u009d\7r\2\2\u009d\26\3\2\2\2\u009e\u009f"+
		"\7r\2\2\u009f\u00a0\7q\2\2\u00a0\u00a1\7q\2\2\u00a1\u00a2\7n\2\2\u00a2"+
		"\30\3\2\2\2\u00a3\u00a4\7e\2\2\u00a4\u00a5\7c\2\2\u00a5\u00a6\7u\2\2\u00a6"+
		"\u00a7\7g\2\2\u00a7\32\3\2\2\2\u00a8\u00a9\7g\2\2\u00a9\u00aa\7u\2\2\u00aa"+
		"\u00ab\7c\2\2\u00ab\u00ac\7e\2\2\u00ac\34\3\2\2\2\u00ad\u00ae\7q\2\2\u00ae"+
		"\u00af\7h\2\2\u00af\36\3\2\2\2\u00b0\u00b1\7p\2\2\u00b1\u00b2\7g\2\2\u00b2"+
		"\u00b3\7y\2\2\u00b3 \3\2\2\2\u00b4\u00b5\7k\2\2\u00b5\u00b6\7u\2\2\u00b6"+
		"\u00b7\7x\2\2\u00b7\u00b8\7q\2\2\u00b8\u00b9\7k\2\2\u00b9\u00ba\7f\2\2"+
		"\u00ba\"\3\2\2\2\u00bb\u00bc\7p\2\2\u00bc\u00bd\7q\2\2\u00bd\u00be\7v"+
		"\2\2\u00be$\3\2\2\2\u00bf\u00c0\7v\2\2\u00c0\u00c1\7t\2\2\u00c1\u00c2"+
		"\7w\2\2\u00c2\u00c9\7g\2\2\u00c3\u00c4\7h\2\2\u00c4\u00c5\7c\2\2\u00c5"+
		"\u00c6\7n\2\2\u00c6\u00c7\7u\2\2\u00c7\u00c9\7g\2\2\u00c8\u00bf\3\2\2"+
		"\2\u00c8\u00c3\3\2\2\2\u00c9&\3\2\2\2\u00ca\u00cb\7=\2\2\u00cb(\3\2\2"+
		"\2\u00cc\u00cd\7<\2\2\u00cd*\3\2\2\2\u00ce\u00cf\7.\2\2\u00cf,\3\2\2\2"+
		"\u00d0\u00d1\7*\2\2\u00d1.\3\2\2\2\u00d2\u00d3\7+\2\2\u00d3\60\3\2\2\2"+
		"\u00d4\u00d5\7}\2\2\u00d5\62\3\2\2\2\u00d6\u00d7\7\177\2\2\u00d7\64\3"+
		"\2\2\2\u00d8\u00d9\7-\2\2\u00d9\66\3\2\2\2\u00da\u00db\7/\2\2\u00db8\3"+
		"\2\2\2\u00dc\u00dd\7,\2\2\u00dd:\3\2\2\2\u00de\u00df\7\61\2\2\u00df<\3"+
		"\2\2\2\u00e0\u00e1\7?\2\2\u00e1>\3\2\2\2\u00e2\u00e3\7>\2\2\u00e3@\3\2"+
		"\2\2\u00e4\u00e5\7>\2\2\u00e5\u00e6\7?\2\2\u00e6B\3\2\2\2\u00e7\u00e8"+
		"\7>\2\2\u00e8\u00e9\7/\2\2\u00e9D\3\2\2\2\u00ea\u00eb\7B\2\2\u00ebF\3"+
		"\2\2\2\u00ec\u00ed\7\60\2\2\u00edH\3\2\2\2\u00ee\u00ef\7?\2\2\u00ef\u00f0"+
		"\7@\2\2\u00f0J\3\2\2\2\u00f1\u00f2\7\u0080\2\2\u00f2L\3\2\2\2\u00f3\u00f5"+
		"\7\17\2\2\u00f4\u00f3\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f6\3\2\2\2"+
		"\u00f6\u00f7\7\f\2\2\u00f7N\3\2\2\2\u00f8\u00f9\7^\2\2\u00f9\u0103\7d"+
		"\2\2\u00fa\u00fb\7^\2\2\u00fb\u0103\7v\2\2\u00fc\u00fd\7^\2\2\u00fd\u0103"+
		"\5M\'\2\u00fe\u00ff\7^\2\2\u00ff\u0103\7h\2\2\u0100\u0101\7^\2\2\u0101"+
		"\u0103\7p\2\2\u0102\u00f8\3\2\2\2\u0102\u00fa\3\2\2\2\u0102\u00fc\3\2"+
		"\2\2\u0102\u00fe\3\2\2\2\u0102\u0100\3\2\2\2\u0103P\3\2\2\2\u0104\u0105"+
		"\7$\2\2\u0105R\3\2\2\2\u0106\u0111\5Q)\2\u0107\u0108\5O(\2\u0108\u0109"+
		"\b*\2\2\u0109\u0110\3\2\2\2\u010a\u0110\7^\2\2\u010b\u010c\7\2\2\2\u010c"+
		"\u0110\b*\3\2\u010d\u010e\13\2\2\2\u010e\u0110\b*\4\2\u010f\u0107\3\2"+
		"\2\2\u010f\u010a\3\2\2\2\u010f\u010b\3\2\2\2\u010f\u010d\3\2\2\2\u0110"+
		"\u0113\3\2\2\2\u0111\u0112\3\2\2\2\u0111\u010f\3\2\2\2\u0112\u011c\3\2"+
		"\2\2\u0113\u0111\3\2\2\2\u0114\u0115\7\2\2\3\u0115\u011d\b*\5\2\u0116"+
		"\u0117\5M\'\2\u0117\u0118\b*\6\2\u0118\u011d\3\2\2\2\u0119\u011a\5Q)\2"+
		"\u011a\u011b\b*\7\2\u011b\u011d\3\2\2\2\u011c\u0114\3\2\2\2\u011c\u0116"+
		"\3\2\2\2\u011c\u0119\3\2\2\2\u011dT\3\2\2\2\u011e\u011f\7/\2\2\u011f\u0120"+
		"\7/\2\2\u0120\u0124\3\2\2\2\u0121\u0123\13\2\2\2\u0122\u0121\3\2\2\2\u0123"+
		"\u0126\3\2\2\2\u0124\u0125\3\2\2\2\u0124\u0122\3\2\2\2\u0125\u0129\3\2"+
		"\2\2\u0126\u0124\3\2\2\2\u0127\u012a\5M\'\2\u0128\u012a\7\2\2\3\u0129"+
		"\u0127\3\2\2\2\u0129\u0128\3\2\2\2\u012a\u012b\3\2\2\2\u012b\u012c\b+"+
		"\b\2\u012cV\3\2\2\2\u012d\u012e\7,\2\2\u012e\u012f\7+\2\2\u012f\u0130"+
		"\3\2\2\2\u0130\u0131\b,\t\2\u0131X\3\2\2\2\u0132\u0133\7*\2\2\u0133\u0134"+
		"\7,\2\2\u0134\u0139\3\2\2\2\u0135\u0138\5Y-\2\u0136\u0138\13\2\2\2\u0137"+
		"\u0135\3\2\2\2\u0137\u0136\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u013a\3\2"+
		"\2\2\u0139\u0137\3\2\2\2\u013a\u0142\3\2\2\2\u013b\u0139\3\2\2\2\u013c"+
		"\u013d\7,\2\2\u013d\u013e\7+\2\2\u013e\u013f\3\2\2\2\u013f\u0143\b-\n"+
		"\2\u0140\u0141\7\2\2\3\u0141\u0143\b-\13\2\u0142\u013c\3\2\2\2\u0142\u0140"+
		"\3\2\2\2\u0143Z\3\2\2\2\u0144\u0145\t\2\2\2\u0145\\\3\2\2\2\u0146\u0147"+
		"\t\3\2\2\u0147^\3\2\2\2\u0148\u0149\t\4\2\2\u0149`\3\2\2\2\u014a\u014b"+
		"\t\5\2\2\u014bb\3\2\2\2\u014c\u0152\5[.\2\u014d\u0151\5_\60\2\u014e\u0151"+
		"\5a\61\2\u014f\u0151\7a\2\2\u0150\u014d\3\2\2\2\u0150\u014e\3\2\2\2\u0150"+
		"\u014f\3\2\2\2\u0151\u0154\3\2\2\2\u0152\u0150\3\2\2\2\u0152\u0153\3\2"+
		"\2\2\u0153d\3\2\2\2\u0154\u0152\3\2\2\2\u0155\u015b\5]/\2\u0156\u015a"+
		"\5_\60\2\u0157\u015a\5a\61\2\u0158\u015a\7a\2\2\u0159\u0156\3\2\2\2\u0159"+
		"\u0157\3\2\2\2\u0159\u0158\3\2\2\2\u015a\u015d\3\2\2\2\u015b\u0159\3\2"+
		"\2\2\u015b\u015c\3\2\2\2\u015cf\3\2\2\2\u015d\u015b\3\2\2\2\u015e\u0160"+
		"\5a\61\2\u015f\u015e\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u015f\3\2\2\2\u0161"+
		"\u0162\3\2\2\2\u0162h\3\2\2\2\u0163\u0165\t\6\2\2\u0164\u0163\3\2\2\2"+
		"\u0165\u0166\3\2\2\2\u0166\u0164\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0168"+
		"\3\2\2\2\u0168\u0169\b\65\b\2\u0169j\3\2\2\2\u016a\u016b\13\2\2\2\u016b"+
		"\u016c\b\66\f\2\u016cl\3\2\2\2\24\2\u00c8\u00f4\u0102\u010f\u0111\u011c"+
		"\u0124\u0129\u0137\u0139\u0142\u0150\u0152\u0159\u015b\u0161\u0166\r\3"+
		"*\2\3*\3\3*\4\3*\5\3*\6\3*\7\b\2\2\3,\b\3-\t\3-\n\3\66\13";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}