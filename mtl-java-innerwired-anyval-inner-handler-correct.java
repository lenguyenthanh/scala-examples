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
 *  scala.MatchError
 *  scala.runtime.BoxesRunTime
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
import scala.Function1;
import scala.MatchError;
import scala.runtime.BoxesRunTime;
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

        Object Marker = new Object();

        return this.inner$1(x$2$proxy1, InnerWired_this, Marker, (EitherT)InnerWired$.MODULE$.inline$body$extension((Function1)InnerWired_this).apply((Object)new InnerHandle(Marker, (ApplicativeError)x$2$proxy1)), (Function1 & Serializable)x$1 -> {

            Error$ error$ = x$1;

            if (Error$.MODULE$.equals(error$)) {
                String string = (String)package.all$.MODULE$.catsSyntaxApplicativeId((Object)"error");
                return (EitherT)ApplicativeIdOps$.MODULE$.pure$extension((Object)string, (Applicative)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval()));
            }

            throw new MatchError((Object)error$);
        });
    }

    private final EitherT inner$1(MonadError x$2$proxy1$1, Function1 InnerWired_this$1, Object Marker$1, EitherT fb, Function1 f) {

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
}
