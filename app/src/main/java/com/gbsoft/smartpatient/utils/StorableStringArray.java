package com.gbsoft.smartpatient.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorableStringArray extends ArrayList<String> {
    public @NotNull String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : this) {
            stringBuilder.append(str);
            stringBuilder.append("$");
        }
        return stringBuilder.toString();
    }

    public List<String> toStringArray(String strArray) {
        List<String> list = new ArrayList<>();
        String[] splitted = strArray.split("\\$");
        Collections.addAll(list, splitted);
        return list;
    }
}
