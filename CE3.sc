import cats.effect.FiberIO
import $ivy.`org.typelevel::cats-effect:3.1.0`


import cats.effect.{Fiber, IO, IOApp, ExitCode}
import cats.effect.unsafe.implicits.global
import scala.concurrent.duration._


object AsynchronousIOs extends IOApp {

    val meaningOfLife: IO[Int] = IO(42)
    val favLang: IO[String]    = IO("scala")

    implicit class IOExtensions[A](io: IO[A]) {
        def debug: IO[A] = io.map { value =>
            println(s"${Thread.currentThread().getName} $value")
            value
        }
    }

    def sameThread() = for {
        _ <- meaningOfLife.debug
        _ <- favLang.debug
    } yield ()

    val aFiber: IO[FiberIO[Int]] = meaningOfLife.debug.start

    def differentThreads() = for {
        _ <- aFiber.debug
        _ <- favLang.debug
    } yield ()

    def runOnAnotherThread[A](io: IO[A]) = for {
        fib    <- io.start
        result <- fib.join
    } yield result

    def throwOnAnotherThread() = for {
        fib <- IO.raiseError[Int](new RuntimeException("No number for you")).start
        result <- fib.join
    } yield result

    def testCancel() = {
        val task = IO("starting").debug *> IO.sleep(1.second) *> IO("done").debug
        for {
            fib <- task.start
            _   <- IO.sleep(500.millis) *> IO("cancelling").debug
            _   <- fib.cancel
            result <- fib.join
        } yield result

    }

    def run(args: List[String]): IO[ExitCode] = testCancel().debug.as(ExitCode.Success)


}

val exitCode = AsynchronousIOs.run(List.empty).unsafeRunSync