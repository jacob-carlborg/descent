package dtool.dom.base;

import descent.internal.core.dom.Type;
import descent.internal.core.dom.TypeIdentifier;
import descent.internal.core.dom.TypeInstance;
import descent.internal.core.dom.TypeQualified;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;

/**
 * A qualified entity/name reference
 * XXX: Consider in the future to be an interface?
 */
public abstract class Entity extends ASTNeoNode {
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	//public EntitySingleRef[] ents; 
	
	public EReferenceConstraint refConstraint = null;
	
	
	public abstract DefUnit getTargetDefUnit();
	

	public static BaseEntityRef.TypeConstraint convertType(Type type) {
		Entity entity = (Entity) DescentASTConverter.convertElem(type);
		return new BaseEntityRef.TypeConstraint(entity);
	}
	
	public static BaseEntityRef.NoConstraint convertAnyEnt(Type type) {
		Entity entity = (Entity) DescentASTConverter.convertElem(type);
		return new BaseEntityRef.NoConstraint(entity);
	}
	
/*	public static ValueConstraint convertValue(descent.internal.core.dom.Identifier id) {
		Entity entity = (Entity) DescentASTConverter.convertElem(type);
		return new EntityConstrainedRef.ValueConstraint(entity);
	}
*/
	
	private static Entity convertIdents(Entity rootent,
			descent.internal.core.dom.TypeQualified elem, int endix) {
		if (endix > 0-1) { 
			EntQualified entref = new EntQualified();
			entref.topent = convertIdents(rootent,elem, endix-1);
			entref.baseent = EntitySingle.convert(elem.idents.get(endix));

			entref.startPos = rootent.startPos;
			entref.setEndPos(entref.baseent.getEndPos());
			return entref;
		} else if( endix == 0-1 ) {
			return rootent;
		}
		return null;
	}
	
	public static Entity convertQualified(Entity rootent, TypeQualified elem) {
		Entity newelem; 
		if(elem.idents.size() > 0){
			newelem = convertIdents(rootent, elem, elem.idents.size()-1);
		} else {
			newelem = rootent;
		}
		return newelem;
	}



	public static Entity convertTypeIdentifierRoot(TypeIdentifier elem) {
		Entity rootent;
		if(elem.startPos == -1 ) { 
			assert(elem.ident.string.equals(""));
			rootent = new EntModuleRoot();
			rootent.startPos = elem.idents.get(0).startPos-1;
			rootent.setEndPos(rootent.startPos+1);
		} else {
			rootent = EntitySingle.convert(elem.ident);
		}
		return rootent;
	}


	public static Entity convertTypeInstanceRoot(TypeInstance elem) {
		Entity rootent = EntitySingle.convert(elem.tempinst);
		return rootent;
	}



}

