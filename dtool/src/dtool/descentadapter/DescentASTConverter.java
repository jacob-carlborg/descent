package dtool.descentadapter;

import java.util.List;

import descent.core.dom.IElement;
import descent.core.domX.ASTNode;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;
import descent.internal.core.dom.Import;
import dtool.dom.ASTElement;
import dtool.dom.DeclarationImport;
import dtool.dom.DeclarationModule;
import dtool.dom.DefinitionAggregate;
import dtool.dom.Definition;
import dtool.dom.DefinitionFunction;
import dtool.dom.DefinitionVariable;
import dtool.dom.EModifiers;
import dtool.dom.EProtection;
import dtool.dom.Module;
import dtool.dom.UnconvertedElement;

public class DescentASTConverter extends ASTVisitor {

	ASTElement ret = null;
	
	private ASTElement[] convertMany(IElement[] children) {
		ASTElement[] rets = new ASTElement[children.length];
		for(int i = 0; i < children.length; ++i) {
			AbstractElement elem = (AbstractElement)children[i];
			//System.out.println("Conv: "+ASTPrinter.trimmedElementName(elem));
			elem.accept(this);
			rets[i] = ret;
		}
		return rets;
	}
	
	private void baseAdapt(ASTElement newelem, AbstractElement elem) {
		newelem.startPos = elem.startPos;
		newelem.length = elem.length;
	}

	private boolean endAdapt(ASTElement newelem) {
		ret = newelem;
		return false;
	}
	
	private void definitionAdapt(Definition newelem,
			descent.internal.core.dom.Dsymbol elem) {
		baseAdapt(newelem, elem);

		newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		newelem.modifiers = EModifiers.adaptFromDescent(elem.modifiers); 
		newelem.protection = EProtection.adaptFromDescent(elem.modifiers); 
	}

	public ASTNode convert(descent.core.domX.AbstractElement elem) {
		elem.accept(this);
		return ret;
	}
	
	/*public Module adapt(descent.internal.core.dom.Module elem) {
		return (Module) adapt(elem);
	}*/
	

	/*  =======================================================  */

	public boolean visit(ASTNode elem) {
		// elem must be ASTElement (already converted)
		return endAdapt((ASTElement) elem);
	}
	
	public boolean visit(AbstractElement elem) {
		UnconvertedElement newelem = new UnconvertedElement(elem);
		baseAdapt(newelem, elem);
		return endAdapt(newelem);
	}

	public boolean visit(descent.internal.core.dom.Module elem) {
		Module newelem = new Module();
		baseAdapt(newelem, elem);
		newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		newelem.md = (DeclarationModule) convert(elem.md);
		newelem.members = convertMany(elem.getDeclarationDefinitions());
		return endAdapt(newelem);
	}
	
	public boolean visit(descent.internal.core.dom.ModuleDeclaration elem) {
		DeclarationModule newelem = new DeclarationModule();
		baseAdapt(newelem, elem);
		newelem.packages = elem.packages;
		newelem.qName = elem.qName;
		return endAdapt(newelem);
	}
	
	public boolean visit(descent.internal.core.dom.ImportDeclaration elem) {
		DeclarationImport newelem = new DeclarationImport();
		baseAdapt(newelem, elem);
		newelem.imports = (List) elem.imports;
		return endAdapt(newelem);
	}
	
	/*  ---------  DEFINITIONS  --------  */

	public boolean visit(descent.internal.core.dom.AggregateDeclaration elem) {
		DefinitionAggregate newelem = new DefinitionAggregate();
		definitionAdapt(newelem, elem);
		//newelem. = elem.;
		newelem.type = elem.type;
		newelem.members = elem.members;
		return endAdapt(newelem);
	}	

	public boolean visit(descent.internal.core.dom.VarDeclaration elem) {
		DefinitionVariable newelem = new DefinitionVariable();
		definitionAdapt(newelem, elem);
		newelem.type = elem.type;
		newelem.init = elem.init;
		return endAdapt(newelem);
	}	

	public boolean visit(descent.internal.core.dom.FuncDeclaration elem) {
		DefinitionFunction newelem = new DefinitionFunction();
		definitionAdapt(newelem, elem);
		newelem.frequire = elem.frequire;
		newelem.fensure = elem.fensure;
		newelem.fbody = elem.fbody;
		newelem.type = elem.type;
		newelem.templateParameters = elem.templateParameters;
		//assert elem.ident == elem.outId;
		return endAdapt(newelem);
	}		
	
	/*  -------------------------------------  */
	
	public void endVisit(ASTNode element) {
		// TODO Auto-generated method stub
	}
	
}
