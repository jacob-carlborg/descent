package foo;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import scratch.swt.SWTApp;

public class VirtualTreeTestMain extends SWTApp {


	public static void main(String[] args) {
		new VirtualTreeTestMain().createAndRunApplication();
	}
	
	private static Tree tree;
	private VirtualTreeViewer virtualTreeViewer;

	@Override
	protected void createShellContents() {
		shell.setLayout(new GridLayout(1, false));
		Composite parent = shell;

		createTree(parent);
	
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.setText("Scroll until only 'x's are visible, then click here.");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				virtualTreeViewer.setInput(VirtualTreeController.toggleModel());
				virtualTreeViewer.refreshStructure(0);
			}
		});
		
		button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.setText("Update Correctly");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				virtualTreeViewer.setInput(VirtualTreeController.toggleModel());
				virtualTreeViewer.refreshStructure(1);
			}
		});
		
		VirtualTreeController.activeModel = VirtualTreeController.model1;
		virtualTreeViewer.setInput(VirtualTreeController.activeModel);
		virtualTreeViewer.refreshStructure(1);
	}

	protected void createTree(Composite parent) {
		tree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		LabelProvider labelProvider = new LabelProvider();
		IVirtualTreeContentProvider elementProvider = new IVirtualTreeContentProvider() {
			@Override
			public int getElementCount() {
				return VirtualTreeController.activeModel.length;
			}
		};
		virtualTreeViewer = new VirtualTreeViewer(tree, elementProvider, labelProvider);
		virtualTreeViewer.install();
		
		
		tree.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.index == 0) {
//					System.out.println(">>> DRAWING TREE ix 0");
					TreeItem item = (TreeItem)event.item;
					TreeItem parent = item.getParentItem();
					int index;
					if (parent != null) {
						index = parent.indexOf(item);
					} else {
						index = item.getParent().indexOf(item);
						if(index == 1) {
//							System.out.print("index:" + index + " " + item.getForeground());
							if(item.getForeground().getRGB().blue == 0) {
								System.out.print(" WTF");
							}
//							System.out.println();
						}
					}
				}
			}
		});

	}

}
