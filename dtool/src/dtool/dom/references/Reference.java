package dtool.dom.references;

import java.util.Collection;

import melnorme.miscutil.Assert;
import descent.internal.core.dom.DotIdExp;
import descent.internal.core.dom.IdentifierExp;
import descent.internal.core.dom.Type;
import descent.internal.core.dom.TypeIdentifier;
import descent.internal.core.dom.TypeInstance;
import descent.internal.core.dom.TypeQualified;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;
import dtool.dom.expressions.ExpEntity;
import dtool.dom.expressions.Expression;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;

/**
 * Common class for entity references.
 */
public abstract class Reference extends ASTNeoNode implements IDefUnitReference {
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	public EReferenceConstraint refConstraint = null;
	
	
	public IScopeNode getTargetScope() {
		DefUnit defunit = findTargetDefUnit(); 
		if(defunit == null)
			return null;
		return defunit.getMembersScope();
	}
	
	public DefUnit findTargetDefUnit() {
		Collection<DefUnit> defunits = findTargetDefUnits(true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next();
	}
	
	
	/* ---------------- Conversion Funcs ---------------- */

	public static Reference convertType(Type type) {
		if(type == null) return null;
		Reference entity = (Reference) DescentASTConverter.convertElem(type);
		return entity;
	}
	
	public static Reference convertAnyEnt(Type type) {
		Reference entity = (Reference) DescentASTConverter.convertElem(type);
		return entity;
	}
	
	private static Reference convertIdents(Reference rootent,
			descent.internal.core.dom.TypeQualified elem, int endix) {
		Assert.isTrue(endix >= 0);

		if( endix == 0 ) {
			return rootent;
		}
		
		Reference qentref;
		if(endix == 1 && rootent == null) {
			RefModuleQualified entroot = new RefModuleQualified(elem.idents.get(endix-1));
			qentref = entroot;
		} else {
			RefQualified entref = new RefQualified();
			entref.root = convertIdents(rootent, elem, endix-1);
			entref.subref = CommonRefSingle.convert(elem.idents.get(endix-1));
			if(rootent != null && rootent.startPos != -1)
				entref.startPos = rootent.startPos;
			else
				entref.startPos = elem.idents.get(0).startPos;
			entref.setEndPos(entref.subref.getEndPos());
			qentref = entref;
		}
		return qentref;
		
	}
	
	public static Reference convertQualified(Reference rootent, TypeQualified elem) {
		Reference newelem; 
		if(elem.idents.size() > 0){
			newelem = convertIdents(rootent, elem, elem.idents.size());
		} else {
			newelem = rootent;
		}
		return newelem;
	}



	public static Reference convertTypeIdentifierRoot(TypeIdentifier elem) {
		Reference rootent;
		if(elem.ident.string.equals("")) { 
			/*rootent = new EntModuleRoot();
			rootent.startPos = elem.idents.get(0).startPos-1;
			rootent.setEndPos(rootent.startPos+1);*/
			rootent = null;
		} else {
			rootent = CommonRefSingle.convert(elem.ident);
		}
		return rootent;
	}


	public static Reference convertTypeInstanceRoot(TypeInstance elem) {
		Reference rootent = CommonRefSingle.convert(elem.tempinst);
		return rootent;
	}



	
	public static Reference convertDotIexp(DotIdExp elem) {
		
		IDefUnitReference rootent;
		Expression expTemp = Expression.convert(elem.e);
		if(expTemp instanceof ExpEntity) {
			rootent = ((ExpEntity) expTemp).entity;
		} else {
			rootent = Expression.convert(elem.e);
		}
		
		if(rootent == null) {
			return new RefModuleQualified(elem.id);
		}
		
		RefQualified newelem = new RefQualified();
		newelem.setSourceRange(elem);
		newelem.root = rootent;
		newelem.subref = CommonRefSingle.convert(elem.id);

		// fix some range discrepancies
		/*if(newelem.root instanceof EntModuleQualified && !newelem.hasNoSourceRangeInfo()) {
			// range error here
			newelem.getRootExp().startPos = newelem.startPos;
			newelem.getRootExp().setEndPos(newelem.subent.getEndPos());
		}*/
		
		// Fix some DMD missing ranges 
		if(newelem.hasNoSourceRangeInfo()) {
			try {
				int newstartPos= newelem.getRootAsNode().startPos;
				int newendPos= newelem.subref.getEndPos()-newelem.getRootAsNode().startPos;
				newelem.setSourceRange(newstartPos, newendPos);
			} catch (RuntimeException re) {
				throw new UnsupportedOperationException(re);
			}
		}
		return newelem;
	}
	
	public static Reference convertRef(IdentifierExp exp) {
		return CommonRefSingle.convert(exp.id);
	}
}

