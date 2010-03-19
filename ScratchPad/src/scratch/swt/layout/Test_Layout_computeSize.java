package scratch.swt.layout;

import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;

import scratch.swt.SWTApp;
import scratch.swt.util.SashDragHandler;

import scratch.utils.SWTUtils;

/**
 * This example illustrates that a Control's computeSize method can override the GridData hints
 * for a GridLayout. (Hence any GridData size hints are just that: hints).
 *
 * However, wrapping the Control on a Composite will achieved the desired result:
 * make the hints enforcable, since the Composite computeSize will respect them, even if its children
 * have bigger sizes
 */
public class Test_Layout_computeSize extends SWTApp {
	
	protected Composite parentContainer;
	protected Composite panelContainer;
	protected Sash sash;
	protected Composite mainContainer;
	
	public static void main(String[] args) {
		new Test_Layout_computeSize().createAndRunApplication();
	}

	
	@Override
	protected void createShellContents() {
		shell.setLayout(new FillLayout());
		
		final Composite parentContainer = shell;
		
		Composite canvasParent;
		
		parentContainer.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).spacing(0, 0).create());
		
		if(true) {
			panelContainer = new Composite(parentContainer, SWT.NONE);
			panelContainer.setLayout(GridLayoutFactory.fillDefaults().create());
			
			Text text = new Text(panelContainer, SWT.BORDER);
			text.setLayoutData(GridDataFactory.fillDefaults().minSize(200, 200).hint(200, 200).create());

			canvasParent = panelContainer;
		} else {
			canvasParent = shell;
		}
			
		Canvas canvas = new Canvas(canvasParent, SWT.NONE) {
			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				if(wHint == SWT.DEFAULT) {
					wHint = 80;
				}
				if(hHint == SWT.DEFAULT) {
					hHint = 80;
				}
				return new Point(wHint + 20, hHint + 20);
//				return new Point(80, 80);
			}
		};
		if(panelContainer == null) {
			panelContainer = canvas;
		}
		
		final GridData panelGridData = GridDataFactory.fillDefaults().grab(false, true).create();
		panelContainer.setLayoutData(panelGridData);
		
		sash = new Sash(parentContainer, SWT.VERTICAL);
		SashDragHandler sashGrabHandler = new SashDragHandler() {
			@Override
			protected int handleSashSelectionX(Event ev, int selectionX) {
				assertTrue(selectionX >= 0);
				panelGridData.widthHint = selectionX;
				parentContainer.layout(true);
				return selectionX;
			}
		};
		sashGrabHandler.install(sash);
		sash.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
		
		mainContainer = new Composite(parentContainer, SWT.NONE);
		mainContainer.setLayout(new FillLayout());
		mainContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		
		panelContainer.setBackground(SWTUtils.getSystemColor(SWT.COLOR_GREEN));
		mainContainer.setBackground(SWTUtils.getSystemColor(SWT.COLOR_MAGENTA));
		
	}
	
}
