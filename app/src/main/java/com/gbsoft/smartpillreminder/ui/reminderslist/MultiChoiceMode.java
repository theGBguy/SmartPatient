package com.gbsoft.smartpillreminder.ui.reminderslist;

import android.os.Bundle;
import android.util.SparseBooleanArray;

public class MultiChoiceMode implements ChoiceMode {
    private final static String KEY_CHECK_STATES = "checkStates";
    private ParcelableSparseBooleanArray checkStates;

    MultiChoiceMode() {
        checkStates = new ParcelableSparseBooleanArray();
    }

    @Override
    public void setSelected(int position, boolean isSelected) {
        if (checkStates != null) {
            if (isSelected)
                checkStates.put(position, true);
            else
                checkStates.delete(position);
        }
    }

    @Override
    public boolean isSelected(int position) {
        if (checkStates != null)
            return checkStates.get(position, false);
        else return false;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        if (checkStates != null)
            state.putParcelable(KEY_CHECK_STATES, checkStates);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        if (state.getParcelable(KEY_CHECK_STATES) != null)
            checkStates = state.getParcelable(KEY_CHECK_STATES);
    }

    @Override
    public int getSelectedCount() {
        if (checkStates != null)
            return checkStates.size();
        else
            return -1;
    }

    @Override
    public void clearSelections() {
        if (checkStates != null)
            checkStates.clear();
    }

    @Override
    public void visitChecks(Visitor v) {
        if (checkStates != null) {
            SparseBooleanArray array = checkStates.clone();
            for (int i = 0; i < array.size(); i++) {
                v.onSelectedPosition(array.keyAt(i));
            }
        }
    }
}
