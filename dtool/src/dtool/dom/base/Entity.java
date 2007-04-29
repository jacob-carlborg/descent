package dtool.dom.base;

import util.tree.TreeVisitor;
import descent.internal.core.dom.Type;
import descent.internal.core.dom.TypeIdentifier;
import descent.internal.core.dom.TypeInstance;
import descent.internal.core.dom.TypeQualified;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.EntitySingle.Identifier;
import dtool.dom.declarations.DefUnit;
import dtool.dom.declarations.Module;
import dtool.model.BindingResolver;
import dtool.model.IScope;

/**
 * A qualified entity/name reference
 * XXX: Consider in the future to be an interface?
 */
public abstract class Entity extends ASTNeoNode {
	
	public static class QualifiedEnt extends Entity {
		public Entity topent;
		public EntitySingle baseent;

		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, topent);
				TreeVisitor.acceptChildren(visitor, baseent);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public DefUnit getTargetDefUnit() {
			IScope scope = topent.getTargetDefUnit().getScope();
			Identifier id = (Identifier) baseent;
			BindingResolver.getDefUnit(scope.getDefUnits(), id.name );

			return null;
		}
		
		public String toString() {
			return topent + " ." + baseent;
		}

	}
	
	public static class ModuleRootEnt extends Entity {
		//public EntitySingle baseent;
		
		public void accept0(IASTNeoVisitor visitor) {
			visitor.visit(this);
/*			if (children) {
				acceptChildren(visitor, baseent);
			}
*/			visitor.endVisit(this);

		}

		public String toString() {
			return ".";
		}

		@Override
		public DefUnit getTargetDefUnit() {
			ASTNode elem = this;
			// Search for module elem
			while((elem instanceof Module) == false)
				elem = elem.getParent();
			return ((Module)elem);
		}
	}

	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	//public EntitySingleRef[] ents; 
	
	public EReferenceConstraint refConstraint = null;
	
	
	public abstract DefUnit getTargetDefUnit();
	

	public static EntityConstrainedRef.TypeConstraint convertType(Type type) {
		Entity entity = (Entity) DescentASTConverter.convertElem(type);
		return new EntityConstrainedRef.TypeConstraint(entity);
	}
	
	public static EntityConstrainedRef.NoConstraint convertAnyEnt(Type type) {
		Entity entity = (Entity) DescentASTConverter.convertElem(type);
		return new EntityConstrainedRef.NoConstraint(entity);
	}
	
/*	public static ValueConstraint convertValue(descent.internal.core.dom.Identifier id) {
		Entity entity = (Entity) DescentASTConverter.convertElem(type);
		return new EntityConstrainedRef.ValueConstraint(entity);
	}
*/
	
	private static Entity convertIdents(Entity rootent,
			descent.internal.core.dom.TypeQualified elem, int endix) {
		if (endix > 0-1) { 
			Entity.QualifiedEnt entref = new Entity.QualifiedEnt();
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
			rootent = new Entity.ModuleRootEnt();
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

