package descent.internal.compiler.parser.ast;

import java.util.List;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.*;
import descent.internal.compiler.parser.Package;
import descent.internal.compiler.parser.Type.Modification;

/**
 * Internal AST visitor for serializing an AST in a quick and dirty fashion.
 * For various reasons the resulting string is not necessarily legal
 * Java code; and even if it is legal Java code, it is not necessarily the string
 * that corresponds to the given AST. Although useless for most purposes, it's
 * fine for generating debug print strings.
 * <p>
 * Example usage:
 * <code>
 * <pre>
 *    NaiveASTFlattener p = new NaiveASTFlattener();
 *    node.accept(p);
 *    String result = p.getResult();
 * </pre>
 * </code>
 * Call the <code>reset</code> method to clear the previous result before reusing an
 * existing instance.
 * </p>
 */
public class NaiveASTFlattener implements IASTVisitor {
	
	private final String EMPTY= ""; //$NON-NLS-1$
	private final String LINE_END= "\n"; //$NON-NLS-1$
	
	/**
	 * The string buffer into which the serialized representation of the AST is
	 * written.
	 */
	private StringBuilder buffer;	
	private int indent = 0;
	
	/**
	 * Creates a new AST printer.
	 */
	public NaiveASTFlattener() {
		this.buffer = new StringBuilder();
	}
	
	/**
	 * Returns the string accumulated in the visit.
	 *
	 * @return the serialized 
	 */
	public String getResult() {
		return this.buffer.toString();
	}
	
	/**
	 * Resets this printer so that it can be used again.
	 */
	public void reset() {
		this.buffer.setLength(0);
	}
	
	private void appendBinding(ASTDmdNode node) {
		if (node.getBinding() != null) {
			this.buffer.append("<<");
			node.getBinding().appendBinding(this.buffer);
			this.buffer.append(">>");
		}
	}
	
	void printIndent() {
		for (int i = 0; i < this.indent; i++) 
			this.buffer.append("  "); //$NON-NLS-1$
	}
	
	void visitModifiers(List<Modifier> ext) {
		visitList(ext, " ", EMPTY, " ");
	}
	
	void visitPreDDocss(List<? extends ASTNode> ext) {
		visitList(ext, LINE_END, EMPTY, LINE_END);
	}
	
	void visitList(List<? extends ASTNode> ext, String separator) {
		visitList(ext, separator, EMPTY, EMPTY);
	}
	
	void visitList(List<? extends ASTNode> ext, String separator, String pre, String post) {
		if (ext == null || ext.isEmpty()) return;
		
		int i = 0;
		this.buffer.append(pre);
		for(ASTNode p : ext) {
			if (i > 0) {
				this.buffer.append(separator);
			}
			p.accept(this);
			i++;
		}
		this.buffer.append(post);
	}

	public boolean visit(ASTNode node) {
		return false;
	}

	public boolean visit(ASTDmdNode node) {
		return false;
	}

	public boolean visit(AddAssignExp node) {
		if (node.isPreIncrement) {
			this.buffer.append("++");
			node.sourceE1.accept(this);
		} else {
			this.buffer.append("(");
			node.sourceE1.accept(this);
			this.buffer.append(" += ");
			node.sourceE2.accept(this);
			this.buffer.append(")");
		}
		appendBinding(node);
		return false;
	}

	public boolean visit(AddExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" + ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(AddrExp node) {
		this.buffer.append("(");
		this.buffer.append("&");
		node.sourceE1.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(AggregateDeclaration node) {
		// abstract node
		return false;
	}

	public boolean visit(AliasDeclaration node) {
		if (node.first) {
			visitPreDDocss(node.preDdocs);
			printIndent();
			visitModifiers(node.modifiers);
			this.buffer.append("alias ");
			if (node.sourceType != null) {
				node.sourceType.accept(this);
				this.buffer.append(" ");
			}
		}
		this.buffer.append(node.ident);
		if (node.next == null) {					
			this.buffer.append(";");
			if (node.postDdoc != null) {
				this.buffer.append(" ");
				node.postDdoc.accept(this);
			}
		} else {
			this.buffer.append(", ");
		}
		
		return false;
	}

	

	public boolean visit(AlignDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("align");
		if (node.salign >= 2) {
			this.buffer.append("(");
			this.buffer.append(node.salign);
			this.buffer.append(")");
		}
		this.buffer.append(" {\n");
		this.indent++;
		visitList(node.decl, LINE_END, EMPTY, LINE_END);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(AndAndExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" && ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(AndAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" &= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(AndExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" & ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(AnonDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		if (node.isunion) {
			this.buffer.append("union");
		} else {
			this.buffer.append("struct");
		}
		this.buffer.append(" ");
		if (node.ident != null) {
			node.ident.accept(this);
		}
		//visitList(node.templateParameters(), ", ", "(", ")");
		this.buffer.append(" {\n");
		this.indent++;
		visitList(node.decl, LINE_END, EMPTY, LINE_END);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(AnonymousAggregateDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(Argument node) {
		visitList(node.modifiers, " ");
		
		boolean mustAppendSpace = node.modifiers != null && node.modifiers.size() > 0;
		if (node.sourceType != null) {
			if (mustAppendSpace) {
				this.buffer.append(" ");
			}
			node.sourceType.accept(this);
			mustAppendSpace = true;
		}
		if (node.ident != null) {
			if (mustAppendSpace) {
				this.buffer.append(" ");
			}
			this.buffer.append(node.ident);
		}
		if (node.defaultArg != null) {
			this.buffer.append(" = ");
			node.defaultArg.accept(this);
		}
		return false;
	}

	public boolean visit(ArrayExp node) {
		node.sourceE1.accept(this);
		this.buffer.append("[");
		visitList(node.arguments, ", ");
		this.buffer.append("]");
		return false;
	}

	public boolean visit(ArrayInitializer node) {
		this.buffer.append("[");
		if (node.index != null) {
			for(int i = 0; i < node.index.size(); i++) {
				if (i != 0) {
					this.buffer.append(", ");
				}
				Expression index = node.index.get(i);
				Initializer value = node.value.get(i);
				if (index != null) {
					index.accept(this);
					this.buffer.append(": ");
				}
				value.accept(this);
			}
		}
		this.buffer.append("]");
		return false;
	}

	public boolean visit(ArrayLiteralExp node) {
		this.buffer.append("[");
		visitList(node.sourceElements, ", ");
		this.buffer.append("]");
		return false;
	}

	public boolean visit(ArrayScopeSymbol node) {
		// compiler node
		return false;
	}

	public boolean visit(AsmBlock node) {
		printIndent();
		this.buffer.append("asm {\n");
		this.indent++;
		visitList(node.statements, LINE_END, EMPTY, LINE_END);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		return false;
	}

	public boolean visit(AsmStatement node) {
		printIndent();
		if (node.toklist != null) {
			for(int i = 0; i < node.toklist.size(); i++) {
				if (i != 0) {
					this.buffer.append(" ");
				}
				this.buffer.append(node.toklist.get(i).toString());
			}
		}
		this.buffer.append(";");
		return false;
	}

	public boolean visit(AssertExp node) {
		this.buffer.append("assert(");
		node.sourceE1.accept(this);
		if (node.msg != null) {
			this.buffer.append(", ");
			node.msg.accept(this);
		}
		this.buffer.append(")");
		return false;
	}

	public boolean visit(AssignExp node) {
		node.sourceE1.accept(this);
		this.buffer.append(" = ");
		node.sourceE2.accept(this);
		return false;
	}

	public boolean visit(AssocArrayLiteralExp node) {
		this.buffer.append("[");
		if (node.keys != null) {
			for(int i = 0; i < node.keys.size(); i++) {
				if (i != 0) {
					this.buffer.append(", ");
				}
				Expression key = node.keys.get(i);
				Expression value = node.values.get(i);
				if (key != null) {
					key.accept(this);
					this.buffer.append(": ");
				}
				value.accept(this);
			}
		}
		this.buffer.append("]");
		return false;
	}

	public boolean visit(AttribDeclaration node) {
		// abstract node
		return false;
	}

	public boolean visit(BaseClass node) {
		if (node.modifier != null) {
			node.modifier.accept(this);
			this.buffer.append(" ");
		}
		node.sourceType.accept(this);
		return false;
	}

	public boolean visit(BinExp node) {
		// abstract node
		return false;
	}

	public boolean visit(BoolExp node) {
		// fake node
		return false;
	}

	public boolean visit(BreakStatement node) {
		printIndent();
		this.buffer.append("break");
		if (node.ident != null) {
			this.buffer.append(" ");
			node.ident.accept(this);
		}
		this.buffer.append(";");
		return false;
	}

	public boolean visit(CallExp node) {
		node.sourceE1.accept(this);
		this.buffer.append("(");
		visitList(node.sourceArguments, ", ");
		this.buffer.append(")");
		return false;
	}

	public boolean visit(CaseStatement node) {
		List<Expression> exps = new Expressions();
		
		CaseStatement x = node;
		while(x.statement instanceof CaseStatement) {
			exps.add(x.exp);
			x = (CaseStatement) x.statement;
		}
		if (x.exp != null) {
			exps.add(x.exp);
		}
		
		printIndent();
		this.buffer.append("case ");
		visitList(exps, ", ");
		this.buffer.append(":\n");
		
		indent++;
		if (x.statement != null && ((CompoundStatement) x.statement).statements.size() > 0) {
			visitList(((CompoundStatement) x.statement).statements, "\n");
		}
		indent--;
		return false;
	}

	public boolean visit(CastExp node) {
		this.buffer.append("cast(");
		if (node.tok == null) {
			node.to.accept(this);
		} else {
			if (node.tok == TOK.TOKconst) {
				this.buffer.append("const");
			} else {
				this.buffer.append("invariant");
			}
		}
		this.buffer.append(") ");
		node.sourceE1.accept(this);
		return false;
	}

	public boolean visit(CatAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" ~= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(Catch node) {
		printIndent();
		this.buffer.append("catch");
		if (node.sourceType != null) {
			this.buffer.append("(");
			node.sourceType.accept(this);
			if (node.ident != null) {
				this.buffer.append(" ");
				node.ident.accept(this);
			}
			this.buffer.append(")");
		}
		this.buffer.append(" ");
		if (node.handler != null) {
			node.handler.accept(this);
		}
		return false;
	}

	public boolean visit(CatExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" ~ ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(ClassDeclaration node) {
		visit(node, "class", null, node.sourceBaseclasses);
		return false;
	}

	public boolean visit(ClassInfoDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(CmpExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" ");
		switch(node.op) {
		case TOKlt: this.buffer.append("<"); break;
	    case TOKle: this.buffer.append("<="); break;
	    case TOKgt: this.buffer.append(">"); break;
	    case TOKge: this.buffer.append(">="); break;
	    case TOKunord: this.buffer.append("!<>="); break;
	    case TOKlg: this.buffer.append("<>"); break;
	    case TOKleg: this.buffer.append("<>="); break;
	    case TOKule: this.buffer.append("!>"); break;
	    case TOKul: this.buffer.append("!>="); break;
	    case TOKuge: this.buffer.append("!<"); break;
	    case TOKug: this.buffer.append("!<="); break;
	    case TOKue:  this.buffer.append("!<>"); break;
		}
		this.buffer.append(" ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(ComExp node) {
		this.buffer.append("~");
		node.sourceE1.accept(this);
		return false;
	}

	public boolean visit(CommaExp node) {
		node.sourceE1.accept(this);
		this.buffer.append(", ");
		node.sourceE2.accept(this);
		return false;
	}

	public boolean visit(CompileDeclaration node) {
		printIndent();
		this.buffer.append("mixin(");
		node.sourceExp.accept(this);
		this.buffer.append(");");
		return false;
	}

	public boolean visit(CompileExp node) {
		this.buffer.append("mixin(");
		node.sourceE1.accept(this);
		this.buffer.append(");");
		return false;
	}

	public boolean visit(CompileStatement node) {
		printIndent();
		this.buffer.append("mixin(");
		node.exp.accept(this);
		this.buffer.append(");");
		return false;
	}

	public boolean visit(ComplexExp node) {
		// compiler node
		return false;
	}

	public boolean visit(CompoundStatement node) {
		this.buffer.append("{\n");
		this.indent++;
		visitList(node.sourceStatements, LINE_END, EMPTY, LINE_END);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		return false;
	}

	public boolean visit(CondExp node) {
		node.sourceEcond.accept(this);
		this.buffer.append(" ? ");
		node.sourceE1.accept(this);
		this.buffer.append(" : ");
		node.sourceE2.accept(this);
		return false;
	}

	public boolean visit(Condition node) {
		// compiler node
		return false;
	}

	public boolean visit(ConditionalDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);

		switch (node.condition.getConditionType()) {
		case Condition.DEBUG: {
			DebugCondition cond = (DebugCondition) node.condition;
			
			this.buffer.append("debug");			
			if (cond.ident != null) {
				this.buffer.append("(");
				this.buffer.append(cond.ident);
				this.buffer.append(")");
			}
			break;
		}
		case Condition.IFTYPE: {
			IftypeCondition cond = (IftypeCondition) node.condition;
			
			this.buffer.append("iftype(");
			if (cond.targ != null) {
				cond.targ.accept(this);
			}
			if (cond.id != null) {
				this.buffer.append(" ");
				cond.id.accept(this);
			}
			if (cond.tok != null) {
				switch (cond.tok) {
				case TOKreserved: break;
				case TOKequal: this.buffer.append(" == "); break;
				case TOKcolon: this.buffer.append(" : "); break;
				}
			}
			if (cond.tspec != null) {
				this.buffer.append(" ");
				cond.tspec.accept(this);
			}
			this.buffer.append(")");
			break;
		}
		case Condition.STATIC_IF: {
			StaticIfCondition cond = (StaticIfCondition) node.condition;
			
			this.buffer.append("static if(");
			if (cond.exp != null) {
				cond.exp.accept(this);
			}
			this.buffer.append(")");
			break;
		}
		case Condition.VERSION: {
			VersionCondition cond = (VersionCondition) node.condition;
			
			this.buffer.append("version");			
			if (cond.ident != null) {
				this.buffer.append("(");
				this.buffer.append(cond.ident);
				this.buffer.append(")");
			}
			break;
		}
		}
		
		this.buffer.append(" {\n");
		this.indent++;
		visitList(node.decl, LINE_END, EMPTY, LINE_END);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		if (node.elsedecl != null && !node.elsedecl.isEmpty()) {
			this.buffer.append(" else {\n");
			this.indent++;
			visitList(node.elsedecl, LINE_END, EMPTY, LINE_END);
			this.indent--;
			printIndent();
			this.buffer.append("}");
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(ConditionalStatement node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);

		switch (node.condition.getConditionType()) {
		case Condition.DEBUG: {
			DebugCondition cond = (DebugCondition) node.condition;
			
			this.buffer.append("debug");			
			if (cond.ident != null) {
				this.buffer.append("(");
				this.buffer.append(cond.ident);
				this.buffer.append(") ");
			}
			break;
		}
		case Condition.IFTYPE: {
			IftypeCondition cond = (IftypeCondition) node.condition;
			
			this.buffer.append("iftype(");
			if (cond.targ != null) {
				cond.targ.accept(this);
			}
			if (cond.id != null) {
				this.buffer.append(" ");
				cond.id.accept(this);
			}
			if (cond.tok != null) {
				switch (cond.tok) {
				case TOKreserved: break;
				case TOKequal: this.buffer.append(" == "); break;
				case TOKcolon: this.buffer.append(" : "); break;
				}
			}
			if (cond.tspec != null) {
				this.buffer.append(" ");
				cond.tspec.accept(this);
			}
			this.buffer.append(") ");
			break;
		}
		case Condition.STATIC_IF: {
			StaticIfCondition cond = (StaticIfCondition) node.condition;
			
			this.buffer.append("static if(");
			if (cond.exp != null) {
				cond.exp.accept(this);
			}
			this.buffer.append(") ");
			break;
		}
		case Condition.VERSION: {
			VersionCondition cond = (VersionCondition) node.condition;
			
			this.buffer.append("version");			
			if (cond.ident != null) {
				this.buffer.append("(");
				this.buffer.append(cond.ident);
				this.buffer.append(") ");
			}
			break;
		}
		}
		
		if (node.ifbody != null) {
			node.ifbody.accept(this);
		}
		if (node.elsebody != null) {
			this.buffer.append(" else ");
			node.elsebody.accept(this);
		}
		return false;
	}

	public boolean visit(ContinueStatement node) {
		printIndent();
		this.buffer.append("continue");
		if (node.ident != null) {
			this.buffer.append(" ");
			node.ident.accept(this);
		}
		this.buffer.append(";");
		return false;
	}

	public boolean visit(CtorDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("this");
		this.buffer.append("(");
		visitList(node.arguments, ", ");
		if (node.varargs != 0) {
			this.buffer.append("...");
		}
		this.buffer.append(")");
		if (node.frequire != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("in ");
			node.frequire.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.fensure != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("out ");
			node.fensure.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.frequire != null || node.fensure != null) {
			this.buffer.append("body");
		}
		this.buffer.append(" ");
		if (node.fbody != null) {
			node.fbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(DebugCondition node) {
		// compiler node
		return false;
	}

	public boolean visit(DebugSymbol node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("debug = ");
		this.buffer.append(node.version.value);
		this.buffer.append(";");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(Declaration node) {
		// abstract node
		return false;
	}

	public boolean visit(DeclarationExp node) {
		// compiler node
		return false;
	}

	public boolean visit(DeclarationStatement node) {
		((DeclarationExp) node.sourceExp).sourceDeclaration.accept(this);
		return false;
	}

	public boolean visit(DefaultStatement node) {
		printIndent();
		this.buffer.append("default:\n");
		indent++;
		visitList(((CompoundStatement) node.sourceStatement).statements, "\n");
		indent--;
		return false;
	}

	public boolean visit(DelegateExp node) {
		// compiler node
		return false;
	}

	public boolean visit(DeleteDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("delete");
		this.buffer.append("(");
		visitList(node.arguments, ", ");
		this.buffer.append(")");
		if (node.frequire != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("in ");
			node.frequire.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.fensure != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("out ");
			node.fensure.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.frequire != null || node.fensure != null) {
			this.buffer.append("body");
		}
		this.buffer.append(" ");
		if (node.fbody != null) {
			node.fbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(DeleteExp node) {
		this.buffer.append("delete ");
		node.sourceE1.accept(this);
		return false;
	}

	public boolean visit(DivAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" /= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(DivExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" / ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(DollarExp node) {
		this.buffer.append("$");
		return false;
	}

	public boolean visit(DoStatement node) {
		printIndent();
		this.buffer.append("do ");
		node.body.accept(this);
		this.buffer.append(" while(");
		node.condition.accept(this);
		this.buffer.append(");");
		return false;
	}

	public boolean visit(DotExp node) {
		// fake node
		return false;
	}

	public boolean visit(DotIdExp node) {
		if (node.sourceE1 != null) {
			node.sourceE1.accept(this);
		}
		this.buffer.append(".");
		node.ident.accept(this);
		return false;
	}

	public boolean visit(DotTemplateExp node) {
		// compiler node
		return false;
	}

	public boolean visit(DotTemplateInstanceExp node) {
		if (node.sourceE1 != null) {
			node.sourceE1.accept(this);
		}
		this.buffer.append(".");
		node.ti.accept(this);
		return false;
	}

	public boolean visit(DotTypeExp node) {
		// compiler node
		return false;
	}

	public boolean visit(DotVarExp node) {
		// compiler node
		return false;
	}

	public boolean visit(Dsymbol node) {
		// abstract node
		return false;
	}

	public boolean visit(DsymbolExp node) {
		// compiler node
		return false;
	}

	public boolean visit(DtorDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("~this");
		this.buffer.append("()");
		if (node.frequire != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("in ");
			node.frequire.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.fensure != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("out ");
			node.fensure.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.frequire != null || node.fensure != null) {
			this.buffer.append("body");
		}
		this.buffer.append(" ");
		if (node.fbody != null) {
			node.fbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(EnumDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("enum");
		if (node.ident != null) {
			this.buffer.append(" ");
			node.ident.accept(this);
		}
		if (node.memtype != null) {
			this.buffer.append(" : ");
			node.memtype.accept(this);
		}
		this.buffer.append(" {\n");
		this.indent++;
		visitList(node.members, ",\n", EMPTY, LINE_END);
		this.indent--;
		this.buffer.append("}");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(EnumMember node) {
		printIndent();
		node.ident.accept(this);
		if (node.value != null) {
			this.buffer.append(" = ");
			node.value.accept(this);
		}
		return false;
	}

	public boolean visit(EqualExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		if (node.op == TOK.TOKequal) {
			this.buffer.append(" == ");
		} else {
			this.buffer.append(" != ");
		}
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(ExpInitializer node) {
		if (node.sourceExp != null) {
			node.sourceExp.accept(this);
		}
		return false;
	}

	public boolean visit(Expression node) {
		// abstract node
		return false;
	}

	public boolean visit(ExpStatement node) {
		printIndent();
		if (node.sourceExp != null) {			
			node.sourceExp.accept(this);			
		}
		this.buffer.append(";");
		return false;
	}

	public boolean visit(FileExp node) {
		this.buffer.append("import(");
		node.sourceE1.accept(this);
		this.buffer.append(");");
		return false;
	}

	public boolean visit(ForeachRangeStatement node) {
		printIndent();
		this.buffer.append("foreach");
		if (node.op == TOK.TOKforeach_reverse) {
			this.buffer.append("_reverse");
		}
		this.buffer.append("(");
		if (node.arg != null) {
			node.arg.accept(this);
		}
		this.buffer.append("; ");
		if (node.lwr != null) {
			node.lwr.accept(this);
		}
		this.buffer.append(" .. ");
		if (node.upr != null) {
			node.upr.accept(this);
		}
		this.buffer.append(") ");
		if (node.body != null) {
			node.body.accept(this);
		}
		return false;
	}

	public boolean visit(ForeachStatement node) {
		printIndent();
		this.buffer.append("foreach");
		if (node.op == TOK.TOKforeach_reverse) {
			this.buffer.append("_reverse");
		}
		this.buffer.append("(");
		visitList(node.arguments, ", ");
		this.buffer.append("; ");
		if (node.sourceAggr != null) {
			node.sourceAggr.accept(this);
		}
		this.buffer.append(") ");
		if (node.body != null) {
			node.body.accept(this);
		}
		return false;
	}

	public boolean visit(ForStatement node) {
		printIndent();
		this.buffer.append("for(");
		if (node.init != null) {
			node.init.accept(this);
		} else {
			this.buffer.append("; ");
		}
		if (node.condition != null) {
			node.condition.accept(this);
		}
		this.buffer.append("; ");
		if (node.increment != null) {
			node.increment.accept(this);
		}
		this.buffer.append(") ");
		if (node.body != null) {
			node.body.accept(this);
		}
		return false;
	}

	public boolean visit(FuncAliasDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(FuncDeclaration node) {
		return visit(node, null);
	}
	
	private boolean visit(FuncDeclaration node, TemplateParameters templateParameters) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		
		TypeFunction ty = (TypeFunction) node.sourceType;
		ty.sourceNext.accept(this); // return type
		this.buffer.append(" ");
		node.ident.accept(this);
		if (templateParameters != null) {
			visitList(templateParameters, ", ", "(", ")");
		}
		this.buffer.append("(");
		visitList(ty.parameters, ", "); // arguments
		if (ty.varargs != 0) { // variadic
			this.buffer.append("...");
		}
		this.buffer.append(")");
		if (node.sourceFrequire != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("in ");
			node.sourceFrequire.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.sourceFensure != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("out ");
			node.sourceFensure.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.sourceFbody != null) {
			if (node.sourceFrequire != null || node.sourceFensure != null) {
				this.buffer.append("body");
			}
			this.buffer.append(" ");
			node.sourceFbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(FuncExp node) {
		if (node.fd.tok == TOK.TOKdelegate) {
			this.buffer.append("delegate ");
		} else if (node.fd.tok == TOK.TOKfunction) {
			this.buffer.append("function ");
		}
		
		TypeFunction typeFunction = (TypeFunction) node.fd.sourceType;
		
		this.buffer.append("(");
		visitList(typeFunction.parameters, ", ");
		if (typeFunction.varargs != 0) {
			this.buffer.append("...");
		}
		this.buffer.append(")");
		if (node.fd.frequire != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append(" in ");
			node.fd.frequire.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.fd.fensure != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append(" out ");
			node.fd.fensure.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.fd.frequire != null || node.fd.fensure != null) {
			this.buffer.append(" body");
		}
		this.buffer.append(" ");
		node.fd.fbody.accept(this);
		return false;
	}

	public boolean visit(FuncLiteralDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(GotoCaseStatement node) {
		printIndent();
		this.buffer.append("goto case ");
		node.exp.accept(this);
		this.buffer.append(";");
		return false;
	}

	public boolean visit(GotoDefaultStatement node) {
		printIndent();
		this.buffer.append("goto default;");
		return false;
	}

	public boolean visit(GotoStatement node) {
		printIndent();
		this.buffer.append("goto ");
		node.ident.accept(this);
		this.buffer.append(";");
		return false;
	}

	public boolean visit(HaltExp node) {
		// compiler node
		return false;
	}

	public boolean visit(IdentifierExp node) {
		buffer.append(node.ident);
		appendBinding(node);
		return false;
	}

	public boolean visit(IdentityExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		switch(node.op) {
		case TOKidentity: this.buffer.append(" === "); break;
		case TOKnotidentity: this.buffer.append(" !== "); break;
		case TOKis: this.buffer.append(" is "); break;
		case TOKnotis: this.buffer.append(" !is "); break;
		}
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(IfStatement node) {
		printIndent();
		this.buffer.append("if(");
		if (node.arg != null) {
			node.arg.accept(this);
			this.buffer.append(" = ");
		}
		node.sourceCondition.accept(this);
		this.buffer.append(") ");
		node.ifbody.accept(this);
		if (node.elsebody != null) {
			this.buffer.append(" else ");
			node.elsebody.accept(this);
		}
		return false;
	}

	public boolean visit(IftypeCondition node) {
		// compiler node
		return false;
	}

	public boolean visit(IftypeExp node) {
		this.buffer.append("is(");
		node.targ.accept(this);
		if (node.id != null) {
			this.buffer.append(" ");
			node.id.accept(this);
		}
		if (node.tok2 == TOK.TOKreserved) {			
			if (node.tspec != null) {
				if (node.tok == TOK.TOKequal) {
					this.buffer.append(" == ");
				} else {
					this.buffer.append(" : ");
				}
				node.tspec.accept(this);
			}
			this.buffer.append(")");
		} else {
			switch(node.tok2) {
			case TOKtypedef: this.buffer.append("typedef"); break;
			case TOKstruct: this.buffer.append("struct"); break;
			case TOKunion: this.buffer.append("union"); break;
			case TOKclass: this.buffer.append("class"); break;
			case TOKenum: this.buffer.append("enum"); break;
			case TOKinterface: this.buffer.append("interface"); break;
			case TOKfunction: this.buffer.append("function"); break;
			case TOKdelegate: this.buffer.append("delegate"); break;
			case TOKreturn: this.buffer.append("return"); break;
			case TOKsuper: this.buffer.append("super"); break;
			}
			this.buffer.append(")");
		}
		return false;
	}

	public boolean visit(Import node) {
		if (!node.first) {
			return false;
		}
		
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		
		if (node.isstatic) {
			this.buffer.append("static ");
		}
		this.buffer.append("import ");
		
		if (node.aliasId != null) {
			node.aliasId.accept(this);
			this.buffer.append(" = ");
		}
		if (node.packages != null) {
			visitList(node.packages, ".", "", ".");
		}
		if (node.id != null) {
			node.id.accept(this);
		}
		if (node.names != null) {
			this.buffer.append(" : ");
			for(int i = 0; i < node.names.size(); i++) {
				if (i > 0) {
					this.buffer.append(", ");
				}
				if (node.aliases.get(i) != null) {
					node.aliases.get(i).accept(this);
					this.buffer.append(" = ");
				}
				node.names.get(i).accept(this);
			}
		}
		
		this.buffer.append(";");
		return false;
	}

	public boolean visit(IndexExp node) {
		// semantic node
		return false;
	}

	public boolean visit(InExp node) {
		node.sourceE1.accept(this);
		this.buffer.append(" in ");
		node.sourceE2.accept(this);
		return false;
	}

	public boolean visit(Initializer node) {
		// abstract node
		return false;
	}

	public boolean visit(IntegerExp node) {
		if (node.str != null) {
			this.buffer.append(node.str);
		} else {
			this.buffer.append(node.value);
		}
		return false;
	}

	public boolean visit(InterfaceDeclaration node) {
		visit(node, "interface", null, node.sourceBaseclasses);
		return false;
	}

	public boolean visit(InvariantDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("invariant() ");
		if (node.fbody != null) {
			node.fbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(LabelDsymbol node) {
		// compiler node
		return false;
	}

	public boolean visit(LabelStatement node) {
		printIndent();
		node.ident.accept(this);
		this.buffer.append(": ");
		node.statement.accept(this);
		return false;
	}

	public boolean visit(LinkDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("extern");
		switch(node.linkage) {
    	case LINKdefault: break;
    	case LINKd: this.buffer.append("(D)"); break;
    	case LINKc: this.buffer.append("(C)"); break;
    	case LINKcpp: this.buffer.append("(C++)"); break;
    	case LINKwindows: this.buffer.append("(Windows)"); break;
    	case LINKpascal: this.buffer.append("(Pascal)"); break;
    	case LINKsystem: this.buffer.append("(System)"); break;
    	default: throw new RuntimeException("Can't happen?");
    	}
		this.buffer.append(" {\n");
		this.indent++;
		visitList(node.decl, LINE_END, EMPTY, LINE_END);
		this.indent--;
		this.buffer.append("}");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(MinAssignExp node) {
		if (node.isPreDecrement) {
			this.buffer.append("--");
			node.sourceE1.accept(this);
		} else {
			this.buffer.append("(");
			node.sourceE1.accept(this);
			this.buffer.append(" -= ");
			node.sourceE2.accept(this);
			this.buffer.append(")");
		}
		appendBinding(node);
		return false;
	}

	public boolean visit(MinExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" - ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(ModAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" %= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(ModExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" % ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(Modifier node) {
		switch(node.tok) {
		case TOKprivate: this.buffer.append("private"); break;
		case TOKpackage: this.buffer.append("package"); break;
		case TOKprotected: this.buffer.append("protected"); break;
		case TOKpublic: this.buffer.append("public"); break;
		case TOKexport: this.buffer.append("export"); break;
		case TOKstatic: this.buffer.append("static"); break;
		case TOKfinal: this.buffer.append("final"); break;
		case TOKabstract: this.buffer.append("abstract"); break;
		case TOKoverride: this.buffer.append("override"); break;
		case TOKauto: this.buffer.append("auto"); break;
		case TOKsynchronized: this.buffer.append("synchronized"); break;
		case TOKdeprecated: this.buffer.append("deprecated"); break;
		case TOKextern: this.buffer.append("extern"); break;
		case TOKconst: this.buffer.append("const"); break;
		case TOKscope: this.buffer.append("scope"); break;
		case TOKinvariant: this.buffer.append("invariant"); break;
		case TOKin: this.buffer.append("in"); break;
		case TOKout: this.buffer.append("out"); break;
		case TOKinout: this.buffer.append("inout"); break;
		case TOKlazy: this.buffer.append("lazy"); break;
		case TOKref: this.buffer.append("ref"); break;
		default:
			throw new IllegalStateException("Invalid modifier: " + node.tok);
		}
		return false;
	}

	public boolean visit(Module node) {
		if (node.md != null) {
			node.md.accept(this);
			this.buffer.append(LINE_END);
		}
		
		visitList(node.sourceMembers, LINE_END);
		return false;
	}

	public boolean visit(ModuleDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		this.buffer.append("module ");
		visitModuleDeclarationName(node);
		this.buffer.append(";");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}
	
	public void visitModuleDeclarationName(ModuleDeclaration node) {
		if (node.packages != null) {
			visitList(node.packages, ".", "", ".");
		}
		if (node.id != null) {
			node.id.accept(this);
		}
	}

	public boolean visit(ModuleInfoDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(MulAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" *= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(MulExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" * ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(NegExp node) {
		this.buffer.append("-");
		node.sourceE1.accept(this);
		return false;
	}

	public boolean visit(NewAnonClassExp node) {
		if (node.thisexp != null) {
			node.thisexp.accept(this);
			this.buffer.append(".");
		}
		this.buffer.append("new ");
		visitList(node.newargs, ", ", "(", ") ");
		this.buffer.append("class ");
		visitList(node.arguments, ", ", "(", ") ");
		visitList(node.cd.sourceBaseclasses, ", ");
		this.buffer.append(" {\n");
		this.indent++;
		visitList(node.cd.members, LINE_END, EMPTY, LINE_END);
		this.indent--;
		this.buffer.append("}");
		return false;
	}

	public boolean visit(NewDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("new");
		this.buffer.append("(");
		visitList(node.arguments, ", ");
		if (node.varargs != 0) {
			this.buffer.append("...");
		}
		this.buffer.append(")");
		if (node.frequire != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("in ");
			node.frequire.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.fensure != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("out ");
			node.fensure.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.frequire != null || node.fensure != null) {
			this.buffer.append("body");
		}
		this.buffer.append(" ");
		if (node.fbody != null) {
			node.fbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(NewExp node) {
		if (node.sourceThisexp != null) {
			node.sourceThisexp.accept(this);
			this.buffer.append(".");
		}
		this.buffer.append("new ");
		visitList(node.sourceNewargs, ", ", "(", ") ");
		node.sourceNewtype.accept(this);
		visitList(node.sourceArguments, ", ", "(", ")");
		return false;
	}

	public boolean visit(NotExp node) {
		this.buffer.append("!");
		node.sourceE1.accept(this);
		return false;
	}

	public boolean visit(NullExp node) {
		this.buffer.append("null");
		return false;
	}

	public boolean visit(OnScopeStatement node) {
		printIndent();
		this.buffer.append("scope(");
		switch(node.tok) {
		case TOKon_scope_exit: this.buffer.append("exit"); break;
		case TOKon_scope_failure: this.buffer.append("failure"); break;
		case TOKon_scope_success: this.buffer.append("success"); break;
		}
		this.buffer.append(") ");
		node.statement.accept(this);
		return false;
	}

	public boolean visit(OrAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" |= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(OrExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" | ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(OrOrExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" || ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(Package node) {
		this.buffer.append(node.ident);
		return false;
	}

	public boolean visit(PostExp node) {
		node.sourceE1.accept(this);
		if (node.op == TOK.TOKplusplus) {
			this.buffer.append("++");
		} else {
			this.buffer.append("--");
		}
		return false;
	}

	public boolean visit(PragmaDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("pragma(");
		node.ident.accept(this);
		visitList(node.args, ", ", ", ", EMPTY);
		this.buffer.append(")");
		if (node.decl != null && !node.decl.isEmpty()) {
			this.buffer.append(" {\n");
			this.indent++;
			visitList(node.decl, LINE_END, EMPTY, LINE_END);
			this.indent--;
			this.buffer.append("}");
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(PragmaStatement node) {
		printIndent();
		this.buffer.append("pragma(");
		node.ident.accept(this);
		visitList(node.args, ", ", ", ", EMPTY);
		this.buffer.append(")");
		if (node.body != null) {
			this.buffer.append(" ");
			node.body.accept(this);
		} else {
			this.buffer.append(";");
		}
		return false;
	}

	public boolean visit(ProtDeclaration node) {
		printIndent();
		
		if (node.single) {
			node.modifier.accept(this);
			this.buffer.append(" ");
			visitList(node.decl, LINE_END);
			return false;
		}
		
		if (node.colon && node.decl != null && node.decl.size() > 0) { 
			for(int i = 0; i < node.decl.size(); i++) {
				Dsymbol dsymbol = node.decl.get(i);
				if (
					(dsymbol instanceof ProtDeclaration && ((ProtDeclaration) dsymbol).colon)
						|| 
					(dsymbol instanceof StorageClassDeclaration && ((StorageClassDeclaration) dsymbol).colon)) {
					node.modifier.accept(this);
					this.buffer.append(":\n");
					this.indent++;
					visitList(node.decl.subList(0, i), "");
					this.indent--;
					this.buffer.append(LINE_END);
					node.decl.get(i).accept(this);
					return false;
				}
			}
		}
		
		node.modifier.accept(this);
		this.buffer.append(":\n");
		this.indent++;
		visitList(node.decl, LINE_END);
		this.indent--;
		return false;
	}

	public boolean visit(PtrExp node) {
		this.buffer.append("*");
		node.sourceE1.accept(this);
		return false;
	}

	public boolean visit(RealExp node) {
		if (node.str != null) {
			this.buffer.append(node.str);
		} else {
			this.buffer.append(node.value);
		}
		return false;
	}

	public boolean visit(RemoveExp node) {
		// compiler node
		return false;
	}

	public boolean visit(ReturnStatement node) {
		printIndent();
		this.buffer.append("return");
		if (node.sourceExp != null) {
			this.buffer.append(" ");
			node.sourceExp.accept(this);
		}
		this.buffer.append(";");
		return false;
	}

	public boolean visit(ScopeDsymbol node) {
		// abstract node
		return false;
	}

	public boolean visit(ScopeExp node) {
		node.sds.accept(this);
		return false;
	}

	public boolean visit(ScopeStatement node) {
		// abstract node
		return false;
	}

	public boolean visit(ShlAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" <<= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(ShlExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" << ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(ShrAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" >>= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(ShrExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" >> ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(SliceExp node) {
		node.sourceE1.accept(this);
		this.buffer.append("[");
		if (node.lwr != null && node.upr != null) {
			node.lwr.accept(this);
			this.buffer.append(" .. ");
			node.upr.accept(this);
		}
		this.buffer.append("]");
		return false;
	}

	public boolean visit(Statement node) {
		// abstract node
		return false;
	}

	public boolean visit(StaticAssert node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("static assert(");
		node.exp.accept(this);
		if (node.msg != null) {
			this.buffer.append(", ");
			node.msg.accept(this);
		}
		this.buffer.append(")");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(StaticAssertStatement node) {
		printIndent();
		node.sa.accept(this);
		this.buffer.append(";");
		return false;
	}

	public boolean visit(StaticCtorDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("static this");
		this.buffer.append("()");
		if (node.frequire != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("in ");
			node.frequire.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.fensure != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("out ");
			node.fensure.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.frequire != null || node.fensure != null) {
			this.buffer.append("body");
		}
		this.buffer.append(" ");
		if (node.fbody != null) {
			node.fbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(StaticDtorDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("static ~this");
		this.buffer.append("()");
		if (node.frequire != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("in ");
			node.frequire.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.fensure != null) {
			this.buffer.append(LINE_END);
			printIndent();
			this.buffer.append("out ");
			node.fensure.accept(this);
			this.buffer.append(LINE_END);
			printIndent();
		}
		if (node.frequire != null || node.fensure != null) {
			this.buffer.append("body");
		}
		this.buffer.append(" ");
		if (node.fbody != null) {
			node.fbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(StaticIfCondition node) {
		// compiler node
		return false;
	}

	public boolean visit(StaticIfDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("static if(");
		node.condition.accept(this);
		this.buffer.append(") {\n");
		this.indent++;
		visitList(node.decl, LINE_END, EMPTY, LINE_END);
		this.indent--;
		this.buffer.append("}");
		if (node.elsedecl != null && !node.elsedecl.isEmpty()) {
			this.buffer.append(" else {\n");
			this.indent++;
			visitList(node.elsedecl, LINE_END, EMPTY, LINE_END);
			this.indent--;
			this.buffer.append("}");
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(StorageClassDeclaration node) {
		printIndent();
		
		if (node.single) {
			node.modifier.accept(this);
			this.buffer.append(" ");
			visitList(node.decl, LINE_END);
			return false;
		}
		
		if (node.colon && node.decl != null && node.decl.size() > 0) { 
			for(int i = 0; i < node.decl.size(); i++) {
				Dsymbol dsymbol = node.decl.get(i);
				if (
					(dsymbol instanceof ProtDeclaration && ((ProtDeclaration) dsymbol).colon)
						|| 
					(dsymbol instanceof StorageClassDeclaration && ((StorageClassDeclaration) dsymbol).colon)) {
					node.modifier.accept(this);
					this.buffer.append(":\n");
					this.indent++;
					visitList(node.decl.subList(0, i), "");
					this.indent--;
					this.buffer.append(LINE_END);
					node.decl.get(i).accept(this);
					return false;
				}
			}
		}
		
		node.modifier.accept(this);
		this.buffer.append(":\n");
		this.indent++;
		visitList(node.decl, LINE_END);
		this.indent--;
		return false;
	}

	public boolean visit(StringExp node) {
		if (node.allStringExps != null) {
			visitList(node.allStringExps, " ");
		} else {
			if (node.sourceString != null) {
				this.buffer.append(node.sourceString);
			} else {
				this.buffer.append('"');
				this.buffer.append(node.string);
				this.buffer.append('"');
			}
		}
		return false;
	}

	public boolean visit(StructDeclaration node) {
		visit(node, "struct", null, null);
		return false;
	}
	
	private boolean visit(AggregateDeclaration node, TemplateParameters parameters) {
		switch(node.getNodeType()) {
		case ASTDmdNode.CLASS_DECLARATION:
			ClassDeclaration classDecl = (ClassDeclaration) node;
			visit(classDecl, "class", parameters, classDecl.sourceBaseclasses);
			break;
		case ASTDmdNode.INTERFACE_DECLARATION:
			InterfaceDeclaration intDecl = (InterfaceDeclaration) node;
			visit(intDecl, "interface", parameters, intDecl.sourceBaseclasses);
			break;
		case ASTDmdNode.STRUCT_DECLARATION:
			StructDeclaration strDecl = (StructDeclaration) node;
			visit(strDecl, "struct", parameters, null);
			break;
		case ASTDmdNode.UNION_DECLARATION:
			UnionDeclaration unDecl = (UnionDeclaration) node;
			visit(unDecl, "union", parameters, null);
			break;
		}
		return false;
	}
	
	private boolean visit(AggregateDeclaration node, String name, TemplateParameters templateParameters, BaseClasses baseClasses) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append(name);
		this.buffer.append(" ");
		if (node.ident != null) {
			this.buffer.append(node.ident);
		}
		if (templateParameters != null) {
			visitList(templateParameters, ", ", "(", ")");
		}
		if (baseClasses != null) {
			visitList(baseClasses, ", ", " : ", EMPTY);
		}
		this.buffer.append(" {\n");
		this.indent++;
		visitList(node.sourceMembers, LINE_END, EMPTY, LINE_END);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(StructInitializer node) {
		this.buffer.append("{ ");
		if (node.sourceField != null) {
			for(int i = 0; i < node.sourceField.size(); i++) {
				if (i != 0) {
					this.buffer.append(", ");
				}
				IdentifierExp index = node.sourceField.get(i);
				Initializer value = node.sourceValue.get(i);
				if (index != null) {
					index.accept(this);
					this.buffer.append(": ");
				}
				value.accept(this);
			}
		}
		this.buffer.append("}");
		return false;
	}

	public boolean visit(SuperExp node) {
		this.buffer.append("super");
		return false;
	}

	public boolean visit(SwitchStatement node) {
		printIndent();
		this.buffer.append("switch(");
		node.condition.accept(this);
		this.buffer.append(") ");
		node.body.accept(this);
		return false;
	}

	public boolean visit(SymOffExp node) {
		// compiler node
		return false;
	}

	public boolean visit(SynchronizedStatement node) {
		printIndent();
		this.buffer.append("synchronized");
		if (node.sourceExp != null) {
			this.buffer.append("(");
			node.sourceExp.accept(this);
			this.buffer.append(")");
		}
		this.buffer.append(" ");
		node.sourceBody.accept(this);
		return false;
	}

	public boolean visit(TemplateAliasParameter node) {
		this.buffer.append("alias ");
		node.ident.accept(this);
		if (node.specAliasT != null) {
			this.buffer.append(" : ");
			node.specAliasT.accept(this);
		}
		if (node.defaultAlias != null) {
			this.buffer.append(" = ");
			node.defaultAlias.accept(this);
		}
		return false;
	}

	public boolean visit(TemplateDeclaration node) {
		if (node.wrapper) {
			Dsymbol wrappedSymbol = node.members.get(0);
			if (wrappedSymbol.getNodeType() == ASTDmdNode.FUNC_DECLARATION) {
				return visit((FuncDeclaration) wrappedSymbol, node.parameters);
			} else {
				return visit((AggregateDeclaration) wrappedSymbol, node.parameters);
			}
		}
		
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("template ");
		node.ident.accept(this);
		visitList(node.parameters, ", ", "(", ")");
		this.buffer.append(" {\n");
		this.indent++;
		visitList(node.members, LINE_END, EMPTY, LINE_END);
		this.indent--;
		this.buffer.append("}");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(TemplateExp node) {
		// compiler node
		return false;
	}

	public boolean visit(TemplateInstance node) {
		if (node.name != null) {
			node.name.accept(this);
		}
		this.buffer.append("!");
		visitList(node.tiargs, ", ", "(", ")");
		return false;
	}

	public boolean visit(TemplateInstanceWrapper node) {
		((TemplateInstanceWrapper) node).tempinst.accept(this);
		return false;
	}

	public boolean visit(TemplateMixin node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("mixin ");
		visitTemplateMixinType(node);
		if (node.ident != null) {
			this.buffer.append(" ");
			node.ident.accept(this);
		}
		this.buffer.append(";");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}
	
	public void visitTemplateMixinType(TemplateMixin node) {
		visitTemplateMixinType(node.tqual, node.idents, node.tiargs);
	}

	private void visitTemplateMixinType(Type typeof, Identifiers ids, Objects tiargs) {
		boolean ret = false;
		
		if (typeof != null) {
			typeof.accept(this);
			ret = true;
		}
		
		for(int i = 0; i < ids.size(); i++) {
			IdentifierExp id = ids.get(i);
			if (id == null || (id.ident != null && CharOperation.equals(id.ident, Id.empty))) continue;
			
			if (!ret) {
				if (i == 1) {
					this.buffer.append(".");
				} else {
					
				}
			} else {
				this.buffer.append(".");
			}
			
			if (i == ids.size() - 1) {
				if (tiargs == null || tiargs.isEmpty()) {
					id.accept(this);
				} else {
					id.accept(this);
					visitList(tiargs, ", ", "!(", ")");
				}
			} else {
				id.accept(this);
			}
			
			ret = true;
		}
	}

	public boolean visit(TemplateParameter node) {
		// abstract node
		return false;
	}

	public boolean visit(TemplateTupleParameter node) {
		node.ident.accept(this);
		this.buffer.append(" ...");
		return false;
	}

	public boolean visit(TemplateTypeParameter node) {
		node.ident.accept(this);
		if (node.specType != null) {
			this.buffer.append(" : ");
			node.specType.accept(this);
		}
		if (node.defaultType != null) {
			this.buffer.append(" = ");
			node.defaultType.accept(this);
		}
		return false;
	}

	public boolean visit(TemplateValueParameter node) {
		node.ident.accept(this);
		if (node.specValue != null) {
			this.buffer.append(" : ");
			node.specValue.accept(this);
		}
		if (node.defaultValue != null) {
			this.buffer.append(" = ");
			node.defaultValue.accept(this);
		}
		return false;
	}

	public boolean visit(ThisDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(ThisExp node) {
		this.buffer.append("this");
		return false;
	}

	public boolean visit(ThrowStatement node) {
		printIndent();
		this.buffer.append("throw ");
		node.exp.accept(this);
		this.buffer.append(";");
		return false;
	}

	public boolean visit(TraitsExp node) {
		this.buffer.append("__traits(");
		node.ident.accept(this);
		this.buffer.append(", ");
		visitList(node.args, ", ");
		this.buffer.append(")");
		return false;
	}

	public boolean visit(TryCatchStatement node) {
		printIndent();
		this.buffer.append("try ");
		node.body.accept(this);
		this.buffer.append(LINE_END);
		visitList(node.catches, LINE_END, EMPTY, LINE_END);
		return false;
	}

	public boolean visit(TryFinallyStatement node) {
		printIndent();
		this.buffer.append("try ");
		node.body.accept(this);
		this.buffer.append(LINE_END);
		if (node.isTryCatchFinally) {
			visitList(((TryCatchStatement) node.body).catches, LINE_END, EMPTY, LINE_END);
		}
		if (node.finalbody != null) {
			this.buffer.append(" finally ");
			node.finalbody.accept(this);
		}
		return false;
	}

	public boolean visit(Tuple node) {
		// compiler node
		return false;
	}

	public boolean visit(TupleDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TupleExp node) {
		// compiler node
		return false;
	}

	public boolean visit(Type node) {
		// abstract node
		return false;
	}
	
	private void startModifiedType(Type node) {
		if (node.modifications != null) {
			for(int i = node.modifications.size() - 1; i >= 0; i--) {
				Modification modification = node.modifications.get(i);
				this.buffer.append(modification.tok.toString());
				this.buffer.append("(");
			}
		}
	}

	private void endModifiedType(Type node) {
		if (node.modifications != null) {
			for(int i = node.modifications.size() - 1; i >= 0; i--) {
				this.buffer.append(")");
			}
		}
	}

	public boolean visit(TypeAArray node) {
		startModifiedType(node);
		node.sourceNext.accept(this);
		this.buffer.append("[");
		node.index.accept(this);
		this.buffer.append("]");
		endModifiedType(node);
		return false;
	}

	public boolean visit(TypeBasic node) {
		startModifiedType(node);
		switch(node.ty) {
		case Tvoid: this.buffer.append("void"); break;
		case Tint8: this.buffer.append("byte"); break;
		case Tuns8: this.buffer.append("ubyte"); break;
		case Tint16: this.buffer.append("short"); break;
		case Tuns16: this.buffer.append("ushort"); break;
		case Tint32: this.buffer.append("int"); break;
		case Tuns32: this.buffer.append("unit"); break;
		case Tint64: this.buffer.append("long"); break;
		case Tuns64: this.buffer.append("ulong"); break;
		case Tfloat32: this.buffer.append("float"); break;
		case Tfloat64: this.buffer.append("double"); break;
		case Tfloat80: this.buffer.append("real"); break;
		case Timaginary32: this.buffer.append("ifloat"); break;
		case Timaginary64: this.buffer.append("idouble"); break;
		case Timaginary80: this.buffer.append("ireal"); break;
		case Tcomplex32: this.buffer.append("cfloat"); break;
		case Tcomplex64: this.buffer.append("cdouble"); break;
		case Tcomplex80: this.buffer.append("creal"); break;
		case Tbit: this.buffer.append("bit"); break;
		case Tbool: this.buffer.append("bool"); break;
		case Tchar: this.buffer.append("char"); break;
		case Twchar: this.buffer.append("wchar"); break;
		case Tdchar: this.buffer.append("dchar"); break;
		}
		endModifiedType(node);
		return false;
	}

	public boolean visit(TypeClass node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeDArray node) {
		startModifiedType(node);
		node.sourceNext.accept(this);
		this.buffer.append("[]");
		endModifiedType(node);
		return false;
	}

	public boolean visit(TypedefDeclaration node) {
		if (node.first) {
			visitPreDDocss(node.preDdocs);
			printIndent();
			visitModifiers(node.modifiers);
			this.buffer.append("typedef ");
			if (node.sourceBasetype != null) {
				node.sourceBasetype.accept(this);
				this.buffer.append(" ");
			}
		}
		if (node.ident != null) {
			this.buffer.append(node.ident);
		}
		if (node.next == null) {					
			this.buffer.append(";");
			if (node.postDdoc != null) {
				this.buffer.append(" ");
				node.postDdoc.accept(this);
			}
		} else {
			this.buffer.append(", ");
		}
		return false;
	}

	public boolean visit(TypeDelegate node) {
		startModifiedType(node);
		
		TypeFunction ty = ((TypeFunction) node.sourceNext);
		ty.sourceNext.accept(this); // return type
		this.buffer.append(" delegate(");
		visitList(ty.parameters, ", ");
		if (ty.varargs != 0) {
			this.buffer.append("...");
		}
		this.buffer.append(")");
		endModifiedType(node);
		return false;
	}

	public boolean visit(TypeDotIdExp node) {
		node.type.accept(this);
		this.buffer.append(".");
		node.ident.accept(this);
		return false;
	}

	public boolean visit(TypeEnum node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeExp node) {
		node.sourceType.accept(this);
		return false;
	}

	public boolean visit(TypeFunction node) {
		// should never reach this
		return false;
	}

	public boolean visit(TypeIdentifier node) {
		startModifiedType(node);
		if (node.ident != null && !CharOperation.equals(node.ident.ident, Id.empty)) {
			node.ident.accept(this);
		}
		
		if (node.idents != null && !node.idents.isEmpty()) {
			visitQualifiedType(node);
		}
		endModifiedType(node);
		return false;
	}
	
	private void visitQualifiedType(TypeQualified node) {
		for(IdentifierExp idExp : node.idents) {
			this.buffer.append(".");
			idExp.accept(this);
		}
	}

	public boolean visit(TypeidExp node) {
		this.buffer.append("typeid(");
		node.typeidType.accept(this);
		this.buffer.append(")");
		return false;
	}

	public boolean visit(TypeInfoArrayDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoAssociativeArrayDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoClassDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoDelegateDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoEnumDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoFunctionDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoInterfaceDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoPointerDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoStaticArrayDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoStructDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInfoTypedefDeclaration node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeInstance node) {
		startModifiedType(node);
		IdentifierExp id = node.tempinst.name;
		if (!CharOperation.equals(id.ident, Id.empty)) {
			id.accept(this);
		}
		visitList(node.tempinst.tiargs, ", ", "!(", ")");
		if (node.idents != null && node.idents.size() > 0) {
			visitQualifiedType(node);
		}
		endModifiedType(node);
		return false;
	}

	public boolean visit(TypePointer node) {
		startModifiedType(node);
		if (node.sourceNext.ty == TY.Tfunction) {
			TypeFunction ty = (TypeFunction) node.sourceNext;
			ty.sourceNext.accept(this);
			this.buffer.append(" function");
			if (ty.parameters != null && ty.parameters.size() > 0) {
				this.buffer.append("(");
				visitList(ty.parameters, ", ");
				if (ty.varargs != 0) {
					this.buffer.append("...");
				}
				this.buffer.append(")");
			}
		} else {
			node.sourceNext.accept(this);
			this.buffer.append("*");
		}
		endModifiedType(node);
		return false;
	}

	public boolean visit(TypeQualified node) {
		// abstract node
		return false;
	}

	public boolean visit(TypeSArray node) {
		startModifiedType(node);
		node.sourceNext.accept(this);
		this.buffer.append("[");
		node.sourceDim.accept(this);
		this.buffer.append("]");
		endModifiedType(node);
		return false;
	}

	public boolean visit(TypeSlice node) {
		startModifiedType(node);
		node.sourceNext.accept(this);
		this.buffer.append("[");
		if (node.lwr != null && node.upr != null) {
			node.lwr.accept(this);
			this.buffer.append(" .. ");
			node.upr.accept(this);
		}
		this.buffer.append("]");
		endModifiedType(node);
		return false;
	}

	public boolean visit(TypeStruct node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeTuple node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeTypedef node) {
		// compiler node
		return false;
	}

	public boolean visit(TypeTypeof node) {
		startModifiedType(node);
		this.buffer.append("typeof(");
		node.exp.accept(this);
		this.buffer.append(")");
		if (node.idents != null && node.idents.size() != 0) {
			visitQualifiedType(node);
		}
		endModifiedType(node);
		return false;
	}

	public boolean visit(UAddExp node) {
		this.buffer.append("+");
		node.sourceE1.accept(this);
		return false;
	}

	public boolean visit(UnaExp node) {
		// abstract node
		return false;
	}

	public boolean visit(UnionDeclaration node) {
		visit(node, "union", null, null);
		return false;
	}

	public boolean visit(UnitTestDeclaration node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("unittest ");
		if (node.fbody != null) {
			node.fbody.accept(this);
		}
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(UnrolledLoopStatement node) {
		// compiler node
		return false;
	}

	public boolean visit(UshrAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" >>>= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(UshrExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" >>> ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(VarDeclaration node) {
		if (node.first) {
			visitPreDDocss(node.preDdocs);
			printIndent();
			visitModifiers(node.modifiers);
			if (node.sourceType != null) {
				node.sourceType.accept(this);
				this.buffer.append(" ");
			}
		}
		if (node.ident != null) {
			this.buffer.append(node.ident);
		}
		
		if (node.sourceInit != null) {
			this.buffer.append(" = ");
			node.sourceInit.accept(this);
		}
		if (node.next == null) {					
			this.buffer.append(";");
			if (node.postDdoc != null) {
				this.buffer.append(" ");
				node.postDdoc.accept(this);
			}
		} else {
			this.buffer.append(", ");
		}
		return false;
	}

	public boolean visit(VarExp node) {
		// compiler node
		return false;
	}

	public boolean visit(Version node) {
		// compiler node
		return false;
	}

	public boolean visit(VersionCondition node) {
		// compiler node
		return false;
	}

	public boolean visit(VersionSymbol node) {
		visitPreDDocss(node.preDdocs);
		printIndent();
		visitModifiers(node.modifiers);
		this.buffer.append("version = ");
		this.buffer.append(node.version.value);
		this.buffer.append(";");
		if (node.postDdoc != null) {
			this.buffer.append(" ");
			node.postDdoc.accept(this);
		}
		return false;
	}

	public boolean visit(VoidInitializer node) {
		this.buffer.append("void");
		return false;
	}

	public boolean visit(VolatileStatement node) {
		printIndent();
		this.buffer.append("volatile ");
		node.sourceStatement.accept(this);
		return false;
	}

	public boolean visit(WhileStatement node) {
		printIndent();
		this.buffer.append("while(");
		node.condition.accept(this);
		this.buffer.append(") ");
		node.body.accept(this);
		return false;
	}

	public boolean visit(WithScopeSymbol node) {
		// abstract node
		return false;
	}

	public boolean visit(WithStatement node) {
		printIndent();
		this.buffer.append("with(");
		node.sourceExp.accept(this);
		this.buffer.append(") ");
		node.sourceBody.accept(this);
		return false;
	}

	public boolean visit(XorAssignExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" ^= ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}

	public boolean visit(XorExp node) {
		this.buffer.append("(");
		node.sourceE1.accept(this);
		this.buffer.append(" ^ ");
		node.sourceE2.accept(this);
		this.buffer.append(")");
		appendBinding(node);
		return false;
	}
	
	public void endVisit(ASTNode node) {
	}

	public void endVisit(AddAssignExp node) {
	}

	public void endVisit(AddExp node) {
	}

	public void endVisit(AddrExp node) {
	}

	public void endVisit(AggregateDeclaration node) {
	}

	public void endVisit(AliasDeclaration node) {
	}

	public void endVisit(AlignDeclaration node) {
	}

	public void endVisit(AndAndExp node) {
	}

	public void endVisit(AndAssignExp node) {
	}

	public void endVisit(AndExp node) {
	}

	public void endVisit(AnonDeclaration node) {
	}

	public void endVisit(AnonymousAggregateDeclaration node) {
	}

	public void endVisit(Argument node) {
	}

	public void endVisit(ArrayExp node) {
	}

	public void endVisit(ArrayInitializer node) {
	}

	public void endVisit(ArrayLiteralExp node) {
	}

	public void endVisit(ArrayScopeSymbol node) {
	}

	public void endVisit(AsmBlock node) {
	}

	public void endVisit(AsmStatement node) {
	}

	public void endVisit(AssertExp node) {
	}

	public void endVisit(AssignExp node) {
	}

	public void endVisit(AssocArrayLiteralExp node) {
	}

	public void endVisit(AttribDeclaration node) {
	}

	public void endVisit(BaseClass node) {
	}

	public void endVisit(BinExp node) {
	}

	public void endVisit(BoolExp node) {
	}

	public void endVisit(BreakStatement node) {
	}

	public void endVisit(CallExp node) {
	}

	public void endVisit(CaseStatement node) {
	}

	public void endVisit(CastExp node) {
	}

	public void endVisit(CatAssignExp node) {
	}

	public void endVisit(Catch node) {
	}

	public void endVisit(CatExp node) {
	}

	public void endVisit(ClassDeclaration node) {
	}

	public void endVisit(ClassInfoDeclaration node) {
	}

	public void endVisit(CmpExp node) {
	}

	public void endVisit(ComExp node) {
	}

	public void endVisit(CommaExp node) {
	}

	public void endVisit(CompileDeclaration node) {
	}

	public void endVisit(CompileExp node) {
	}

	public void endVisit(CompileStatement node) {
	}

	public void endVisit(ComplexExp node) {
	}

	public void endVisit(CompoundStatement node) {
	}

	public void endVisit(CondExp node) {
	}

	public void endVisit(Condition node) {
	}

	public void endVisit(ConditionalDeclaration node) {
	}

	public void endVisit(ConditionalStatement node) {
	}

	public void endVisit(ContinueStatement node) {
	}

	public void endVisit(CtorDeclaration node) {
	}

	public void endVisit(DebugCondition node) {
	}

	public void endVisit(DebugSymbol node) {
	}

	public void endVisit(Declaration node) {
	}

	public void endVisit(DeclarationExp node) {
	}

	public void endVisit(DeclarationStatement node) {
	}

	public void endVisit(DefaultStatement node) {
	}

	public void endVisit(DelegateExp node) {
	}

	public void endVisit(DeleteDeclaration node) {
	}

	public void endVisit(DeleteExp node) {
	}

	public void endVisit(DivAssignExp node) {
	}

	public void endVisit(DivExp node) {
	}

	public void endVisit(DollarExp node) {
	}

	public void endVisit(DoStatement node) {
	}

	public void endVisit(DotExp node) {
	}

	public void endVisit(DotIdExp node) {
	}

	public void endVisit(DotTemplateExp node) {
	}

	public void endVisit(DotTemplateInstanceExp node) {
	}

	public void endVisit(DotTypeExp node) {
	}

	public void endVisit(DotVarExp node) {
	}

	public void endVisit(Dsymbol node) {
	}

	public void endVisit(DsymbolExp node) {
	}

	public void endVisit(DtorDeclaration node) {
	}

	public void endVisit(EnumDeclaration node) {
	}

	public void endVisit(EnumMember node) {
	}

	public void endVisit(EqualExp node) {
	}

	public void endVisit(ExpInitializer node) {
	}

	public void endVisit(Expression node) {
	}

	public void endVisit(ExpStatement node) {
	}

	public void endVisit(FileExp node) {
	}

	public void endVisit(ForeachRangeStatement node) {
	}

	public void endVisit(ForeachStatement node) {
	}

	public void endVisit(ForStatement node) {
	}

	public void endVisit(FuncAliasDeclaration node) {
	}

	public void endVisit(FuncDeclaration node) {
	}

	public void endVisit(FuncExp node) {
	}

	public void endVisit(FuncLiteralDeclaration node) {
	}

	public void endVisit(GotoCaseStatement node) {
	}

	public void endVisit(GotoDefaultStatement node) {
	}

	public void endVisit(GotoStatement node) {
	}

	public void endVisit(HaltExp node) {
	}

	public void endVisit(IdentifierExp node) {
	}

	public void endVisit(IdentityExp node) {
	}

	public void endVisit(IfStatement node) {
	}

	public void endVisit(IftypeCondition node) {
	}

	public void endVisit(IftypeExp node) {
	}

	public void endVisit(Import node) {
	}

	public void endVisit(IndexExp node) {
	}

	public void endVisit(InExp node) {
	}

	public void endVisit(Initializer node) {
	}

	public void endVisit(IntegerExp node) {
	}

	public void endVisit(InterfaceDeclaration node) {
	}

	public void endVisit(InvariantDeclaration node) {
	}

	public void endVisit(LabelDsymbol node) {
	}

	public void endVisit(LabelStatement node) {
	}

	public void endVisit(LinkDeclaration node) {
	}

	public void endVisit(MinAssignExp node) {
	}

	public void endVisit(MinExp node) {
	}

	public void endVisit(ModAssignExp node) {
	}

	public void endVisit(ModExp node) {
	}

	public void endVisit(Modifier node) {
	}

	public void endVisit(Module node) {
	}

	public void endVisit(ModuleDeclaration node) {
	}

	public void endVisit(ModuleInfoDeclaration node) {
	}

	public void endVisit(MulAssignExp node) {
	}

	public void endVisit(MulExp node) {
	}

	public void endVisit(NegExp node) {
	}

	public void endVisit(NewAnonClassExp node) {
	}

	public void endVisit(NewDeclaration node) {
	}

	public void endVisit(NewExp node) {
	}

	public void endVisit(NotExp node) {
	}

	public void endVisit(NullExp node) {
	}

	public void endVisit(OnScopeStatement node) {
	}

	public void endVisit(OrAssignExp node) {
	}

	public void endVisit(OrExp node) {
	}

	public void endVisit(OrOrExp node) {
	}

	public void endVisit(Package node) {
	}

	public void endVisit(PostExp node) {
	}

	public void endVisit(PragmaDeclaration node) {
	}

	public void endVisit(PragmaStatement node) {
	}

	public void endVisit(ProtDeclaration node) {
	}

	public void endVisit(PtrExp node) {
	}

	public void endVisit(RealExp node) {
	}

	public void endVisit(RemoveExp node) {
	}

	public void endVisit(ReturnStatement node) {
	}

	public void endVisit(ScopeDsymbol node) {
	}

	public void endVisit(ScopeExp node) {
	}

	public void endVisit(ScopeStatement node) {
	}

	public void endVisit(ShlAssignExp node) {
	}

	public void endVisit(ShlExp node) {
	}

	public void endVisit(ShrAssignExp node) {
	}

	public void endVisit(ShrExp node) {
	}

	public void endVisit(SliceExp node) {
	}

	public void endVisit(Statement node) {
	}

	public void endVisit(StaticAssert node) {
	}

	public void endVisit(StaticAssertStatement node) {
	}

	public void endVisit(StaticCtorDeclaration node) {
	}

	public void endVisit(StaticDtorDeclaration node) {
	}

	public void endVisit(StaticIfCondition node) {
	}

	public void endVisit(StaticIfDeclaration node) {
	}

	public void endVisit(StorageClassDeclaration node) {
	}

	public void endVisit(StringExp node) {
	}

	public void endVisit(StructDeclaration node) {
	}

	public void endVisit(StructInitializer node) {
	}

	public void endVisit(SuperExp node) {
	}

	public void endVisit(SwitchStatement node) {
	}

	public void endVisit(SymOffExp node) {
	}

	public void endVisit(SynchronizedStatement node) {
	}

	public void endVisit(TemplateAliasParameter node) {
	}

	public void endVisit(TemplateDeclaration node) {
	}

	public void endVisit(TemplateExp node) {
	}

	public void endVisit(TemplateInstance node) {
	}

	public void endVisit(TemplateInstanceWrapper node) {
	}

	public void endVisit(TemplateMixin node) {
	}

	public void endVisit(TemplateParameter node) {
	}

	public void endVisit(TemplateTupleParameter node) {
	}

	public void endVisit(TemplateTypeParameter node) {
	}

	public void endVisit(TemplateValueParameter node) {
	}

	public void endVisit(ThisDeclaration node) {
	}

	public void endVisit(ThisExp node) {
	}

	public void endVisit(ThrowStatement node) {
	}

	public void endVisit(TraitsExp node) {
	}

	public void endVisit(TryCatchStatement node) {
	}

	public void endVisit(TryFinallyStatement node) {
	}

	public void endVisit(Tuple node) {
	}

	public void endVisit(TupleDeclaration node) {
	}

	public void endVisit(TupleExp node) {
	}

	public void endVisit(Type node) {
	}

	public void endVisit(TypeAArray node) {
	}

	public void endVisit(TypeBasic node) {
	}

	public void endVisit(TypeClass node) {
	}

	public void endVisit(TypeDArray node) {
	}

	public void endVisit(TypedefDeclaration node) {
	}

	public void endVisit(TypeDelegate node) {
	}

	public void endVisit(TypeDotIdExp node) {
	}

	public void endVisit(TypeEnum node) {
	}

	public void endVisit(TypeExp node) {
	}

	public void endVisit(TypeFunction node) {
	}

	public void endVisit(TypeIdentifier node) {
	}

	public void endVisit(TypeidExp node) {
	}

	public void endVisit(TypeInfoArrayDeclaration node) {
	}

	public void endVisit(TypeInfoAssociativeArrayDeclaration node) {
	}

	public void endVisit(TypeInfoClassDeclaration node) {
	}

	public void endVisit(TypeInfoDeclaration node) {
	}

	public void endVisit(TypeInfoDelegateDeclaration node) {
	}

	public void endVisit(TypeInfoEnumDeclaration node) {
	}

	public void endVisit(TypeInfoFunctionDeclaration node) {
	}

	public void endVisit(TypeInfoInterfaceDeclaration node) {
	}

	public void endVisit(TypeInfoPointerDeclaration node) {
	}

	public void endVisit(TypeInfoStaticArrayDeclaration node) {
	}

	public void endVisit(TypeInfoStructDeclaration node) {
	}

	public void endVisit(TypeInfoTypedefDeclaration node) {
	}

	public void endVisit(TypeInstance node) {
	}

	public void endVisit(TypePointer node) {
	}

	public void endVisit(TypeQualified node) {
	}

	public void endVisit(TypeSArray node) {
	}

	public void endVisit(TypeSlice node) {
	}

	public void endVisit(TypeStruct node) {
	}

	public void endVisit(TypeTuple node) {
	}

	public void endVisit(TypeTypedef node) {
	}

	public void endVisit(TypeTypeof node) {
	}

	public void endVisit(UAddExp node) {
	}

	public void endVisit(UnaExp node) {
	}

	public void endVisit(UnionDeclaration node) {
	}

	public void endVisit(UnitTestDeclaration node) {
	}

	public void endVisit(UnrolledLoopStatement node) {
	}

	public void endVisit(UshrAssignExp node) {
	}

	public void endVisit(UshrExp node) {
	}

	public void endVisit(VarDeclaration node) {
	}

	public void endVisit(VarExp node) {
	}

	public void endVisit(Version node) {
	}

	public void endVisit(VersionCondition node) {
	}

	public void endVisit(VersionSymbol node) {
	}

	public void endVisit(VoidInitializer node) {
	}

	public void endVisit(VolatileStatement node) {
	}

	public void endVisit(WhileStatement node) {
	}

	public void endVisit(WithScopeSymbol node) {
	}

	public void endVisit(WithStatement node) {
	}

	public void endVisit(XorAssignExp node) {
	}

	public void endVisit(XorExp node) {
	}

	public void postVisit(ASTNode node) {
	}

	public void preVisit(ASTNode node) {
	}

}
