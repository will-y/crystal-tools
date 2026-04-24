package dev.willyelton.crystal_tools.api.common.inventory.container;

public interface ScrollableMenu {
    int getRowIndexForScroll(float scrollOffset);

    float getScrollForRowIndex(int row);

    void scrollTo(int row);

    void setMaxRows(int maxRows);

    boolean canScroll();

    int getRows();

    void setUpSlots();
}
