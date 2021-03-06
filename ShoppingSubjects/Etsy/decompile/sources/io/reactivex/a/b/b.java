package io.reactivex.a.b;

import android.os.Handler;
import android.os.Message;
import io.reactivex.disposables.Disposable;
import io.reactivex.u;
import io.reactivex.u.c;
import java.util.concurrent.TimeUnit;

/* compiled from: HandlerScheduler */
final class b extends u {
    private final Handler b;

    /* compiled from: HandlerScheduler */
    private static final class a extends c {
        private final Handler a;
        private volatile boolean b;

        a(Handler handler) {
            this.a = handler;
        }

        public Disposable a(Runnable runnable, long j, TimeUnit timeUnit) {
            if (runnable == null) {
                throw new NullPointerException("run == null");
            } else if (timeUnit == null) {
                throw new NullPointerException("unit == null");
            } else if (this.b) {
                return io.reactivex.disposables.b.a();
            } else {
                C0182b bVar = new C0182b(this.a, io.reactivex.d.a.a(runnable));
                Message obtain = Message.obtain(this.a, bVar);
                obtain.obj = this;
                this.a.sendMessageDelayed(obtain, Math.max(0, timeUnit.toMillis(j)));
                if (!this.b) {
                    return bVar;
                }
                this.a.removeCallbacks(bVar);
                return io.reactivex.disposables.b.a();
            }
        }

        public void dispose() {
            this.b = true;
            this.a.removeCallbacksAndMessages(this);
        }

        public boolean isDisposed() {
            return this.b;
        }
    }

    /* renamed from: io.reactivex.a.b.b$b reason: collision with other inner class name */
    /* compiled from: HandlerScheduler */
    private static final class C0182b implements Disposable, Runnable {
        private final Handler a;
        private final Runnable b;
        private volatile boolean c;

        C0182b(Handler handler, Runnable runnable) {
            this.a = handler;
            this.b = runnable;
        }

        public void run() {
            try {
                this.b.run();
            } catch (Throwable th) {
                IllegalStateException illegalStateException = new IllegalStateException("Fatal Exception thrown on Scheduler.", th);
                io.reactivex.d.a.a((Throwable) illegalStateException);
                Thread currentThread = Thread.currentThread();
                currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, illegalStateException);
            }
        }

        public void dispose() {
            this.c = true;
            this.a.removeCallbacks(this);
        }

        public boolean isDisposed() {
            return this.c;
        }
    }

    b(Handler handler) {
        this.b = handler;
    }

    public Disposable a(Runnable runnable, long j, TimeUnit timeUnit) {
        if (runnable == null) {
            throw new NullPointerException("run == null");
        } else if (timeUnit == null) {
            throw new NullPointerException("unit == null");
        } else {
            C0182b bVar = new C0182b(this.b, io.reactivex.d.a.a(runnable));
            this.b.postDelayed(bVar, Math.max(0, timeUnit.toMillis(j)));
            return bVar;
        }
    }

    public c a() {
        return new a(this.b);
    }
}
