package mmrnmhrm.ui.editor.templates;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import org.eclipse.dltk.ui.templates.ScriptTemplateContextType;
import org.eclipse.dltk.ui.templates.ScriptTemplateVariables;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;

public class DeeUniversalTemplateContextType extends ScriptTemplateContextType {

	public static final String CONTEXT_TYPE_ID = "DeeUniversalTemplateContextType";
	
	private void addGlobalResolvers() {
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Year());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
		addResolver(new ScriptTemplateVariables.File());		
		addResolver(new ScriptTemplateVariables.Language());
	}

	public DeeUniversalTemplateContextType() {
		addGlobalResolvers();
	}

	public DeeUniversalTemplateContextType(String id) {
		super(id);
		addGlobalResolvers();
	}

	public DeeUniversalTemplateContextType(String id, String name) {
		super(id, name);
		addGlobalResolvers();
	}

	@Override
	public ScriptTemplateContext createContext(IDocument document,
			int completionPosition, int length, ISourceModule sourceModule) {
		return new DeeTemplateContext(this, document, completionPosition, length, sourceModule);
	}

}
