package descent.internal.core.search.indexing;

import descent.core.Flags;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.ISourceElementRequestor;
import descent.internal.compiler.SourceElementParser;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.core.CompilerConfiguration;

// TODO JDT stub
public class IndexingParser extends SourceElementParser {
	
	private CompilerConfiguration config;
	
	public IndexingParser(ISourceElementRequestor requestor, CompilerOptions options) {
		super(requestor, options);
		
		config = new CompilerConfiguration();
	}
	
	public Module parseCompilationUnit(descent.internal.compiler.env.ICompilationUnit unit, boolean resolveBindings) {
		char[] contents = unit.getContents();
		Parser parser = new Parser(contents, 0, contents.length, false, false, false, false, getASTlevel(), null, null, false, unit.getFileName());
		parser.nextToken();
		
		module = parser.parseModuleObj();
		module.moduleName = unit.getFullyQualifiedName();
	
		requestor.enterCompilationUnit();
		module.accept(this);
		requestor.exitCompilationUnit(endOf(module));
		
		return module;
	}
	
	@Override
	public boolean visit(Module node) {
		// Report package if missing declaration 
		if (node.md == null) {
			requestor.acceptPackage(0, 0, module.moduleName.toCharArray());
		}
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ConditionalDeclaration node) {
		// Try not to visit version or debug conditional that are not in effect,
		// in order to hide them from completion proposals
		char[] displayString = CharOperation.NO_CHAR;
		int flags = 0;
		switch(node.condition.getConditionType()) {
		case Condition.DEBUG: {
			DebugCondition cond = (DebugCondition) node.condition;
			displayString = cond.toCharArray();
			
			if (cond.ident != null) {
				if (config.debugIdentifiers.containsKey(cond.ident)) {
					return visitTrue(node, flags, displayString);
				} else {
					return visitFalse(node, flags, displayString);
				}
			} else {
				if (cond.level >= config.debugLevel) {
					return visitTrue(node, flags, displayString);
				} else {
					return visitFalse(node, flags, displayString);
				}
			}
		}
		case Condition.IFTYPE: {
			return super.visit(node);
		}
		case Condition.STATIC_IF: {
			return super.visit(node);
		}
		case Condition.VERSION: {
			VersionCondition cond = (VersionCondition) node.condition;
			displayString = cond.toCharArray();
			flags = Flags.AccVersionDeclaration;
			
			if (cond.ident != null) {
				if (config.versionIdentifiers.containsKey(cond.ident)) {
					return visitTrue(node, flags, displayString);
				} else {
					return visitFalse(node, flags, displayString);
				}
			} else {
				if (cond.level >= config.versionLevel) {
					return visitTrue(node, flags, displayString);
				} else {
					return visitFalse(node, flags, displayString);
				}
			}
		}
		}
		
		return super.visit(node);
	}
	
	private boolean visitTrue(ConditionalDeclaration node, long flags, char[] displayString) {
		requestor.enterConditional(startOf(node), getFlags(node, node.modifiers) | flags, displayString, 0);
		
		Dsymbols thenDeclarations = node.decl;
		Dsymbols elseDeclarations = node.elsedecl;
		
		if (thenDeclarations != null && !thenDeclarations.isEmpty()) {
			if (elseDeclarations != null && !elseDeclarations.isEmpty()) {
				requestor.enterConditionalThen(startOf((Dsymbol) thenDeclarations.get(0))); // SEMANTIC
			}
			for(IDsymbol ideclaration : thenDeclarations) {
				Dsymbol declaration = (Dsymbol) ideclaration; // SEMANTIC
				declaration.accept(this);
			}
			if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
				requestor.exitConditionalThen(endOf((Dsymbol) thenDeclarations.get(thenDeclarations.size() - 1))); // SEMANTIC
			}
		}
		
		pushLevelInAttribDeclarationStack();
		return false;
	}

	private boolean visitFalse(ConditionalDeclaration node, long flags, char[] displayString) {
		requestor.enterConditional(startOf(node), getFlags(node, node.modifiers) | flags, displayString, 0);
		
		Dsymbols thenDeclarations = node.decl;
		Dsymbols elseDeclarations = node.elsedecl;
		
		if (thenDeclarations != null && !thenDeclarations.isEmpty()) {
			if (elseDeclarations != null && !elseDeclarations.isEmpty()) {
				requestor.enterConditionalThen(startOf((Dsymbol) thenDeclarations.get(0))); // SEMANTIC
			}
			if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
				requestor.exitConditionalThen(endOf((Dsymbol) thenDeclarations.get(thenDeclarations.size() - 1))); // SEMANTIC
			}
		}
		
		if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
			requestor.enterConditionalElse(startOf((Dsymbol) elseDeclarations.get(0))); // SEMANTIC
			for(IDsymbol ideclaration : elseDeclarations) {
				Dsymbol declaration = (Dsymbol) ideclaration; // SEMANTIC
				declaration.accept(this);
			}
			requestor.exitConditionalElse(endOf((Dsymbol) elseDeclarations.get(elseDeclarations.size() - 1))); // SEMANTIC
		}
		
		pushLevelInAttribDeclarationStack();
		return false;
	}

}
