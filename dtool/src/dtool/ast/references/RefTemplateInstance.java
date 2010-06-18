package dtool.ast.references;

import static melnorme.miscutil.Assert.assertTrue;

import java.util.List;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.TemplateInstance;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTPrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter;

public class RefTemplateInstance extends CommonRefSingle {

	public final ASTNeoNode[] tiargs;

	public RefTemplateInstance(
			descent.internal.compiler.parser.TemplateInstance elem) {
		this(elem, elem.name, elem.tiargs);
	}

	public RefTemplateInstance(TemplateInstance elem, IdentifierExp tplIdent,
			List<ASTDmdNode> tiargs) {
		if(elem.hasNoSourceRangeInfo()) {
			setSourceRange(tplIdent);
			assertTrue(!tplIdent.hasNoSourceRangeInfo());
		} else {
			setSourceRange(elem);
			Assert.isTrue(elem.getStartPos() == tplIdent.getStartPos());
		}
		Assert.isTrue(tplIdent.ident != null);
		this.name = new String(tplIdent.ident);
		if (tiargs == null)
			this.tiargs = ASTNeoNode.NO_ELEMENTS;
		else
			this.tiargs = DescentASTConverter.convertMany(tiargs);
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
		return name + "!" + ASTPrinter.toStringParamListAsElements(tiargs);
	}

}
