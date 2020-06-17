package com.gbsoft.smartpillreminder.ui.reminderslist;

import android.os.Bundle;

public interface ChoiceMode {
    void setSelected(int position, boolean isSelected);

    boolean isSelected(int position);

    void onSaveInstanceState(Bundle state);

    void onRestoreInstanceState(Bundle state);

    int getSelectedCount();

    void clearSelections();

    void visitChecks(Visitor v);

    interface Visitor {
        void onSelectedPosition(int position);
    }
}
