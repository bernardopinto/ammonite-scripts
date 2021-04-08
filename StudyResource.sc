import $ivy.`org.typelevel::cats-effect:2.4.1`

import cats.effect.{Resource, IO}
import scala.io.Source
import scala.util.Try
import scala.util.{Success, Failure}


val res = Resource.make {
    IO{
    Try(Source.fromFile("nonExisting")) match {
        case Success(source)    => Right(source)
        case Failure(exception) => Left(exception.getMessage())
    }
}
} { source =>
    IO(source.map(_.close))
}

val program = res.use {
    s => 
    s match {
        case Right(source)  => IO(println(source.mkString))
        case Left(x)        => IO(println(s"failed with $x"))
    }
}

program.unsafeRunSync



val f = () => Resource.make {
    IO{
      Try(scala.io.Source.fromResource("nonExisting")) match {
        case Success(source)    => Right(source)
        case Failure(throwable) => Left(throwable.getMessage)
      }
    }
  }{ sourceEither =>
    IO(sourceEither.map(_.close()))}
    .use {
      file =>
    IO(file match {
        case Right(source) => println(source.mkString)
        case Left(error)   => println(error)
    })
  }

  val io = f()
  io.unsafeRunSync