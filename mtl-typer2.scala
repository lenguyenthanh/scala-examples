    def test: cats.data.EitherT[cats.Eval, Throwable, String] =
      cats.mtl.Handle.allow[Error.type].apply[
        [A] =>> cats.data.EitherT[cats.Eval, Throwable, A], String](
        {
          def $anonfun(using
            evidence$1:

                cats.mtl.Handle[
                  [A] =>> cats.data.EitherT[cats.Eval, Throwable, A], Error.type
                  ]

          ): cats.data.EitherT[cats.Eval, Throwable, String] =
            cats.syntax.all.toFunctorOps[
              [A] =>> cats.data.EitherT[cats.Eval, Throwable, A], String](
              cats.mtl.syntax.all.toRaiseOps[Error.type](Error).raise[F, String]
                (evidence$1)
            )(
              cats.data.EitherT.catsDataMonadErrorForEitherT[cats.Eval,
                Throwable](cats.Eval.catsBimonadForEval)
            ).as[String]("nope")
          closure($anonfun)
        }
      ).rescue(
        {
          def $anonfun(x$1: Error.type):
            cats.data.EitherT[cats.Eval, Throwable, String] =
            x$1 match
              {
                case Error =>
                  cats.syntax.all.catsSyntaxApplicativeId[String]("error").pure[
                    F](
                    cats.data.EitherT.catsDataMonadErrorForEitherT[cats.Eval,
                      Throwable](cats.Eval.catsBimonadForEval)
                  )
              }
          closure($anonfun)
        }
      ).apply(
        cats.data.EitherT.catsDataMonadErrorForEitherT[cats.Eval, Throwable](
          cats.Eval.catsBimonadForEval)
      )
  }
