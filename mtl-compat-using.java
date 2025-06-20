
/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cats.ApplicativeError
 *  cats.package$ApplicativeThrow$
 *  scala.Function1
 *  scala.runtime.BoxesRunTime
 *  scala.runtime.LazyRef
 */
package cats.mtl;

import cats.ApplicativeError;
import cats.mtl.Handle;
import cats.mtl.Handle$;
import cats.mtl.HandleCrossCompat$given_Handle_F_E$2$;
import cats.package;
import java.io.Serializable;
import scala.Function1;
import scala.runtime.BoxesRunTime;
import scala.runtime.LazyRef;

public interface HandleCrossCompat {

    public final class AdHocSyntaxWired<E> {
        public AdHocSyntaxWired() {
            if (HandleCrossCompat.this == null) {
                throw new NullPointerException();
            }
        }

        public final /* synthetic */ HandleCrossCompat cats$mtl$HandleCrossCompat$AdHocSyntaxWired$$$outer() {
            return HandleCrossCompat.this;
        }
    }

    public final class InnerWired<F, E, A> {
        private final Function1<Handle<F, E>, F> body;
        private final /* synthetic */ HandleCrossCompat $outer;

        public InnerWired(HandleCrossCompat $outer, Function1<Handle<F, E>, Object> body) {
            this.body = body;
            if ($outer == null) {
                throw new NullPointerException();
            }
            this.$outer = $outer;
        }

        public F rescue(Function1<E, F> h, ApplicativeError<F, Throwable> x$2) {
            LazyRef lazyRef = new LazyRef();
            Object Marker = new Object();
            return (F)this.cats$mtl$HandleCrossCompat$InnerWired$$_$inner$1(x$2, Marker, this.body.apply((Object)this.given_Handle_F_E$1(lazyRef, x$2, Marker)), h);
        }

        public final /* synthetic */ HandleCrossCompat cats$mtl$HandleCrossCompat$InnerWired$$$outer() {
            return this.$outer;
        }

        public final Object cats$mtl$HandleCrossCompat$InnerWired$$_$inner$1(ApplicativeError x$2$1, Object Marker$1, Object fb, Function1 f) {
            return package.ApplicativeThrow$.MODULE$.apply(x$2$1).handleErrorWith(fb, (Function1 & Serializable)x$1 -> {
                Throwable throwable = x$1;
                if (throwable instanceof Handle.Submarine) {
                    Handle$ cfr_ignored_0 = (Handle$)this.$outer;
                    Handle.Submarine submarine = Handle.Submarine$.MODULE$.unapply((Handle.Submarine)throwable);
                    Object e = submarine._1();
                    Object object = submarine._2();
                    Object e2 = e;
                    if (BoxesRunTime.equals((Object)Marker$1, (Object)object)) {
                        return f.apply(e2);
                    }
                }
                Throwable t = throwable;
                return package.ApplicativeThrow$.MODULE$.apply(x$2$1).raiseError((Object)t);
            });
        }

        private final HandleCrossCompat$given_Handle_F_E$2$ given_Handle_F_E$lzyINIT1$1(LazyRef given_Handle_F_E$lzy1$1, ApplicativeError x$2$3, Object Marker$3) {
            HandleCrossCompat$given_Handle_F_E$2$ handleCrossCompat$given_Handle_F_E$2$;
            LazyRef lazyRef = given_Handle_F_E$lzy1$1;
            synchronized (lazyRef) {
                handleCrossCompat$given_Handle_F_E$2$ = (HandleCrossCompat$given_Handle_F_E$2$)(given_Handle_F_E$lzy1$1.initialized() ? given_Handle_F_E$lzy1$1.value() : given_Handle_F_E$lzy1$1.initialize((Object)new HandleCrossCompat$given_Handle_F_E$2$(x$2$3, Marker$3, this)));
            }
            return handleCrossCompat$given_Handle_F_E$2$;
        }

        private final HandleCrossCompat$given_Handle_F_E$2$ given_Handle_F_E$1(LazyRef given_Handle_F_E$lzy1$2, ApplicativeError x$2$4, Object Marker$4) {
            return (HandleCrossCompat$given_Handle_F_E$2$)(given_Handle_F_E$lzy1$2.initialized() ? given_Handle_F_E$lzy1$2.value() : this.given_Handle_F_E$lzyINIT1$1(given_Handle_F_E$lzy1$2, x$2$4, Marker$4));
        }
    }
}
