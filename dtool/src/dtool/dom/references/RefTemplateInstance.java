package dtool.dom.references;

import java.util.Collections;
import java.util.List;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.TemplateInstance;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.ast.IASTNeoVisitor;

public class RefTemplateInstance extends CommonRefSingle {

	public List<ASTNeoNode> tiargs;

	public RefTemplateInstance(
			descent.internal.compiler.parser.TemplateInstance elem) {
		this(elem, elem.idents.get(0), elem.tiargs);
		Assert.isTrue(elem.idents.size() == 1);
	}

	public RefTemplateInstance(TemplateInstance elem, IdentifierExp tplIdent,
			List<ASTDmdNode> tiargs) {
		setSourceRange(elem);
		Assert.isTrue(elem.getStartPos() == tplIdent.getStartPos());
		Assert.isTrue(tplIdent.ident != null);
		this.name = new String(tplIdent.ident);
		this.tiargs = DescentASTConverter.convertManyL(tiargs, this.tiargs);
		if (this.tiargs == null)
			this.tiargs = Collections.emptyList();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			// TreeVisitor.acceptChildren(visitor, name);
			TreeVisitor.acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}

	@Override
	public String toStringAsElement() {
		return name + "!" + ASTPrinter.toStringAsElements(tiargs, ", ");
	}

}
