import coursier.bootstrap.launcher.o


// "monoid in the category of T[_]
trait MonoidInCategoryK2[T[_], ~>[_[_], _[_]], U[_], P[_]] {
    def unit: U ~> T // same as ~>[U, T]
    def combine: P ~> T
}

// "monoid ni a monoidal category" = "monoid in the category of T
trait MonoidInCategory[T, ~>[_, _], U, P] {
    def unit: U ~> T // same as ~>[U, T]
    def combine: P ~> T
}


trait GeneralMonoid[T, U, P] extends MonoidInCategory[T, Function1, U, P] {
    // def unit: U => T
    // def combine: P => T
}


trait FunctionalMonoid[T] extends GeneralMonoid[T, Unit, (T, T)] {
    // def unit: Unit => T
    // def combine: ((T, T)) => T
}


trait Monoid[T] extends FunctionalMonoid[T] {
    def empty: T
    def combine(a: T, b: T): T

    // hiden API

    def unit = _ => empty
    def combine = t => combine(t._1, t._2)
}

object IntMonoitAdd extends Monoid[Int] {
    override def empty: Int = 0
    override def combine(a: Int, b: Int): Int = a + b
}


// endoFunctorsx, in scala thes are what functors are. 
// Mappings of a F[A] => F[B] where the category remains the same

trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
}

implicit val ListFunctor = new Functor[List] {
    override def map[A, B](fa: List[A])(f: A => B) = fa.map(f)
}

trait MyFunction1[-A, +B] {
    def apply(a: A): B
}

// In practice a scala natural transformation would be a 
// List.headOption, which is a natural transformation
// from a List => Option.
trait FunctorNatTransformation[-F[_], +G[_]] {
    def apply[A](fa: F[A]): G[A]
}

// the id functor

type Id[A] = A
implicit val idFunctor = new Functor[Id] {
    def map[A, B](fa: A)(f: A => B): B = f(fa)
}

// composing functors

type HKTComposition[F[_], G[_], A] = G[F[A]]
type SameTypeComposition[F[_], A]  = F[F[A]]


trait MonoidInCategoryOfFunctors[F[_]] extends MonoidInCategoryK2[F, FunctorNatTransformation, Id, ({type X[A] = SameTypeComposition[F, A]})#X] {
    type EndofunctorComposition[A]  = F[F[A]]
    def unit: FunctorNatTransformation[Id, F]
    // We could replace EndofunctorComposition with our type lambda ({type X[A] = SameTypeComposition[F, A]})#X
    def combine: FunctorNatTransformation[EndofunctorComposition, F]
    
    def pure[A](a: A): F[A] = unit(a)
    def flatMap[A, B](ma: F[A])(f: A => F[B])(implicit functor: Functor[F]): F[B] = combine(functor.map(ma)(f))
}


object SpecialListMonoid extends MonoidInCategoryOfFunctors[List] {
    override def unit: FunctorNatTransformation[Id,List] = new FunctorNatTransformation[Id, List]{
        override def apply[A](fa: Id[A]): List[A] = List(fa)
    }
    
    override def combine: FunctorNatTransformation[EndofunctorComposition,List] = new FunctorNatTransformation[EndofunctorComposition, List] {
        override def apply[A](fa: EndofunctorComposition[A]): List[A] = fa.flatten
    }

}


// Monads

trait Monad[F[_]] extends Functor[F] with MonoidInCategoryK2[F, FunctorNatTransformation, Id, ({type X[A] = SameTypeComposition[F, A]})#X] {
    type EndofunctorComposition[A] = F[F[A]]

    // the public API - don't touch this
    def pure[A](a: A): F[A]
    def flatMap[A, B](ma: F[A])(f: A => F[B]): F[B]

    
    // the method from Functor, in terms of pure + flatMap
    override def map[A, B](fa: F[A])(f: A => B) = flatMap(fa)(a => pure(f(a)))

    def flatten[A](ffa: F[F[A]]): F[A] = flatMap(ffa)(identity) 

    def unit: FunctorNatTransformation[Id, F] = new FunctorNatTransformation[Id, F] {
      override def apply[A](fa: Id[A]) = pure(fa)
    }

    def combine: FunctorNatTransformation[EndofunctorComposition, F] = new FunctorNatTransformation[EndofunctorComposition, F] {
        override def apply[A](fa: F[F[A]]) = flatten(fa)
        }
}

