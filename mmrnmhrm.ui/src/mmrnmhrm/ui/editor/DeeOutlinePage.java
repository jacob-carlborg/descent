package mmrnmhrm.ui.editor;

import melnorme.miscutil.tree.IElement;
import melnorme.util.ui.swt.SWTUtil2;
import mmrnmhrm.core.dltk.ModelUtil;
import mmrnmhrm.ui.editor.outline.DeeOutlineContentProvider;
import mmrnmhrm.ui.editor.outline.DeeOutlineLabelProvider;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.definitions.Module;

public class DeeOutlinePage extends ScriptOutlinePage {

	public DeeOutlinePage(ScriptEditor editor, IPreferenceStore store) {
		super(editor, store);
	}

	
	private final class DeeOutlinePageContentProvider extends ChildrenProvider {
		private final class OutlineElementChangedListener implements
				IElementChangedListener {
			public void elementChanged(org.eclipse.dltk.core.ElementChangedEvent event) {
				SWTUtil2.runInSWTThread(new Runnable() {
					public void run() {
						getControl().setRedraw(false);
						fOutlineViewer.refresh();
						fOutlineViewer.expandAll();
						getControl().setRedraw(true);
					}
				});
			}
		}

		@Override
		public Object[] getChildren(Object element) {
			if(element instanceof Module 
					|| DeeOutlineContentProvider.isDeclarationWithDefUnits(element)) {
				IASTNode node = (IASTNode) element;
				return DeeOutlineContentProvider.filterElements(node.getChildren());
			}
			return super.getChildren(element);
		}

		@Override
		public Object[] getElements(Object parent) {
			if(parent instanceof ISourceModule) {
				ISourceModule sourceModule = (ISourceModule) parent;
				ModuleDeclaration moduleDec = ModelUtil.parseModule(sourceModule);
				if(moduleDec != null)
					return ModelUtil.getNeoASTModule(moduleDec).getChildren();
			}
				
			return super.getElements(parent);
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) {
			boolean isCU = (newInput instanceof ISourceModule);

			if (isCU && fListener == null) {
				fListener = new OutlineElementChangedListener();
				DLTKCore.addElementChangedListener(fListener);
			} else if (!isCU && fListener != null) {
				DLTKCore.removeElementChangedListener(fListener);
				fListener = null;
			}
		}
		
	}

	public static class DeeOutlineLabelDecorator extends DeeOutlineLabelProvider
		implements ILabelDecorator {

		public Image decorateImage(Image image, Object element) {
			if(element instanceof IElement)
				return getImage(element);
			return null;
		}

		public String decorateText(String text, Object element) {
			if(element instanceof IElement)
				return getText(element);
			return null;
		}
	}
	
	private IElementChangedListener fListener;
	
	protected ILabelDecorator getLabelDecorator() {
		return new DeeOutlineLabelDecorator(); 
	}
	
	@Override
	public void createControl(Composite parent) {
		
		super.createControl(parent);
		fOutlineViewer.setComparator(null);
		fOutlineViewer.setContentProvider(new DeeOutlinePageContentProvider());
		fOutlineViewer.setLabelProvider(new DeeOutlineLabelDecorator());
		fOutlineViewer.expandAll();
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	
	
	//TODO convert
/*
	protected void registerSpecialToolbarActions(IActionBars actionBars) {
		// TODO: help support

		IToolBarManager toolBarManager = actionBars.getToolBarManager();

		MemberFilterActionGroup fMemberFilterActionGroup = new MemberFilterActionGroup(
				fOutlineViewer, fStore); //$NON-NLS-1$

		String title, helpContext;
		ArrayList actions = new ArrayList(3);

		// Hide variables
		title = ActionMessages.MemberFilterActionGroup_hide_variables_label;

		helpContext = "";// IDLTKHelpContextIds.FILTER_FIELDS_ACTION;
		MemberFilterAction hideVariables = new MemberFilterAction(
				fMemberFilterActionGroup, title, new ModelElementFilter(
						IModelElement.FIELD), helpContext, true);
		hideVariables
				.setDescription(ActionMessages.MemberFilterActionGroup_hide_variables_description);
		hideVariables
				.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_variables_tooltip);
		DLTKPluginImages.setLocalImageDescriptors(hideVariables,
				"filter_fields.gif"); //$NON-NLS-1$
		actions.add(hideVariables);

		// Hid functions
		title = ActionMessages.MemberFilterActionGroup_hide_functions_label;
		helpContext = "";// IDLTKHelpContextIds.FILTER_STATIC_ACTION;
		MemberFilterAction hideProcedures = new MemberFilterAction(
				fMemberFilterActionGroup, title, new ModelElementFilter(
						IModelElement.METHOD), helpContext, true);
		hideProcedures
				.setDescription(ActionMessages.MemberFilterActionGroup_hide_functions_description);
		hideProcedures
				.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_functions_tooltip);
		// TODO: add correct icon
		DLTKPluginImages.setLocalImageDescriptors(hideProcedures,
				"filter_methods.gif"); //$NON-NLS-1$
		actions.add(hideProcedures);

		// Hide classes
		title = ActionMessages.MemberFilterActionGroup_hide_classes_label;
		helpContext = "";// IDLTKHelpContextIds.FILTER_PUBLIC_ACTION;
		MemberFilterAction hideNamespaces = new MemberFilterAction(
				fMemberFilterActionGroup, title, new ModelElementFilter(
						IModelElement.TYPE), helpContext, true);
		hideNamespaces
				.setDescription(ActionMessages.MemberFilterActionGroup_hide_classes_description);
		hideNamespaces
				.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_classes_tooltip);
		DLTKPluginImages.setLocalImageDescriptors(hideNamespaces,
				"filter_classes.gif"); //$NON-NLS-1$
		actions.add(hideNamespaces);

		// Adding actions to toobar
		MemberFilterAction[] fFilterActions = (MemberFilterAction[]) actions
				.toArray(new MemberFilterAction[actions.size()]);

		fMemberFilterActionGroup.setActions(fFilterActions);
		fMemberFilterActionGroup.contributeToToolBar(toolBarManager);
	}*/

	
}
