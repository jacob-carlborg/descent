package mmrnmhrm.ui.editor;

import mmrnmhrm.core.dltk.DeeSourceParser;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.core.runtime.ILog;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ISourceParser;
import org.eclipse.dltk.ui.text.folding.AbstractASTFoldingStructureProvider;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionFunction;

// TODO finish
public class DeeFoldingStructureProvider extends
		AbstractASTFoldingStructureProvider {

	@Override
	protected ILog getLog() {
		return DeePlugin.getInstance().getLog();
	}


	@Override
	protected String getPartition() {
		return DeePartitions.DEE_PARTITIONING;
	}
	
	@Override
	protected String[] getPartitionTypes() {
		return DeePartitions.LEGAL_CONTENT_TYPES;
	}
	
	@Override
	protected String getCommentPartition() {
		// Hum, seems script only wants one comment partition?
		return DeePartitions.DEE_DOCCOMMENT;
	}


	@Override
	protected IPartitionTokenScanner getPartitionScanner() {
		return DeePlugin.getInstance().getTextTools().getPartitionScanner();
	}

	@Override
	protected ISourceParser getSourceParser() {
		return DeeSourceParser.getInstance();
	}

	@Override
	protected boolean initiallyCollapse(ASTNode s,
			FoldingStructureComputationContext ctx) {
		return false;
	}

	@Override
	protected boolean initiallyCollapseComments(
			FoldingStructureComputationContext ctx) {
		return true;
	}
	
	@Override
	protected FoldingASTVisitor getFoldingVisitor(int offset) {
		return new FoldingASTVisitor(offset) {
			@Override
			public boolean visit(ASTNode node) throws Exception {
				if(node instanceof DefinitionAggregate) {
					add(node);
				} else if (node instanceof DefinitionFunction) {
					add(node);
				}
				return super.visit(node);
			}
			
		};
	}

	@Override
	protected boolean mayCollapse(ASTNode node,
			FoldingStructureComputationContext ctx) {
		if(node instanceof DefinitionAggregate) {
			return true;
		} else if(node instanceof DefinitionFunction) {
			return true;
		}
		return false;
	}

}
