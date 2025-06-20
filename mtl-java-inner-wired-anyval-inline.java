/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cats.Applicative
 *  cats.Applicative$
 *  cats.ApplicativeError
 *  cats.Eval
 *  cats.Eval$
 *  cats.Functor
 *  cats.Monad
 *  cats.MonadError
 *  cats.data.EitherT
 *  cats.data.EitherT$
 *  cats.mtl.Handle
 *  cats.mtl.Handle$
 *  cats.mtl.Handle$Submarine
 *  cats.mtl.InnerWired$
 *  cats.mtl.Raise
 *  cats.mtl.syntax.RaiseOps$
 *  cats.mtl.syntax.all$
 *  cats.package$ApplicativeThrow$
 *  cats.syntax.ApplicativeIdOps$
 *  cats.syntax.package$all$
 *  scala.Function1
 *  scala.MatchError
 *  scala.runtime.BoxesRunTime
 *  scala.runtime.LazyRef
 *  scala.runtime.ModuleSerializationProxy
 */
import cats.Applicative;
import cats.Applicative$;
import cats.ApplicativeError;
import cats.Eval;
import cats.Eval$;
import cats.Functor;
import cats.Monad;
import cats.MonadError;
import cats.data.EitherT;
import cats.data.EitherT$;
import cats.mtl.Handle;
import cats.mtl.Handle$;
import cats.mtl.InnerWired$;
import cats.mtl.Raise;
import cats.mtl.syntax.RaiseOps$;
import cats.mtl.syntax.all$;
import cats.package;
import cats.syntax.ApplicativeIdOps$;
import cats.syntax.package;
import java.io.Serializable;
import scala.Function1;
import scala.MatchError;
import scala.runtime.BoxesRunTime;
import scala.runtime.LazyRef;
import scala.runtime.ModuleSerializationProxy;

public final class mtl$minussubmarine$package$
implements Serializable {
    public static final mtl.minussubmarine.package.given_Handle_F_Error_type$ given_Handle_F_Error_type;
    public static final mtl$minussubmarine$package$ MODULE$;

    private mtl$minussubmarine$package$() {
    }

    static {
        MODULE$ = new mtl$minussubmarine$package$();
    }

    private Object writeReplace() {
        return new ModuleSerializationProxy(mtl$minussubmarine$package$.class);
    }

    public EitherT<Eval, Throwable, String> test() {
        Handle$ HandleCrossCompat_this = Handle$.MODULE$;

        Function1 & Serializable InnerWired_this = (Function1 & Serializable)a -> {
            Error$ error$ = (Error$)all$.MODULE$.toRaiseOps((Object)Error$.MODULE$);
            return (EitherT)package.all$.MODULE$.toFunctorOps(RaiseOps$.MODULE$.raise$extension((Object)error$, (Raise)a), (Functor)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval())).as((Object)"nope");
        };

        MonadError x$2$proxy1 = EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval());
        LazyRef lazyRef = new LazyRef();
        Object Marker = new Object();

        return this.mtl$minussubmarine$package$$$_$inner$1(x$2$proxy1, InnerWired_this, Marker, (EitherT)InnerWired$.MODULE$.inline$body$extension((Function1)InnerWired_this).apply((Object)this.hfe$1(lazyRef, x$2$proxy1, InnerWired_this, Marker)), (Function1 & Serializable)x$1 -> {
            Error$ error$ = x$1;
            if (Error$.MODULE$.equals(error$)) {
                String string = (String)package.all$.MODULE$.catsSyntaxApplicativeId((Object)"error");
                return (EitherT)ApplicativeIdOps$.MODULE$.pure$extension((Object)string, (Applicative)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval()));
            }
            throw new MatchError((Object)error$);
        });
    }

    public final EitherT mtl$minussubmarine$package$$$_$inner$1(MonadError x$2$proxy1$1, Function1 InnerWired_this$1, Object Marker$1, EitherT fb, Function1 f) {
        return (EitherT)package.ApplicativeThrow$.MODULE$.apply((ApplicativeError)x$2$proxy1$1).handleErrorWith((Object)fb, (Function1 & Serializable)x$1 -> {
            Throwable throwable = x$1;
            if (throwable instanceof Handle.Submarine) {
                Handle.Submarine submarine = InnerWired$.MODULE$.inline$Submarine$i1$extension(InnerWired_this$1, Handle$.MODULE$).unapply((Handle.Submarine)throwable);
                Object object = submarine._1();
                Object object2 = submarine._2();
                Object e = object;
                if (BoxesRunTime.equals((Object)Marker$1, (Object)object2)) {
                    return (EitherT)f.apply((Object)((Error$)e));
                }
            }
            Throwable t = throwable;
            return (EitherT)package.ApplicativeThrow$.MODULE$.apply((ApplicativeError)x$2$proxy1$1).raiseError((Object)t);
        });
    }

    private final Handle hfe$lzyINIT1$1(LazyRef hfe$lzy1$1, MonadError x$2$proxy1$3, Function1 InnerWired_this$3, Object Marker$3) {
        Handle handle;
        LazyRef lazyRef = hfe$lzy1$1;
        synchronized (lazyRef) {
            handle = (Handle)(hfe$lzy1$1.initialized() ? hfe$lzy1$1.value() : hfe$lzy1$1.initialize((Object)new Handle<?, Error$>(x$2$proxy1$3, InnerWired_this$3, Marker$3){
                private final MonadError x$2$proxy1$4;
                private final Function1 InnerWired_this$4;
                private final Object Marker$4;
                {
                    this.x$2$proxy1$4 = x$2$proxy1$6;
                    this.InnerWired_this$4 = InnerWired_this$6;
                    this.Marker$4 = Marker$6;
                }

                public Applicative applicative() {
                    return Applicative$.MODULE$.apply((Applicative)this.x$2$proxy1$4);
                }

                public EitherT raise(Error$ e) {
                    return (EitherT)package.ApplicativeThrow$.MODULE$.apply((ApplicativeError)this.x$2$proxy1$4).raiseError((Object)InnerWired$.MODULE$.inline$Submarine$i2$extension(this.InnerWired_this$4, Handle$.MODULE$).apply((Object)e, this.Marker$4));
                }

                public EitherT handleWith(EitherT fb, Function1 f) {
                    return mtl$minussubmarine$package$.MODULE$.mtl$minussubmarine$package$$$_$inner$1(this.x$2$proxy1$4, this.InnerWired_this$4, this.Marker$4, fb, f);
                }
            }));
        }
        return handle;
    }

    private final Handle hfe$1(LazyRef hfe$lzy1$2, MonadError x$2$proxy1$5, Function1 InnerWired_this$5, Object Marker$5) {
        return (Handle)(hfe$lzy1$2.initialized() ? hfe$lzy1$2.value() : this.hfe$lzyINIT1$1(hfe$lzy1$2, x$2$proxy1$5, InnerWired_this$5, Marker$5));
    }
}

