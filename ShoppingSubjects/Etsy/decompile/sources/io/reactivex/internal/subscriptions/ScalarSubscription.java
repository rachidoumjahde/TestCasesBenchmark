package io.reactivex.internal.subscriptions;

import io.reactivex.internal.a.d;
import java.util.concurrent.atomic.AtomicInteger;
import org.reactivestreams.c;

public final class ScalarSubscription<T> extends AtomicInteger implements d<T> {
    static final int CANCELLED = 2;
    static final int NO_REQUEST = 0;
    static final int REQUESTED = 1;
    private static final long serialVersionUID = -3830916580126663321L;
    final c<? super T> subscriber;
    final T value;

    public int requestFusion(int i) {
        return i & 1;
    }

    public ScalarSubscription(c<? super T> cVar, T t) {
        this.subscriber = cVar;
        this.value = t;
    }

    public void request(long j) {
        if (SubscriptionHelper.validate(j) && compareAndSet(0, 1)) {
            c<? super T> cVar = this.subscriber;
            cVar.onNext(this.value);
            if (get() != 2) {
                cVar.onComplete();
            }
        }
    }

    public void cancel() {
        lazySet(2);
    }

    public boolean isCancelled() {
        return get() == 2;
    }

    public boolean offer(T t) {
        throw new UnsupportedOperationException("Should not be called!");
    }

    public boolean offer(T t, T t2) {
        throw new UnsupportedOperationException("Should not be called!");
    }

    public T poll() {
        if (get() != 0) {
            return null;
        }
        lazySet(1);
        return this.value;
    }

    public boolean isEmpty() {
        return get() != 0;
    }

    public void clear() {
        lazySet(1);
    }
}