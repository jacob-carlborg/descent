package descent.internal.ui.traceeditor;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
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
	
	private class TraceContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object element) {
			if (element instanceof ITraceNode) {
				ITraceNode node = (ITraceNode) element;
				IFan[] fanIn = node.getFanIn();
				IFan[] fanOut = node.getFanOut();
				Object[] children = new Object[fanIn.length + fanOut.length];
				System.arraycopy(fanIn, 0, children, 0, fanIn.length);
				System.arraycopy(fanOut, 0, children, fanIn.length, fanOut.length);
				return children;
			} else if (element instanceof IFan) {
				return getChildren(((IFan) element).getTraceNode());
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
			} else if (element instanceof IFan) {
				return hasChildren(((IFan) element).getTraceNode());
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
	
	private class TraceLabelProvider implements ITableLabelProvider {

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
					return String.valueOf(node.getFunctionTime());
				case COLUMN_FUNCTION_TIME_PER_CALL:
					return String.valueOf(node.getFunctionTimePerCall());
				case COLUMN_NUMBER_OF_CALLS:
					return fan.getNumberOfCalls() + " / " + node.getNumberOfCalls(); //$NON-NLS-1$
				case COLUMN_TICKS:
					return String.valueOf(node.getTicks());
				case COLUMN_TREE_TICKS:
					return String.valueOf(node.getTreeTicks());
				case COLUMN_TREE_TIME:
					return String.valueOf(node.getTreeTime());
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
	
	private class TraceSorter extends ViewerSorter {
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			final Tree tree = ((TreeViewer) viewer).getTree();
			final TreeColumn sortCol = tree.getSortColumn();
			if (sortCol == null) {
				return super.compare(viewer, e1, e2);
			}
			
			final int sortDirection = tree.getSortDirection();
			final int columnIndex = tree.indexOf(sortCol);
			
			int result = 0;
			ITraceNode t1 = null;
			ITraceNode t2 = null;
			IFan f1 = null;
			IFan f2 = null;
			if (e1 instanceof ITraceNode && e2 instanceof ITraceNode) {
				t1 = (ITraceNode) e1;
				t2 = (ITraceNode) e2;
			} else if (e1 instanceof IFan && e2 instanceof IFan) {
				f1 = (IFan) e1;
				f2 = (IFan) e2;
				t1 = f1.getTraceNode();
				t2 = f2.getTraceNode();
			}
			
			switch (columnIndex) {
				case COLUMN_FUNCTION_NAME:
					result = t1.getDemangledName().compareTo(t2.getDemangledName());
					break;
				case COLUMN_FUNCTION_TIME:
					result = compareLongs(t1.getFunctionTime(), t2.getFunctionTime());
					break;
				case COLUMN_FUNCTION_TIME_PER_CALL:
					result = compareLongs(t1.getFunctionTimePerCall(), t2.getFunctionTimePerCall());
					break;
				case COLUMN_NUMBER_OF_CALLS:
					if (f1 != null) {
						result = compareLongs(f1.getNumberOfCalls(), f2.getNumberOfCalls());
					} else {
						result = compareLongs(t1.getNumberOfCalls(), t2.getNumberOfCalls());
					}
					break;
				case COLUMN_TICKS:
					result = compareLongs(t1.getTicks(), t2.getTicks());
					break;
				case COLUMN_TREE_TICKS:
					result = compareLongs(t1.getTreeTicks(), t2.getTreeTicks());
					break;
				case COLUMN_TREE_TIME:
					result = compareLongs(t1.getTreeTime(), t2.getTreeTime());
					break;
			}
			if (sortDirection == SWT.DOWN) {
				return -result;
			} else {
				return result;
			}
		}
		
		private int compareLongs(long e1, long e2) {
			return e1 > e2 ? 1 : (e1 < e2 ? -1 : 0);
		}
		
	}
	
	private class TraceFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof ITraceNode) {
				if (filterText == null) {
					return true;
				} else {
					ITraceNode node = (ITraceNode) element;
					return node.getDemangledName().contains(filterText);
				}
			} else if (element instanceof IFan) {
				IFan fan = (IFan) element;
				if (fan.isIn()) {
					return !filterFanIn;
				} else {
					return !filterFanOut;
				}
			} else {
				return true;
			}
		}
		
	}
	
	private TreeViewer viewer;
	private ITrace trace;
	
	// Filter support
	private String filterText;
	private boolean filterFanIn;
	private boolean filterFanOut;

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
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(4, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setText("Filter:");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		
		final Text text = new Text(group, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filterText = text.getText();
				viewer.refresh();
			}
		});
		
		final Button buttonFanIn = new Button(group, SWT.TOGGLE);
		buttonFanIn.setImage(JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_FAN_IN));
		buttonFanIn.setSelection(true);
		buttonFanIn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		buttonFanIn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				filterFanIn = !buttonFanIn.getSelection();
				viewer.refresh();
			}
			public void widgetSelected(SelectionEvent e) {
				filterFanIn = !buttonFanIn.getSelection();
				viewer.refresh();
			}
		});
		
		final Button buttonFanOut = new Button(group, SWT.TOGGLE);
		buttonFanOut.setImage(JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_FAN_OUT));
		buttonFanOut.setSelection(true);
		buttonFanOut.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		buttonFanOut.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				filterFanOut = !buttonFanOut.getSelection();
				viewer.refresh();
			}
			public void widgetSelected(SelectionEvent e) {
				filterFanOut = !buttonFanOut.getSelection();
				viewer.refresh();
			}
		});
		
		Tree tree = createTreeViewer(group);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		label = new Label(group, SWT.NONE);
		label.setText("Timer Is " + trace.getTicksPerSecond() + " ticks/sec, times are in microsecs");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	private Tree createTreeViewer(Composite parent) {
		viewer = new TreeViewer(parent);		
	    final Tree tree = viewer.getTree();
	    
	    tree.setHeaderVisible(true);
	    tree.setLinesVisible(true);
	    
	    TreeColumn col = new TreeColumn(tree, SWT.LEFT);
		col.setText("Function");
		col.setWidth(300);
		
	    col = new TreeColumn(tree, SWT.RIGHT);
	    col.setText("Number of calls (fan / total)");
	    col.setWidth(140);
	    
	    col = new TreeColumn(tree, SWT.RIGHT);
	    col.setText("Tree time");
	    col.setWidth(60);
	    
	    col = new TreeColumn(tree, SWT.RIGHT);
	    col.setText("Function time");
	    col.setWidth(80);
	    
	    col = new TreeColumn(tree, SWT.RIGHT);
	    col.setText("Function time per call");
	    col.setWidth(120);
	    
	    col = new TreeColumn(tree, SWT.RIGHT);
	    col.setText("Ticks");
	    col.setWidth(60);
	    
	    col = new TreeColumn(tree, SWT.RIGHT);
	    col.setText("Tree ticks");
	    col.setWidth(60);
	    
	    Listener sortListener = new Listener() {
	        public void handleEvent(Event event) {
	        	 final Tree tree = viewer.getTree();
				final TreeColumn sortColumn = tree.getSortColumn();
				final TreeColumn currentColumn = (TreeColumn) event.widget;
				int dir = SWT.UP;
				if (sortColumn == currentColumn) {
					dir = (tree.getSortDirection() == SWT.UP) ? SWT.DOWN
							: SWT.UP;
				} else {
					tree.setSortColumn(currentColumn);
				}
				tree.setSortDirection(dir);
				viewer.refresh();
	        }
	    };
	    
	    for(TreeColumn column : tree.getColumns()) {
	    	column.addListener(SWT.Selection, sortListener);
	    }
	    
	    viewer.setContentProvider(new TraceContentProvider());
		viewer.setLabelProvider(new TraceLabelProvider());
		viewer.setSorter(new TraceSorter());
		viewer.addFilter(new TraceFilter());
		viewer.setInput(trace);
		
		return tree;
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
