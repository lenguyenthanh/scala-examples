
/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cats.Applicative
 *  cats.ApplicativeError
 *  cats.Eval
 *  cats.Eval$
 *  cats.Functor
 *  cats.Monad
 *  cats.MonadError
 *  cats.data.EitherT
 *  cats.data.EitherT$
 *  cats.mtl.Handle$
 *  cats.mtl.Handle$Submarine
 *  cats.mtl.InnerHandle
 *  cats.mtl.InnerWired$
 *  cats.mtl.Raise
 *  cats.mtl.syntax.RaiseOps$
 *  cats.mtl.syntax.all$
 *  cats.package$ApplicativeThrow$
 *  cats.syntax.ApplicativeIdOps$
 *  cats.syntax.package$all$
 *  scala.Function1
 *  scala.Function2
 *  scala.MatchError
 *  scala.runtime.BoxesRunTime
 *  scala.runtime.LambdaDeserialize
 *  scala.runtime.LazyRef
 *  scala.runtime.ModuleSerializationProxy
 */
import cats.Applicative;
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
import cats.mtl.InnerHandle;
import cats.mtl.InnerWired$;
import cats.mtl.Raise;
import cats.mtl.syntax.RaiseOps$;
import cats.mtl.syntax.all$;
import cats.package;
import cats.syntax.ApplicativeIdOps$;
import cats.syntax.package;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.SerializedLambda;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.runtime.BoxesRunTime;
import scala.runtime.LambdaDeserialize;
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
        return (EitherT)this.inner$1(lazyRef, x$2$proxy1, InnerWired_this, Marker).apply(InnerWired$.MODULE$.inline$body$extension((Function1)InnerWired_this).apply((Object)new InnerHandle(Marker, this.inner$1(lazyRef, x$2$proxy1, InnerWired_this, Marker), (ApplicativeError)x$2$proxy1)), (Function1 & Serializable)x$1 -> {
            Error$ error$ = x$1;
            if (Error$.MODULE$.equals(error$)) {
                String string = (String)package.all$.MODULE$.catsSyntaxApplicativeId((Object)"error");
                return (EitherT)ApplicativeIdOps$.MODULE$.pure$extension((Object)string, (Applicative)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval()));
            }
            throw new MatchError((Object)error$);
        });
    }

    private final Function2 inner$lzyINIT1$1(LazyRef inner$lzy1$1, MonadError x$2$proxy1$1, Function1 InnerWired_this$1, Object Marker$1) {
        Function2 function2;
        LazyRef lazyRef = inner$lzy1$1;
        synchronized (lazyRef) {
            function2 = (Function2)(inner$lzy1$1.initialized() ? inner$lzy1$1.value() : inner$lzy1$1.initialize((Object)new Function2(x$2$proxy1$1, InnerWired_this$1, Marker$1){
                private final MonadError x$2$proxy1$2;
                private final Function1 InnerWired_this$2;
                private final Object Marker$2;
                {
                    this.x$2$proxy1$2 = x$2$proxy1$4;
                    this.InnerWired_this$2 = InnerWired_this$4;
                    this.Marker$2 = Marker$4;
                }

                public Object apply(Object fb, Object f) {
                    return package.ApplicativeThrow$.MODULE$.apply((ApplicativeError)this.x$2$proxy1$2).handleErrorWith(fb, (Function1 & Serializable)x$1 -> {
                        Throwable throwable = x$1;
                        if (throwable instanceof Handle.Submarine) {
                            Handle.Submarine submarine = InnerWired$.MODULE$.inline$Submarine$i1$extension(this.InnerWired_this$2, Handle$.MODULE$).unapply((Handle.Submarine)throwable);
                            Object object = submarine._1();
                            Object object2 = submarine._2();
                            Object e = object;
                            if (BoxesRunTime.equals((Object)this.Marker$2, (Object)object2)) {
                                return (EitherT)((Function1)f).apply((Object)((Error$)e));
                            }
                        }
                        Throwable t = throwable;
                        return (EitherT)package.ApplicativeThrow$.MODULE$.apply((ApplicativeError)this.x$2$proxy1$2).raiseError((Object)t);
                    });
                }

                private static /* synthetic */ Object $deserializeLambda$(SerializedLambda serializedLambda) {
                    return LambdaDeserialize.bootstrap("lambdaDeserialize", new MethodHandle[]{apply$$anonfun$1(java.lang.Object java.lang.Throwable )}, serializedLambda);
                }
            }));
        }
        return function2;
    }

    private final Function2 inner$1(LazyRef inner$lzy1$2, MonadError x$2$proxy1$3, Function1 InnerWired_this$3, Object Marker$3) {
        return (Function2)(inner$lzy1$2.initialized() ? inner$lzy1$2.value() : this.inner$lzyINIT1$1(inner$lzy1$2, x$2$proxy1$3, InnerWired_this$3, Marker$3));
    }
}
