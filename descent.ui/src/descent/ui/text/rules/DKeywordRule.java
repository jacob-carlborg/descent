package descent.ui.text.rules;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;

import descent.ui.text.worddetectors.DKeywordDetector;

public class DKeywordRule extends WordRule {
	
	public DKeywordRule(IToken token, IToken defaultToken) {
		super(new DKeywordDetector(), defaultToken);
		
		this.addWord("abstract", token);
		this.addWord("alias", token);
		this.addWord("align", token);
		this.addWord("asm", token);
		this.addWord("assert", token);
		this.addWord("auto", token);

		this.addWord("body", token);
		this.addWord("bool", token);
		this.addWord("break", token);
		this.addWord("byte", token);

		this.addWord("case", token);
		this.addWord("cast", token);
		this.addWord("catch", token);
		this.addWord("cdouble", token);
		this.addWord("cent", token);
		this.addWord("cfloat", token);
		this.addWord("char", token);
		this.addWord("class", token);
		this.addWord("const", token);
		this.addWord("continue", token);
		this.addWord("creal", token);

		this.addWord("dchar", token);
		this.addWord("debug", token);
		this.addWord("default", token);
		this.addWord("delegate", token);
		this.addWord("delete", token);
		this.addWord("deprecated", token);
		this.addWord("do", token);
		this.addWord("double", token);

		this.addWord("else", token);
		this.addWord("enum", token);
		this.addWord("export", token);
		this.addWord("extern", token);

		this.addWord("false", token);
		this.addWord("final", token);
		this.addWord("finally", token);
		this.addWord("float", token);
		this.addWord("for", token);
		this.addWord("foreach", token);
		this.addWord("foreach_reverse", token);
		this.addWord("function", token);

		this.addWord("goto", token);

		this.addWord("idouble", token);
		this.addWord("if", token);
		this.addWord("ifloat", token);
		this.addWord("iftype", token);
		this.addWord("import", token);
		this.addWord("in", token);
		this.addWord("inout", token);
		this.addWord("int", token);
		this.addWord("interface", token);
		this.addWord("invariant", token);
		this.addWord("ireal", token);
		this.addWord("is", token);

		this.addWord("lazy", token);
		this.addWord("long", token);

		this.addWord("mixin", token);
		this.addWord("module", token);

		this.addWord("new", token);
		this.addWord("null", token);

		this.addWord("out", token);
		this.addWord("override", token);

		this.addWord("package", token);
		this.addWord("pragma", token);
		this.addWord("private", token);
		this.addWord("protected", token);
		this.addWord("public", token);

		this.addWord("real", token);
		this.addWord("return", token);

		this.addWord("scope", token);
		this.addWord("short", token);
		this.addWord("static", token);
		this.addWord("struct", token);
		this.addWord("super", token);
		this.addWord("switch", token);
		this.addWord("synchronized", token);

		this.addWord("template", token);
		this.addWord("this", token);
		this.addWord("throw", token);
		this.addWord("true", token);
		this.addWord("try", token);
		this.addWord("typedef", token);
		this.addWord("typeid", token);
		this.addWord("typeof", token);

		this.addWord("ubyte", token);
		this.addWord("ucent", token);
		this.addWord("uint", token);
		this.addWord("ulong", token);
		this.addWord("union", token);
		this.addWord("unittest", token);
		this.addWord("ushort", token);

		this.addWord("version", token);
		this.addWord("void", token);
		this.addWord("volatile", token);

		this.addWord("wchar", token);
		this.addWord("while", token);
		this.addWord("with", token);
	}

}
