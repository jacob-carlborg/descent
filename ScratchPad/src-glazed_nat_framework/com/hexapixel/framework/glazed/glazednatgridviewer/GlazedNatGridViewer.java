package com.hexapixel.framework.glazed.glazednatgridviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.nattable.GridRegionEnum;
import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.action.IMouseEventAction;
import net.sourceforge.nattable.data.IColumnAccessor;
import net.sourceforge.nattable.data.ListDataProvider;
import net.sourceforge.nattable.event.matcher.MouseEventMatcher;
import net.sourceforge.nattable.listener.NatEventData;
import net.sourceforge.nattable.painter.IOverlayPainter;
import net.sourceforge.nattable.util.GUIHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.Matcher;

import com.hexapixel.framework.glazed.glazednatgridviewer.nat.GenericNatGridTableAdapter;
import com.hexapixel.framework.glazed.glazednatgridviewer.nat.NatBodyConfig;
import com.hexapixel.framework.glazed.glazednatgridviewer.nat.NatHeaderConfig;
import com.hexapixel.framework.glazed.griddata.DataFilter;
import com.hexapixel.framework.glazed.griddata.DataObjectFilterMatcher;
import com.hexapixel.framework.glazed.griddata.GridException;
import com.hexapixel.framework.glazed.griddata.IDataObject;

/**
 * Framework implementation of Glazed Lists on a <code>Nat Table</code> (Glazed lists homepage: <a
 * href="http://publicobject.com/glazedlists/">http:// publicobject.com/glazedlists/</a>. Nat Table homepage: <a
 * href="http://nattable.sourceforge.net/">http://nattable.sourceforge.net/</a>). GlazedLists work fully with SWT and
 * are framework independent. As nearly all examples for GlazedLists are in Swing however, it's good to have a proper
 * SWT/JFace example implementation.
 * <p>
 * If you're not familiar with Glazed Lists, please read up on them beforehand to understand what you can do with them,
 * including all different kinds of lists (such as thread-safe lists that support simultaneous read/write etc, property
 * driven updates, live sorting, concurrency, thresholds (paging), etc).
 * <p>
 * Benefits of using Glazed Lists are many, as it's completely driven by direct modifications to the array list that
 * serves as the table input. Other benefits include;
 * <p>
 * <ul>
 * <li>possibilities to add property driven updates (detailed databinding).
 * <li>[infinitely] stacked lists for additional sorting, filtering, unique-items-views and so on, the sky's the limit.
 * <li>insanely easy to add new levels of complexity onto the existing structure without more than a few lines of code.
 * <li>See the homepage for more info.
 * </ul>
 * <p>
 * This example goes slightly overboard as it also shows how to sort columns horizontally as well as implementing column
 * wrappers that allow for column freezing (when sorting horizontally), data holders etc. It also supports all that for
 * grouped columns (and thus, shows how to sort grouped columns). And for ease of use, it also allows the user to use
 * ViewerFilters as filter class just because in JFace these are the ones used for sorting/filtering (although here they
 * are only used for filtering, sorting is done via a class implementing Comparator)
 * <p>
 * Note all the locking/unlocking done to the root event list at each important read/write instance. This is important.
 * 
 * @author cre
 */
public final class GlazedNatGridViewer implements IGlazedNatGridViewer {

    public static final String                  TABLE_COLUMN_ORDER   = "-tableColumnOrder";
    public static final String                  TABLE_COLUMN_WIDTHS  = "-tableColumnWidths";
    public static final String                  TABLE_HIDDEN_COLUMNS = "-tableHiddenColumns";

    private NatTable                            _grid;

    private List<IDataObject>                   _sourceInput;
    private EventList<IDataObject>              _eventList;
    private SortedList<IDataObject>             _sortedList;
    private FilterList<IDataObject>             _filterList;

    private List<ViewerFilter>                  _activeViewerFilters;
    private List<NatGridViewerColumn>           _columns;

    private NatGridViewerColumn                 _sortColumn;
    private boolean                             _columnsSorted;
    private int[]                               _originalTableOrder;
    private int[]                               _loadedTableOrder;

    private String                              _tableId;

    private IGenericNatGridTable                _genericGridTable;

    private IPreferenceStore                    _store;
    private boolean                             _allowStateSaving;

    // multi-column sorting variables
    private int                                 _stateMask;
    private List<NatGridViewerColumn>           _multiColumnSortSelectOrder;
    private boolean                             _allowMultiColumnSort;
    // end

    private boolean                             _debug               = false;

    private boolean                             _isHighlighting;
    private boolean                             _caseSensitiveHighlight;
    private String                              _highlightText;

    private GlazedNatGridViewerTableModel       _model;

    private ListDataProvider<IDataObject>       _dataProvider;

    private NatHeaderConfig                     _headerConfig;

    private AbstractGlazedNatVisualConfig       _gridColorManager;

    private Listener                            _dragOrResizeFinishedListener;
    private boolean                             _dndResizeListenerActive;

    private NatGridViewerColumn                 _hoverColumn;

    private HashMap<IDataObject, List<Integer>> _selectionMemory;

    private List<Integer>                       _colOrderPreSortClick;

    private NatBodyConfig                       _bodyConfig;

    private ViewerFilter                        _tableFilter;
    private List<ViewerFilter>                  _savedFilterState;

    private boolean                             _multiSel;
    private boolean                             _fullRow;

    private boolean                             _columnMenuEnabled   = true;

    // used to determine when a multi-sort table goes from multi-sort to no-sort
    private int                                 _lastMultiColumnSortSize;

    private List<Integer>                       _preColumnDoubleclickResizeWidth;

    // this var is used to circumvent the column reorder bug on simple column clicking
    private Rectangle                           _mouseDownColumnArea;
    private int[]                               _mouseDownColumnOrder;

    /**
     * Minimalistic constructor for the table, which uses default implementations wherever possible. It is not suggested
     * you use this constructor except for small and pointless tables.
     * 
     * @param parent Parent composite
     * @param style Table style
     */
    public GlazedNatGridViewer(Composite parent, int style) {
        this(parent, style, new GenericNatGridTableAdapter(), null, null, false, new DefaultGlazedNatVisualConfig());
    }

    /**
     * See constructor for
     * {@link #GlazedNatGridViewer(Composite, int, IGenericNatGridTable, String, IPreferenceStore, boolean, AbstractGlazedNatVisualConfig)}
     * .
     * 
     * @param parent Parent composite
     * @param style Table style
     * @param generic Generic table implementor
     * @param config Table config
     */
    public GlazedNatGridViewer(Composite parent, int style, IGenericNatGridTable generic, AbstractGlazedNatVisualConfig config) {
        this(parent, style, generic, null, null, false, config);
    }

    /**
     * See constructor for
     * {@link #GlazedNatGridViewer(Composite, int, IGenericNatGridTable, String, IPreferenceStore, boolean, AbstractGlazedNatVisualConfig)}
     * .
     * 
     * @param parent Parent composite
     * @param style Table style
     * @param generic Generic table implementor
     * @param tableId Table id
     * @param config Table config
     */
    public GlazedNatGridViewer(Composite parent, int style, IGenericNatGridTable generic, String tableId, AbstractGlazedNatVisualConfig config) {
        this(parent, style, generic, tableId, null, false, config);
    }

    /**
     * Creates a new GridViewer GlazedLists implementation on the given Composite parent. This constructor has the
     * option to create a normal Grid (which should be used for SWT.VIRTUAL tables as the viewer [currently] does not
     * support a virtual state).
     * 
     * @param parent Parent composite
     * @param style Table style
     * @param generic Generic interface implementor
     * @param normalGridOnly Whether to create the table as a normal grid instead of a viewer
     * @param tableId Table id (may be null)
     * @param store Preference store, used for saving/loading table states (may be null)
     * @param allowStateSaving Whether this table should save table state (table id and store must be set)
     * @paran config Custom config, if null, default will be used
     */
    public GlazedNatGridViewer(Composite parent, int style, IGenericNatGridTable generic, String tableId, IPreferenceStore store, boolean allowStateSaving,
            AbstractGlazedNatVisualConfig config) {

        _genericGridTable = generic;
        _store = store;
        _tableId = tableId;
        _allowStateSaving = allowStateSaving;
        _gridColorManager = config;
        _selectionMemory = new HashMap<IDataObject, List<Integer>>();
        _colOrderPreSortClick = new ArrayList<Integer>();
        _savedFilterState = new ArrayList<ViewerFilter>();
        if (_gridColorManager == null) _gridColorManager = new DefaultGlazedNatVisualConfig();

        _multiSel = ((style & SWT.MULTI) != 0);
        _fullRow = ((style & SWT.FULL_SELECTION) != 0);

        _sourceInput = new ArrayList<IDataObject>();

        init();
        createInput();
        buildTable();

        _grid = new NatTable(parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL, _model);

        initListeners();
    }

    private void init() {
        _multiColumnSortSelectOrder = new ArrayList<NatGridViewerColumn>();
        _columns = new ArrayList<NatGridViewerColumn>();
        _activeViewerFilters = new ArrayList<ViewerFilter>();
    }

    private void buildTable() {
        _dataProvider = assembleListDataProvider(_filterList);
        loadDataModel(_dataProvider);
        _headerConfig = new NatHeaderConfig(this);
        _model.setColumnHeaderConfig(_headerConfig);
        _model.setEnableMoveColumn(true);
        _model.setFullRowSelection(_fullRow);
        _model.setSingleCellSelection(!_fullRow);
        // _model.setColumnSelectionBehavior(net.sourceforge.nattable.action.ColumnSelectionBehaviorEnum.
        // DOUBLE_CLICK_SELECTION_SINGLE_CLICK_SORTING);
        _model.setSortingEnabled(true);
        _model.setMultipleSelection(_multiSel);
        _model.getBodyConfig().getColumnWidthConfig().setDefaultResizable(true);
    }

    /**
     * Sets the visual config to be used on this table. This method does not cause a redraw.
     * 
     * @param config Visual config
     */
    public void setVisualConfig(AbstractGlazedNatVisualConfig config) {
        _gridColorManager = config;
    }

    /**
     * Returns the currently set visual config or creates a default one if the currently set one is null.
     * 
     * @return Visual config
     */
    public AbstractGlazedNatVisualConfig getVisualConfig() {
        if (_gridColorManager == null) {
            _gridColorManager = new DefaultGlazedNatVisualConfig();
        }

        return _gridColorManager;
    }

    /**
     * Returns the currently set Highlight text.
     * 
     * @return Highlight text
     */
    public String getHighlightText() {
        return _highlightText;
    }

    /**
     * Sets whether the current highlight matching is case sensitive.
     * 
     * @param sensitive true for case sensitive matching
     */
    public void setCaseSensitiveHighlight(boolean sensitive) {
        _caseSensitiveHighlight = sensitive;
    }

    /**
     * Returns whether the current highlight matching is case sensitive.
     * 
     * @return true if highlight matching is case sensitive.
     */
    public boolean isCaseSensitiveHighlight() {
        return _caseSensitiveHighlight;
    }

    /**
     * Returns the table model.
     * 
     * @return Table model
     */
    public GlazedNatGridViewerTableModel getModel() {
        return _model;
    }

    private void loadDataModel(ListDataProvider<IDataObject> list) {
        _bodyConfig = new NatBodyConfig(this, list);
        _model.setBodyConfig(_bodyConfig);
    }

    /**
     * Returns whether a column is the last column in the table.
     * 
     * @param col Column to check
     * @return true if it's the last column in the table
     */
    public boolean isLastColumn(NatGridViewerColumn col) {
        List<Integer> order = getVisibleColumnOrder();
        return order.get(order.size() - 1) == col.getIndex();
    }

    /**
     * Sets a selection on the table.
     * 
     * @param toSelect List of objects to select.
     */
    public void setSelection(List<IDataObject> toSelect) {
        try {
            _grid.getSelectionModel().clearSelection();
            List<Integer> rows = new ArrayList<Integer>();
            for (IDataObject obj : toSelect) {
                int row = _filterList.indexOf(obj);
                if (row != -1) {
                    rows.add(row);
                }
            }
            for (Integer row : rows) {
                if (_fullRow) {
                    for (int i = 0; i < getColumnCount(); i++) {
                        _grid.getSelectionModel().addSelection(row, i);
                    }
                } else {
                    _grid.getSelectionModel().addSelection(row, 0);
                }
            }

            rememberSelection();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Selects a row in the table.
     * 
     * @param row Row to select
     */
    public void select(final int row) {
        _grid.getSelectionModel().clearSelection();
        if (_fullRow) {
            for (int i = 0; i < getColumnCount(); i++) {
                _grid.getSelectionModel().addSelection(row, i);
            }
        } else {
            _grid.getSelectionModel().addSelection(row, 0);
        }

        rememberSelection();
    }

    /**
     * Scrolls the table to the first selected item.
     */
    public void showSelection() {
        if (getSelectionCount() == 0) return;

        _grid.showBodyRow(getSelectedPoints().get(0).y);
        rememberSelection();
    }

    // builds the list data provider and bases it on our IDataObject
    private ListDataProvider<IDataObject> assembleListDataProvider(EventList<IDataObject> list) {
        ListDataProvider<IDataObject> dataList = new ListDataProvider<IDataObject>(list, new IColumnAccessor<IDataObject>() {

            @Override
            public int getColumnCount() {
                return _columns.size();
            }

            @Override
            public Object getColumnValue(IDataObject obj, int col) {
                return obj.getColumnText(col);
            }

        });
        return dataList;
    }

    // creates the GlazedLists input
    private void createInput() {
        try {
            _eventList = GlazedLists.eventList(_sourceInput);

            _eventList.getReadWriteLock().readLock().lock();

            _sortedList = new SortedList<IDataObject>(_eventList, null);
            _tableFilter = _genericGridTable.getTableFilter();
            _filterList = new FilterList<IDataObject>(_sortedList, new DataObjectFilterMatcher());

            _model = new GlazedNatGridViewerTableModel(this);

            _eventList.getReadWriteLock().readLock().unlock();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public GlazedNatGridViewer getViewer() {
        return this;
    }

    /**
     * Returns whether multi column sort is allowed.
     * 
     * @return true if yes
     */
    public boolean isAllowMultiColumnSort() {
        return _allowMultiColumnSort;
    }

    /**
     * Whether to allow multi colum sort (by holding down the shift key)
     * 
     * @param allowMultiColumnSort true to allow, false to disable
     */
    public void setAllowMultiColumnSort(boolean allowMultiColumnSort) {
        _allowMultiColumnSort = allowMultiColumnSort;
    }

    /**
     * Returns whether the given column is currently part of a multisort.
     * 
     * @param col Column
     * @return true if column is being multisorted
     */
    public boolean isActiveMultiSortColumn(NatGridViewerColumn col) {
        return (_multiColumnSortSelectOrder.contains(col));
    }

    /**
     * Adds a new column to the table.
     * 
     * @param name Visible name
     * @param frozen Whether column is frozen when sorting columns
     * @param moveable Whether column is moveable
     * @param sortable Whether column is sorteable
     * @return Created column
     */
    public NatGridViewerColumn addColumn(String name, boolean frozen, boolean moveable, boolean sortable, boolean hideable) {
        NatGridViewerColumn gc = new NatGridViewerColumn(this, name, frozen, moveable, sortable, hideable);
        _columns.add(gc);
        columnNumbersChanged();
        return gc;
    }

    /**
     * Adds a new column to the table onto an existing group.
     * 
     * @param group Column group parent
     * @param name Visible name
     * @param frozen Whether column is frozen when sorting columns
     * @param moveable Whether column is moveable
     * @param sortable Whether column is sorteable
     * @return Created column
     */
    public NatGridViewerColumn addColumnToGroup(NatGridViewerColumnGroup group, String name, boolean frozen, boolean moveable, boolean sortable, boolean hideable) {
        NatGridViewerColumn gc = new NatGridViewerColumn(group, this, name, frozen, moveable, sortable, hideable);
        columnNumbersChanged();
        return gc;
    }

    /**
     * Creates a Grouped column with a list of child columns.
     * 
     * @param parentName Grouped colum name
     * @param columnNames List of child column names
     * @param frozen Whether column is frozen when sorting columns
     * @param moveable Whether column is moveable
     * @param sortable Whether column is sorteable
     * @return Created column
     */
    // TODO: When needed
    /*
     * public NatGridViewerColumnGroup addGroupedColumn(String parentName, List<String> columnNames, boolean frozen,
     * boolean moveable, boolean sortable, boolean hideable) { NatGridViewerColumnGroup group = new
     * NatGridViewerColumnGroup(parentName, this); for (String name : columnNames) new NatGridViewerColumn(group, this,
     * name, frozen, moveable, sortable, hideable); columnNumbersChanged(); // _loadedTableOrder =
     * _grid.getModelBodyColumnOrder(); return group; }
     */

    @Override
    public int getStateMask() {
        return _stateMask;
    }

    /**
     * Returns whether the table is currently multisorting or not.
     * 
     * @return true if multisorting.
     */
    public boolean isMultiSorting() {
        return _multiColumnSortSelectOrder.size() > 0;
    }

    /**
     * Returns the column sort order of a column during multisort. This is mostly meant to be internal but is public
     * anyway.
     * 
     * @param column Column
     * @return index or -1 if not found, or multisort is not currently active
     */
    public int getMultiSortOrder(NatGridViewerColumn column) {
        if (!isMultiSorting()) { return -1; }

        return _multiColumnSortSelectOrder.indexOf(column);
    }

    private void initListeners() {

        // old sort code, LEAVE IT IN but COMMENTED OUT
        /*
         * _grid.addSortingDirectionChangeListener(new ISortingDirectionChangeListener() {
         * @Override public void sortingDirectionChanged(SortingDirection[] arg0) { rememberSelection(); } });
         */
        /*
         * @Override public void sortingDirectionChanged(SortingDirection[] dirs) { try { if (dirs.length != 0) { for
         * (SortingDirection sd : dirs) { sortColumn(_columns.get(sd.getColumn()), sd.getColumn()); } }// else { //
         * sortColumn(_sortColumn, _sortColumnIndex); //} } catch (Exception err) { err.printStackTrace(); } } }); /
         * _grid.addListener(SWT.MouseHover, new Listener() {
         * @Override public void handleEvent(Event event) { int headerHeight = _grid.getTotalColumnHeaderHeight(); if
         * (event.y <= headerHeight && event.y >= 0) { int col = _grid.getModelGridColumnByX(event.x);
         * NatGridViewerColumn gvc = _columns.get(col); if (gvc.isTruncatedHeaderText()) {
         * _grid.setToolTipText(gvc.getName()); } else { _grid.setToolTipText(null); } } } });
         */
        /*
         * _grid.addListener(SWT.MouseHover, new Listener() {
         * @Override public void handleEvent(Event event) { if (wasHeaderClick(event.x, event.y)) { int col =
         * _grid.getModelGridColumnByX(event.x); NatGridViewerColumn gvc = _columns.get(col); if
         * (gvc.isTruncatedHeaderText()) { //_grid.setToolTipText(); } else { //_grid.setToolTipText(null); } } } });
         */

        // possible header size-doubleclick, save table state
        _grid.addListener(SWT.MouseDoubleClick, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (wasHeaderClick(event.x, event.y)) {
                    saveTableState();
                   
                    // this is a hack to fix that tables that have no cells get columns resized to zero width 
                    // when user doubleclicks between columns to auto-size.
                    // if the new width here is zero, we reset it to what the width was when the mouth was pressed down (which
                    // is where we save these values)
                    if (_grid.getCursor() != null && _grid.getCursor().equals(Display.getDefault().getSystemCursor(SWT.CURSOR_SIZEW))) {

                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                boolean any = false;
                                for (int i = 0; i < _model.getBodyColumnCount(); i++) {
                                    if (_model.getBodyColumnWidth(i) == 0) {
                                        _model.setBodyColumnWidth(i, _preColumnDoubleclickResizeWidth.get(i));
                                        any = true;
                                    }
                                }

                                if (any) {
                                    _grid.updateResize(true);
                                }
                            }
                            
                        });
                    }

                }
            }

        });

        // this is a hack to show gridlines in table if it has no items in it yet, or the
        // table would just be blank. This code is copied from NatTable source and modified/simplified.
        _grid.addOverlayPainter(new IOverlayPainter() {

            @Override
            public void paintOverlay(GC gc) {
                if (_gridColorManager.isShowGridLinesOnEmptyTable()) {
                    try {
                        if (_eventList.size() == 0 || _gridColorManager.isDrawGridLinesToBottom()) {
                            Color bg = gc.getBackground();
                            Color fg = gc.getForeground();

                            gc.setForeground(_gridColorManager.getGridLineColor());
                            final List<Integer> visibleModelBodyColList = _grid.getVisibleModelBodyColumns();
                            final List<Integer> visibleModelBodyRowList = _grid.getVisibleModelBodyRows();

                            int freezeCol = _model.getFreezeColumnCount() - 1;
                            int totalRowHeaderWidth = 0;// visibleMetricsSupport.getTotalRowHeaderWidth();

                            int rowHeaderColumnCount = _model.getRowHeaderColumnCount();
                            for (int col = 0; col < rowHeaderColumnCount; col++) {
                                totalRowHeaderWidth += _model.getRowHeaderColumnWidth(col);
                            }

                            int columnHeaderRowCount = _model.getColumnHeaderRowCount();
                            int visibleBodyColumnCount = visibleModelBodyColList.size();
                            int visibleBodyRowCount = visibleModelBodyRowList.size();
                            // +1 in case we hit bottom a bit offset
                            visibleBodyRowCount = (_grid.getClientArea().height / _bodyConfig.getRowHeightConfig().getDefaultSize()) + 1;

                            // Calculate available width
                            int availableWidth = _grid.getClientArea().width;
                            for (int col = 0; col < visibleBodyColumnCount; col++) {
                                availableWidth += _model.getBodyColumnWidth(visibleModelBodyColList.get(col).intValue());
                            }

                            // Calculate available height, drawing grid lines along the way
                            int currentHeight = 0;

                            // Draw horizontal lines separating column header rows
                            for (int row = 0; row < columnHeaderRowCount; row++) {
                                gc.drawLine(0, currentHeight, availableWidth, currentHeight);
                                currentHeight += _model.getColumnHeaderRowHeight(row);
                            }

                            final int regionDelimiterHeight = currentHeight;

                            // Draw horizontal line separating column headers from body
                            gc.drawLine(0, regionDelimiterHeight, availableWidth, regionDelimiterHeight);

                            // Draw horizontal lines separating body rows
                            for (int row = 0; row < visibleBodyRowCount; row++) {
                                currentHeight += _bodyConfig.getRowHeightConfig().getDefaultSize();
                                gc.drawLine(0, currentHeight, availableWidth, currentHeight);
                            }

                            // Current height is now at max y extent
                            final int availableHeight = currentHeight;

                            int currentWidth = 0;
                            // Draw vertical lines separating row header columns
                            for (int col = 0; col < rowHeaderColumnCount; col++) {
                                gc.drawLine(currentWidth, 0, currentWidth, availableHeight);
                                currentWidth += _model.getRowHeaderColumnWidth(col);
                            }

                            final int regionDelimiterWidth = currentWidth;

                            int yStart = _gridColorManager.getDefaultHeaderHeight();

                            // Draw vertical line separating row headers from body
                            gc.drawLine(regionDelimiterWidth, yStart, regionDelimiterWidth, availableHeight - yStart);

                            // Draw vertical lines separating body columns
                            for (int col = 0; col < visibleBodyColumnCount; col++) {
                                final int visibleBodyCol = visibleModelBodyColList.get(col).intValue();
                                currentWidth += _model.getBodyColumnWidth(visibleBodyCol);
                                if (visibleBodyCol == freezeCol) {
                                    gc.setForeground(GUIHelper.COLOR_BLACK);
                                }
                                gc.drawLine(currentWidth, yStart, currentWidth, availableHeight - yStart);
                                if (visibleBodyCol == freezeCol) {
                                    // Reset to default color
                                    gc.setForeground(_gridColorManager.getGridLineColor());
                                }
                            }

                            gc.setBackground(bg);
                            gc.setForeground(fg);
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }

        });

        // the internal listeners on the NatTable don't fire for some reason, so we do our own implementation
        // to listen to when a resize or drag/drop (column reordering) finishes.
        // this is only to save the table state, so if it fires on false positives, that's more or less harmless.
        _dragOrResizeFinishedListener = new Listener() {

            @Override
            public void handleEvent(Event event) {
                Display.getDefault().removeFilter(SWT.MouseUp, _dragOrResizeFinishedListener);
                _dndResizeListenerActive = false;
                saveTableState();
            }

        };

        // resize listener
        _grid.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                if (e.stateMask != 0) {
                    if (_dndResizeListenerActive) { return; }

                    _dndResizeListenerActive = true;
                    Display.getDefault().addFilter(SWT.MouseUp, _dragOrResizeFinishedListener);
                }
            }

        });

        // hover column settings
        _grid.addListener(SWT.MouseMove, new Listener() {

            @Override
            public void handleEvent(Event event) {
                int headerHeight = _grid.getTotalColumnHeaderHeight();
                if (event.y <= headerHeight && event.y >= 0) {

                    int col = _grid.getModelGridColumnByX(event.x);
                    if (col == -1)
                    	return;
                    
                    NatGridViewerColumn gvc = _columns.get(col);

                    if (_hoverColumn != null) {
                        _hoverColumn.setHoverColumn(false);
                        _hoverColumn = gvc;
                        gvc.setHoverColumn(true);
                    } else {
                        _hoverColumn = gvc;
                        _hoverColumn.setHoverColumn(true);
                    }
                    _grid.redrawColumnHeaders();
                } else {
                    if (_hoverColumn != null) {
                        _hoverColumn.setHoverColumn(false);
                        _hoverColumn = null;
                        _grid.redrawColumnHeaders();
                    }
                }
            }

        });

        // hover check
        _grid.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.stateMask == 0) {
                    deselectAll();
                }
                
                int headerHeight = _grid.getTotalColumnHeaderHeight();
                rememberSelection();
                if (event.y <= headerHeight && event.y >= 0) {

                    if (_grid.getCursor() != null && _grid.getCursor().equals(Display.getDefault().getSystemCursor(SWT.CURSOR_SIZEW))) {
                        _preColumnDoubleclickResizeWidth = new ArrayList<Integer>();
                        for (int i = 0; i < _model.getBodyColumnCount(); i++) {
                            _preColumnDoubleclickResizeWidth.add(_model.getBodyColumnWidth(i));
                        }
                    }

                    int col = _grid.getModelGridColumnByX(event.x);
                    NatGridViewerColumn gvc = _columns.get(col);
                    _mouseDownColumnArea = _grid.getModelGridCellBound(0, col);
                    _mouseDownColumnOrder = _grid.getModelBodyColumnOrder();
                    gvc.setHoverSelectColumn(true);
                    _grid.redrawColumnHeaders();
                } else {
                    _mouseDownColumnArea = null;
                    _mouseDownColumnOrder = null;
                }
            }
        });

        // this is a workaround fix to NTBL-153 (http://nattable.org/jira/browse/NTBL-153)
        // that I opened to report the random column reorder fix
        _grid.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                if (_mouseDownColumnArea != null) {
                    if (_mouseDownColumnArea.contains(event.x, event.y)) {
                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                // check column order
                                int colOrderNow[] = _grid.getModelBodyColumnOrder();

                                // if column order has changed, then it's a fake reorder and we need to change it
                                // back right away
                                if (!Arrays.equals(_mouseDownColumnOrder, colOrderNow)) {
                                    // set column order back
                                    _grid.setModelBodyColumnOrder(_mouseDownColumnOrder);
                                }

                            }

                        });
                    }
                }
            }

        });

        // hover cleanup check
        _grid.addListener(SWT.MouseExit, new Listener() {

            @Override
            public void handleEvent(Event event) {
                for (NatGridViewerColumn col : _columns) {
                    col.setHoverColumn(false);
                }
                _hoverColumn = null;
                _grid.redrawColumnHeaders();
            }

        });

        // save the column order so we know on mouse up if it was a column reordering
        _grid.addListener(SWT.MouseDown, new Listener() {

            @Override
            public void handleEvent(Event event) {
                _colOrderPreSortClick.clear();

                int[] order = _grid.getModelBodyColumnOrder();
                for (int i = 0; i < order.length; i++) {
                    _colOrderPreSortClick.add(order[i]);
                }
            }

        });

        // sorting
        _grid.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                try {
                    // we do the whole sorting listener thing in a mouse up for a few reasons:
                    // 1. we could use the internal sort listener but then multisort goes out the window
                    // 2. we could use mouse down, but then we don't know what the user did in between
                    // 3. this gives us full control over various aspects of the sort, but we have to check
                    // stuff such as the cursor and the reordering of columns before we let it off as a sort
                    // click

                    if (event.button != 1) { return; }

                    // ignore if we're resizing
                    if (_grid.getCursor() != null && _grid.getCursor().equals(Display.getDefault().getSystemCursor(SWT.CURSOR_SIZEW))) { return; }

                    // bit of a hack, we check column order post click to see if it was a DND, then we don't sort
                    int[] order = _grid.getModelBodyColumnOrder();
                    List<Integer> current = new ArrayList<Integer>();
                    for (int i = 0; i < order.length; i++) {
                        current.add(order[i]);
                    }

                    // col reordering, ignore
                    if (!_colOrderPreSortClick.equals(current)) { return; }

                    int headerHeight = _grid.getTotalColumnHeaderHeight();
                    if ((event.stateMask & SWT.SHIFT) != 0) {
                        if (event.y <= headerHeight && event.y >= 0) {
                            int col = _grid.getModelGridColumnByX(event.x);
                            NatGridViewerColumn gvc = _columns.get(col);
                            if (!_multiColumnSortSelectOrder.contains(gvc)) {
                                _multiColumnSortSelectOrder.add(gvc);
                            }

                            int sortDirection = gvc.getSortDirection();

                            switch (sortDirection) {
                                case SWT.UP:
                                    sortDirection = SWT.DOWN;
                                    break;
                                case SWT.DOWN:
                                    sortDirection = SWT.NONE;
                                    break;
                                case SWT.NONE:
                                    sortDirection = SWT.UP;
                                    break;
                            }

                            if (sortDirection == SWT.NONE) {
                                _multiColumnSortSelectOrder.remove(gvc);
                            }

                            gvc.setSortDirection(sortDirection);
                            // System.out.println(_multiColumnSortSelectOrder.size());
                            _grid.redraw();
                        }
                    } else {
                        if (event.y <= headerHeight && event.y >= 0) {
                            int col = _grid.getModelGridColumnByX(event.x);
                            NatGridViewerColumn gvc = _columns.get(col);
                            sortColumn(_columns.get(gvc.getIndex()), gvc.getIndex());
                        }
                    }

                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

        });

        // multi sorting
        _grid.addListener(SWT.KeyUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.SHIFT) {
                    // table goes from multi-sort to non-sort state (not single sort, that's a straight conversion and not covered here).
                    // as the table might be in a sorted state, we unsort it at this point.
                    if (_lastMultiColumnSortSize != 0 && _multiColumnSortSelectOrder.size() == 0) {
                        _lastMultiColumnSortSize = 0;
                        _sortColumn = _columns.get(0);
                        _columns.get(0).setSortColumn(true);
                        _columns.get(0).setSortDirection(SWT.DOWN);
                        sortColumn(_columns.get(0), 0);
                    }
                }

                // do multi sort
                if (event.keyCode == SWT.SHIFT && _multiColumnSortSelectOrder.size() > 0) {
                    _lastMultiColumnSortSize = _multiColumnSortSelectOrder.size();
                    sortColumns(_multiColumnSortSelectOrder);
                }
            }

        });

        // column menu for hideable columns
        _grid.getEventBindingSupport().registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegionEnum.COLUMN_HEADER.toString(), 3), new IMouseEventAction() {

            @Override
            public void run(MouseEvent e) {
                if (!_columnMenuEnabled) { return; }

                NatEventData natEventData = (NatEventData) e.data;
                int columnIndex = natEventData.getColumnIndex();
                NatGridViewerColumn ngc = _columns.get(columnIndex);
                MenuManager mm = new MenuManager();
                if (ngc.isHideable()) {
                    mm.add(new HideAction(ngc));
                    mm.add(new Separator());
                }
                _genericGridTable.popuplateColumnMenu(mm);

                createColumnMenu(mm);
                mm.add(new Separator());
                if (ngc.isHideable()) {
                    mm.add(new ShowAllColumns());
                }
                mm.add(new ResetColumnOrder());
                mm.add(new Separator());
                mm.add(new PackColumnWidths());
                mm.add(new ResetColumnWidths());

                // last minute additions
                // freezing works very strangely and is bug-ridden, lets disable it until
                // it's all fixed, see http://nattable.org/jira/browse/NTBL-50 for example
                /*
                 * mm.add(new Separator()); mm.add(new SplitVerticallyAction(ngc)); mm.add(new
                 * ClearHorizontalFreezeAction());
                 */
                Menu menu = mm.createContextMenu(_grid);
                menu.setLocation(_grid.toDisplay(new Point(e.x, e.y)));
                menu.setVisible(true);
            }

        });

        // statemask saving
        _grid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                _stateMask = e.stateMask;
            }

            @Override
            public void mouseUp(MouseEvent e) {
                _stateMask = e.stateMask;
            }

        });
    }

    /**
     * Adds a listener to the table. See {@link NatTable#addListener(int, Listener)}. This method intercepts
     * SWT.Selection and lets all other events to directly to the table.
     * 
     * @param type
     * @param listener
     */
    public void addListener(int type, Listener listener) {
        if (type == SWT.Selection) {
            _grid.addListener(SWT.MouseUp, listener);
            _grid.addListener(SWT.KeyUp, listener);
        } else {
            _grid.addListener(type, listener);
        }
    }

    /**
     * Removes a listener from the table. See {@link NatTable#addListener(int, Listener)}. This method intercepts
     * SWT.Selection and lets all other events to directly to the table.
     * 
     * @param type
     * @param listener
     */
    public void removeListener(int type, Listener listener) {
        if (type == SWT.Selection) {
            _grid.removeListener(SWT.MouseUp, listener);
            _grid.removeListener(SWT.KeyUp, listener);
        } else {
            _grid.removeListener(type, listener);
        }
    }

    /**
     * Returns a column based on rectangular location (only x is used).
     * 
     * @param rect Rectangle
     * @return Column or null if none matched the area
     */
    public NatGridViewerColumn getColumn(Rectangle rect) {
        int col = _grid.getModelGridColumnByX(rect.x);
        NatGridViewerColumn gvc = _columns.get(col);
        return gvc;
    }

    private synchronized void columnNumbersChanged() {
        // this screws things up for some reason, all columns are the same if we use this method call
        // _loadedTableOrder = _grid.getModelBodyColumnOrder();
        _loadedTableOrder = new int[_columns.size()];
        for (int i = 0; i < _columns.size(); i++) {
            _loadedTableOrder[i] = i;
        }
        updateTableColumnOrder();
    }

    void setDefaultWidth(int col, int width) {
        _bodyConfig.getColumnWidthConfig().setInitialSize(col, width);
    }

    /**
     * Loads the columns menu onto a menumanager.
     * 
     * @param manager Menumanager. Must not be null.
     */
    public void loadColumnsMenu(IMenuManager manager) {
        try {
            manager.removeAll();

            for (NatGridViewerColumn col : getColumnsVisualOrdered()) {
                if (!col.isHideable()) continue;

                HideShowAction hs = new HideShowAction(col);
                hs.setChecked(!col.isHidden());

                manager.add(hs);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Loads the "Filter" menu onto the given MenuManager as a raw list of filter items as Actions. This method removes
     * all previous items on the given MenuManager.
     * <p>
     * This is probably not the method you want, but {@link #createFilterMenu(IMenuManager, List)}.
     * 
     * @param manager MenuManager onto which to load the filter menu.
     */
    public void loadFilterMenu(IMenuManager manager, List<DataFilter> allFilters) {
        try {
            manager.removeAll();

            if (allFilters == null || allFilters.size() == 0) return;

            manager.add(new ClearAllFilters());
            manager.add(new Separator());

            for (DataFilter df : allFilters) {
                FilterAction fa = new FilterAction(df);
                if (_activeViewerFilters.contains(df)) {
                    fa.setChecked(true);
                }

                manager.add(fa);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Creates the Filter Menu onto any menu manager, using a "Filters" parent menu where all individual filters are
     * loaded. Any active filter will be checked.
     * 
     * @param mm MenuManager
     * @param allFilters All filters in a list
     */
    public void createFilterMenu(IMenuManager mm, final List<DataFilter> allFilters) {
        final MenuManager filterMenu = new MenuManager("Filters");
        mm.add(filterMenu);

        mm.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                loadFilterMenu(filterMenu, allFilters);
            }
        });
    }

    public void setColumnMenuEnabled(boolean enabled) {
        _columnMenuEnabled = enabled;
    }

    /**
     * Creates a "Columns" menu with a submenu containing all columns, which is used for showing/hiding columns.
     * 
     * @param mm MenuManager onto which to create the menu. Must not be null.
     */
    public void createColumnMenu(IMenuManager mm) {
        final MenuManager columnsMenu = new MenuManager("Columns");
        mm.add(columnsMenu);

        mm.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                loadColumnsMenu(columnsMenu);
            }
        });
    }

    // re-index all columns and tells them what index they have, used for
    // sorting columns
    private void updateTableColumnOrder() {
        try {
            for (int i = 0; i < _columns.size(); i++) {
                _columns.get(i).setData(COLUMN_INDEX_DATA, i);
                _columns.get(i).setIndex(i);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void sortColumns(List<NatGridViewerColumn> columns) {
        try {
            updateTableColumnOrder();

            // int[] colOrder = _grid.getModelBodyColumnOrder();
            if (columns.size() == 0) return;

            List<NatGridViewerColumn> actualList = new ArrayList<NatGridViewerColumn>();

            for (NatGridViewerColumn gvc : columns) {
                if (gvc.isSortable() && gvc.getSortDirection() != SWT.NONE) {
                    actualList.add(gvc);
                }
            }
            if (actualList.size() == 0) return;

            List<Comparator<IDataObject>> compList = new ArrayList<Comparator<IDataObject>>();

            for (NatGridViewerColumn col : actualList) {
                col.setSortColumn(true);

                // update comparator
                Object sortData = col.getData(COLUMN_INDEX_DATA);

                if (sortData == null) throw new GridException("Columns are not indexed! Please ensure you call updateTableColumnOrder() after creating/altering table columns");

                compList.add(_genericGridTable.getComparator((Integer) sortData, col.getSortDirection()));
            }

            Comparator<IDataObject> chain = GlazedLists.chainComparators(compList);
            _sortColumn = actualList.get(0);
            _sortedList.setComparator(chain);

            updateHighlightPostSort();
            // _grid.setModelBodyColumnOrder(colOrder);
            restoreSelection();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void sortColumn(final NatGridViewerColumn column, final int index) {
        try {
            // int[] colOrder = _grid.getModelBodyColumnOrder();

            for (NatGridViewerColumn col : _multiColumnSortSelectOrder) {
                col.setSortDirection(SWT.NONE);
            }

            _multiColumnSortSelectOrder.clear();

            long time1 = System.currentTimeMillis();
            if (!column.isSortable()) { return; }

            // clear multi column sort indicators on single column sort
            for (NatGridViewerColumn col : _columns) {
                if (col == column) continue;

                col.setSortDirection(SWT.NONE);
            }

            int sortDirection = column.getSortDirection();
            // System.out.println("Fetched sort dir is " + (sortDirection == SWT.UP ? "UP" : (sortDirection == SWT.DOWN
            // ? "DOWN" : "NONE")));

            if (_sortColumn == null) {
                _sortColumn = column;
                sortDirection = SWT.UP;
                column.setSortColumn(true);
                column.setSortDirection(sortDirection);
                // System.out.println("Col root sort dir is " + sortDirection);
            } else {
                // same column sorted again
                if (column.equals(_sortColumn)) {
                    switch (sortDirection) {
                        case SWT.UP:
                            sortDirection = SWT.DOWN;
                            break;
                        case SWT.DOWN:
                            sortDirection = SWT.NONE;
                            break;
                        case SWT.NONE:
                            sortDirection = SWT.UP;
                            break;
                    }

                    column.setSortColumn(true);
                    column.setSortDirection(sortDirection);

                    // System.out.println("Switched sort dir is " + sortDirection + " " + newSort);
                } else {
                    _sortColumn.setSortColumn(false);
                    _sortColumn.setSortDirection(SWT.NONE);
                    _sortColumn = column;
                    sortDirection = SWT.UP;
                    column.setSortColumn(true);
                    column.setSortDirection(sortDirection);
                }
            }

            if (sortDirection == SWT.NONE) {
                if (_sortColumn != null) {
                    _sortColumn.setSortColumn(false);
                    _sortColumn.setSortDirection(SWT.NONE);
                }
                // remove comparator, that's it
                _sortedList.setComparator(null);
                // _grid.setModelBodyColumnOrder(colOrder);
                restoreSelection();
                return;
            }

            long updateOne = System.currentTimeMillis();
            _sortedList.setComparator(_genericGridTable.getComparator(index, sortDirection));
            if (_debug) {
                long updateTwo = System.currentTimeMillis();
                System.err.println("Glazed: " + (updateTwo - updateOne)); // NOPMD: debug
            }

            if (_debug) {
                long time3 = System.currentTimeMillis();
                System.err.println("Sort click time: " + (time3 - time1)); // NOPMD: debug
            }

            updateHighlightPostSort();
            // _grid.setModelBodyColumnOrder(colOrder);
            restoreSelection();
            _grid.redrawColumnHeaders();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Checks whether an event x/y location was a click on a column header. This is very useful when you want to make
     * sure whatever event fired was fired from the table area and not a column.
     * 
     * @param x X location of event
     * @param y Y location of event
     * @return true if x/y location was on a column header
     */
    public boolean wasHeaderClick(int x, int y) {
        int headerHeight = _grid.getTotalColumnHeaderHeight();
        if (y <= headerHeight && y >= 0) {
            int col = _grid.getModelGridColumnByX(x);
            if (col != -1) return true;
        }

        return false;
    }

    // saves the selection
    private void rememberSelection() {
        try {
            _selectionMemory.clear();

            List<IDataObject> sel = getSelectedItems();

            for (IDataObject data : sel) {
                _selectionMemory.put(data, getSelectedColumnsFor(data));
                if (!_multiSel) break;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Returns a list of all columns that contains selected cells.
     * 
     * @param obj Object to check
     * @return List of selected integers representing column indexes
     * @throws Exception
     */
    private List<Integer> getSelectedColumnsFor(IDataObject obj) throws Exception {
        List<Integer> cols = new ArrayList<Integer>();
        int row = _filterList.indexOf(obj);
        for (int col = 0; col < getColumnCount(); col++) {
            if (_grid.getSelectionModel().isSelected(row, col)) {
                cols.add(col);
            }
        }

        return cols;
    }

    // restores selections asynchronously
    private void restoreSelection() {
        // needs to run asynchronously
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    _grid.clearSelection();
                    for (IDataObject obj : _selectionMemory.keySet()) {
                        int row = _filterList.indexOf(obj);
                        List<Integer> cols = _selectionMemory.get(obj);
                        for (Integer col : cols) {
                            _grid.getSelectionModel().addSelection(row, col);
                            // no multi selection and no full row, that means single cell only and we're done
                            if (!_multiSel && !_fullRow) break;
                        }

                        // multiple entries can be same object, so we need to break if we don't allow multi selection
                        if (!_multiSel) break;
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
                _selectionMemory.clear();
                _grid.redraw();
            }
        });
    }

    @Override
    public boolean isEvenRow(Object data) {
        return _filterList.indexOf(data) % 2 == 0;
    }

    /**
     * Sorts the columns according to their name either from high to low or low to high. To Unsort columns call
     * {@link #unsortColumns()}.
     * 
     * @param lowToHigh true if to sort low to high, false to sort high to low
     */
    public void sortColumns(boolean lowToHigh) {
        // TODO: Cleanup for nat if we need grouped columns
        try {
            // As we may have to deal with grouped columns, we need to sort
            // those independently from
            // normal columns as otherwise the table will throw an Exception
            // when we try to set an
            // order that doesn't make sense (you can't break out columns from
            // their group parent, they
            // all need to be next to each other). Thus, we sort column groups
            // by themselves, and non-grouped
            // columns by themselves.
            updateTableColumnOrder();

            if (!_columnsSorted) _originalTableOrder = _grid.getModelBodyColumnOrder();

            _columnsSorted = true;

            // get all columns that are in groups
            // GridColumnGroup[] groups = _grid.getColumnGroups();
            // List<NatGridViewerColumn> columnsInGroups = new ArrayList<NatGridViewerColumn>();
            /*
             * for (GridColumnGroup group : groups) { GridColumn[] gcs = group.getColumns(); for (GridColumn gc : gcs) {
             * columnsInGroups.add(getGridColumnFor(gc)); } }
             */
            // get all columns that are not in groups
            List<NatGridViewerColumn> columnsNotInGroups = new ArrayList<NatGridViewerColumn>();
            /*
             * for (GridColumn gc : _grid.getColumns()) { GridViewerColumn ngc = getGridColumnFor(gc); if
             * (columnsInGroups.contains(getGridColumnFor(gc))) continue; columnsNotInGroups.add(ngc); }
             */
            // what columns are frozen? (we don't do frozen grouped columns, for
            // now)
            List<NatGridViewerColumn> frozen = new ArrayList<NatGridViewerColumn>();
            for (NatGridViewerColumn col : columnsNotInGroups) {
                if (col.isFrozen()) frozen.add(col);
            }

            // do sorting on non-grouped
            Collections.sort(columnsNotInGroups);
            if (lowToHigh) Collections.reverse(columnsNotInGroups);

            // re-set frozen cols
            for (NatGridViewerColumn frz : frozen)
                columnsNotInGroups.remove(frz);
            // add them in reverse to index 0
            for (int i = frozen.size() - 1; i >= 0; i--)
                columnsNotInGroups.add(0, frozen.get(i));

            List<Integer> correctOrder = new ArrayList<Integer>();
            for (NatGridViewerColumn col : columnsNotInGroups)
                correctOrder.add(col.getIndex());

            /*
             * // now do sorting on grouped for (GridColumnGroup group : groups) { GridColumn[] gcs =
             * group.getColumns(); List<GridViewerColumn> temp = new ArrayList<GridViewerColumn>(); for (GridColumn gc :
             * gcs) { temp.add(getGridColumnFor(gc)); } // now sort those Collections.sort(temp); if (lowToHigh)
             * Collections.reverse(temp); // add them to the correct order of things for (GridViewerColumn gc : temp)
             * correctOrder.add(gc.getIndex()); }
             */

            // finally, get the indexes and set them on the table
            int[] out = new int[correctOrder.size()];
            for (int i = 0; i < correctOrder.size(); i++)
                out[i] = correctOrder.get(i);

            _grid.setModelBodyColumnOrder(out);

        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    /**
     * Resets column order to the order when it was created
     */
    public void unsortColumns() {
        _columnsSorted = false;

        try {
            _grid.setModelBodyColumnOrder(_originalTableOrder);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Removes all items in the table.
     */
    public void removeAll() {
        setInput(null);
    }

    /**
     * Replaces input on the table with a new set of data.
     * 
     * @param newInput New input to set. null will clear table.
     */
    public void setInput(List<IDataObject> newInput) {
        try {
            if (newInput == null) {
                newInput = new ArrayList<IDataObject>();
            }

            if (_sourceInput.size() != 0) {
                _sourceInput.clear();
            }
            _sourceInput.addAll(newInput);
            _eventList.addAll(newInput);

            _eventList.getReadWriteLock().writeLock().lock();

            // System.err.println("Setting input " + _eventList.size());
            _dataProvider = assembleListDataProvider(_filterList);
            // _model.setBodyConfig(new BodyConfig(_dataProvider));

            _eventList.clear();
            if (newInput.size() != 0) {
                _eventList.addAll(newInput);
            }

            _grid.reset();
            _grid.updateResize(true);

        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            _eventList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Returns a list of all selected points where x is the column and y is the row.
     * 
     * @return Point list
     */
    public List<Point> getSelectedPoints() {
        List<Point> ret = new ArrayList<Point>();
        for (int row = 0; row < getItemCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                NatGridViewerColumn column = findColumn(col);
                if (column == null || column.isHidden()) continue;

                if (_grid.getSelectionModel().isSelected(row, col)) {
                    // convert the column index to the right location before adding it, as this location
                    // may not match the internal location, so they need to match
                    ret.add(new Point(convertColumnIndex(col), row)); // x = col, y = row
                }
            }
        }

        return ret;
    }

    /**
     * Returns the cell text at a given row/column index.
     * 
     * @param row row location
     * @param col column location
     * @param useVisibleLocation whether the column index given is the visible location of the column or the original
     *        location of the column. If a column is moved by the user, its original location may have been 1, but its
     *        visible location is 5. Also see {@link #getVisibleColumnOrder()} for details on visibility.
     * @return String of cell at given index, null if not found
     */
    public String getData(final int row, final int col, final boolean useVisibleLocation) {
        if (_filterList.size() == 0) return null;

        int column = convertColumnIndex(col);

        return _filterList.get(row).getColumnText(column);
    }

    // finds a column by its index
    private NatGridViewerColumn findColumn(final int index) {
        for (NatGridViewerColumn col : _columns) {
            if (col.getIndex() == index) return col;
        }

        return null;
    }

    // converts a column index to the columns actual location index
    private int convertColumnIndex(int location) {
        int[] order = _grid.getModelBodyColumnOrder();
        List<Integer> currentOrder = new ArrayList<Integer>();
        for (int i = 0; i < order.length; i++)
            currentOrder.add(order[i]);

        return currentOrder.indexOf(location);
    }

    /**
     * Returns the actual location of a column if they are reordered etc, as index 1 (for example) may not be located in
     * position 1 anymore despite retaining its original index.
     * 
     * @param col Column to find
     * @return Actual column index location
     */
    public int getActualColumnIndex(int col) {
        return convertColumnIndex(col);
    }

    /**
     * Returns an array of all selected points where x is the column and y is the row.
     * 
     * @return Point array
     */
    public Point[] getCellSelection() {
        List<Point> all = getSelectedPoints();
        Point[] ret = new Point[all.size()];
        for (int i = 0; i < all.size(); i++) {
            ret[i] = all.get(i);
        }
        return ret;
    }

    /**
     * Returns an item at the given row index.
     * 
     * @param row index
     * @return Object
     */
    public IDataObject getItem(int row) {
        return _filterList.get(row);
    }

    /**
     * Returns the number of selected cells in the grid.
     * 
     * @return count
     */
    public int getSelectionCount() {
        return getCellSelectionCount();
    }

    /**
     * Selects all items in the grid.
     */
    public void selectAll() {
        _grid.getSelectionModel().addSelection(new Rectangle(0, 0, _columns.size(), _filterList.size()));
        _grid.redraw();
    }

    public void deselectAll() {
        _grid.getSelectionModel().clearSelection();
        _grid.redraw();
    }

    /**
     * Returns the visible number of rows in the table.
     * 
     * @return row count
     */
    public int getItemCount() {
        return _model.getBodyRowCount();
    }

    /**
     * Returns all selected objects as a list of data objects, or an empty list if none are selected.
     * 
     * @return List of selected data objects
     */
    public IDataObject[] getSelection() {
        try {
            int[] rows = _grid.getSelectionModel().getSelectedRows();
            IDataObject[] ret = new IDataObject[rows.length];
            if (rows.length == 0) return ret;

            for (int i = 0; i < rows.length; i++) {
                ret[i] = _filterList.get(rows[i]);
            }
            return ret;
        } catch (Exception err) {
            err.printStackTrace();
        }

        return new IDataObject[0];
    }

    /**
     * Returns a list of all items currently in the table, in the order they are visibly in.
     * 
     * @return All items currently in the table
     */
    public List<IDataObject> getAllItems() {
        return _filterList;
    }

    /**
     * Returns a list of all items in the table regardless of filter or sort state.
     * 
     * @return All items in table
     */
    public List<IDataObject> getItems() {
        return _eventList;
    }

    /**
     * Same as getSelection() but as a list object instead of an array
     * 
     * @return list of selected items or empty list if none
     */
    public List<IDataObject> getSelectedItems() {
        List<IDataObject> ret = new ArrayList<IDataObject>();
        IDataObject[] objs = getSelection();
        if (objs.length == 0) return ret;

        for (IDataObject obj : objs)
            ret.add(obj);
        return ret;
    }

    /**
     * Returns the number of selected cells in the grid.
     * 
     * @return cell selection count
     */
    public int getCellSelectionCount() {
        int cnt = 0;
        for (int row = 0; row < getItemCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                if (_grid.getSelectionModel().isSelected(row, col)) cnt++;
            }
        }

        return cnt;
    }

    /**
     * Adds an object to the table.
     * 
     * @param item Item to add
     */
    public void add(IDataObject item) {
        add(item, -1);
    }

    /**
     * Adds an object to the table at a certain index.
     * 
     * @param item Item to add
     * @param index Index to create item at
     */
    public void add(IDataObject item, int index) {
        try {
            _eventList.getReadWriteLock().writeLock().lock();
            if (index != -1) {
                _eventList.add(index, item);
            } else {
                _eventList.add(item);
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            _eventList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Removes an object from the table.
     * 
     * @param item Item to remove
     */
    public void remove(IDataObject item) {
        try {
            _eventList.getReadWriteLock().writeLock().lock();
            _eventList.remove(item);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            _eventList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Forces the table to update the row for a given item (to display changed values)
     * 
     * @param item Item to update
     */
    public void update(IDataObject item) {
        try {
            _eventList.getReadWriteLock().writeLock().lock();
            if (_eventList.indexOf(item) == -1) return;

            // not sure this is how it's best done with glazed lists, but it
            // works
            _eventList.set(_eventList.indexOf(item), item);
        } finally {
            _eventList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Replaces one entry with another in the same position of the table. Does nothing if the old object is not found in
     * the table.
     * 
     * @param oldMessage Old object to replace
     * @param newMessage New replacement object
     */
    public void replace(IDataObject oldMessage, IDataObject newMessage) {
        try {
            _eventList.getReadWriteLock().writeLock().lock();
            int index = _eventList.indexOf(oldMessage);
            if (index != -1) {
                _eventList.set(index, newMessage);
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            _eventList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Updates the entire table, forcing all labels to update
     */
    public void updateTable() {
        // this is such a hack, but I haven't found a better way
        try {
            _eventList.getReadWriteLock().writeLock().lock();
            for (int i = 0; i < _eventList.size(); i++) {
                _eventList.set(i, _eventList.get(i));
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            _eventList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * For any direct input modifications, you should use the list returned by this method.
     * 
     * @return List usable for modification
     */
    public List<IDataObject> getInput() {
        return _eventList;
    }

    /**
     * Applies a filter to this table if the Matcher is a {@link DataObjectFilterMatcher}.
     * 
     * @param vf ViewerFilter to apply
     */
    public void addFilter(ViewerFilter vf) {
        if (_activeViewerFilters.contains(vf)) return;

        _activeViewerFilters.add(vf);

        updateFilters();
    }

    /**
     * Removes a filter to this table if the Matcher is a {@link DataObjectFilterMatcher}.
     * 
     * @param vf ViewerFilter to remove
     */
    public void removeFilter(ViewerFilter vf) {
        _activeViewerFilters.remove(vf);

        updateFilters();
    }

    /**
     * Clears all filters from this table if the Matcher is a {@link DataObjectFilterMatcher}.
     */
    public void clearFilters() {
        _activeViewerFilters.clear();
        _tableFilter = null;

        updateFilters();
    }

    /**
     * Updates all filters if the Matcher is a {@link DataObjectFilterMatcher}.
     * 
     * @param vf ViewerFilter that changed, may be null
     */
    public void filterChanged(ViewerFilter vf) {
        updateFilters();
    }

    // updates all filters by setting a new matcher if the old was of the same
    // type, otherwise does nothing.
    // we must set a new matcher as they are immutable
    private void updateFilters() {
        DataObjectFilterMatcher dofm = new DataObjectFilterMatcher(_activeViewerFilters);
        _tableFilter = _genericGridTable.getTableFilter();
        if (_tableFilter != null) {
            dofm.addFilter(_tableFilter);
        }

        filterWasModified(dofm);
        _grid.reset();
        _grid.updateResize(true);
    }

    /**
     * Saves the current filter state so it can be restored with {@link #restoreFilterState()}.
     */
    public void saveFilterState() {
        _savedFilterState = new ArrayList<ViewerFilter>(_activeViewerFilters);
    }

    /**
     * Restores the filter state that was saved with {@link #saveFilterState()}.
     */
    public void restoreFilterState() {
        _activeViewerFilters = new ArrayList<ViewerFilter>(_savedFilterState);
        _savedFilterState.clear();

        updateFilters();
    }

    @Override
    public int getColumnCount() {
        return _columns.size();
    }

    /**
     * Gets the first column that matches the given name. If multiple columns have the same name, the first match is
     * returned.
     * 
     * @param name Column name
     * @return Column or null if none found.
     */
    public NatGridViewerColumn getColumn(String name) {
        for (NatGridViewerColumn col : _columns) {
            if (col.getName().equals(name)) return col;
        }

        return null;
    }

    @Override
    public String getColumnName(int index) {
        if (index > _columns.size() - 1) return null;

        return _columns.get(index).getName();
    }

    @Override
    public NatTable getGrid() {
        return _grid;
    }

    /**
     * Returns a list of all columns. When handed false as parameter, the columns are returned in the order they were
     * created. When given true, the visual order is returned. Hidden columns are always ignored and are always in the
     * returned list. Alternatively you can also use {@link #getColumnsVisualOrdered()}.
     * 
     * @return List of columns
     */
    public List<NatGridViewerColumn> getColumns(boolean visualOrder) {
        if (!visualOrder) {
            return _columns;
        } else {
            return getColumnsVisualOrdered();
        }
    }

    /**
     * Returns all columns regardless of hidden state in the order they are visible in the table.
     * 
     * @return List of columns in visual order
     */
    public List<NatGridViewerColumn> getColumnsVisualOrdered() {
        List<Integer> order = new ArrayList<Integer>();
        int[] orderNow = _grid.getModelBodyColumnOrder();
        for (int i = 0; i < orderNow.length; i++)
            order.add(orderNow[i]);

        List<NatGridViewerColumn> cols = new ArrayList<NatGridViewerColumn>();
        for (Integer i : order) {
            cols.add(_columns.get(i));
        }
        return cols;
    }

    /**
     * Resets the column order to the order it was loaded in.
     */
    public void resetColumnOrder() {
        if (_loadedTableOrder != null) {
            _grid.setModelBodyColumnOrder(_loadedTableOrder);
        }
    }

    /**
     * Shows all hidden columns.
     */
    public void showAllColumns() {
        try {
            // we do it this way as the getHiddenModelBodyColumns cannot be concurrently modified
            while (_grid.getHiddenModelBodyColumns().size() != 0) {
                int col = _grid.getHiddenModelBodyColumns().iterator().next();
                _grid.showModelBodyColumn(col);
                findColumn(col).setHidden(false);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        saveTableState();
    }

    /**
     * Returns any set table id.
     * 
     * @return String or null
     */
    public String getTableId() {
        return _tableId;
    }

    /**
     * Sets an ID on this table to remember it by.
     * 
     * @param tableId Table id, or null.
     */
    public void setTableId(String tableId) {
        _tableId = tableId;
    }

    /**
     * Returns the column at a given column index. Do note that this method does not take column orders or visibilities
     * into account. For getting a column at a visible index, use {@link #getColumnVisibleAt(int)}.
     * 
     * @param index Column index
     * @return GridViewerColumn
     */
    public NatGridViewerColumn getColumn(int index) {
        return _columns.get(index);
    }

    /**
     * Returns the correct column at a visible index in a table. So if column "Security" is moved to column location 5
     * from it's original 0, the visible location would be 5 whereas {@link #getColumn(int)} would return the column at
     * location 0 instead. This method returns the column from the current visible location.
     * 
     * @param index Column index
     * @return GridViewerColumn
     */
    public NatGridViewerColumn getColumnVisibleAt(int index) {
        return _columns.get(convertColumnIndex(index));
    }

    /**
     * Returns the correct index list of all visible columns in the table. This method should be used by any
     * implementation having to fetch data from the _visible_ table.
     * 
     * @return List of visible column indexes
     */
    public List<Integer> getVisibleColumnOrder() {
        int[] cols = _grid.getModelBodyColumnOrder();
        List<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i < cols.length; i++) {
            NatGridViewerColumn col = findColumn(cols[i]);
            if (col.isHidden()) continue;

            ret.add(cols[i]);
        }

        return ret;
    }

    /**
     * Returns the current sort column or null if none.
     * 
     * @return Sort column
     */
    public NatGridViewerColumn getSortColumn() {
        return _sortColumn;
    }

    // updates highlights after a sort
    private void updateHighlightPostSort() {
        if (!_isHighlighting) return;

        highlight(_highlightText, _caseSensitiveHighlight);
    }

    /**
     * Highlights text in table by coloring columns according to matched text and background according to matched row.
     * Entire column text cell is colored and not just the substring of the matched text.
     * 
     * @param text Text to highlight
     * @param caseSensetive Whether the highlight "search" is case sensetive or not
     */
    public void highlight(String text, boolean caseSensetive) {
        _highlightText = text;
        _caseSensitiveHighlight = caseSensetive;
        _grid.redraw();
    }

    public void updateHighlight() {
        _grid.redraw();
    }

    /**
     * Clears the current highlight from the table and restores the highlighted items to their old colors/fonts/etc
     */
    public void clearHighlight() {
        try {
            _isHighlighting = false;
            _highlightText = null;
            _grid.redraw();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Updates the table and refreshes all rows of their colors/fonts etc. If you know which rows you need to update,
     * please consider using one of the more detailed refresh methods as this is the slowest of them (for large data
     * sets).
     */
    public void refreshTable() {
        try {
            if (_columns.size() == 0) return;
            if (_multiColumnSortSelectOrder.size() != 0 && _allowMultiColumnSort) {
                List<Comparator<IDataObject>> compList = new ArrayList<Comparator<IDataObject>>();
                for (NatGridViewerColumn col : _columns) {
                    if (!col.isSortColumn()) continue;
                    int colIndex = (Integer) _sortColumn.getData(COLUMN_INDEX_DATA);
                    _genericGridTable.getComparator(colIndex, col.getSortDirection());
                    compList.add(_genericGridTable.getComparator(colIndex, col.getSortDirection()));
                }
                _sortedList.setComparator(GlazedLists.chainComparators(compList));
            } else {
                NatGridViewerColumn col = _sortColumn;
                if (col == null) col = _columns.get(0);
                int currentSortCol = -1;
                if (_sortColumn != null) {
                    currentSortCol = (Integer) _sortColumn.getData(COLUMN_INDEX_DATA);
                }
                _sortedList.setComparator(_genericGridTable.getComparator(currentSortCol, col.getSortDirection()));
            }

            updateHighlightPostSort();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Tells the table that all filters should be reloaded
     */
    public void filterWasModified() {
        _tableFilter = _genericGridTable.getTableFilter();
        updateFilters();
        refreshTable();
        _grid.reset();
        _grid.updateResize(true);
    }

    /**
     * Updates to a new filter. You may send a pre-existing filter, but ensure it's a new object! Filters need to be
     * immutable (glazed lists are synchronized)
     * 
     * @param newMatcher
     */
    public void filterWasModified(Matcher<IDataObject> newMatcher) {
        try {
            _filterList.getReadWriteLock().writeLock().lock();
            _filterList.setMatcher(newMatcher);
            _genericGridTable.tableFilterModified();
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            _filterList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Forces a save of the current table state if the preference store, table id and allowStateSaving flags are all
     * set. This method is automatically called when needed internally and should not need to be called externally
     * unless an explicit need to do so exists.
     * 
     * @return true if saved
     */
    public boolean saveTableState() {
        return saveTableState(null);
    }

    /**
     * Same as {@link #saveTableState()} but takes a parameter to save this state under a custom name. Name should be
     * void of spaces or special characters.
     * 
     * @param customState Name of custom state or null
     * @return true if saved
     */
    public boolean saveTableState(final String state) {

        String customState = state;

        // save it if we have what we need, otherwise, don't
        if (getTableId() == null || !_allowStateSaving || _store == null) return false;
        if (customState == null) {
            customState = "";
        }
        try {
            int[] colOrder = _grid.getModelBodyColumnOrder();
            StringBuffer strColOrder = new StringBuffer();
            for (int i = 0; i < colOrder.length; i++) {
                strColOrder.append(colOrder[i]);
                strColOrder.append("~");
                strColOrder.append(_columns.get(colOrder[i]).getName());
                if (i != colOrder.length - 1) strColOrder.append("|");
            }
            StringBuffer hiddens = new StringBuffer();
            StringBuffer sizes = new StringBuffer();
            for (int i = 0; i < _columns.size(); i++) {
                NatGridViewerColumn col = _columns.get(i);
                sizes.append(_model.getBodyColumnWidth(i));
                sizes.append("@");
                sizes.append(colOrder[i]);
                sizes.append("~");
                sizes.append(col.getName());
                sizes.append("|");
                if (col.isHidden()) {
                    hiddens.append(col.getIndex());
                    hiddens.append("~");
                    hiddens.append(col.getName());
                    hiddens.append("|");
                }
            }
            _store.setValue(getTableId() + customState + TABLE_COLUMN_ORDER, strColOrder.toString());
            _store.setValue(getTableId() + customState + TABLE_HIDDEN_COLUMNS, hiddens.toString());
            _store.setValue(getTableId() + customState + TABLE_COLUMN_WIDTHS, sizes.toString());
            ((PreferenceStore) _store).save();
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Returns true if the table id has a saved state.
     * 
     * @return false if no table id is set, no store is set, or no state is saved
     */
    public boolean hasSavedTableState() {
        if (getTableId() == null || _store == null) return false;

        // we don't check hidden columns because they may be null even if there
        // is a saved state,
        // the others will never be null on a saved state however (unless the
        // table has no columns)
        String oldColOrder = _store.getString(getTableId() + TABLE_COLUMN_ORDER);
        String oldSizes = _store.getString(getTableId() + TABLE_COLUMN_WIDTHS);

        return (oldColOrder != null && oldSizes != null && oldColOrder.length() > 0 && oldSizes.length() > 0);
    }

    /**
     * Loads the last table state assuming the table has an ID, this method must be called explicitly by code and is not
     * called internally.
     * 
     * @return true if loaded
     */
    public boolean loadTableState() {
        return loadTableState(null);
    }

    public boolean hasColumn(String name) {
        for (NatGridViewerColumn col : _columns) {
            if (name.equals(col.getName())) { return true; }
        }

        return false;
    }

    /**
     * Loads the last table state assuming the table has an ID, this method must be called explicitly by code and is not
     * called internally.
     * 
     * @param customState custom name for custom saved state or null.
     * @return true if loaded
     */
    public boolean loadTableState(final String state) {

        String customState = state;

        if (getTableId() == null || _store == null) return false;
        if (customState == null) {
            customState = "";
        }

        try {
            String oldColOrder = _store.getString(getTableId() + customState + TABLE_COLUMN_ORDER);
            String oldHiddenCols = _store.getString(getTableId() + customState + TABLE_HIDDEN_COLUMNS);
            String oldSizes = _store.getString(getTableId() + customState + TABLE_COLUMN_WIDTHS);

            if (oldColOrder != null) {
                StringTokenizer st = new StringTokenizer(oldColOrder, "|");
                List<Integer> order = new ArrayList<Integer>();
                int cnt = 0;
                while (st.hasMoreTokens()) {
                    StringTokenizer st2 = new StringTokenizer(st.nextToken(), "~");
                    Integer o = Integer.valueOf(st2.nextToken());
                    String colName = st2.nextToken();

                    if (hasColumn(colName)) {
                        order.add(o);
                    }

                    cnt++;
                }

                // if saved data != current data, we need to modify it
                if (order.size() != getColumns(false).size()) {
                    List<Integer> missingColumns = new ArrayList<Integer>();
                    for (int i = 0; i < _columns.size(); i++) {
                        if (!order.contains(i)) missingColumns.add(i);
                    }

                    // add missing columns to the columns, their order will just be last
                    // we might want to correctly place them somehow though (later)
                    order.addAll(missingColumns);
                }
                int[] correctOrder = new int[order.size()];
                for (int i = 0; i < order.size(); i++) {
                    correctOrder[i] = order.get(i);
                }
                _grid.setModelBodyColumnOrder(correctOrder);
            }

            List<NatGridViewerColumn> hidden = new ArrayList<NatGridViewerColumn>();
            if (oldHiddenCols != null) {
                StringTokenizer st = new StringTokenizer(oldHiddenCols, "|");

                while (st.hasMoreTokens()) {
                    StringTokenizer st2 = new StringTokenizer(st.nextToken(), "~");

                    String tok = st2.nextToken();
                    String name = st2.nextToken();

                    if (tok.length() == 0) continue;
                    // number may exist, but the column name is no longer the same, so we ignore
                    if (!hasColumn(name)) continue;

                    int index = Integer.valueOf(tok);
                    NatGridViewerColumn found = findColumn(index);
                    if (found != null && found.isHideable()) {
                        hideColumn(index);
                        hidden.add(found);
                    }
                }
            }
            // show columns that are not hidden
            for (NatGridViewerColumn col : _columns) {
                if (col.isHidden() && !hidden.contains(col)) {
                    showColumn(col);
                }
            }

            if (oldSizes != null) {
                StringTokenizer st = new StringTokenizer(oldSizes, "|");

                List<Integer> sizeList = new ArrayList<Integer>();
                List<Integer> indexList = new ArrayList<Integer>();

                while (st.hasMoreTokens()) {
                    StringTokenizer st2 = new StringTokenizer(st.nextToken(), "~");
                    StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "@");
                    String name = st2.nextToken();
                    String tok = st3.nextToken();
                    String colIndex = st3.nextToken();

                    if (tok.length() == 0) continue;
                    if (!hasColumn(name)) continue;

                    int width = Integer.valueOf(tok);
                    sizeList.add(width);
                    indexList.add(Integer.valueOf(colIndex));
                }

                int cnt = 0;
                for (int col : indexList) {
                    int size = sizeList.get(cnt);
                    // don't allow zero size columns
                    if (size < 1) size = 1;
                    _model.setBodyColumnWidth(col, size);
                    cnt++;
                }

            }

        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Updates the table column order. (callback from column chooser).
     * 
     * @param newOrder
     */
    public void updateColumnOrder(int[] newOrder) {
        try {
            // this should never happen, but we check anyway
            if (newOrder.length != _columns.size()) { return; }

            int cnt = 0;
            for (NatGridViewerColumn col : _columns) {
                col.setIndex(newOrder[cnt]);
                cnt++;
            }
            saveTableState();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Returns the set preference store.
     * 
     * @return Preference store or null if none
     */
    public IPreferenceStore getStore() {
        return _store;
    }

    /**
     * Sets the preference store to be used when saving table states.
     * 
     * @param store Prefrence store or null.
     */
    public void setStore(IPreferenceStore store) {
        _store = store;
    }

    /**
     * Returns whether table state saving is enabled.
     * 
     * @return true if enabled
     */
    public boolean isAllowStateSaving() {
        return _allowStateSaving;
    }

    /**
     * Sets whether state saving is allowed. Remember that the table needs a preference store set as well which can be
     * done via {@link #setStore(IPreferenceStore)}.
     * 
     * @param allowStateSaving true to allow state saving
     */
    public void setAllowStateSaving(boolean allowStateSaving) {
        _allowStateSaving = allowStateSaving;
    }

    /**
     * Hides the column with the given column index.
     * 
     * @param index Column index to hide
     */
    public void hideColumn(int index) {
        _columns.get(index).setHidden(true);
        _grid.hideModelBodyColumn(index);
    }

    /**
     * Shows the column with the given column index.
     * 
     * @param index Column index to show
     */
    public void showColumn(int index) {
        _columns.get(index).setHidden(false);
        _grid.showModelBodyColumn(index);
    }

    /**
     * Hides the specific column.
     * 
     * @param col Column to hide
     */
    public void hideColumn(NatGridViewerColumn col) {
        hideColumn(col.getIndex());
    }

    /**
     * Shows the specific column.
     * 
     * @param col Column to show.
     */
    public void showColumn(NatGridViewerColumn col) {
        showColumn(col.getIndex());
    }

    class ClearHorizontalFreezeAction extends Action {
        public ClearHorizontalFreezeAction() {
            super("Clear horizontal freeze");
        }

        @Override
        public void run() {
            _model.clearFrozenColumns();
            _grid.updateResize(true);
        }

    }

    class SplitVerticallyAction extends Action {
        private NatGridViewerColumn _col;

        public SplitVerticallyAction(NatGridViewerColumn col) {
            super("Freeze horizontal scrollbar on column");
            _col = col;
        }

        @Override
        public void run() {
            _model.setFrozenCols(_col.getIndex() + 1);
            _grid.updateResize(true);
        }

    }

    class ResetColumnWidths extends Action {

        public ResetColumnWidths() {
            super.setText("Reset Column Widths");
            if (_gridColorManager.getResetColumnWidthsActionImage() != null) setImageDescriptor(_gridColorManager.getResetColumnWidthsActionImage());

        }

        @Override
        public void run() {
            boolean doit = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Reset Column Widths",
                    "This will reset column widths back to their default size, are you sure?");
            if (!doit) return;

            for (NatGridViewerColumn col : _columns) {
                _model.setBodyColumnWidth(col.getIndex(), col.getDefaultWidth() <= 0 ? _bodyConfig.getColumnWidthConfig().getDefaultSize() : col.getDefaultWidth());
            }

            _grid.updateResize(true);
            saveTableState();
        }

    }

    class PackColumnWidths extends Action {

        public PackColumnWidths() {
            super("Pack All Columns");
            if (_gridColorManager.getPackAllColumnsActionImage() != null) setImageDescriptor(_gridColorManager.getPackAllColumnsActionImage());
        }

        @Override
        public void run() {
            // don't pack on zero items or columns pack to zero width!
            if (getItemCount() == 0) return;

            for (NatGridViewerColumn col : _columns) {
                _grid.getColumnResizeSupport().resizeModelBodyColumnWidthByMaxText(_model, col.getIndex());
            }

            _grid.updateResize(true);
            saveTableState();
        }

    }

    class ResetColumnOrder extends Action {
        public ResetColumnOrder() {
            super("Reset Column Order");
            if (_gridColorManager.getResetColumnOrderActionImage() != null) {
                setImageDescriptor(_gridColorManager.getResetColumnOrderActionImage());
            }

        }

        @Override
        public void run() {
            resetColumnOrder();
            saveTableState();
        }
    }

    class ShowAllColumns extends Action {
        public ShowAllColumns() {
            super("Show All Columns");
            if (_gridColorManager.getShowAllColumnsActionImage() != null) {
                setImageDescriptor(_gridColorManager.getShowAllColumnsActionImage());
            }
        }

        @Override
        public void run() {
            showAllColumns();
            _grid.updateResize(true);
            saveTableState();
        }
    }

    class HideAction extends Action {
        private NatGridViewerColumn column;

        public HideAction(NatGridViewerColumn col) {
            super("Hide Column '" + col.getName() + "'");
            if (_gridColorManager.getHideColumnActionImage() != null) {
                setImageDescriptor(_gridColorManager.getHideColumnActionImage());
            }
            this.column = col;
        }

        public void run() {
            try {
                column.setHidden(true);
                _grid.hideModelBodyColumn(column.getIndex());
                saveTableState();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    class HideShowAction extends Action {
        private NatGridViewerColumn column;

        public HideShowAction(NatGridViewerColumn col) {
            super(col.getName(), IAction.AS_CHECK_BOX);
            this.column = col;
        }

        @Override
        public void run() {
            try {
                if (column.isHidden()) {
                    _grid.showModelBodyColumn(column.getIndex());
                } else {
                    _grid.hideModelBodyColumn(column.getIndex());
                }
                column.setHidden(!isChecked());

                saveTableState();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

    }

    class ClearAllFilters extends Action {

        public ClearAllFilters() {
            super("Clear All Filters");
        }

        @Override
        public void run() {
            _activeViewerFilters.clear();
            updateFilters();
        }

    }

    class FilterAction extends Action {
        private DataFilter dataFilter;

        public FilterAction(DataFilter df) {
            super(df.getName(), IAction.AS_CHECK_BOX);
            this.dataFilter = df;
        }

        public DataFilter getDataFilter() {
            return dataFilter;
        }

        public void run() {
            if (isChecked()) {
                addFilter(dataFilter);
            } else {
                removeFilter(dataFilter);
            }

            updateFilters();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((dataFilter == null) ? 0 : dataFilter.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            FilterAction other = (FilterAction) obj;
            if (!getOuterType().equals(other.getOuterType())) return false;
            if (dataFilter == null) {
                if (other.dataFilter != null) return false;
            } else if (!dataFilter.equals(other.dataFilter)) return false;
            return true;
        }

        private GlazedNatGridViewer getOuterType() {
            return GlazedNatGridViewer.this;
        }

    }

}
