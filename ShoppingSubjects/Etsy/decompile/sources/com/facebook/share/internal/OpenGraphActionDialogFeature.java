package com.facebook.share.internal;

import com.facebook.internal.e;

public enum OpenGraphActionDialogFeature implements e {
    OG_ACTION_DIALOG(20130618);
    
    private int minVersion;

    public String getAction() {
        return "com.facebook.platform.action.request.OGACTIONPUBLISH_DIALOG";
    }

    private OpenGraphActionDialogFeature(int i) {
        this.minVersion = i;
    }

    public int getMinVersion() {
        return this.minVersion;
    }
}
