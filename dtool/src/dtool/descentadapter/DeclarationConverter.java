package dtool.descentadapter;

import util.Assert;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IDebugStatement;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IStaticIfStatement;
import descent.core.dom.IVersionDeclaration;
import descent.core.dom.IVersionStatement;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.DebugSymbol;
import descent.internal.core.dom.TemplateAliasParameter;
import descent.internal.core.dom.TemplateTupleParameter;
import descent.internal.core.dom.TemplateTypeParameter;
import descent.internal.core.dom.TemplateValueParameter;
import dtool.dom.declarations.DebugSymbolDefinition;
import dtool.dom.declarations.DeclarationAlign;
import dtool.dom.declarations.DeclarationConditional;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.declarations.DeclarationInvariant;
import dtool.dom.declarations.DeclarationLinkage;
import dtool.dom.declarations.DeclarationPragma;
import dtool.dom.declarations.DeclarationProtection;
import dtool.dom.declarations.DeclarationStaticAssert;
import dtool.dom.declarations.DeclarationStaticIf;
import dtool.dom.declarations.DeclarationStorageClass;
import dtool.dom.declarations.DeclarationUnitTest;
import dtool.dom.declarations.DeclarationVersion;
import dtool.dom.declarations.DefinitionAggregate;
import dtool.dom.declarations.DefinitionAlias;
import dtool.dom.declarations.DefinitionEnum;
import dtool.dom.declarations.DefinitionFunction;
import dtool.dom.declarations.DefinitionMixin;
import dtool.dom.declarations.DefinitionTemplate;
import dtool.dom.declarations.DefinitionTypedef;
import dtool.dom.declarations.DefinitionVariable;
import dtool.dom.declarations.Parameter;

/**
 * Converts from DMD's AST to a nicer AST ("Neo AST")
 */
abstract class DeclarationConverter extends BaseConverter {


	public boolean visit(IDebugDeclaration element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

	public boolean visit(IVersionDeclaration element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

	public boolean visit(IIftypeDeclaration element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

	public boolean visit(IDebugStatement element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

	public boolean visit(IVersionStatement element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

	public boolean visit(IStaticIfStatement element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}


	/*  =======================================================  */

	public boolean visit(descent.internal.core.dom.Module elem) {
		Assert.fail(); return false;
	}
	
	public boolean visit(descent.internal.core.dom.Import elem) {
		Assert.fail(); return false;
	}
	
	public boolean visit(descent.internal.core.dom.BaseClass elem) {
		Assert.fail(); return false;
	}
	
	public boolean visit(descent.internal.core.dom.Catch elem) {
		Assert.fail(); return false;
	}

	public boolean visit(descent.internal.core.dom.TypeSpecialization elem) {
		Assert.fail(); return false;
	}
	
	public boolean visit(descent.internal.core.dom.TemplateParameter elem) {
		Assert.fail(); return false;
	}

	/*  ---------  Decls  --------  */
	

	public boolean visit(descent.internal.core.dom.AlignDeclaration elem) {
		return endAdapt(new DeclarationAlign(elem));
	}

	public boolean visit(descent.internal.core.dom.ConditionalDeclaration elem) {
		return endAdapt(new DeclarationConditional(elem));
	}

	public boolean visit(descent.internal.core.dom.ImportDeclaration elem) {
		return endAdapt(new DeclarationImport(elem));
	}

	public boolean visit(descent.internal.core.dom.InvariantDeclaration elem) {
		return endAdapt(new DeclarationInvariant(elem));
	}

	public boolean visit(descent.internal.core.dom.LinkDeclaration elem) {
		return endAdapt(new DeclarationLinkage(elem));
	}

	public boolean visit(descent.internal.core.dom.PragmaDeclaration elem) {
		return endAdapt(new DeclarationPragma(elem));
	}

	public boolean visit(descent.internal.core.dom.ProtDeclaration elem) {
		return endAdapt(new DeclarationProtection(elem));
	}

	public boolean visit(descent.internal.core.dom.StaticAssert elem) {
		return endAdapt(new DeclarationStaticAssert(elem));
	}

	public boolean visit(descent.internal.core.dom.StaticIfDeclaration elem) {
		return endAdapt(new DeclarationStaticIf(elem));
	}

	public boolean visit(descent.internal.core.dom.StorageClassDeclaration elem) {
		return endAdapt(new DeclarationStorageClass(elem));
	}

	public boolean visit(descent.internal.core.dom.UnitTestDeclaration elem) {
		return endAdapt(new DeclarationUnitTest(elem));
	}

	public boolean visit(descent.internal.core.dom.VersionSymbol elem) {
		return endAdapt(new DeclarationVersion(elem));
	}
	
	
	/*  ---------  DEFINITIONS  --------  */

	public final boolean visit(descent.internal.core.dom.AliasDeclaration elem) {
		return endAdapt(new DefinitionAlias(elem));
	}	

	
	public boolean visit(descent.internal.core.dom.TemplateDeclaration elem) {
		return endAdapt(new DefinitionTemplate(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.TemplateMixin elem) {
		return endAdapt(new DefinitionMixin(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.TypedefDeclaration elem) {
		return endAdapt(new DefinitionTypedef(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.VarDeclaration elem) {
		return endAdapt(new DefinitionVariable(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.EnumDeclaration elem) {
		return endAdapt(new DefinitionEnum(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.EnumMember elem) {
		return endAdapt(new DefinitionEnum.EnumMember(elem));
	}

	/* agregates */
	
	
	public boolean visit(descent.internal.core.dom.ClassDeclaration elem) {
		return endAdapt(new DefinitionAggregate(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.InterfaceDeclaration elem) {
		return endAdapt(new DefinitionAggregate(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.StructDeclaration elem) {
		return endAdapt(new DefinitionAggregate(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.UnionDeclaration elem) {
		return endAdapt(new DefinitionAggregate(elem));
	}	
	
	
	/* --- func --- */

	public boolean visit(descent.internal.core.dom.FuncDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(Argument elem) {
		return endAdapt(new Parameter(elem));
	}
	

	public boolean visit(descent.internal.core.dom.CtorDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(descent.internal.core.dom.DeleteDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(descent.internal.core.dom.DtorDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(descent.internal.core.dom.NewDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(descent.internal.core.dom.StaticCtorDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}	
	public boolean visit(descent.internal.core.dom.StaticDtorDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	
	/* ---- other ---- */
	
	public boolean visit(DebugSymbol elem) {
		return endAdapt(new DebugSymbolDefinition(elem));
	}

	
	public boolean visit(TemplateAliasParameter element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

	public boolean visit(TemplateTupleParameter element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

	public boolean visit(TemplateTypeParameter element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

	public boolean visit(TemplateValueParameter element) {
		Assert.fail();
		// TODO Auto-generated method stub
		return false;
	}

}
