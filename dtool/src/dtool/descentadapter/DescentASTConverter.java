package dtool.descentadapter;

import java.util.List;

import descent.core.domX.ASTNode;
import descent.internal.core.dom.Identifier;
import dtool.dom.DeclarationImport;
import dtool.dom.Def_EProtection;
import dtool.dom.Def_Modifiers;
import dtool.dom.Definition;
import dtool.dom.DefinitionAggregate;
import dtool.dom.DefinitionAlias;
import dtool.dom.DefinitionFunction;
import dtool.dom.DefinitionVariable;
import dtool.dom.Entity;
import dtool.dom.EntitySingle;
import dtool.dom.Module;
import dtool.dom.TypeDynArray;
import dtool.dom.TypeTypeof;
import dtool.dom.DefUnit.Symbol;
import dtool.dom.Module.DeclarationModule;

/**
 * Converts from DMD's AST to a proper DOM AST ("Neo AST")
 */
public class DescentASTConverter extends ASTCommonConverter {


	
	private Symbol convertIdentifier(Identifier ident) {
		return (ident != null) ? new Symbol(ident.string) : null;
	}
	
	private void definitionAdapt(Definition newelem, descent.internal.core.dom.Dsymbol elem) {
		rangeAdapt(newelem, elem);

		newelem.name = convertIdentifier(elem.ident); 
		newelem.modifiers = Def_Modifiers.adaptFromDescent(elem.modifiers); 
		newelem.protection = Def_EProtection.adaptFromDescent(elem.modifiers); 
	}
	

	/*  =======================================================  */

	public boolean visit(descent.internal.core.dom.Module elem) {
		Module newelem = new Module();
		rangeAdapt(newelem, elem);
		//newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		if(elem.md != null){
			newelem.name = convertIdentifier(elem.ident); // If there is md there is this
			newelem.md = new DeclarationModule();
			rangeAdapt(newelem.md, elem.md);
			// XXX: Check this
			newelem.md.moduleName = new EntitySingle.Identifier(newelem.name.name);
			//newelem.md.moduleName = new SymbolReference(elem.md.qName.toString());
			newelem.md.packages = new EntitySingle.Identifier[elem.md.packages.size()];
			convertMany(newelem.md.packages, elem.md.packages.toArray());
		}
		newelem.members = convertMany(elem.getDeclarationDefinitions());
		return endAdapt(newelem);
	}


	public boolean visit(descent.internal.core.dom.ImportDeclaration elem) {
		DeclarationImport newelem = new DeclarationImport();
		rangeAdapt(newelem, elem);
		newelem.imports = (List) elem.imports;
		newelem.imports.get(0).getSelectiveImports();
		return endAdapt(newelem);
	}
	
	/* ---- Entities Core ---- */
	public boolean visit(descent.internal.core.dom.Identifier elem) {
		EntitySingle.Identifier newelem = new EntitySingle.Identifier(elem.string);
		rangeAdapt(newelem, elem);
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
	
	
	/*  ---------  DEFINITIONS  --------  */

	public final boolean visit(descent.internal.core.dom.AliasDeclaration elem) {
		DefinitionAlias newelem = new DefinitionAlias();
		definitionAdapt(newelem, elem);
		newelem.target = (Entity) convert(elem.type);
		/*TypeConstraint typeEntRef = (TypeConstraint)convert(elem.type);
		newelem.target = new NoConstraint();
		newelem.target.ents = typeEntRef.ents;
		newelem.target.moduleRoot = typeEntRef.moduleRoot;
		rangeAdapt(newelem.target, typeEntRef);*/
		return endAdapt(newelem);
	}	
	public boolean visit(descent.internal.core.dom.VarDeclaration elem) {
		DefinitionVariable newelem = new DefinitionVariable();
		definitionAdapt(newelem, elem);
		newelem.type = (Entity) convert(elem.type);
		newelem.init = elem.init;
		return endAdapt(newelem);
	}	

	public boolean visit(descent.internal.core.dom.AggregateDeclaration elem) {
		DefinitionAggregate newelem = new DefinitionAggregate();
		definitionAdapt(newelem, elem);
		newelem.members = elem.members;
		return endAdapt(newelem);
	}	

	public boolean visit(descent.internal.core.dom.FuncDeclaration elem) {
		DefinitionFunction newelem = new DefinitionFunction();
		definitionAdapt(newelem, elem);
		newelem.frequire = elem.frequire;
		newelem.fensure = elem.fensure;
		newelem.fbody = elem.fbody;
		//newelem.type = elem.type;
		newelem.templateParameters = elem.templateParameters;
		//assert elem.ident == elem.outId;
		return endAdapt(newelem);
	}		
	
	/*  -------------------------------------  */
	
	public void endVisit(ASTNode element) {
		// TODO Auto-generated method stub
	}
	
}
