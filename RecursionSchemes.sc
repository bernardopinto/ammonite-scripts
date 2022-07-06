import $ivy.`org.typelevel::cats-core:2.3.0`

sealed trait OpF[+A] // F suffix for Functor
case class Literal(i : Int) extends OpF[Nothing] // notice how the type parameter is set to `Nothing` in leaf cases
case class Add[A](left : A, right : A) extends OpF[A]
case class Mul[A](left : A, right : A) extends OpF[A]

object OpF {
  import cats. _
  implicit val opFunctor : Functor[OpF] = new Functor[OpF] {

    def map[A, B](fa : OpF[A])(f : A => B) : OpF[B] = fa match {
      case l @ Literal(_) => l // possible because Literal extends Op[Nothing]
      case Add(l, r)      => Add(f(l), f(r))
      case Mul(l, r)      => Mul(f(l), f(r))
    }
  }
}