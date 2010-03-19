package com.hexapixel.framework.glazed.glazednatgridviewer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class NatSelectionList extends Composite {

    private final ListViewer _availableListViewer;
    private final ListViewer _selectedListViewer;
    private List             _availableList;
    private List             _selectedList;
    private ListenerList     _listeners;

    private Image            _left;
    private Image            _right;
    private Image            _up;
    private Image            _down;

    private Button           _addButton;
    private Button           _removeButton;
    private Button           _upButton;
    private Button           _downButton;

    public NatSelectionList(Composite parent, int style, String availableLabel, String selectedLabel, Image left, Image right, Image up, Image down) {
        super(parent, style);

        _left = left;
        _right = right;
        _up = up;
        _down = down;

        _listeners = new ListenerList();

        setLayout(new GridLayout(4, false));

        createLabels(this, availableLabel, selectedLabel);

        _availableList = new List(this, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        _availableListViewer = new ListViewer(_availableList);

        GridData gridData = GridDataFactory.fillDefaults().grab(true, true).create();
        _availableList.setLayoutData(gridData);
        _availableList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                addSelected();
                _addButton.setEnabled((_availableList.getSelectionCount() > 0));
            }

            @Override
            public void mouseDown(MouseEvent e) {
                _addButton.setEnabled((_availableList.getSelectionCount() > 0));
            }

            
            
        });

        _availableList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.character == ' ') addSelected();
            }
        });
        
        Composite buttonComposite = new Composite(this, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(1, true));

        _addButton = new Button(buttonComposite, SWT.PUSH);
        _addButton.setEnabled(false);
        _addButton.setImage(_right);
        gridData = GridDataFactory.fillDefaults().grab(false, true).align(SWT.CENTER, SWT.CENTER).create();
        _addButton.setLayoutData(gridData);
        _addButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                addSelected();
                _addButton.setEnabled((_availableList.getSelectionCount() > 0));
            }

        });

        _removeButton = new Button(buttonComposite, SWT.PUSH);
        _removeButton.setImage(_left);
        _removeButton.setEnabled(false);
        gridData = GridDataFactory.copyData(gridData);
        _removeButton.setLayoutData(gridData);
        _removeButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                removeSelected();
                _removeButton.setEnabled(_selectedList.getSelectionCount() > 0);
            }

        });

        _selectedList = new List(this, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        _selectedListViewer = new ListViewer(_selectedList);

        gridData = GridDataFactory.fillDefaults().grab(true, true).create();
        _selectedList.setLayoutData(gridData);
        _selectedList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                removeSelected();
                _removeButton.setEnabled(_selectedList.getSelectionCount() > 0);
                _upButton.setEnabled(_selectedList.getSelectionCount() > 0);
                _downButton.setEnabled(_selectedList.getSelectionCount() > 0);
            }

            @Override
            public void mouseDown(MouseEvent e) {
                _removeButton.setEnabled(_selectedList.getSelectionCount() > 0);
                _upButton.setEnabled(_selectedList.getSelectionCount() > 0);
                _downButton.setEnabled(_selectedList.getSelectionCount() > 0);
            }
        });

        _selectedList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean controlMask = (e.stateMask & SWT.CONTROL) == SWT.CONTROL;
                if (controlMask && e.keyCode == SWT.ARROW_UP) {
                    moveSelectedUp();
                    e.doit = false;
                } else if (controlMask && e.keyCode == SWT.ARROW_DOWN) {
                    moveSelectedDown();
                    e.doit = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

                if (e.character == ' ') removeSelected();
            }
        });

        Composite upDownbuttonComposite = new Composite(this, SWT.NONE);
        upDownbuttonComposite.setLayout(new GridLayout(1, true));

        _upButton = new Button(upDownbuttonComposite, SWT.PUSH);
        _upButton.setImage(_up);
        gridData = GridDataFactory.fillDefaults().grab(false, true).align(SWT.CENTER, SWT.CENTER).create();
        _upButton.setLayoutData(gridData);
        _upButton.setEnabled(false);
        _upButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                moveSelectedUp();
                _upButton.setEnabled(_selectedList.getSelectionCount() > 0);
            }

        });

        _downButton = new Button(upDownbuttonComposite, SWT.PUSH);
        _downButton.setImage(_down);
        _downButton.setEnabled(false);
        gridData = GridDataFactory.copyData(gridData);
        _downButton.setLayoutData(gridData);
        _downButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                moveSelectedDown();
                _downButton.setEnabled(_selectedList.getSelectionCount() > 0);
            }

        });

    }

    private void createLabels(Composite parent, String availableStr, String selectedStr) {
        boolean availableSet = StringUtils.isNotEmpty(availableStr);
        boolean selectedSet = StringUtils.isNotEmpty(selectedStr);

        if (availableSet && selectedSet) {
            if (availableSet) {
                Label availableLabel = new Label(parent, SWT.NONE);
                availableLabel.setText(availableStr);
                GridDataFactory.swtDefaults().applyTo(availableLabel);
            }

            Label filler = new Label(parent, SWT.NONE);
            GridDataFactory.swtDefaults().span(availableSet ? 1 : 2, 1).applyTo(filler);

            if (selectedSet) {
                Label selectedLabel = new Label(parent, SWT.NONE);
                selectedLabel.setText(selectedStr);
                GridDataFactory.swtDefaults().span(2, 1).applyTo(selectedLabel);
            }
        }
    }

    public void addSelectionListListener(ISelectionListListener listener) {
        _listeners.add(listener);
    }

    public void removeSelectionListListener(ISelectionListListener listener) {
        _listeners.remove(listener);
    }

    protected final void fireItemsSelected(Object[] addedItems) {
        for (Object listener : _listeners.getListeners()) {
            ((ISelectionListListener) listener).itemsSelected(this, addedItems);
        }
    }

    protected final void fireItemsRemoved(Object[] removedItems) {
        for (Object listener : _listeners.getListeners()) {
            ((ISelectionListListener) listener).itemsRemoved(this, removedItems);
        }
    }

    protected final void fireItemsMoved(int[] oldIndexes, int[] newIndexes) {
        for (Object listener : _listeners.getListeners()) {
            ((ISelectionListListener) listener).itemsMoved(this, oldIndexes, newIndexes);
        }
    }

    public ListViewer getAvailableListViewer() {
        return _availableListViewer;
    }

    public ListViewer getSelectedListViewer() {
        return _selectedListViewer;
    }

    private void addSelected() {
        int[] selected = _availableList.getSelectionIndices();
        Object[] selectedObjects = new Object[selected.length];

        for (int i = 0; i < selected.length; i++) {
            Object item = _availableListViewer.getElementAt(selected[i]);
            selectedObjects[i] = item;
        }

        int selectionIndex = _selectedList.getSelectionIndex();

        if (selectionIndex < 0) _selectedListViewer.add(selectedObjects);
        else {
            for (int i = 0; i < selectedObjects.length; i++) {
                _selectedListViewer.insert(selectedObjects[i], selectionIndex + i + 1);
            }
        }

        _availableListViewer.remove(selectedObjects);

        fireItemsSelected(selectedObjects);
    }

    private void removeSelected() {
        int[] selected = _selectedList.getSelectionIndices();
        Object[] selectedObjects = new Object[selected.length];

        for (int i = 0; i < selected.length; i++) {
            Object item = _selectedListViewer.getElementAt(selected[i]);
            selectedObjects[i] = item;
        }

        _availableListViewer.add(selectedObjects);
        _selectedListViewer.remove(selectedObjects);

        fireItemsRemoved(selectedObjects);
    }

    private void moveSelectedUp() {
        int[] selected = _selectedList.getSelectionIndices();
        int[] newSelectionIndex = new int[selected.length];

        boolean changed = false;

        for (int i = 0; i < selected.length; i++) {
            Object item = _selectedListViewer.getElementAt(selected[i]);

            if (selected[i] > i) {
                _selectedListViewer.remove(item);
                _selectedListViewer.insert(item, selected[i] - 1);
                newSelectionIndex[i] = selected[i] - 1;
                changed = true;
            } else {
                newSelectionIndex[i] = selected[i];
            }
        }

        if (changed) {
            _selectedList.setSelection(newSelectionIndex);

            fireItemsMoved(selected, newSelectionIndex);
        }
    }

    private void moveSelectedDown() {
        int[] selected = _selectedList.getSelectionIndices();
        int[] newSelectionIndex = new int[selected.length];

        boolean changed = false;

        for (int i = selected.length - 1; i >= 0; i--) {
            Object item = _selectedListViewer.getElementAt(selected[i]);

            if (selected[i] < _selectedList.getItemCount() - (selected.length - i)) {
                _selectedListViewer.remove(item);
                _selectedListViewer.insert(item, selected[i] + 1);
                newSelectionIndex[i] = selected[i] + 1;
                changed = true;
            } else {
                newSelectionIndex[i] = selected[i];
            }
        }

        if (changed) {
            _selectedList.setSelection(newSelectionIndex);
            _selectedList.redraw();

            fireItemsMoved(selected, newSelectionIndex);
        }
    }

    public Object[] getSelectedItems() {
        Object[] items = new Object[_selectedList.getItemCount()];

        for (int i = 0; i < items.length; i++) {
            items[i] = _selectedListViewer.getElementAt(i);
        }

        return items;
    }

    @Override
    public void dispose() {
        _listeners.clear();

        super.dispose();
    }

}
