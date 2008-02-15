/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.internal.ui.javaeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;

import descent.core.IJavaElement;
import descent.core.dom.ASTNode;
import descent.core.dom.Block;
import descent.core.dom.BooleanLiteral;
import descent.core.dom.CharacterLiteral;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConditionalDeclaration;
import descent.core.dom.ConditionalStatement;
import descent.core.dom.DebugDeclaration;
import descent.core.dom.DebugStatement;
import descent.core.dom.Declaration;
import descent.core.dom.Expression;
import descent.core.dom.GenericVisitor;
import descent.core.dom.IftypeDeclaration;
import descent.core.dom.IftypeStatement;
import descent.core.dom.NumberLiteral;
import descent.core.dom.SimpleName;
import descent.core.dom.Statement;
import descent.core.dom.StaticIfDeclaration;
import descent.core.dom.StaticIfStatement;
import descent.core.dom.VersionDeclaration;
import descent.core.dom.VersionStatement;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.SemanticHighlightingManager.HighlightedPosition;
import descent.internal.ui.javaeditor.SemanticHighlightingManager.Highlighting;
import descent.internal.ui.text.java.IJavaReconcilingListener;


/**
 * Semantic highlighting reconciler - Background thread implementation.
 *
 * @since 3.0
 */
public class SemanticHighlightingReconciler implements IJavaReconcilingListener, ITextInputListener {

	/**
	 * Collects positions from the AST.
	 */
	private class PositionCollector extends GenericVisitor {

		/** The semantic token */
		private SemanticToken fToken= new SemanticToken();

		/*
		 * @see descent.internal.corext.dom.GenericVisitor#visitNode(descent.core.dom.ASTNode)
		 */
		protected boolean visitNode(ASTNode node) {
			if ((node.getFlags() & ASTNode.MALFORMED) == ASTNode.MALFORMED) {
				retainPositions(node.getStartPosition(), node.getLength());
				return false;
			}
			return true;
		}
		
		/*
		 * @see descent.core.dom.ASTVisitor#visit(descent.core.dom.BooleanLiteral)
		 */
		public boolean visit(BooleanLiteral node) {
			return visitLiteral(node);
		}
		
		/*
		 * @see descent.core.dom.ASTVisitor#visit(descent.core.dom.CharacterLiteral)
		 */
		public boolean visit(CharacterLiteral node) {
			return visitLiteral(node);
		}
		
		/*
		 * @see descent.core.dom.ASTVisitor#visit(descent.core.dom.NumberLiteral)
		 */
		public boolean visit(NumberLiteral node) {
			return visitLiteral(node);
		}
		
		private boolean visitLiteral(Expression node) {
			fToken.update(node);
			for (int i= 0, n= fJobSemanticHighlightings.length; i < n; i++) {
				SemanticHighlighting semanticHighlighting= fJobSemanticHighlightings[i];
				if (fJobHighlightings[i].isEnabled() && semanticHighlighting.consumesLiteral(fToken)) {
					int offset= node.getStartPosition();
					int length= node.getLength();
					if (offset > -1 && length > 0)
						addPosition(offset, length, fJobHighlightings[i]);
					break;
				}
			}
			fToken.clear();
			return false;
		}

		/*
		 * @see descent.core.dom.ASTVisitor#visit(descent.core.dom.SimpleName)
		 */
		public boolean visit(SimpleName node) {
			fToken.update(node);
			for (int i= 0, n= fJobSemanticHighlightings.length; i < n; i++) {
				SemanticHighlighting semanticHighlighting= fJobSemanticHighlightings[i];
				if (fJobHighlightings[i].isEnabled() && semanticHighlighting.consumes(fToken)) {
					int offset= node.getStartPosition();
					int length= node.getLength();
					if (offset > -1 && length > 0)
						addPosition(offset, length, fJobHighlightings[i]);
					break;
				}
			}
			fToken.clear();
			return false;
		}
		
		@Override
		public boolean visit(VersionDeclaration node) {
			return visitConditionalDeclaration(node);
		}
		
		@Override
		public boolean visit(StaticIfDeclaration node) {
			return visitConditionalDeclaration(node);
		}
		
		@Override
		public boolean visit(IftypeDeclaration node) {
			return visitConditionalDeclaration(node);
		}
		
		@Override
		public boolean visit(DebugDeclaration node) {
			return visitConditionalDeclaration(node);
		}
		
		private boolean visitConditionalDeclaration(ConditionalDeclaration node) {
			Boolean active = node.isActive();
			if (active == null) {
				return true;
			}
			
			List<Declaration> enabledDeclarations;
			List<Declaration> disabledDeclarations;
			if (active) {
				enabledDeclarations = node.thenDeclarations();
				disabledDeclarations = node.elseDeclarations();
			} else {
				enabledDeclarations = node.elseDeclarations();
				disabledDeclarations = node.thenDeclarations();
			}
			
			if (disabledDeclarations != null && disabledDeclarations.size() > 0) {
				Declaration first = disabledDeclarations.get(0);
				Declaration last = disabledDeclarations.get(disabledDeclarations.size() - 1);
				int offset, length;
				
				// If there's no else, disable everything
//				if (enabledDeclarations == null || enabledDeclarations.size() == 0) {
//					offset = node.getStartPosition();
//					length = node.getLength();
//				} else {
					CompilationUnit root = (CompilationUnit) node.getRoot();
					offset = root.getExtendedStartPosition(first);
					length = root.getExtendedStartPosition(last) + root.getExtendedLength(last) - offset;
//				}
				addPosition(offset, length, fDisabledHighlighting);
			}
			
			if (enabledDeclarations != null) {
				for(Declaration d : enabledDeclarations) {
					d.accept(this);
				}
			}
			
			return false;
		}
		
		@Override
		public boolean visit(VersionStatement node) {
			return visitConditionalStatement(node);
		}
		
		@Override
		public boolean visit(StaticIfStatement node) {
			return visitConditionalStatement(node);
		}
		
		@Override
		public boolean visit(IftypeStatement node) {
			return visitConditionalStatement(node);
		}
		
		@Override
		public boolean visit(DebugStatement node) {
			return visitConditionalStatement(node);
		}
		
		private boolean visitConditionalStatement(ConditionalStatement node) {
			Boolean active = node.isActive();
			if (active == null) {
				return true;
			}
			
			Statement enabledStatement;
			Statement disabledStatement;
			if (active) {
				enabledStatement = node.getThenBody();
				disabledStatement = node.getElseBody();
			} else {
				enabledStatement = node.getElseBody();
				disabledStatement = node.getThenBody();
			}
			
			if (disabledStatement != null) {
				int offset, length;
				
				// If it's a block, it looks nicer if only the statements
				// inside it are shown as disbaled
				if (disabledStatement instanceof Block) {
					List<Statement> disabledStatements = ((Block) disabledStatement).statements();
					if (disabledStatements.size() > 0) {
						Statement first = disabledStatements.get(0);
						Statement last = disabledStatements.get(disabledStatements.size() - 1);
						if (enabledStatement == null) {
							offset = node.getStartPosition();
							length = node.getLength();
						} else {
							CompilationUnit root = (CompilationUnit) node.getRoot();
							offset = root.getExtendedStartPosition(first);
							length = root.getExtendedStartPosition(last) + root.getExtendedLength(last) - offset;
						}
						addPosition(offset, length, fDisabledHighlighting);
					}
				} else {
//					if (enabledStatement == null) {
//						offset = node.getStartPosition();
//						length = node.getLength();
//					} else {
						CompilationUnit root = (CompilationUnit) node.getRoot();
						offset = root.getExtendedStartPosition(disabledStatement);
						length = root.getExtendedLength(disabledStatement);
//					}
					addPosition(offset, length, fDisabledHighlighting);
				}
			}
			
			if (enabledStatement != null) {
				enabledStatement.accept(this);
			}
			
			return false;
		}

		/**
		 * Add a position with the given range and highlighting iff it does not exist already.
		 * @param offset The range offset
		 * @param length The range length
		 * @param highlighting The highlighting
		 */
		private void addPosition(int offset, int length, Highlighting highlighting) {
			boolean isExisting= false;
			// TODO: use binary search
			for (int i= 0, n= fRemovedPositions.size(); i < n; i++) {
				HighlightedPosition position= (HighlightedPosition) fRemovedPositions.get(i);
				if (position == null)
					continue;
				if (position.isEqual(offset, length, highlighting)) {
					isExisting= true;
					fRemovedPositions.set(i, null);
					fNOfRemovedPositions--;
					break;
				}
			}

			if (!isExisting) {
				Position position= fJobPresenter.createHighlightedPosition(offset, length, highlighting);
				fAddedPositions.add(position);
			}
		}

		/**
		 * Retain the positions completely contained in the given range.
		 * @param offset The range offset
		 * @param length The range length
		 */
		private void retainPositions(int offset, int length) {
			// TODO: use binary search
			for (int i= 0, n= fRemovedPositions.size(); i < n; i++) {
				HighlightedPosition position= (HighlightedPosition) fRemovedPositions.get(i);
				if (position != null && position.isContained(offset, length)) {
					fRemovedPositions.set(i, null);
					fNOfRemovedPositions--;
				}
			}
		}
	}

	/** Position collector */
	private PositionCollector fCollector= new PositionCollector();

	/** The Java editor this semantic highlighting reconciler is installed on */
	private JavaEditor fEditor;
	/** The source viewer this semantic highlighting reconciler is installed on */
	private ISourceViewer fSourceViewer;
	/** The semantic highlighting presenter */
	private SemanticHighlightingPresenter fPresenter;
	/** Semantic highlightings */
	private SemanticHighlighting[] fSemanticHighlightings;
	/** Highlightings */
	private Highlighting[] fHighlightings;

	/** Background job's added highlighted positions */
	private List fAddedPositions= new ArrayList();
	/** Background job's removed highlighted positions */
	private List fRemovedPositions= new ArrayList();
	/** Number of removed positions */
	private int fNOfRemovedPositions;

	/** Background job */
	private Job fJob;
	/** Background job lock */
	private final Object fJobLock= new Object();
	/**
	 * Reconcile operation lock.
	 * @since 3.2
	 */
	private final Object fReconcileLock= new Object();
	/**
	 * <code>true</code> if any thread is executing
	 * <code>reconcile</code>, <code>false</code> otherwise.
	 * @since 3.2
	 */
	private boolean fIsReconciling= false;

	/** The semantic highlighting presenter - cache for background thread, only valid during {@link #reconciled(CompilationUnit, boolean, IProgressMonitor)} */
	private SemanticHighlightingPresenter fJobPresenter;
	/** Semantic highlightings - cache for background thread, only valid during {@link #reconciled(CompilationUnit, boolean, IProgressMonitor)} */
	private SemanticHighlighting[] fJobSemanticHighlightings;
	/** Highlightings - cache for background thread, only valid during {@link #reconciled(CompilationUnit, boolean, IProgressMonitor)} */
	private Highlighting[] fJobHighlightings;
	private Highlighting fDisabledHighlighting;

	/*
	 * @see descent.internal.ui.text.java.IJavaReconcilingListener#aboutToBeReconciled()
	 */
	public void aboutToBeReconciled() {
		// Do nothing
	}

	/*
	 * @see descent.internal.ui.text.java.IJavaReconcilingListener#reconciled(CompilationUnit, boolean, IProgressMonitor)
	 */
	public void reconciled(CompilationUnit ast, boolean forced, IProgressMonitor progressMonitor) {
		// ensure at most one thread can be reconciling at any time
		synchronized (fReconcileLock) {
			if (fIsReconciling)
				return;
			else
				fIsReconciling= true;
		}
		fJobPresenter= fPresenter;
		fJobSemanticHighlightings= fSemanticHighlightings;
		fJobHighlightings= fHighlightings;
		
		try {
			if (fJobPresenter == null || fJobSemanticHighlightings == null || fJobHighlightings == null)
				return;
			
			fJobPresenter.setCanceled(progressMonitor.isCanceled());
			
			if (ast == null || fJobPresenter.isCanceled())
				return;
			
			ASTNode[] subtrees= getAffectedSubtrees(ast);
			if (subtrees.length == 0)
				return;
			
			startReconcilingPositions();
			
			if (!fJobPresenter.isCanceled()) {
				long time = System.currentTimeMillis();
				reconcilePositions(subtrees);
				time = System.currentTimeMillis() - time;
				System.out.println("Semantic highlighting took " + time + " milliseconds to complete.");
			}
			
			TextPresentation textPresentation= null;
			if (!fJobPresenter.isCanceled())
				textPresentation= fJobPresenter.createPresentation(fAddedPositions, fRemovedPositions);
			
			if (!fJobPresenter.isCanceled())
				updatePresentation(textPresentation, fAddedPositions, fRemovedPositions);
			
			stopReconcilingPositions();
		} finally {
			fJobPresenter= null;
			fJobSemanticHighlightings= null;
			fJobHighlightings= null;
			synchronized (fReconcileLock) {
				fIsReconciling= false;
			}
		}
	}

	/**
	 * @param node Root node
	 * @return Array of subtrees that may be affected by past document changes
	 */
	private ASTNode[] getAffectedSubtrees(ASTNode node) {
		// TODO: only return nodes which are affected by document changes - would require an 'anchor' concept for taking distant effects into account
		return new ASTNode[] { node };
	}

	/**
	 * Start reconciling positions.
	 */
	private void startReconcilingPositions() {
		fJobPresenter.addAllPositions(fRemovedPositions);
		fNOfRemovedPositions= fRemovedPositions.size();
	}

	/**
	 * Reconcile positions based on the AST subtrees
	 *
	 * @param subtrees the AST subtrees
	 */
	private void reconcilePositions(ASTNode[] subtrees) {
		// FIXME: remove positions not covered by subtrees
		for (int i= 0, n= subtrees.length; i < n; i++)
			subtrees[i].accept(fCollector);
		List oldPositions= fRemovedPositions;
		List newPositions= new ArrayList(fNOfRemovedPositions);
		for (int i= 0, n= oldPositions.size(); i < n; i ++) {
			Object current= oldPositions.get(i);
			if (current != null)
				newPositions.add(current);
		}
		fRemovedPositions= newPositions;
	}

	/**
	 * Update the presentation.
	 *
	 * @param textPresentation the text presentation
	 * @param addedPositions the added positions
	 * @param removedPositions the removed positions
	 */
	private void updatePresentation(TextPresentation textPresentation, List addedPositions, List removedPositions) {
		Runnable runnable= fJobPresenter.createUpdateRunnable(textPresentation, addedPositions, removedPositions);
		if (runnable == null)
			return;

		JavaEditor editor= fEditor;
		if (editor == null)
			return;

		IWorkbenchPartSite site= editor.getSite();
		if (site == null)
			return;

		Shell shell= site.getShell();
		if (shell == null || shell.isDisposed())
			return;

		Display display= shell.getDisplay();
		if (display == null || display.isDisposed())
			return;

		display.asyncExec(runnable);
	}

	/**
	 * Stop reconciling positions.
	 */
	private void stopReconcilingPositions() {
		fRemovedPositions.clear();
		fNOfRemovedPositions= 0;
		fAddedPositions.clear();
	}

	/**
	 * Install this reconciler on the given editor, presenter and highlightings.
	 * @param editor the editor
	 * @param sourceViewer the source viewer
	 * @param presenter the semantic highlighting presenter
	 * @param semanticHighlightings the semantic highlightings
	 * @param highlightings the highlightings
	 */
	public void install(JavaEditor editor, ISourceViewer sourceViewer, SemanticHighlightingPresenter presenter, SemanticHighlighting[] semanticHighlightings, Highlighting[] highlightings, Highlighting disabledHighlighting) {
		fPresenter= presenter;
		fSemanticHighlightings= semanticHighlightings;
		fHighlightings= highlightings;

		fEditor= editor;
		fSourceViewer= sourceViewer;
		fDisabledHighlighting = disabledHighlighting;

		if (fEditor instanceof CompilationUnitEditor) {
			((CompilationUnitEditor)fEditor).addReconcileListener(this);
		} else {
			fSourceViewer.addTextInputListener(this);
			scheduleJob();
		}
	}

	/**
	 * Uninstall this reconciler from the editor
	 */
	public void uninstall() {
		if (fPresenter != null)
			fPresenter.setCanceled(true);

		if (fEditor != null) {
			if (fEditor instanceof CompilationUnitEditor)
				((CompilationUnitEditor)fEditor).removeReconcileListener(this);
			else
				fSourceViewer.removeTextInputListener(this);
			fEditor= null;
		}

		fSourceViewer= null;
		fSemanticHighlightings= null;
		fHighlightings= null;
		fPresenter= null;
	}

	/**
	 * Schedule a background job for retrieving the AST and reconciling the Semantic Highlighting model.
	 */
	private void scheduleJob() {
		final IJavaElement element= fEditor.getInputJavaElement();

		synchronized (fJobLock) {
			final Job oldJob= fJob;
			if (fJob != null) {
				fJob.cancel();
				fJob= null;
			}
			
			if (element != null) {
				fJob= new Job(JavaEditorMessages.SemanticHighlighting_job) {
					protected IStatus run(IProgressMonitor monitor) {
						if (oldJob != null) {
							try {
								oldJob.join();
							} catch (InterruptedException e) {
								JavaPlugin.log(e);
								return Status.CANCEL_STATUS;
							}
						}
						if (monitor.isCanceled())
							return Status.CANCEL_STATUS;
						CompilationUnit ast= JavaPlugin.getDefault().getASTProvider().getAST(element, ASTProvider.WAIT_YES, monitor);
						reconciled(ast, false, monitor);
						synchronized (fJobLock) {
							// allow the job to be gc'ed
							if (fJob == this)
								fJob= null;
						}
						return Status.OK_STATUS;
					}
				};
				fJob.setSystem(true);
				fJob.setPriority(Job.DECORATE);
				fJob.schedule();
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
	 */
	public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		synchronized (fJobLock) {
			if (fJob != null) {
				fJob.cancel();
				fJob= null;
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
	 */
	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
		if (newInput != null)
			scheduleJob();
	}
	
	/**
	 * Refreshes the highlighting.
	 * 
	 * @since 3.2
	 */
	public void refresh() {
		scheduleJob();
	}
}
