package com.gbsoft.smartpillreminder.ui.reminderslist;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

abstract public class ChoiceCapableAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private final ChoiceMode choiceMode;

    ChoiceCapableAdapter(ChoiceMode choiceMode) {
        this.choiceMode = choiceMode;
    }

    public void setSelected(int position, boolean isSelected) {
        choiceMode.setSelected(position, isSelected);
    }

    boolean isSelected(int position) {
        return choiceMode.isSelected(position);
    }

    void onSaveInstanceState(Bundle state) {
        choiceMode.onSaveInstanceState(state);
    }

    void onRestoreInstanceState(Bundle state) {
        choiceMode.onRestoreInstanceState(state);
    }

    int getSelectedCount() {
        return choiceMode.getSelectedCount();
    }

    void clearSelections() {
        choiceMode.clearSelections();
    }

    void visitChecks(ChoiceMode.Visitor visitor) {
        choiceMode.visitChecks(visitor);
    }
}
