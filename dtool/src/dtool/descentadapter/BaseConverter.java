package dtool.descentadapter;

import descent.internal.core.dom.Identifier;
import dtool.dom.base.ASTNode;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;
import dtool.dom.base.TypeDynArray;
import dtool.dom.base.TypeTypeof;
import dtool.dom.base.DefUnit.Symbol;

/**
 * This class is a mixin. 
 * Do not use it, instead use it's subclass: {@link DefConverter}
 */
public abstract class BaseConverter extends ASTCommonConverter {

	public BaseConverter() {
		super();
	}
	
	protected Symbol convertIdentifierToSymbol(Identifier ident) {
		if(ident == null)
			return null;
		Symbol newelem = new Symbol(ident.string);
		rangeAdapt(newelem, ident);
		return newelem;
	}
	
	/*  =======================================================  */

	/* ---- Entities Core ---- */
	public boolean visit(descent.internal.core.dom.Identifier elem) {
		EntitySingle.Identifier newelem = new EntitySingle.Identifier(elem.string);
		rangeAdapt(newelem, elem);
		return endAdapt(newelem);
	}
	
	
	public boolean visit(descent.internal.core.dom.TypeBasic elem) {
		EntitySingle.Identifier newelem = new EntitySingle.Identifier();
		newelem.name = elem.toString();
		rangeAdapt(newelem, elem); 
		//TODO set binding to intrinsic?
		return endAdapt(newelem);
	}
	
	
	private Entity convertIdents(Entity rootent,
			descent.internal.core.dom.TypeQualified elem, int endix) {
		if (endix > 0-1) { 
			Entity.QualifiedEnt entref = new Entity.QualifiedEnt();
			entref.topent = convertIdents(rootent,elem, endix-1);
			entref.baseent = (EntitySingle) convert(elem.idents.get(endix));

			entref.startPos = rootent.startPos;
			entref.setEndPos(entref.baseent.getEndPos());
			return entref;
		} else if( endix == 0-1 ) {
			return rootent;
		}
		return null;
	}
	
	public boolean visit(descent.internal.core.dom.TypeIdentifier elem) {
		
		Entity rootent;
		
		if(elem.startPos == -1 ) { 
			assert(elem.ident.string.equals(""));
			rootent = new Entity.ModuleRootEnt();

			rootent.startPos = elem.idents.get(0).startPos-1;
			rootent.setEndPos(rootent.startPos+1);
		} else {
			rootent = (EntitySingle.Identifier) convert(elem.ident);
		}
		
		Entity newelem; 
		if(elem.idents.size() > 0){
			newelem = convertIdents(rootent, elem, elem.idents.size()-1);
		} else {
			newelem = rootent;
		}
		
		return endAdapt(newelem);
	}

	public boolean visit(descent.internal.core.dom.TypeTypeof elem) {

		Entity rootent = new TypeTypeof();
		rangeAdapt(rootent, elem); //FIXME end range
		
		Entity newelem; 
		if(elem.idents.size() > 0){
			newelem = convertIdents(rootent, elem, elem.idents.size()-1);
		} else {
			newelem = rootent;
		}
		
		return endAdapt(newelem);
	}

	
	public boolean visit(descent.internal.core.dom.TypeDArray elem) {
		
		TypeDynArray newelem = new TypeDynArray();
		rangeAdapt(newelem, elem); //FIXME end range

		newelem.elemtype = (Entity) convert(elem.next);

		return endAdapt(newelem);
	}
	
	/*  -------------------------------------  */
	
	public void endVisit(ASTNode element) {
	}
}