package dtool.descentadapter;

import descent.core.dom.SelectiveImport;
import descent.core.domX.ASTRangeLessNode;
import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AggregateDeclaration;
import descent.internal.compiler.parser.AnonymousAggregateDeclaration;
import descent.internal.compiler.parser.ArrayScopeSymbol;
import descent.internal.compiler.parser.BoolExp;
import descent.internal.compiler.parser.ClassInfoDeclaration;
import descent.internal.compiler.parser.ComplexExp;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.Declaration;
import descent.internal.compiler.parser.DelegateExp;
import descent.internal.compiler.parser.DotExp;
import descent.internal.compiler.parser.DotTemplateExp;
import descent.internal.compiler.parser.DotTypeExp;
import descent.internal.compiler.parser.DotVarExp;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncAliasDeclaration;
import descent.internal.compiler.parser.HaltExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.LabelDsymbol;
import descent.internal.compiler.parser.ModifierDeclaration;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.ModuleInfoDeclaration;
import descent.internal.compiler.parser.Package;
import descent.internal.compiler.parser.RemoveExp;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.SymOffExp;
import descent.internal.compiler.parser.TemplateExp;
import descent.internal.compiler.parser.ThisDeclaration;
import descent.internal.compiler.parser.Tuple;
import descent.internal.compiler.parser.TupleDeclaration;
import descent.internal.compiler.parser.TupleExp;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeClass;
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.TypeInfoArrayDeclaration;
import descent.internal.compiler.parser.TypeInfoAssociativeArrayDeclaration;
import descent.internal.compiler.parser.TypeInfoClassDeclaration;
import descent.internal.compiler.parser.TypeInfoDeclaration;
import descent.internal.compiler.parser.TypeInfoDelegateDeclaration;
import descent.internal.compiler.parser.TypeInfoEnumDeclaration;
import descent.internal.compiler.parser.TypeInfoFunctionDeclaration;
import descent.internal.compiler.parser.TypeInfoInterfaceDeclaration;
import descent.internal.compiler.parser.TypeInfoPointerDeclaration;
import descent.internal.compiler.parser.TypeInfoStaticArrayDeclaration;
import descent.internal.compiler.parser.TypeInfoStructureDeclaration;
import descent.internal.compiler.parser.TypeInfoTypedefDeclaration;
import descent.internal.compiler.parser.TypeQualified;
import descent.internal.compiler.parser.TypeTuple;
import descent.internal.compiler.parser.TypeTupleDelegateDeclaration;
import descent.internal.compiler.parser.TypeTypedef;
import descent.internal.compiler.parser.UnrolledLoopStatement;
import descent.internal.compiler.parser.VarExp;
import descent.internal.compiler.parser.WithScopeSymbol;

public abstract class CoreConverter extends ASTCommonConverter {

	
	public boolean visit(BoolExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(DotExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(ModifierDeclaration node) {
		return assertFailFAKENODE();	
	}

	public boolean visit(DotTemplateExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(ComplexExp node) {
		return assertFailFAKENODE();
	}


	public boolean visit(DotVarExp node) {
		return assertFailFAKENODE();
	}

	public boolean visit(DsymbolExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(TupleExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(DotTypeExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(AnonymousAggregateDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(LabelDsymbol node) {
		return assertFailFAKENODE();
	}


	public boolean visit(FuncAliasDeclaration node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(ArrayScopeSymbol node) {
		return assertFailFAKENODE();
	}

	public boolean visit(ClassInfoDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(DelegateExp node) {
		return assertFailFAKENODE();
	}



	public boolean visit(TupleDeclaration node) {
		return assertFailFAKENODE();
	}
	public boolean visit(TemplateExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(ThisDeclaration node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(Tuple node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(TypeTypedef node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(TypeClass node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeEnum node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeTuple node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(descent.internal.compiler.parser.TypeStruct elem) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(RemoveExp node) {
		return assertFailFAKENODE();
	}

	public boolean visit(SymOffExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(WithScopeSymbol node) {
		return assertFailFAKENODE();
	}

	public boolean visit(VarExp node) {
		return assertFailFAKENODE();
	}
	public boolean visit(HaltExp node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(UnrolledLoopStatement node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(ModuleInfoDeclaration node) {
		return assertFailFAKENODE();
	}

	
	public boolean visit(TypeTupleDelegateDeclaration node) {
		return assertFailFAKENODE();
	}
	
	public boolean visit(TypeInfoArrayDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoAssociativeArrayDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoClassDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoDelegateDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoEnumDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoFunctionDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoInterfaceDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoPointerDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoStaticArrayDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoStructureDeclaration node) {
		return assertFailFAKENODE();
	}

	public boolean visit(TypeInfoTypedefDeclaration node) {
		return assertFailFAKENODE();
	}


	/*  =======================================================  */

	public boolean visit(ASTNode elem) {
		return assertFailABSTRACT_NODE();
	}
	public void endVisit(ASTNode node) {
	}
	
	public boolean visit(ASTDmdNode elem) {
		return assertFailABSTRACT_NODE();
	}
	
	public boolean visit(ASTRangeLessNode elem) {
		return assertFailABSTRACT_NODE();
	}


	public boolean visit(Dsymbol elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(Declaration elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(Initializer elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(AggregateDeclaration elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(Statement elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(Type elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(Expression elem) {
		return assertFailABSTRACT_NODE();
	}


	public boolean visit(ModuleDeclaration elem) {
		return assertFailABSTRACT_NODE();
	}
	public boolean visit(SelectiveImport elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(ScopeDsymbol elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(TypeQualified elem) {
		return assertFailABSTRACT_NODE();
	}

	public boolean visit(Package elem) {
		return assertFailABSTRACT_NODE();
	}
	
	public boolean visit(Condition node) {
		return assertFailABSTRACT_NODE();
	}

}