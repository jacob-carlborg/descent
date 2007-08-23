package mmrnmhrm.ui.editor;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ui.EditorUtil;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

public abstract class LangEditor extends AbstractDecoratedTextEditor {
	
	public class AdaptedSourceViewer extends ProjectionViewer {

		public AdaptedSourceViewer(Composite parent, IVerticalRuler ruler,
				IOverviewRuler overviewRuler, boolean showsAnnotationOverview,
				int styles) {
			super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
		}
		
		@Override
		public void doOperation(int operation) {
			/*
			 * XXX: We can get rid of this once the SourceViewer has a way to update the status line
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=133787
			 */
			switch (operation) {
			case CONTENTASSIST_PROPOSALS:
				long time= DeeEditor.CODE_ASSIST_DEBUG ? System.currentTimeMillis() : 0;
				String msg= fContentAssistant.showPossibleCompletions();
				if (DeeEditor.CODE_ASSIST_DEBUG) {
					long delta= System.currentTimeMillis() - time;
					Logg.main.println("Code Assist (total): " + delta); //$NON-NLS-1$
				}
				setStatusLineErrorMessage(msg);
				return;

			}
			
			super.doOperation(operation);
		}
	}
	
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		fAnnotationAccess= getAnnotationAccess();
		fOverviewRuler= createOverviewRuler(getSharedColors());
	
		ISourceViewer viewer = createSourceViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
	
		return viewer;
	}

	private ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, IOverviewRuler overviewRuler,
			boolean overviewRulerVisible, int styles) {
		return new AdaptedSourceViewer(parent, ruler, getOverviewRuler(),
				isOverviewRulerVisible(), styles);
	}


	protected IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
		List<IPreferenceStore> stores= new ArrayList<IPreferenceStore>(2);
	
		//add project scope
		IProject project = EditorUtil.getProject(input);
		if (project != null) {
			// Project scopes not supported yet.
			//stores.add(new EclipsePreferencesAdapter(new ProjectScope(project.getProject()), ActualPlugin.PLUGIN_ID));
		}
	
		stores.add(ActualPlugin.getInstance().getPreferenceStore());
		stores.add(EditorsUI.getPreferenceStore());
		//stores.toArray(a)
		return new ChainedPreferenceStore(stores.toArray(new IPreferenceStore[stores.size()]));
	}
	
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		setPreferenceStore(createCombinedPreferenceStore(input));
	}

	public TextSelection getSelection() {
		return (TextSelection) getSelectionProvider().getSelection();
	}


}