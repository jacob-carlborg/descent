package com.hexapixel.framework.glazed.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hexapixel.framework.glazed.glazednatgridviewer.DefaultGlazedNatVisualConfig;
import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer;
import com.hexapixel.framework.glazed.glazednatgridviewer.IGenericNatGridTable;
import com.hexapixel.framework.glazed.glazednatgridviewer.NatGridViewerColumn;
import com.hexapixel.framework.glazed.griddata.DataFilter;
import com.hexapixel.framework.glazed.griddata.IDataObject;

public class GlazedNatTableTester implements IGenericNatGridTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new GlazedNatTableTester();

	}

	public GlazedNatTableTester() {
		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setText("Nat Glazed Lists Tester");
			shell.setSize(500, 400);
			shell.setLayout(new FillLayout());

			GlazedNatGridViewer viewer = new GlazedNatGridViewer(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION, this, new DefaultGlazedNatVisualConfig());
			viewer.setAllowMultiColumnSort(true);

			for (int i = 0; i < 10; i++) {
				NatGridViewerColumn col = viewer.addColumn("Column " + i, false, true, true, true);
				col.setDefaultWidth(100);
			}

			List<IDataObject> input = new ArrayList<IDataObject>();
			for (int i = 0; i < 1000; i++) {
				input.add(new OneRow(i));
			}
			viewer.setInput(input);

			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		}
		catch (Exception err) {
			err.printStackTrace();
		}

	}

	@Override
	public Comparator<IDataObject> getComparator(int column, int direction) {
		return new GlazedComparator(column, direction);
	}

	@Override
	public DataFilter getTableFilter() {
		return null;
	}

	@Override
	public void popuplateColumnMenu(MenuManager menuManager) {
	}

	@Override
	public void tableFilterModified() {
	}

	class GlazedComparator implements Comparator<IDataObject> {

		private int	_column;
		private int	_order;

		public GlazedComparator(int column, int order) {
			_column = column;
			_order = order;
		}

		@Override
		public int compare(IDataObject one, IDataObject two) {
			int ret = Integer.valueOf(one.getColumnText(_column)).compareTo(Integer.valueOf(two.getColumnText(_column)));

			if (_order == SWT.DOWN)
				ret = -ret;

			return ret;
		}

	}

	class OneRow implements IDataObject {

		private int	_row;

		public OneRow(int row) {
			_row = row;
		}

		@Override
		public Color getBackground(int index, boolean even) {
			return null;
		}

		@Override
		public Image getColumnImage(int index) {
			return null;
		}

		@Override
		public String getColumnText(int index) {
			return "" + (_row + index);
		}

		@Override
		public String getFilterMatchText() {
			return null;
		}

		@Override
		public Font getFont(int index) {
			return null;
		}

		@Override
		public Color getForeground(int index, boolean even) {
			return null;
		}

		@Override
		public Color getHighlightBackgroundColor(int column) {
			return null;
		}

		@Override
		public Color getHighlightForegroundColor(int column) {
			return null;
		}

		@Override
		public String getSearchMatchText(int column) {
			return getColumnText(column);
		}

	}

}
