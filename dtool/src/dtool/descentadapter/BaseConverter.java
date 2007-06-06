package dtool.descentadapter;

import util.Assert;
import descent.core.domX.AbstractElement;
import descent.internal.core.dom.AggregateDeclaration;
import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.Dsymbol;
import descent.internal.core.dom.Expression;
import descent.internal.core.dom.FuncLiteralDeclaration;
import descent.internal.core.dom.Initializer;
import descent.internal.core.dom.ModuleDeclaration;
import descent.internal.core.dom.QualifiedName;
import descent.internal.core.dom.ScopeDsymbol;
import descent.internal.core.dom.SelectiveImport;
import descent.internal.core.dom.Statement;
import descent.internal.core.dom.Type;
import descent.internal.core.dom.TypeArray;
import descent.internal.core.dom.TypeExp;
import descent.internal.core.dom.TypeInstance;
import descent.internal.core.dom.TypeQualified;
import dtool.dom.ast.ASTNode;
import dtool.dom.base.Entity;
import dtool.dom.base.EntIdentifier;
import dtool.dom.base.EntTemplateInstance;
import dtool.dom.base.TypeDelegate;
import dtool.dom.base.TypeDynArray;
import dtool.dom.base.TypeFunction;
import dtool.dom.base.TypeMapArray;
import dtool.dom.base.TypePointer;
import dtool.dom.base.TypeStaticArray;
import dtool.dom.base.TypeStruct;
import dtool.dom.base.TypeTypeof;
import dtool.dom.expressions.ExpSlice;

/**
 * This class is a mixin. 
 * Do not use it, instead use it's subclass: {@link DeclarationConverter}
 */
abstract class BaseConverter extends ASTCommonConverter {

	/*  =======================================================  */


	public boolean visit(AbstractElement elem) {
		Assert.fail("Error visited abstract class."); return false;
	}

	public boolean visit(Dsymbol elem) {
		Assert.fail("Error visited abstract class."); return false;
	}

	public boolean visit(Declaration elem) {
		Assert.fail("Error visited abstract class."); return false;
	}

	public boolean visit(Initializer elem) {
		Assert.fail("Error visited abstract class."); return false;
	}

	public boolean visit(AggregateDeclaration elem) {
		Assert.fail("Error visited abstract class."); return false;
	}

	public boolean visit(Statement elem) {
		Assert.fail("Error visited abstract class."); return false;
	}

	public boolean visit(Type elem) {
		Assert.fail("Error visited abstract class."); return false;
	}

	public boolean visit(Expression elem) {
		Assert.fail("Error visited abstract class."); return false;
	}


	public boolean visit(ModuleDeclaration elem) {
		Assert.fail("visited supervised class."); return false;
	}
	public boolean visit(SelectiveImport elem) {
		Assert.fail("visited supervised class."); return false;
	}

	public boolean visit(ScopeDsymbol elem) {
		Assert.fail("Error visited abstract class."); return false;
	}


	public boolean visit(TypeArray elem) {
		Assert.fail("Error visited abstract class."); return false;
	}


	public boolean visit(TypeQualified elem) {
		Assert.fail("Error visited abstract class."); return false;
	}


	public boolean visit(QualifiedName elem) {
		Assert.fail("WHAT IS THIS?"); return false;
	}
	
	public boolean visit(FuncLiteralDeclaration elem) {
		Assert.fail("WHAT IS THIS?"); return false;
	}
	
	public boolean visit(TypeExp elem) {
		Assert.fail("WHAT IS THIS?"); return false;
	}

	
	/* ---- Entities Core ---- */
	public boolean visit(descent.internal.core.dom.Identifier elem) {
		return endAdapt(new EntIdentifier(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypeBasic elem) {
		return endAdapt(new EntIdentifier(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypeIdentifier elem) {
		Entity rootent = Entity.convertTypeIdentifierRoot(elem);
		return endAdapt(Entity.convertQualified(rootent, elem));
	}

	public boolean visit(descent.internal.core.dom.TemplateInstance elem) {
		return endAdapt(new EntTemplateInstance(elem));
	}
	
	public boolean visit(TypeInstance elem) {
		Entity rootent = Entity.convertTypeInstanceRoot(elem);
		return endAdapt(Entity.convertQualified(rootent, elem));
	}
	
	
	public boolean visit(descent.internal.core.dom.TypeTypeof elem) {
		Entity rootent = new TypeTypeof(elem);
		
		return endAdapt(Entity.convertQualified(rootent, elem));
	}

	
	public boolean visit(descent.internal.core.dom.TypeAArray elem) {
		return endAdapt(new TypeMapArray(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypeDArray elem) {
		return endAdapt(new TypeDynArray(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypeSArray elem) {
		return endAdapt(new TypeStaticArray(elem));
	}
	
	
	public boolean visit(descent.internal.core.dom.TypeDelegate elem) {
		return endAdapt(new TypeDelegate(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypeFunction elem) {
		return endAdapt(new TypeFunction(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypePointer elem) {
		return endAdapt(new TypePointer(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypeSlice elem) {
		return endAdapt(new ExpSlice(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypeStruct elem) {
		return endAdapt(new TypeStruct(elem));
	}

	
	/*  -------------------------------------  */
	
	public void endVisit(ASTNode elem) {
	}
}