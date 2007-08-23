package mmrnmhrm.ui.editor.templates;

import java.io.IOException;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

// TODO: DLTK learn more
public class RubyTemplateAccess {
	// Template
	private static final String CUSTOM_TEMPLATES_KEY = "mmrnmhrm.Templates";
	
	private static TemplateStore fStore;
	private static ContributionContextTypeRegistry fRegistry;
	
	protected static IPreferenceStore getPreferenceStore() { 
		return DeePlugin.getDefault().getPreferenceStore();
	}
	
	public static ContextTypeRegistry getContextTypeRegistry() {
		if (fRegistry == null) {
			fRegistry = new ContributionContextTypeRegistry();
			fRegistry.addContextType(DeeUniversalTemplateContextType.CONTEXT_TYPE_ID);
		}

		return fRegistry;
	}
	
	public static TemplateStore getTemplateStore() {
		if (fStore == null) {
			fStore = new ContributionTemplateStore(getContextTypeRegistry(),
					getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
			try {
				fStore.load();
			} catch (IOException e) {
				// TODO: handle
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return fStore;
	}

}
