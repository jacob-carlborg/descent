package descent.internal.ui.typehierarchy;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.eclipse.ui.keys.KeySequence;
import org.eclipse.ui.keys.SWTKeySupport;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IType;
import descent.core.ITypeHierarchy;
import descent.core.JavaModelException;
import descent.core.Signature;

import descent.ui.JavaElementLabels;
import descent.ui.ProblemsLabelDecorator;
import descent.ui.actions.IJavaEditorActionDefinitionIds;

import descent.internal.corext.util.JavaModelUtil;
import descent.internal.corext.util.Messages;
import descent.internal.corext.util.MethodOverrideTester;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.text.AbstractInformationControl;
import descent.internal.ui.typehierarchy.SuperTypeHierarchyViewer.SuperTypeHierarchyContentProvider;
import descent.internal.ui.typehierarchy.TraditionalHierarchyViewer.TraditionalHierarchyContentProvider;

/**
 * Show hierarchy in light-weight control.
 * 
 * @since 3.0
 */
public class HierarchyInformationControl extends AbstractInformationControl {
	
	private TypeHierarchyLifeCycle fLifeCycle;
	private HierarchyLabelProvider fLabelProvider;
	private KeyAdapter fKeyAdapter;
	
	private Object[] fOtherExpandedElements;
	private TypeHierarchyContentProvider fOtherContentProvider;
	
	private IMethod fFocus; // method to filter for or null if type hierarchy
	private boolean fDoFilter;
	
	private MethodOverrideTester fMethodOverrideTester;

	public HierarchyInformationControl(Shell parent, int shellStyle, int treeStyle) {
		super(parent, shellStyle, treeStyle, IJavaEditorActionDefinitionIds.OPEN_HIERARCHY, true);
		fOtherExpandedElements= null;
		fDoFilter= true;
		fMethodOverrideTester= null;
	}
	
	private KeyAdapter getKeyAdapter() {
		if (fKeyAdapter == null) {
			fKeyAdapter= new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
					KeySequence keySequence = KeySequence.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
					KeySequence[] sequences= getInvokingCommandKeySequences();
					if (sequences == null)
						return;
					
					for (int i= 0; i < sequences.length; i++) {
						if (sequences[i].equals(keySequence)) {
							e.doit= false;
							toggleHierarchy();
							return;
						}
					}
				}
			};			
		}
		return fKeyAdapter;		
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean hasHeader() {
		return true;
	}

	protected Text createFilterText(Composite parent) {
		// text set later
		Text text= super.createFilterText(parent);
		text.addKeyListener(getKeyAdapter());
		return text;
	}	
		
	
	/* (non-Javadoc)
	 * @see descent.internal.ui.text.JavaOutlineInformationControl#createTreeViewer(org.eclipse.swt.widgets.Composite, int)
	 */
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		Tree tree= new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.heightHint= tree.getItemHeight() * 12;
		tree.setLayoutData(gd);

		TreeViewer treeViewer= new TreeViewer(tree);
		treeViewer.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof IType;
			}
		});		
		
		fLifeCycle= new TypeHierarchyLifeCycle(false);

		treeViewer.setSorter(new HierarchyViewerSorter(fLifeCycle));
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

		fLabelProvider= new HierarchyLabelProvider(fLifeCycle);
		fLabelProvider.setFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return hasFocusMethod((IType) element);
			}
		});	

		fLabelProvider.setTextFlags(JavaElementLabels.ALL_DEFAULT | JavaElementLabels.T_POST_QUALIFIED);
		fLabelProvider.addLabelDecorator(new ProblemsLabelDecorator(null));
		treeViewer.setLabelProvider(fLabelProvider);
		
		treeViewer.getTree().addKeyListener(getKeyAdapter());	
		
		return treeViewer;
	}
	
	protected boolean hasFocusMethod(IType type) {
		if (fFocus == null) {
			return true;
		}
		if (type.equals(fFocus.getDeclaringType())) {
			return true;
		}
		
		try {
			IMethod method= findMethod(fFocus, type);
			if (method != null) {
				// check visibility
				IPackageFragment pack= (IPackageFragment) fFocus.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
				if (JavaModelUtil.isVisibleInHierarchy(method, pack)) {
					return true;
				}
			}
		} catch (JavaModelException e) {
			// ignore
			JavaPlugin.log(e);
		}
		return false;			
		
	}
	
	private IMethod findMethod(IMethod filterMethod, IType typeToFindIn) throws JavaModelException {
		IType filterType= filterMethod.getDeclaringType();
		ITypeHierarchy hierarchy= fLifeCycle.getHierarchy();
		
		boolean filterOverrides= JavaModelUtil.isSuperType(hierarchy, typeToFindIn, filterType);
		IType focusType= filterOverrides ? filterType : typeToFindIn;
		
		if (fMethodOverrideTester == null || !fMethodOverrideTester.getFocusType().equals(focusType)) {
			fMethodOverrideTester= new MethodOverrideTester(focusType, hierarchy);
		}

		if (filterOverrides) {
			return fMethodOverrideTester.findOverriddenMethodInType(typeToFindIn, filterMethod);
		} else {
			return fMethodOverrideTester.findOverridingMethodInType(typeToFindIn, filterMethod);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInput(Object information) {
		if (!(information instanceof IJavaElement)) {
			inputChanged(null, null);
			return;
		}
		IJavaElement input= null;
		IMethod locked= null;
		try {
			IJavaElement elem= (IJavaElement) information;
			if (elem.getElementType() == IJavaElement.LOCAL_VARIABLE) {
				elem= elem.getParent();
			}
			
			switch (elem.getElementType()) {
				case IJavaElement.JAVA_PROJECT :
				case IJavaElement.PACKAGE_FRAGMENT_ROOT :
				case IJavaElement.PACKAGE_FRAGMENT :
				case IJavaElement.TYPE :
					input= elem;
					break;
				case IJavaElement.COMPILATION_UNIT :
					input= ((ICompilationUnit) elem).findPrimaryType();
					break;
				case IJavaElement.CLASS_FILE :
					input= ((IClassFile) elem).findPrimaryType();
					break;
				case IJavaElement.METHOD :
					IMethod method= (IMethod) elem;
					if (!method.isConstructor()) {
						locked= method;				
					}
					input= method.getDeclaringType();
					break;
				case IJavaElement.FIELD :
				case IJavaElement.INITIALIZER :
					input= ((IMember) elem).getDeclaringType();
					break;
				case IJavaElement.PACKAGE_DECLARATION :
					input= elem.getParent().getParent();
					break;
				case IJavaElement.IMPORT_DECLARATION :
					IImportDeclaration decl= (IImportDeclaration) elem;
//					if (decl.isOnDemand()) {
//						input= JavaModelUtil.findTypeContainer(decl.getJavaProject(), Signature.getQualifier(decl.getElementName()));
//					} else {
						input= decl.getJavaProject().findType(decl.getElementName());
//					}
					break;
				default :
					JavaPlugin.logErrorMessage("Element unsupported by the hierarchy: " + elem.getClass()); //$NON-NLS-1$
					input= null;
			}
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
		
		super.setTitleText(getHeaderLabel(locked == null ? input : locked));
		try {
			fLifeCycle.ensureRefreshedTypeHierarchy(input, JavaPlugin.getActiveWorkbenchWindow());
		} catch (InvocationTargetException e1) {
			input= null;
		} catch (InterruptedException e1) {
			dispose();
			return;
		}
		IMember[] memberFilter= locked != null ? new IMember[] { locked } : null;
		
		TraditionalHierarchyContentProvider contentProvider= new TraditionalHierarchyContentProvider(fLifeCycle);
		contentProvider.setMemberFilter(memberFilter);
		getTreeViewer().setContentProvider(contentProvider);		
		
		fOtherContentProvider= new SuperTypeHierarchyContentProvider(fLifeCycle);
		fOtherContentProvider.setMemberFilter(memberFilter);
		
		fFocus= locked;
		
		Object[] topLevelObjects= contentProvider.getElements(fLifeCycle);
		if (topLevelObjects.length > 0 && contentProvider.getChildren(topLevelObjects[0]).length > 40) {
			fDoFilter= false;
		} else {
			getTreeViewer().addFilter(new NamePatternFilter());
		}

		Object selection= null;
		if (input instanceof IMember) {
			selection=  input;
		} else if (topLevelObjects.length > 0) {
			selection=  topLevelObjects[0];
		}
		inputChanged(fLifeCycle, selection);
	}
	
	protected void stringMatcherUpdated() {
		if (fDoFilter) {
			super.stringMatcherUpdated(); // refresh the view
		} else {
			selectFirstMatch();
		}
	}
	
	protected void toggleHierarchy() {
		TreeViewer treeViewer= getTreeViewer();
		
		treeViewer.getTree().setRedraw(false);
		
		Object[] expandedElements= treeViewer.getExpandedElements();
		TypeHierarchyContentProvider contentProvider= (TypeHierarchyContentProvider) treeViewer.getContentProvider();
		treeViewer.setContentProvider(fOtherContentProvider);

		treeViewer.refresh();
		if (fOtherExpandedElements != null) {
			treeViewer.setExpandedElements(fOtherExpandedElements);
		} else {
			treeViewer.expandAll();
		}
		
		treeViewer.getTree().setRedraw(true);
		
		fOtherContentProvider= contentProvider;
		fOtherExpandedElements= expandedElements;
		
		updateStatusFieldText();
	}
	
	
	private String getHeaderLabel(IJavaElement input) {
		if (input instanceof IMethod) {
			String[] args= { input.getParent().getElementName(), JavaElementLabels.getElementLabel(input, JavaElementLabels.ALL_DEFAULT) };
			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_methodhierarchy_label, args); 
		} else if (input != null) {
			String arg= JavaElementLabels.getElementLabel(input, JavaElementLabels.DEFAULT_QUALIFIED);
			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_hierarchy_label, arg);	 
		} else {
			return ""; //$NON-NLS-1$
		}
	}
	
	protected String getStatusFieldText() {
		KeySequence[] sequences= getInvokingCommandKeySequences();
		String keyName= ""; //$NON-NLS-1$
		if (sequences != null && sequences.length > 0)
			keyName= sequences[0].format();
		
		if (fOtherContentProvider instanceof TraditionalHierarchyContentProvider) {
			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_toggle_traditionalhierarchy_label, keyName); 
		} else {
			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_toggle_superhierarchy_label, keyName); 
		}
	}
	
	/*
	 * @see descent.internal.ui.text.AbstractInformationControl#getId()
	 */
	protected String getId() {
		return "descent.internal.ui.typehierarchy.QuickHierarchy"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object getSelectedElement() {
		Object selectedElement= super.getSelectedElement();
		if (selectedElement instanceof IType && fFocus != null) {
			IType type= (IType) selectedElement;
			try {
				return findMethod(fFocus, type);
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return selectedElement;
	}
}
