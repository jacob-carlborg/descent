package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import net.sourceforge.nattable.data.IColumnHeaderLabelProvider;
import net.sourceforge.nattable.painter.cell.ICellPainter;
import net.sourceforge.nattable.renderer.DefaultColumnHeaderRenderer;
import net.sourceforge.nattable.sorting.ISortingDirectionChangeListener;
import net.sourceforge.nattable.sorting.SortingDirection;
import net.sourceforge.nattable.typeconfig.style.ColumnHeaderStyleConfig;
import net.sourceforge.nattable.typeconfig.style.DisplayModeEnum;
import net.sourceforge.nattable.typeconfig.style.IStyleConfig;
import net.sourceforge.nattable.util.GUIHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer;
import com.hexapixel.framework.glazed.glazednatgridviewer.NatGridViewerColumn;

public class NatColumnHeaderRenderer extends DefaultColumnHeaderRenderer {

    NatHeaderColumnCellPainter leftCellPainter;
    NatHeaderColumnCellPainter centerCellPainter;
    NatHeaderColumnCellPainter rightCellPainter;
    NatHeaderColumnCellPainter multiLeftCellPainter;
    NatHeaderColumnCellPainter multiCenterCellPainter;
    NatHeaderColumnCellPainter multiRightCellPainter;

    GlazedNatGridViewer           _viewer;

    HeaderStyleConfig          _headerStyleConfig = new HeaderStyleConfig(false);
    HeaderStyleConfig          _headerStyleConfigSelected = new HeaderStyleConfig(true);

    public NatColumnHeaderRenderer(GlazedNatGridViewer viewer, IColumnHeaderLabelProvider labelProvider) {
        super(labelProvider);
        _viewer = viewer;

        leftCellPainter = new NatHeaderColumnCellPainter(_viewer, SWT.LEFT | SWT.BORDER);
        centerCellPainter = new NatHeaderColumnCellPainter(_viewer, SWT.CENTER | SWT.BORDER);
        rightCellPainter = new NatHeaderColumnCellPainter(_viewer, SWT.RIGHT | SWT.BORDER);
        multiLeftCellPainter = new NatHeaderColumnCellPainter(_viewer, SWT.LEFT | SWT.BORDER, true);
        multiCenterCellPainter = new NatHeaderColumnCellPainter(_viewer, SWT.CENTER | SWT.BORDER, true);
        multiRightCellPainter = new NatHeaderColumnCellPainter(_viewer, SWT.RIGHT | SWT.BORDER, true);

    }

    @Override
    public ICellPainter getCellPainter(int row, int col) {
        NatGridViewerColumn column = _viewer.getColumn(col);
        if (_viewer.isActiveMultiSortColumn(column)) {
            switch (column.getHorzontalHeaderCellAlignment()) {
                default:
                case SWT.LEFT:
                    return multiLeftCellPainter;
                case SWT.CENTER:
                    return multiCenterCellPainter;
                case SWT.RIGHT:
                    return multiRightCellPainter;
            }
        } else {
            switch (column.getHorzontalHeaderCellAlignment()) {
                default:
                case SWT.LEFT:
                    return leftCellPainter;
                case SWT.CENTER:
                    return centerCellPainter;
                case SWT.RIGHT:
                    return rightCellPainter;
            }
        }
    }

    @Override
    public IStyleConfig getStyleConfig(String displayMode, int row, int col) {
        if (displayMode.equals(DisplayModeEnum.SELECT.toString()))
            return _headerStyleConfigSelected;
            
        return _headerStyleConfig;
        
    }

    @Override
    public void sortingDirectionChanged(SortingDirection[] directions) {
        if (_headerStyleConfig instanceof ISortingDirectionChangeListener) {
            ISortingDirectionChangeListener sortingColumnHeaderStyleConfig = (ISortingDirectionChangeListener) _headerStyleConfig;
            sortingColumnHeaderStyleConfig.sortingDirectionChanged(directions);
        }
    }

    // sort images go here
    class HeaderStyleConfig extends ColumnHeaderStyleConfig {

        private static final long serialVersionUID = -2239606968530051776L;
        SortingDirection[]        _sortingDirections;

        private boolean           _selected;

        public HeaderStyleConfig(boolean selected) {
            super();
            _selected = selected;
        }

        @Override
        public Image getImage(int row, int col) {

            Image up = _viewer.getVisualConfig().getUpSortImage();
            Image down = _viewer.getVisualConfig().getDownSortImage();

            if (up == null) {
                up = GUIHelper.UP_IMAGE;
            }
            if (down == null) {
                down = GUIHelper.DOWN_IMAGE;
            }

            if (up == null && down == null) return null;

            if (_viewer.isMultiSorting()) {
                // this is for multi sort, we show sort images as user clicks columns
                NatGridViewerColumn column = _viewer.getColumn(col);
                if (_viewer.isActiveMultiSortColumn(column)) {
                    if (column.getSortDirection() == SWT.UP) {
                        return up;   
                    }
                    else if (column.getSortDirection() == SWT.DOWN) {
                        return down;
                    }
                    else {
                        return null;
                    }
                }
            } else {
               /* single sort (this code doesn't work properly. It gets confused when same column is sorted over and over)
                * leaving it in for now in case nat updates their code -- cre
                if (_sortingDirections != null) {
                    for (int i = 0; i < _sortingDirections.length; i++) {
                        if (_sortingDirections[i].getColumn() == col) {
                            NatGridViewerColumn column = _viewer.getColumn(col);
                            if (column.getSortDirection() == SWT.UP) { 
                                return up;
                            }
                            else if (column.getSortDirection() == SWT.DOWN) {
                                return down;
                            }               
                            else {
                                return null;
                            }
                        }
                    }
                }
                else {*/
                    // column is probably pre-sorted, so show icon if it is
                    if (_viewer.getColumn(col).isSortColumn()) {
                        NatGridViewerColumn column = _viewer.getColumn(col);
                        if (column.getSortDirection() == SWT.UP) { 
                            return up;
                        }
                        else if (column.getSortDirection() == SWT.DOWN) {
                            return down;
                        }
                        else {
                            return null;
                        }
                    }
             //   }
            }
            return null;
        }

        @Override
        public Color getForegroundColor(int row, int col) {
            if (_selected)
                return _viewer.getVisualConfig().getColumnHeaderForegroundSelected();
            else
                return _viewer.getVisualConfig().getColumnHeaderForegroundColor();
        }

        public void sortingDirectionChanged(SortingDirection[] sortingDirections) {
            _sortingDirections = sortingDirections;
        }

    }
}
