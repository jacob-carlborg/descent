package mmrnmhrm.core.dltk.search;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.search.matching.PatternLocator;
import org.eclipse.dltk.internal.core.search.matching.FieldPattern;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;
import org.eclipse.dltk.internal.core.search.matching.MethodPattern;
import org.eclipse.dltk.internal.core.search.matching.QualifiedTypeDeclarationPattern;
import org.eclipse.dltk.internal.core.search.matching.TypeDeclarationPattern;

import dtool.dom.definitions.Definition;

public class NeoTypeDefinitionLocator extends PatternLocator {
	protected TypeDeclarationPattern pattern; // can be a
												// QualifiedTypeDeclarationPattern
	public char[] simpleName;
	public char[] pkg;
	public char[][] enclosingTypeNames;

	
	public NeoTypeDefinitionLocator(TypeDeclarationPattern pattern) {
		super(pattern);
		this.pattern = pattern;
		this.simpleName = pattern.simpleName;
	}

	public NeoTypeDefinitionLocator(FieldPattern pattern) {
		super(pattern);
		//this.simpleName = pattern.getIndexKey();
	}

	public NeoTypeDefinitionLocator(MethodPattern pattern) {
		super(pattern);
		this.simpleName = pattern.declaringSimpleName;
	}

	@SuppressWarnings("restriction")
	public int match(ASTNode node, MatchingNodeSet nodeSet) {
		if(node instanceof Definition)
			return match((Definition) node, nodeSet);
		return IMPOSSIBLE_MATCH;
	}
	
	@SuppressWarnings("restriction")
	public int match(Definition node, MatchingNodeSet nodeSet) {
		if(simpleName == null 
				|| matchesName(simpleName, node.getName().toCharArray())) {
			
			//	fully qualified name
			if (this.pattern instanceof QualifiedTypeDeclarationPattern) {
//				QualifiedTypeDeclarationPattern qualifiedPattern = (QualifiedTypeDeclarationPattern) this.pattern;
//				return resolveLevelForType(qualifiedPattern.simpleName, qualifiedPattern.qualification, node);
			} else {
				char[] enclosingTypeName = this.pattern.enclosingTypeNames == null ? 
						null : CharOperation.concatWith(this.pattern.enclosingTypeNames, '$');
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
		return "Locator for " + this.pattern.toString(); //$NON-NLS-1$
	}
}
