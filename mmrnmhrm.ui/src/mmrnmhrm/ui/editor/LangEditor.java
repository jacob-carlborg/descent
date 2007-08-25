package mmrnmhrm.ui.editor;

import melnorme.miscutil.log.Logg;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

public abstract class LangEditor extends AbstractDecoratedTextEditor {
	
	protected static final boolean CODE_ASSIST_DEBUG = true ||
	"true".equalsIgnoreCase(Platform.getDebugOption(
			DeePlugin.PLUGIN_ID+"/debug/ResultCollector"));
	
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
				long time= CODE_ASSIST_DEBUG ? System.currentTimeMillis() : 0;
				String msg= fContentAssistant.showPossibleCompletions();
				if (CODE_ASSIST_DEBUG) {
					long delta= System.currentTimeMillis() - time;
					Logg.main.println("Code Assist (total): " + delta); //$NON-NLS-1$
				}
				setStatusLineErrorMessage(msg);
				return;

			}
			
			super.doOperation(operation);
		}
	}

}