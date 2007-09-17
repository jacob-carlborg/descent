package mmrnmhrm.core.dltk.search;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.search.matching.PatternLocator;
import org.eclipse.dltk.internal.core.search.matching.FieldPattern;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;
import org.eclipse.dltk.internal.core.search.matching.MethodPattern;
import org.eclipse.dltk.internal.core.search.matching.QualifiedTypeDeclarationPattern;
import org.eclipse.dltk.internal.core.search.matching.TypeDeclarationPattern;
import org.eclipse.dltk.internal.core.search.matching.TypeReferencePattern;

import dtool.ast.definitions.Definition;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.references.NamedReference;

public class DeeNeoPatternMatcher extends PatternLocator {
												
	public char[] simpleName;
	public char[] pkg;
	public char[][] enclosingTypeNames;
	
	public boolean matchTypes;
	public boolean matchVars;
	public boolean matchFunctions;

	protected TypeDeclarationPattern typeDecPattern; 
	protected TypeReferencePattern typeRefPattern;
	
	public boolean matchReferences;
	public boolean matchDefinitions;
	
	public DeeNeoPatternMatcher(TypeDeclarationPattern pattern) {
		super(pattern);
		this.typeDecPattern = pattern;
		this.simpleName = pattern.simpleName;
		matchTypes = true;
		matchDefinitions = true;
	}

	public DeeNeoPatternMatcher(TypeReferencePattern pattern) {
		super(pattern);
		this.typeRefPattern = pattern;
		this.simpleName = pattern.getIndexKey();
		matchTypes = true;
		matchReferences = true;
	}
	
	public DeeNeoPatternMatcher(FieldPattern pattern) {
		super(pattern);
		this.simpleName = pattern.getIndexKey();
		matchVars = true;
		matchDefinitions = true;
		matchReferences = true;
	}

	public DeeNeoPatternMatcher(MethodPattern pattern) {
		super(pattern);
		this.simpleName = pattern.declaringSimpleName;
		matchFunctions = true;
		matchDefinitions = true;
		matchReferences = true;
	}


	@SuppressWarnings("restriction")
	public int match(ASTNode node, MatchingNodeSet nodeSet) {
		if(matchDefinitions && node instanceof DefinitionAlias)
			return match((Definition) node, nodeSet);
		if(matchDefinitions && matchTypes && node instanceof DefinitionAggregate)
			return match((Definition) node, nodeSet);
		if(matchDefinitions && matchFunctions && node instanceof DefinitionFunction)
			return matchSimple((Definition) node, nodeSet);
		if(matchDefinitions && matchVars && node instanceof DefinitionVariable)
			return matchSimple((Definition) node, nodeSet);
		
		if(matchReferences && node instanceof NamedReference)
			return matchSimple((NamedReference) node, nodeSet);
		if(matchReferences && node instanceof NamedReference)
			return matchSimple((NamedReference) node, nodeSet);
		if(matchReferences && node instanceof NamedReference)
			return matchSimple((NamedReference) node, nodeSet);
		return IMPOSSIBLE_MATCH;
	}
	
	
	//@SuppressWarnings("restriction")
	public int matchSimple(Definition node, MatchingNodeSet nodeSet) {
		if(simpleName == null 
				|| matchesName(simpleName, node.getName().toCharArray())) {
			
			return nodeSet.addMatch(node, ACCURATE_MATCH);
		}
		return IMPOSSIBLE_MATCH;
	}
	
	public int matchSimple(NamedReference node, MatchingNodeSet nodeSet) {
		if(simpleName == null 
				|| matchesName(simpleName, node.toStringAsElement().toCharArray())) {
			
			return nodeSet.addMatch(node, ACCURATE_MATCH);
		}
		return IMPOSSIBLE_MATCH;
	}
	
	
	@SuppressWarnings("restriction")
	public int match(Definition node, MatchingNodeSet nodeSet) {
		if(simpleName == null 
				|| matchesName(simpleName, node.getName().toCharArray())) {
			
			//	fully qualified name
			if (this.typeDecPattern instanceof QualifiedTypeDeclarationPattern) {
//				QualifiedTypeDeclarationPattern qualifiedPattern = (QualifiedTypeDeclarationPattern) this.pattern;
//				return resolveLevelForType(qualifiedPattern.simpleName, qualifiedPattern.qualification, node);
			} else 
			if(typeDecPattern != null)
			{
				char[] enclosingTypeName = this.typeDecPattern.enclosingTypeNames == null ? 
						null : CharOperation.concatWith(this.typeDecPattern.enclosingTypeNames, '$');
				//char[] enclosingNodeTypeName = node.getEnclosingTypeName().toCharArray();
				char[] enclosingNodeTypeName = null;
				if(!matchesName(enclosingTypeName, enclosingNodeTypeName)) {
					return IMPOSSIBLE_MATCH;
				}
			}
			return nodeSet.addMatch(node, ACCURATE_MATCH);
		}
		return IMPOSSIBLE_MATCH;
	}

	public String toString() {
		return "Locator for " + this.typeDecPattern.toString(); //$NON-NLS-1$
	}
}
