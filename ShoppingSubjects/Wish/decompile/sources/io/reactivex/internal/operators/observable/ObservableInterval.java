package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableInterval extends Observable<Long> {
    final long initialDelay;
    final long period;
    final Scheduler scheduler;
    final TimeUnit unit;

    static final class IntervalObserver extends AtomicReference<Disposable> implements Disposable, Runnable {
        final Observer<? super Long> actual;
        long count;

        IntervalObserver(Observer<? super Long> observer) {
            this.actual = observer;
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }

        public void run() {
            if (get() != DisposableHelper.DISPOSED) {
                Observer<? super Long> observer = this.actual;
                long j = this.count;
                this.count = j + 1;
                observer.onNext(Long.valueOf(j));
            }
        }

        public void setResource(Disposable disposable) {
            DisposableHelper.setOnce(this, disposable);
        }
    }

    public ObservableInterval(long j, long j2, TimeUnit timeUnit, Scheduler scheduler2) {
        this.initialDelay = j;
        this.period = j2;
        this.unit = timeUnit;
        this.scheduler = scheduler2;
    }

    public void subscribeActual(Observer<? super Long> observer) {
        IntervalObserver intervalObserver = new IntervalObserver(observer);
        observer.onSubscribe(intervalObserver);
        intervalObserver.setResource(this.scheduler.schedulePeriodicallyDirect(intervalObserver, this.initialDelay, this.period, this.unit));
    }
}
