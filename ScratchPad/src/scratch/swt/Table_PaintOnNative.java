package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class Table_PaintOnNative {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setBounds(50, 50, 350, 200);
		
		Image xImage = new Image(display, 16, 16);
		GC gc = new GC(xImage);
		gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
		gc.drawLine(1, 1, 14, 14);
		gc.drawLine(1, 14, 14, 1);
		gc.drawOval(2, 2, 11, 11);
		gc.dispose();
		final int IMAGE_MARGIN = 2;
		final Tree tree = new Tree(shell, SWT.CHECK);
		tree.setBounds(10, 10, 300, 150);
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setText("root item");
		for (int i = 0; i < 4; i++) {
			TreeItem newItem = new TreeItem(item, SWT.NONE);
			newItem.setText("descendent " + i);
			if (i % 2 == 0)
				newItem.setData(xImage);
			item.setExpanded(true);
			item = newItem;
		}
		tree.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				Image trailingImage = (Image) item.getData();
				if (trailingImage != null) {
					event.width += trailingImage.getBounds().width + IMAGE_MARGIN;
				}
			}
		});
		tree.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				Image trailingImage = (Image) item.getData();
				if (trailingImage != null) {
					int x = event.x + event.width + IMAGE_MARGIN;
					int itemHeight = tree.getItemHeight();
					int imageHeight = trailingImage.getBounds().height;
					int y = event.y + (itemHeight - imageHeight) / 2;
					event.gc.drawImage(trailingImage, x, y);
				}
			}
		});
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		xImage.dispose();
		display.dispose();
	}
}

