package com.hexapixel.framework.glazed.glazednatgridviewer;

import java.util.ArrayList;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.support.ColumnGroupSupport;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class NatColumnChooser extends Dialog {

    private NatTable               _table;
    private GlazedNatGridViewer    _viewer;
    private ArrayList<ColumnEntry> _hiddenColumns;
    private ArrayList<ColumnEntry> _displayedColumns;
    private NatSelectionList       _selectionList;
    private Spinner                _columnCountSpinner;
    private final boolean          _withFrozenColumnOptions;
    private boolean                _withColumnGroups = false;

    private Image                  _left;
    private Image                  _right;
    private Image                  _up;
    private Image                  _down;

    private NatColumnChooser(GlazedNatGridViewer table, boolean withFrozenColumnOptions, Image left, Image right, Image up, Image down) {
        super(table.getGrid().getShell());
        _viewer = table;
        this._withFrozenColumnOptions = withFrozenColumnOptions;
        setShellStyle(SWT.RESIZE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

        _left = left;
        _right = right;
        _up = up;
        _down = down;

        this._table = table.getGrid();
    }

    public void dispose() {
        if (_selectionList != null) _selectionList.dispose();

        _table = null;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Done", true);
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);

        composite.setLayout(new GridLayout(1, true));

        composite.getShell().setText("Column Chooser");
        initModel();

        createSelectionList(composite);

        if (_withFrozenColumnOptions) createFrozenControl(composite);

        Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);

        return composite;
    }

    private void createFrozenControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new RowLayout());

        Label label = new Label(composite, SWT.NONE);
        label.setText("&Frozen column count:");

        _columnCountSpinner = new Spinner(composite, SWT.WRAP | SWT.BORDER);
        _columnCountSpinner.setMinimum(0);
        _columnCountSpinner.setMaximum(_table.getNatTableModel().getBodyColumnCount() - _table.getHiddenModelBodyColumns().size());
        _columnCountSpinner.setSelection(_table.getNatTableModel().getFreezeColumnCount());
        _columnCountSpinner.setPageIncrement(1);

        _columnCountSpinner.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                frozenColumnChanged(_columnCountSpinner.getSelection());
            }

        });
    }

    private void createSelectionList(Composite parent) {
        _selectionList = new NatSelectionList(parent, SWT.NONE, "Available Columns", "Current Columns", _left, _right, _up, _down);
        _selectionList.getAvailableListViewer().setSorter(new ViewerSorter());

        _selectionList.getAvailableListViewer().add(_hiddenColumns.toArray());

        _selectionList.getSelectedListViewer().add(_displayedColumns.toArray());

        _selectionList.addSelectionListListener(new ISelectionListListener() {

            public void itemsRemoved(NatSelectionList selectionList, Object[] removedItems) {
                if (removedItems != null && removedItems.length > 0) {
                    for (Object item : removedItems) {
                        ColumnEntry columnEntry = (ColumnEntry) item;
                        if (columnEntry.isColumnGroup) {
                            _table.getColumnGroupSupport().hideColumnGroup(columnEntry.getName());
                        } else {
                            _viewer.hideColumn(columnEntry.getColumn());
                            //_table.hideModelBodyColumn(columnEntry.getColumn());
                        }
                    }
                    reindexBodyColumns();
                }
            }

            public void itemsSelected(NatSelectionList selectionList, Object[] addedItems) {
                if (addedItems != null && addedItems.length > 0) {
                    for (Object item : addedItems) {
                        ColumnEntry columnEntry = (ColumnEntry) item;

                        if (columnEntry.isColumnGroup()) {
                            _table.getColumnGroupSupport().showColumnGroup(columnEntry.getName());
                        } else {
                            //_table.showModelBodyColumn(columnEntry.getColumn());
                            _viewer.showColumn(columnEntry.getColumn());
                        }
                    }
                    reindexBodyColumns();
                }
            }

            public void itemsMoved(NatSelectionList selectionList, int[] oldIndexes, int[] newIndexes) {
                if (newIndexes != null && newIndexes.length > 0) reindexBodyColumns();
            }

        });

        if (_withFrozenColumnOptions) {
            _selectionList.addPaintListener(new PaintListener() {

                public void paintControl(PaintEvent e) {
                    GC gc = e.gc;

                    if (_columnCountSpinner.getSelection() > 0) {
                        gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLUE));
                        gc.setLineWidth(2);

                        List list = _selectionList.getSelectedListViewer().getList();
                        Rectangle listBounds = list.getBounds();

                        int totalFrozenHeight = list.getItemHeight() * _columnCountSpinner.getSelection();
                        int innerListHeight = listBounds.height - (2 * list.getBorderWidth());

                        gc.drawLine(listBounds.x - 4, listBounds.y + list.getBorderWidth(), listBounds.x - 4, listBounds.y
                                + Math.min(totalFrozenHeight, innerListHeight));
                    }
                }

            });
        }

        GridDataFactory.fillDefaults().grab(true, true).applyTo(_selectionList);
    }

    private void frozenColumnChanged(int frozenColumn) {
        _table.getNatTableModel().setFreezeColumnCount(frozenColumn);
        _table.reset();
        _table.updateResize(true);
        _selectionList.redraw();
    }

    private void reindexBodyColumns() {
        int[] order = _table.getModelBodyColumnOrder();
        int[] visibleMask = new int[order.length];

        for (int i = 0; i < order.length; i++) {
            if (!_table.isModelBodyColumnViewable(order[i]) && !isVisibleColumnGroup(order[i])) {
                visibleMask[i] = Integer.MIN_VALUE;
            } else {
                visibleMask[i] = order[i];
            }
        }

        Object[] selectedItems;
        if (_withColumnGroups) {
            selectedItems = buildSelectedItems(_selectionList.getSelectedItems());
        } else {
            selectedItems = _selectionList.getSelectedItems();
        }

        int selectedIndex = 0;
        for (int i = 0; i < visibleMask.length; i++) {
            if (visibleMask[i] != Integer.MIN_VALUE) {
                ColumnEntry entry = (ColumnEntry) selectedItems[selectedIndex];

                order[i] = entry.getColumn();
                selectedIndex++;
            }
        }

        _table.setModelBodyColumnOrder(order);
        _viewer.updateColumnOrder(order);
        if (_columnCountSpinner != null) {
            _columnCountSpinner.setMaximum(selectedItems.length);
            _columnCountSpinner.setSelection(Math.min(_columnCountSpinner.getSelection(), selectedItems.length));
        }
    }

    private Object[] buildSelectedItems(Object[] selectedItems) {
        ArrayList<ColumnEntry> entries = new ArrayList<ColumnEntry>();
        for (int i = 0; i < selectedItems.length; i++) {
            ColumnEntry entry = (ColumnEntry) selectedItems[i];
            if (entry.isColumnGroup()) {
                for (Integer groupMember : _table.getColumnGroupSupport().getColumnGroupMembers(entry.getName())) {
                    entries.add(new ColumnEntry(groupMember.intValue(), entry.getName(), entry.isOriginallyHidden(), true));
                }
            } else {
                entries.add(entry);
            }
        }
        return entries.toArray(new Object[0]);
    }

    private boolean isVisibleColumnGroup(int modelBodyColumn) {
        if (_table.isColumnGroupsEnabled()) {
            ColumnGroupSupport support = _table.getColumnGroupSupport();
            if (support.isColumnInColumnGroup(modelBodyColumn)) {
                String groupName = support.getColumnGroupName(modelBodyColumn);
                java.util.List<Integer> groupMembers = support.getColumnGroupMembers(groupName);
                // if the first col in a group is visible then the group is considered to be visible
                return (_table.isModelBodyColumnViewable(groupMembers.get(0).intValue()));
            }
        }
        return false;
    }

    private void initModel() {
        _hiddenColumns = new ArrayList<ColumnEntry>();
        _displayedColumns = new ArrayList<ColumnEntry>();
        int headerRowIndex = _table.isColumnGroupsEnabled() ? 1 : 0;
        ColumnEntry entry = null;
        boolean hidden = false;

        for (int col : _table.getModelBodyColumnOrder()) {
            if (_table.isColumnGroupsEnabled() && _table.getColumnGroupSupport().isColumnInColumnGroup(col)) {
                ColumnGroupSupport support = _table.getColumnGroupSupport();
                String name = support.getColumnGroupName(col);
                int firstGroupCol = support.getColumnGroupMembers(name).get(0).intValue();
                hidden = !_table.isModelBodyColumnViewable(firstGroupCol);
                entry = new ColumnEntry(firstGroupCol, name, hidden, true);
                _withColumnGroups = true;
            } else {
                String name = _table.getNatTableModel().getColumnHeaderCellRenderer().getDisplayText(headerRowIndex, col);
                hidden = !_table.isModelBodyColumnViewable(col);
                entry = new ColumnEntry(col, name, hidden, false);
            }
            if (hidden) {
                if (!_hiddenColumns.contains(entry)) _hiddenColumns.add(entry);
            } else if (!_displayedColumns.contains(entry)) {
                _displayedColumns.add(entry);
            }
        }

    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setSize(500, 400);
        WidgetUtilities.centerShell(newShell);
        newShell.addListener(SWT.Dispose, new Listener() {

            @Override
            public void handleEvent(Event event) {
                _viewer.saveTableState();
            }
            
        });
    }
/*
    public static void open(NatTable table, Image left, Image right, Image up, Image down) {
        open(table, true, left, right, up, down);
    }
*/
    public static void open(GlazedNatGridViewer table, boolean withFrozenColumnOptions, Image left, Image right, Image up, Image down) {
        NatColumnChooser chooser = new NatColumnChooser(table, withFrozenColumnOptions, left, right, up, down);

        chooser.setBlockOnOpen(true);
        chooser.open();
    }

    static class ColumnEntry {
        private final String  name;
        private final int     column;
        private final boolean originallyHidden;
        private final boolean isColumnGroup;

        public ColumnEntry(int column, String name, boolean originallyHidden, boolean isColumnGroup) {
            this.column = column;
            this.name = name;
            this.originallyHidden = originallyHidden;
            this.isColumnGroup = isColumnGroup;
        }

        public int getColumn() {
            return column;
        }

        public String getName() {
            return name;
        }

        public boolean isOriginallyHidden() {
            return originallyHidden;
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean isColumnGroup() {
            return isColumnGroup;
        }

        @Override
        public int hashCode() {
            return 73 * name.hashCode() + column;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ColumnEntry) {
                ColumnEntry that = (ColumnEntry) obj;
                return name.equals(that.name) && column == that.column;
            }

            return super.equals(obj);
        }
    }
}
