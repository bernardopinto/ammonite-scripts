import $ivy.`org.typelevel::cats-mtl:1.2.0`
import $plugin.$ivy.`org.typelevel:kind-projector_2.13.2:0.13.0`
import $ivy.`org.typelevel::cats-effect:3.3.12`
import scala.util.Try
import cats.data.EitherT
import cats.data.Kleisli

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import scala.concurrent.Future
import scala.math.pow

implicit val runtime = IORuntime.global


def parseDouble(s: String): Either[String, Double] =
  Try(s.toDouble).map(Right(_)).getOrElse(Left(s"$s is not a number"))

def divide(a: Double, b: Double): Either[String, Double] =
  Either.cond(b != 0, a / b, "Cannot divide by zero")

def parseDoubleAsync(s: String): IO[Either[String, Double]] =
  IO.delay(parseDouble(s))


def divideAsync(a: Double, b: Double): IO[Either[String, Double]] =
  IO.delay(divide(a, b))



  // transform divisionProgramAsync to use EitherT

  def divisionProgramAsyncET(inputA: String, inputB: String): EitherT[IO, String, Double] =
    for {
        a <- EitherT(parseDoubleAsync(inputA))
        b <- EitherT(parseDoubleAsync(inputB))
        d <- EitherT(divideAsync(a, b))
    } yield d


val divisionProgram = divisionProgramAsyncET("10", "5").value



// Cats Arrow study

import cats.arrow.Arrow
import cats.syntax.arrow._
import cats.syntax.compose._



def combine[F[_, _]: Arrow, A, B, C](fab: F[A, B], fac: F[A, C]): F[A, (B, C)] =
  Arrow[F].lift((a: A) => (a, a)) >>> (fab *** fac)


  val combined = combine((_: List[Int]).sum, (_: List[Int]).size)

  
  val mean: List[Int] => Double = combine((_: List[Int]).sum, (_: List[Int]).size) >>> {case (x, y) => x.toDouble / y }

  val variance: List[Int] => Double = combine(((_: List[Int]).map(x => x * x)) >>> mean, mean) >>> {case (x, y) => x - y * y}

  val variance2: List[Int] => Double = l => {
    val m = l.sum.toDouble / l.size
    l.map(i => pow((i - m), 2)).sum / l.size
  }

  println(variance(List(1, 2, 3, 4, 5, 6, 7, 8)))
  println(variance2(List(1, 2, 3, 4, 5, 6, 7, 8)))


  val headK = Kleisli((_: List[Int]).headOption)
  val lastK = Kleisli((_: List[Int]).lastOption)
  
  val headPlusLast = combine(headK, lastK) >>> Arrow[Kleisli[Option, *, *]].lift(((_: Int) + (_: Int)).tupled)

  println(headPlusLast.run(List(2, 3, 5, 8)))

  val tt: ((Int, Int)) => Int = ((_: Int) + (_: Int)).tupled

// >>> is a simple andThen function that takes an [A, B] and a [B, C] and returns a [A, C]
  // F[A, (A, A)] >>> F[(A, A), (B, C)]
  // The whole process is then a [A, (A, A)] composed with a [(A, A), (B, C)] so we can combined these two and
  // get a [A, (B, C)] which is our output type

// val mean: List[Int] => Double =


// Apply

import cats.syntax.apply._


final case class Person(name: String, surname: String, dateOfBirth: String)
  val maybeIdpGender = Option("name")
  val maybeIdpDateOfBirth: Option[String] = Option.empty
  val maybeIdpDateOfBirthSource = Option("date-of-birth")

  val personMaybe = (maybeIdpGender, maybeIdpDateOfBirth, maybeIdpDateOfBirthSource).mapN{ case (a, b, c) => Person(a, b, c)}

  println(personMaybe)



@main
def newMain() = {
    divisionProgram.unsafeRunSync()
}









