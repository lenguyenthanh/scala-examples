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
 *  cats.data.EitherT
 *  cats.data.EitherT$
 *  cats.mtl.Handle
 *  cats.mtl.Handle$
 *  cats.mtl.InnerWired
 *  cats.mtl.Raise
 *  cats.mtl.syntax.RaiseOps$
 *  cats.mtl.syntax.all$
 *  cats.syntax.ApplicativeIdOps$
 *  cats.syntax.package$all$
 *  scala.Function1
 *  scala.MatchError
 *  scala.runtime.ModuleSerializationProxy
 */
import cats.Applicative;
import cats.ApplicativeError;
import cats.Eval;
import cats.Eval$;
import cats.Functor;
import cats.Monad;
import cats.data.EitherT;
import cats.data.EitherT$;
import cats.mtl.Handle;
import cats.mtl.Handle$;
import cats.mtl.InnerWired;
import cats.mtl.Raise;
import cats.mtl.syntax.RaiseOps$;
import cats.mtl.syntax.all$;
import cats.syntax.ApplicativeIdOps$;
import cats.syntax.package;
import java.io.Serializable;
import scala.Function1;
import scala.MatchError;
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
        return (EitherT)new InnerWired((Function1 & Serializable)a -> {

            Handle handle = this.given_a_type$1((Handle)a);

            Error$ error$ = (Error$)all$.MODULE$.toRaiseOps((Object)Error$.MODULE$);

            return (EitherT)package.all$.MODULE$.toFunctorOps(RaiseOps$.MODULE$.raise$extension((Object)error$, (Raise)handle), (Functor)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval())).as((Object)"nope");
        }).rescue((Function1 & Serializable)x$1 -> {
            Error$ error$ = x$1;
            if (Error$.MODULE$.equals(error$)) {
                String string = (String)package.all$.MODULE$.catsSyntaxApplicativeId((Object)"error");
                return (EitherT)ApplicativeIdOps$.MODULE$.pure$extension((Object)string, (Applicative)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval()));
            }
            throw new MatchError((Object)error$);
        }, (ApplicativeError)EitherT$.MODULE$.catsDataMonadErrorForEitherT((Monad)Eval$.MODULE$.catsBimonadForEval()));
    }

    private final Handle given_a_type$1(Handle a$1) {
        return a$1;
    }
}

