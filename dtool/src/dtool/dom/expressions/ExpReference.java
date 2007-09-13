package dtool.dom.expressions;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.ScopeExp;
import descent.internal.compiler.parser.TypeDotIdExp;
import descent.internal.compiler.parser.TypeExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.CommonRefSingle;
import dtool.dom.references.RefQualified;
import dtool.dom.references.Reference;
import dtool.dom.references.ReferenceConverter;

/**
 * An Expression wrapping a Reference
 */
public class ExpReference extends Expression {
	
	public Reference ref;
	
	public ExpReference(IdentifierExp elem) {
		convertNode(elem);
		this.ref = CommonRefSingle.convertToSingleRef(elem);
	}

	public ExpReference(TypeExp elem) {
		convertNode(elem);
		this.ref = ReferenceConverter.convertType(elem.type);
	}
	
	public ExpReference(DotIdExp elem) {
		convertNode(elem);
		this.ref = ReferenceConverter.convertDotIdexp(elem);
	}
	
	public ExpReference(DotTemplateInstanceExp elem) {
		convertNode(elem);
		this.ref = ReferenceConverter.convertDotTemplateIdexp(elem);
	}
	
	public ExpReference(TypeDotIdExp elem) {
		convertNode(elem);
		this.ref = new RefQualified(
				ReferenceConverter.convertType(elem.type),
				CommonRefSingle.convertToSingleRef(elem.ident)
				);
		//this.baseEntity = new BaseEntityRef.ValueConstraint(qent);
	}
	
	public ExpReference(ScopeExp elem) {
		convertNode(elem);
		this.ref = (Reference) DescentASTConverter.convertElem(elem.sds);
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
