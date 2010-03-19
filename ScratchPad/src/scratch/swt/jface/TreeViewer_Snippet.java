package scratch.swt.jface;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public abstract class TreeViewer_Snippet {
	
	protected static Display display;
	protected static Shell shell;
	
	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
		create(shell);
		runEventLoop();
	}
	
	protected static void runEventLoop() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
	private static void create(Composite parent) {
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
		TreeViewer treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new ContentProvider(model));
		treeViewer.setLabelProvider(new FooLabelProvider());
//		treeViewer.setInput(new Object());
	}
	
	private static FooElement model = new FooElement(null,
			new FooElement[]{
			new FooElement("blah"),
			new FooElement("foo", new FooElement[]{
					new FooElement("nodeA"),
					new FooElement("nodeB"),
					new FooElement("nodeC"),
			}),
			new FooElement("bar"),
			new FooElement("xpto"),
			new FooElement("blah"),
	});
	
	public static class ContentProvider implements ITreeContentProvider {
		
		private final FooElement root;
		
		public ContentProvider(FooElement root) {
			this.root = root;
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			return root.getChildren();
		}
		
		@Override
		public boolean hasChildren(Object element) {
			return ((FooElement) element).getChildren() != null;
		}
		
		@Override
		public Object[] getChildren(Object parentElement) {
			return ((FooElement) parentElement).getChildren();
		}
		
		@Override
		public Object getParent(Object element) {
			return ((FooElement) element).getParent();
		}
		
		@Override
		public void dispose() {
		}
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//assertFail();
		}
		
	}
	
}