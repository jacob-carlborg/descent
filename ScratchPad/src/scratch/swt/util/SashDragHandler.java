package scratch.swt.util;

import static melnorme.miscutil.Assert.assertNotNull;
import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

/**
 * Handler extends the base {@link Sash} functionality by allowing selection values outside the normal
 * Sash range (which is limited to the bounds of the Sash's parent)
 * Must be installed once and only once before use.
 */
public class SashDragHandler {
	
	protected Point mouseDownRelPoint;
	protected Sash sash;
	
	public void install(final Sash sash) {
		assertTrue(this.sash == null);
		assertNotNull(sash);
		this.sash = sash;
		sash.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("mouseDown");
				handleMouseDown(event);
			}
		});
		sash.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("mouseUp");
				handleMouseUp(event);
			}
		});
		sash.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event ev) {
				System.out.println("selection");
				handleRawSashSelectionY(sash, ev);
			}
		});
	}
	
	protected void handleMouseDown(Event event) {
		mouseDownRelPoint = new Point(event.x, event.y);
		assertTrue(mouseDownRelPoint.x >= 0 && mouseDownRelPoint.y >= 0);
	}
	
	@SuppressWarnings("unused")
	protected void handleMouseUp(Event event) {
		mouseDownRelPoint = null; 
	}
	
	protected void handleRawSashSelectionY(final Sash sash, Event ev) {
		if(mouseDownRelPoint != null) {
			Point cursorLoc = Display.getCurrent().getCursorLocation();
			Point baseSelection = Display.getCurrent().map(null, sash.getParent(), cursorLoc.x, cursorLoc.y);
			int selectionY = baseSelection.y - mouseDownRelPoint.y;
			int selectionX = baseSelection.x - mouseDownRelPoint.x;
			
			ev.y = handleSashSelectionY(ev, selectionY);
			ev.x = handleSashSelectionX(ev, selectionX);
			
			System.out.println(baseSelection.x + " -- " + baseSelection.y);
		}
	}
	
	@SuppressWarnings("unused")
	protected int handleSashSelectionY(Event ev,  int selectionY){
		return ev.y;
	}
	
	@SuppressWarnings("unused")
	protected int handleSashSelectionX(Event ev, int selectionX){
		return ev.x;
	}
	
}