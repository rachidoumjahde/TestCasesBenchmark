package com.google.android.gms.internal.measurement;

abstract class zzhj extends zzhi {
    private boolean zzvn;

    zzhj(zzgn zzgn) {
        super(zzgn);
        this.zzacv.zzb(this);
    }

    /* access modifiers changed from: 0000 */
    public final boolean isInitialized() {
        return this.zzvn;
    }

    /* access modifiers changed from: protected */
    public final void zzch() {
        if (!isInitialized()) {
            throw new IllegalStateException("Not initialized");
        }
    }

    public final void zzgm() {
        if (this.zzvn) {
            throw new IllegalStateException("Can't initialize twice");
        }
        zzgo();
        this.zzacv.zzkf();
        this.zzvn = true;
    }

    /* access modifiers changed from: protected */
    public abstract boolean zzgn();

    /* access modifiers changed from: protected */
    public void zzgo() {
    }

    public final void zzm() {
        if (this.zzvn) {
            throw new IllegalStateException("Can't initialize twice");
        } else if (!zzgn()) {
            this.zzacv.zzkf();
            this.zzvn = true;
        }
    }
}
