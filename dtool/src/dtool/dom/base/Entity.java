package dtool.dom.base;

import util.Assert;
import descent.internal.core.dom.Type;
import descent.internal.core.dom.TypeIdentifier;
import descent.internal.core.dom.TypeInstance;
import descent.internal.core.dom.TypeQualified;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;
import dtool.model.IEntQualified;
import dtool.model.IScope;
import dtool.model.IScopeBinding;

/**
 * A qualified entity/name reference
 */
public abstract class Entity extends ASTNeoNode implements IScopeBinding {
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	public EReferenceConstraint refConstraint = null;
	
		
	public DefUnit getTargetDefUnit() {
		
		if(getParent() instanceof IEntQualified) {
			IEntQualified parent = (IEntQualified) getParent();
			if(parent.getSubEnt() == this) {
				return parent.getTargetDefUnit();
			} else {
				Assert.isTrue(parent.getRoot() == this);
			}
		}
		return getTargetDefUnitAsRoot();
	}

	
	protected abstract DefUnit getTargetDefUnitAsRoot();

	
	public IScope getTargetScope() {
		return getTargetDefUnit().getBindingScope();
	}
	
	
	/* -------- Conversion Tools -------- */

	public static BaseEntityRef.TypeConstraint convertType(Type type) {
		if(type == null) return null;
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
			entref.rootent = convertIdents(rootent,elem, endix-1);
			entref.subent = EntitySingle.convert(elem.idents.get(endix));

			entref.startPos = rootent.startPos;
			entref.setEndPos(entref.subent.getEndPos());
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

