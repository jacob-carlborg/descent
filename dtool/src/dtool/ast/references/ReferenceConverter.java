package dtool.ast.references;

import java.util.List;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.TypeInstance;
import descent.internal.compiler.parser.TypeQualified;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.Expression;
import dtool.descentadapter.DescentASTConverter;
import dtool.refmodel.IDefUnitReferenceNode;


/** 
 * Helper class for converting references from the DMD AST 
 */
public abstract class ReferenceConverter {

	public static Reference convertType(Type type) {
		Reference entity = (Reference) DescentASTConverter.convertElem(type);
		return entity;
	}

	public static Reference convertTypeQualified(Reference rootent, TypeQualified elem) {
		if(elem.idents != null && elem.idents.size() > 0){
			return createQualifiedRefFromIdents(rootent, elem.idents, elem.idents.size());
		} else {
			return rootent;
		}
	}

	public static Reference convertTemplateInstance(TemplateInstance tplInstance) {
		return convertTemplateInstance(tplInstance, tplInstance.tiargs);
	}

	public static Reference convertTypeInstance(TypeInstance elem) {
		Reference rootRef = convertTemplateInstance(elem.tempinst);
		if(elem.idents == null) {
			return rootRef;
		} else {
			//Assert.isTrue(elem.idents == null);
			return createQualifiedRefFromIdents(rootRef, elem.idents, elem.idents.size());
		}
		
	}
	
	static Reference createQualifiedRefFromIdents(Reference rootRef,
			List<IdentifierExp> idents, int endix) {
		Assert.isTrue(endix >= 0);

		if( endix == 0 ) {
			return rootRef;
		}
		
		Reference ref;
		if(endix == 1 && rootRef == null) {
			RefModuleQualified entroot = new RefModuleQualified(idents.get(endix-1));
			ref = entroot;
		} else {
			RefQualified qref = new RefQualified();
			qref.root = createQualifiedRefFromIdents(rootRef, idents, endix-1);
			qref.subref = CommonRefSingle.convertToSingleRef(idents.get(endix-1));
			if(rootRef != null && rootRef.getStartPos() != -1)
				qref.setStart(rootRef.getStartPos());
			else
				qref.setStart(idents.get(0).start);
			qref.setEndPos(qref.subref.getEndPos());
			ref = qref;
		}
		return ref;
		
	}

	public static Reference convertTypeIdentifier_ToRoot(TypeIdentifier elem) {
		Reference rootent;
		if(elem.ident.ident.length == 0) { 
			/*rootent = new EntModuleRoot();
			rootent.startPos = elem.idents.get(0).startPos-1;
			rootent.setEndPos(rootent.startPos+1);*/
			rootent = null;
		} else {
			rootent = CommonRefSingle.convertToSingleRef(elem.ident);
		}
		return rootent;
	}
	
	public static Reference convertTemplateInstance(TemplateInstance tplInstance, List<ASTDmdNode> tiargs) {
		List<IdentifierExp> idents = tplInstance.idents;
		int numIdents = idents.size();
		IdentifierExp tplIdent = idents.get(numIdents-1);
		RefTemplateInstance refTpl = new RefTemplateInstance(tplInstance, tplIdent, tiargs);

		if(numIdents == 1) {
			return refTpl;
		} else {
			RefQualified qref = new RefQualified();
			Reference rootent = CommonRefSingle.convertToSingleRef(idents.get(0));
			qref.root = createQualifiedRefFromIdents(rootent, idents, numIdents-1);
			qref.subref = refTpl;
			return qref;
		}
	}
	
	public static Reference convertDotIdexp(DotIdExp elem) {
		
		IDefUnitReferenceNode rootent;
		Expression expTemp = Expression.convert(elem.e1);
		if(expTemp instanceof ExpReference) {
			rootent = ((ExpReference) expTemp).ref;

			if(rootent == null) {
				return new RefModuleQualified(elem.ident);
			}
		} else {
			rootent = expTemp;
		}

		
		RefQualified newelem = new RefQualified();
		newelem.setSourceRange(elem);
		newelem.root = rootent;
		newelem.subref = CommonRefSingle.convertToSingleRef(elem.ident);

		// Fix some DMD missing ranges 
		if(newelem.hasNoSourceRangeInfo()) {
			try {
				int newstartPos= newelem.getRootAsNode().getStartPos();
				int newendPos= newelem.subref.getEndPos()-newelem.getRootAsNode().getStartPos();
				newelem.setSourceRange(newstartPos, newendPos);
			} catch (RuntimeException re) {
				throw new UnsupportedOperationException(re);
			}
		}
		return newelem;
	}
	
	public static Reference convertDotTemplateIdexp(DotTemplateInstanceExp elem) {
		
		IDefUnitReferenceNode rootent;
		Expression expTemp = Expression.convert(elem.e1);
		if(expTemp instanceof ExpReference) {
			rootent = ((ExpReference) expTemp).ref;

			if(rootent == null) {
				return new RefModuleQualified(elem.ti.idents.get(0));
			}
		} else {
			rootent = expTemp;
		}

		
		RefQualified newelem = new RefQualified();
		newelem.setSourceRange(elem);
		newelem.root = rootent;
		newelem.subref = new RefTemplateInstance(elem.ti);

		return newelem;
	}

}
