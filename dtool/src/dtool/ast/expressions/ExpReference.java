package dtool.ast.expressions;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.ScopeExp;
import descent.internal.compiler.parser.TypeExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.CommonRefSingle;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

/**
 * An Expression wrapping a Reference
 */
public class ExpReference extends Expression {
	
	public Reference ref;
	
	public ExpReference(IdentifierExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.ref = CommonRefSingle.convertToSingleRef(elem, convContext);
	}

	public ExpReference(TypeExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.ref = ReferenceConverter.convertType(elem.type, convContext);
	}
	
	public ExpReference(DotIdExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.ref = ReferenceConverter.convertDotIdexp(elem, convContext);
	}
	
	public ExpReference(DotTemplateInstanceExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.ref = ReferenceConverter.convertDotTemplateIdexp(elem, convContext);
	}
	
	public ExpReference(ScopeExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.ref = (Reference) DescentASTConverter.convertElem(elem.sds, convContext);
	}



	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ref);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return ref.findTargetDefUnits(findFirstOnly);
	}

}
