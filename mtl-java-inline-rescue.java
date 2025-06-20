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
 *  cats.mtl.Handle
 *  cats.mtl.Handle$
 *  cats.mtl.Handle$Submarine
 *  cats.mtl.InnerWired$InnerWired
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
import cats.mtl.InnerWired;
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
    public static final mtl$minussubmarine$package$ MODULE$ = new mtl$minussubmarine$package$();

    private mtl$minussubmarine$package$() {
    }

    private Object writeReplace() {
        return new ModuleSerializationProxy(mtl$minussubmarine$package$.class);
    }

    public EitherT<Eval, Throwable, String> test() {
        MonadError monadError = EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval());
        Handle$ HandleCrossCompat_this = Handle$.MODULE$;
        InnerWired.InnerWired InnerWired_this = new InnerWired.InnerWired((Function1 & Serializable)evidence$1 -> {
            Error$ error$ = (Error$)all$.MODULE$.toRaiseOps((Object)Error$.MODULE$);
            return (EitherT)package.all$.MODULE$.toFunctorOps(RaiseOps$.MODULE$.raise$extension((Object)error$, (Raise)evidence$1), (Functor)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval())).as((Object)"nope");
        });
        LazyRef lazyRef = new LazyRef();
        Object Marker = new Object();
        return this.mtl$minussubmarine$package$$$_$inner$1(monadError, InnerWired_this, Marker, (EitherT)InnerWired_this.inline$body((Handle)this.given_Handle_F_E$1(lazyRef, monadError, InnerWired_this, Marker)), (Function1 & Serializable)x$1 -> {
            Error$ error$ = x$1;
            if (Error$.MODULE$.equals(error$)) {
                String string = (String)package.all$.MODULE$.catsSyntaxApplicativeId((Object)"error");
                return (EitherT)ApplicativeIdOps$.MODULE$.pure$extension((Object)string, (Applicative)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval()));
            }
            throw new MatchError((Object)error$);
        });
    }

    public final EitherT mtl$minussubmarine$package$$$_$inner$1(MonadError evidence$2$1, InnerWired.InnerWired InnerWired_this$1, Object Marker$1, EitherT fb, Function1 f) {
        return (EitherT)package.ApplicativeThrow$.MODULE$.apply((ApplicativeError)evidence$2$1).handleErrorWith((Object)fb, (Function1 & Serializable)x$1 -> {
            Throwable throwable = x$1;
            if (throwable instanceof Handle.Submarine) {
                Handle.Submarine submarine = InnerWired_this$1.inline$Submarine$i1(Handle$.MODULE$).unapply((Handle.Submarine)throwable);
                Object object = submarine._1();
                Object object2 = submarine._2();
                Object e = object;
                if (BoxesRunTime.equals((Object)Marker$1, (Object)object2)) {
                    return (EitherT)f.apply((Object)((Error$)e));
                }
            }
            Throwable t = throwable;
            return (EitherT)package.ApplicativeThrow$.MODULE$.apply((ApplicativeError)evidence$2$1).raiseError((Object)t);
        });
    }

    private final mtl$minussubmarine$package$given_Handle_F_E$2$ given_Handle_F_E$lzyINIT1$1(LazyRef given_Handle_F_E$lzy1$1, MonadError evidence$2$3, InnerWired.InnerWired InnerWired_this$3, Object Marker$3) {
        mtl$minussubmarine$package$given_Handle_F_E$2$ mtl$minussubmarine$package$given_Handle_F_E$2$2;
        LazyRef lazyRef = given_Handle_F_E$lzy1$1;
        synchronized (lazyRef) {
            mtl$minussubmarine$package$given_Handle_F_E$2$2 = (mtl$minussubmarine$package$given_Handle_F_E$2$)(given_Handle_F_E$lzy1$1.initialized() ? given_Handle_F_E$lzy1$1.value() : given_Handle_F_E$lzy1$1.initialize((Object)new mtl$minussubmarine$package$given_Handle_F_E$2$(evidence$2$3, InnerWired_this$3, Marker$3)));
        }
        return mtl$minussubmarine$package$given_Handle_F_E$2$2;
    }

    private final mtl$minussubmarine$package$given_Handle_F_E$2$ given_Handle_F_E$1(LazyRef given_Handle_F_E$lzy1$2, MonadError evidence$2$4, InnerWired.InnerWired InnerWired_this$4, Object Marker$4) {
        return (mtl$minussubmarine$package$given_Handle_F_E$2$)(given_Handle_F_E$lzy1$2.initialized() ? given_Handle_F_E$lzy1$2.value() : this.given_Handle_F_E$lzyINIT1$1(given_Handle_F_E$lzy1$2, evidence$2$4, InnerWired_this$4, Marker$4));
    }
}

