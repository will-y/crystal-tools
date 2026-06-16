package dev.willyelton.crystal.core.common.inventory.container;

public interface ScrollableMenu {
    int getRowIndexForScroll(float scrollOffset);

    float getScrollForRowIndex(int row);

    void scrollTo(int row);

    void setMaxRows(int maxRows);

    boolean canScroll();

    int getRows();

    void setUpSlots();
}
