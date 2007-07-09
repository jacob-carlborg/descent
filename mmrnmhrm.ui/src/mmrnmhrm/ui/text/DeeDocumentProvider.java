package mmrnmhrm.ui.text;


import org.eclipse.ui.editors.text.ForwardingDocumentProvider;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class DeeDocumentProvider extends TextFileDocumentProvider {

	
	public DeeDocumentProvider() {

		IDocumentProvider provider= new TextFileDocumentProvider();
		provider= new ForwardingDocumentProvider(IDeePartitions.DEE_PARTITIONING, new LangDocumentSetupParticipant(), provider);
		setParentDocumentProvider(provider);
		
		/*
		fGlobalAnnotationModelListener= new GlobalAnnotationModelListener();
		fPropertyListener= new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (HANDLE_TEMPORARY_PROBLEMS.equals(event.getProperty()))
					enableHandlingTemporaryProblems();
			}
		};
		JavaPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fPropertyListener);
		*/
	}


	
}