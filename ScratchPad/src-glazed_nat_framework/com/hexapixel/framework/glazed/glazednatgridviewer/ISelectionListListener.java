package com.hexapixel.framework.glazed.glazednatgridviewer;

public interface ISelectionListListener {
    void itemsSelected(NatSelectionList selectionList, Object[] addedItems);

    void itemsRemoved(NatSelectionList selectionList, Object[] removedItems);

    void itemsMoved(NatSelectionList selectionList, int[] oldIndexes,
            int[] newIndexes);
}
