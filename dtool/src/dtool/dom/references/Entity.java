package dtool.dom.references;

import util.Assert;
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
import dtool.refmodel.IScope;

/**
 * A qualified entity/name reference
 */
public abstract class Entity extends ASTNeoNode implements IDefUnitReference {
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	public EReferenceConstraint refConstraint = null;
	
	
	public abstract DefUnit getTargetDefUnit();
	
	public IScope getTargetScope() {
		DefUnit defunit = getTargetDefUnit();
		if(defunit == null)
			return null;
		return defunit.getMembersScope();
	}
	
	
	/* -------- Conversion Funcs -------- */

	public static Entity convertType(Type type) {
		if(type == null) return null;
		Entity entity = (Entity) DescentASTConverter.convertElem(type);
		return entity;
	}
	
	public static Entity convertAnyEnt(Type type) {
		Entity entity = (Entity) DescentASTConverter.convertElem(type);
		return entity;
	}
	
	private static Entity convertIdents(Entity rootent,
			descent.internal.core.dom.TypeQualified elem, int endix) {
		Assert.isTrue(endix >= 0);

		if( endix == 0 ) {
			return rootent;
		}
		
		Entity qentref;
		if(endix == 1 && rootent == null) {
			EntModuleQualified entroot = new EntModuleQualified(elem.idents.get(endix-1));
			qentref = entroot;
		} else {
			EntQualified entref = new EntQualified();
			entref.root = convertIdents(rootent, elem, endix-1);
			entref.subent = EntitySingle.convert(elem.idents.get(endix-1));
			if(rootent != null && rootent.startPos != -1)
				entref.startPos = rootent.startPos;
			else
				entref.startPos = elem.idents.get(0).startPos;
			entref.setEndPos(entref.subent.getEndPos());
			qentref = entref;
		}
		return qentref;
		
	}
	
	public static Entity convertQualified(Entity rootent, TypeQualified elem) {
		Entity newelem; 
		if(elem.idents.size() > 0){
			newelem = convertIdents(rootent, elem, elem.idents.size());
		} else {
			newelem = rootent;
		}
		return newelem;
	}



	public static Entity convertTypeIdentifierRoot(TypeIdentifier elem) {
		Entity rootent;
		if(elem.ident.string.equals("")) { 
			/*rootent = new EntModuleRoot();
			rootent.startPos = elem.idents.get(0).startPos-1;
			rootent.setEndPos(rootent.startPos+1);*/
			rootent = null;
		} else {
			rootent = EntitySingle.convert(elem.ident);
		}
		return rootent;
	}


	public static Entity convertTypeInstanceRoot(TypeInstance elem) {
		Entity rootent = EntitySingle.convert(elem.tempinst);
		return rootent;
	}



	
	public static Entity convertDotIexp(DotIdExp elem) {
		
		IDefUnitReference rootent;
		Expression expTemp = Expression.convert(elem.e);
		if(expTemp instanceof ExpEntity) {
			rootent = ((ExpEntity) expTemp).entity;
		} else {
			rootent = Expression.convert(elem.e);
		}
		
		if(rootent == null) {
			return new EntModuleQualified(elem.id);
		}
		
		EntQualified newelem = new EntQualified();
		newelem.setSourceRange(elem);
		newelem.root = rootent;
		newelem.subent = EntitySingle.convert(elem.id);

		// fix some range discrepancies
		/*if(newelem.root instanceof EntModuleQualified && !newelem.hasNoSourceRangeInfo()) {
			// range error here
			newelem.getRootExp().startPos = newelem.startPos;
			newelem.getRootExp().setEndPos(newelem.subent.getEndPos());
		}*/
		
		// Fix some DMD missing ranges 
		if(newelem.hasNoSourceRangeInfo()) {
			try {
				int newstartPos= newelem.getRootExp().startPos;
				int newendPos= newelem.subent.getEndPos()-newelem.getRootExp().startPos;
				newelem.setSourceRange(newstartPos, newendPos);
			} catch (RuntimeException re) {
				throw new UnsupportedOperationException(re);
			}
		}
		return newelem;
	}
	
	public static Entity convertRef(IdentifierExp exp) {
		return EntitySingle.convert(exp.id);
	}
}

