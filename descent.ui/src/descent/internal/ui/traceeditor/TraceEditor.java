package descent.internal.ui.traceeditor;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import descent.core.trace.IFan;
import descent.core.trace.ITrace;
import descent.core.trace.ITraceNode;
import descent.core.trace.TraceParser;
import descent.ui.ISharedImages;
import descent.ui.JavaUI;

public class TraceEditor extends EditorPart {
	
	private final static int COLUMN_FUNCTION_NAME = 0;
	private final static int COLUMN_NUMBER_OF_CALLS = 1;
	private final static int COLUMN_TREE_TIME = 2;
	private final static int COLUMN_FUNCTION_TIME = 3;
	private final static int COLUMN_FUNCTION_TIME_PER_CALL = 4;
	private final static int COLUMN_TICKS = 5;
	private final static int COLUMN_TREE_TICKS = 6;
	
	class TraceContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object element) {
			if (element instanceof ITraceNode) {
				ITraceNode node = (ITraceNode) element;
				IFan[] fanIn = node.getFanIn();
				IFan[] fanOut = node.getFanOut();
				Object[] children = new Object[fanIn.length + fanOut.length];
				System.arraycopy(fanIn, 0, children, 0, fanIn.length);
				System.arraycopy(fanOut, 0, children, fanIn.length, fanOut.length);
				return children;
			}
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof ITraceNode) {
				ITraceNode node = (ITraceNode) element;
				return node.getFanIn().length + node.getFanOut().length > 0;
			}
			return false;
		}

		public Object[] getElements(Object inputElement) {
			return trace.getNodes();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}
	
	class TraceLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof ITraceNode) {
				switch(columnIndex) {
				case COLUMN_FUNCTION_NAME:
					return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PUBLIC);
				}
			} else if (element instanceof IFan) {
				IFan fan = (IFan) element;
				switch(columnIndex) {
				case COLUMN_FUNCTION_NAME:
					if (fan.isIn()) {
						return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_FAN_IN);
					} else {
						return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_FAN_OUT);
					}
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ITraceNode) {
				ITraceNode node = (ITraceNode) element;
				switch(columnIndex) {
				case COLUMN_FUNCTION_NAME:
					return node.getDemangledName();
				case COLUMN_FUNCTION_TIME:
					return String.valueOf(node.getFunctionTime());
				case COLUMN_FUNCTION_TIME_PER_CALL:
					return String.valueOf(node.getFunctionTimePerCall());
				case COLUMN_NUMBER_OF_CALLS:
					return String.valueOf(node.getNumberOfCalls());
				case COLUMN_TICKS:
					return String.valueOf(node.getTicks());
				case COLUMN_TREE_TICKS:
					return String.valueOf(node.getTreeTicks());
				case COLUMN_TREE_TIME:
					return String.valueOf(node.getTreeTime());
				}
			} else if (element instanceof IFan) {
				IFan fan = (IFan) element;
				ITraceNode node = fan.getTraceNode();
				switch(columnIndex) {
				case COLUMN_FUNCTION_NAME:
					 return node.getDemangledName();
				case COLUMN_FUNCTION_TIME:
					return "-";
				case COLUMN_FUNCTION_TIME_PER_CALL:
					return "-";
				case COLUMN_NUMBER_OF_CALLS:
					return String.valueOf(fan.getNumberOfCalls());
				case COLUMN_TICKS:
					return "-";
				case COLUMN_TREE_TICKS:
					return "-";
				case COLUMN_TREE_TIME:
					return "-";
				}
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
		
	}
	
	private TreeViewer viewer;
	private ITrace trace;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof IFileEditorInput)) {
		     throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		
		try {
			InputStream stream = ((IFileEditorInput) input).getFile().getContents();
			InputStreamReader reader = new InputStreamReader(stream);
			
			TraceParser parser = new TraceParser();
			trace = parser.parse(reader);
		} catch (Exception e) {
			 throw new PartInitException(e.getMessage());
		}
		
		setSite(site);
		setInput(input);
	}	

	@SuppressWarnings("deprecation")
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
	    Tree tree = viewer.getTree();
	    
	    new TreeColumn(tree, SWT.LEFT).setText("Function");
	    new TreeColumn(tree, SWT.RIGHT).setText("Number of calls");
	    new TreeColumn(tree, SWT.RIGHT).setText("Tree time");
	    new TreeColumn(tree, SWT.RIGHT).setText("Function time");
	    new TreeColumn(tree, SWT.RIGHT).setText("Function time per call");
	    new TreeColumn(tree, SWT.RIGHT).setText("Ticks");
	    new TreeColumn(tree, SWT.RIGHT).setText("Tree ticks");
	    
	    for (int i = 0, n = tree.getColumnCount(); i < n; i++) {
	    	tree.getColumn(i).pack();
	    }
	    
	    tree.setHeaderVisible(true);
	    tree.setLinesVisible(true);
	    
	    viewer.setContentProvider(new TraceContentProvider());
		viewer.setLabelProvider(new TraceLabelProvider());
		viewer.setInput(trace);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public boolean isSaveAsAllowed() {
		// Not saveable
		return false;
	}
	
	@Override
	public boolean isDirty() {
		// Not saveable
		return false;
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		// Not saveable
	}

	@Override
	public void doSaveAs() {
		// Not saveable
	}

}
