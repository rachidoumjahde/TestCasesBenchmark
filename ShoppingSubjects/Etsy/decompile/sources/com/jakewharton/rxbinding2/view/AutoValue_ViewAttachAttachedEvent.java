package com.jakewharton.rxbinding2.view;

import android.support.annotation.NonNull;
import android.view.View;
import org.apache.commons.math3.geometry.VectorFormat;

final class AutoValue_ViewAttachAttachedEvent extends d {
    private final View view;

    AutoValue_ViewAttachAttachedEvent(View view2) {
        if (view2 == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view2;
    }

    @NonNull
    public View view() {
        return this.view;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ViewAttachAttachedEvent{view=");
        sb.append(this.view);
        sb.append(VectorFormat.DEFAULT_SUFFIX);
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        return this.view.equals(((d) obj).view());
    }

    public int hashCode() {
        return this.view.hashCode() ^ 1000003;
    }
}
