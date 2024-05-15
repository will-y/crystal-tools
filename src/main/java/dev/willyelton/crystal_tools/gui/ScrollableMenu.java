package dev.willyelton.crystal_tools.gui;

public interface ScrollableMenu {
    int getRowIndexForScroll(float scrollOffset);

    float getScrollForRowIndex(int row);

    void scrollTo(int row);

    void setMaxRows(int maxRows);

    boolean canScroll();

    float subtractInputFromScroll(float scrollOffset, double delta);

    int getRows();
}
