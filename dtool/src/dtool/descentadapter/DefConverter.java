package dtool.descentadapter;

import java.util.List;

import dtool.dom.base.ASTNeoNode;
import dtool.dom.base.DeclarationImport;
import dtool.dom.base.Def_EProtection;
import dtool.dom.base.Def_Modifiers;
import dtool.dom.base.Definition;
import dtool.dom.base.DefinitionAggregate;
import dtool.dom.base.DefinitionAlias;
import dtool.dom.base.DefinitionFunction;
import dtool.dom.base.DefinitionVariable;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;
import dtool.dom.base.Module;
import dtool.dom.base.Module.DeclarationModule;

/**
 * Converts from DMD's AST to a proper DOM AST ("Neo AST")
 * FIXME: make package protection?
 */
public class DefConverter extends BaseConverter {


	private void definitionAdapt(Definition newelem, descent.internal.core.dom.Dsymbol elem) {
		rangeAdapt(newelem, elem);

		newelem.symbol = convertIdentifierToSymbol(elem.ident); 
		newelem.modifiers = Def_Modifiers.adaptFromDescent(elem.modifiers); 
		newelem.protection = Def_EProtection.adaptFromDescent(elem.modifiers); 
	}
	

	/*  =======================================================  */

	public boolean visit(descent.internal.core.dom.Module elem) {
		Module newelem = new Module();
		rangeAdapt(newelem, elem);
		//newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		if(elem.md != null){
			newelem.symbol = convertIdentifierToSymbol(elem.ident); // If there is md there is this
			newelem.md = new DeclarationModule();
			rangeAdapt(newelem.md, elem.md);
			// XXX: Check this
			newelem.md.moduleName = new EntitySingle.Identifier(newelem.symbol.name);
			//newelem.md.moduleName = new SymbolReference(elem.md.qName.toString());
			if(elem.md.packages != null) {
				newelem.md.packages = new EntitySingle.Identifier[elem.md.packages.size()];
				convertMany(newelem.md.packages, elem.md.packages.toArray());
			} else {
				newelem.md.packages = new EntitySingle.Identifier[0];
			}
			
		}
		newelem.members = convertMany(elem.getDeclarationDefinitions());
		return endAdapt(newelem);
	}


	public boolean visit(descent.internal.core.dom.ImportDeclaration elem) {
		DeclarationImport newelem = new DeclarationImport();
		rangeAdapt(newelem, elem);
		newelem.imports = elem.imports;
		newelem.imports.get(0).getSelectiveImports();
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
	
	@SuppressWarnings("unchecked")
	public boolean visit(descent.internal.core.dom.AggregateDeclaration elem) {
		DefinitionAggregate newelem = new DefinitionAggregate();
		definitionAdapt(newelem, elem);
		newelem.members = (List<ASTNeoNode>) elem.members;
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

	
}
