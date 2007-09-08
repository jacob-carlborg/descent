package dtool.dom.ast;

import descent.internal.compiler.parser.ast.IASTVisitor;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Definition;
import dtool.dom.definitions.DefinitionAggregate;
import dtool.dom.definitions.DefinitionAlias;
import dtool.dom.definitions.DefinitionClass;
import dtool.dom.definitions.DefinitionEnum;
import dtool.dom.definitions.DefinitionFunction;
import dtool.dom.definitions.DefinitionTemplate;
import dtool.dom.definitions.DefinitionTypedef;
import dtool.dom.definitions.DefinitionVariable;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.CommonRefNative;
import dtool.dom.references.CommonRefQualified;
import dtool.dom.references.CommonRefSingle;
import dtool.dom.references.RefIdentifier;
import dtool.dom.references.RefTemplateInstance;
import dtool.dom.references.Reference;

public interface IASTNeoVisitor extends IASTVisitor {
	
	void preVisit(ASTNeoNode node);
	void postVisit(ASTNeoNode node);

	boolean visit(ASTNeoNode node);
	void endVisit(ASTNeoNode node);

	/* ---------------------------------- */
	boolean visit(Symbol elem);

	boolean visit(DefUnit elem);
	void endVisit(DefUnit node);
	
	boolean visit(Module elem);
	void endVisit(Module node);

	boolean visit(Definition elem);
	void endVisit(Definition node);

	boolean visit(DefinitionAggregate elem);
	void endVisit(DefinitionAggregate node);

	boolean visit(DefinitionTemplate elem);
	void endVisit(DefinitionTemplate node);

	boolean visit(DefinitionClass elem);
	void endVisit(DefinitionClass node);
	
	
	public boolean visit(DefinitionVariable elem);
	public void endVisit(DefinitionVariable elem);
		
	public boolean visit(DefinitionEnum elem);
	public void endVisit(DefinitionEnum elem);
		
	public boolean visit(DefinitionTypedef elem);
	public void endVisit(DefinitionTypedef elem);
		
	public boolean visit(DefinitionAlias elem);
	public void endVisit(DefinitionAlias elem);
		
	public boolean visit(DefinitionFunction elem);
	public void endVisit(DefinitionFunction elem);

	/* ---------------------------------- */

	boolean visit(Reference elem);
	void endVisit(Reference node);
	
	boolean visit(CommonRefNative elem);
	
	boolean visit(CommonRefQualified elem);
	
	boolean visit(CommonRefSingle elem);

	boolean visit(RefIdentifier elem);

	boolean visit(RefTemplateInstance elem);

	/* ---------------------------------- */
	boolean visit(DeclarationImport elem);

}