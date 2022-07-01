import $ivy.`org.http4s::http4s-dsl:0.23.12`
import $ivy.`org.http4s::http4s-blaze-client:0.23.12`
import $ivy.`org.http4s::http4s-blaze-server:0.23.12`
import $ivy.`com.softwaremill.sttp.client3::core:3.6.2`
import $ivy.`com.softwaremill.sttp.client3::http4s-backend:3.6.2`


import cats.effect.unsafe.implicits._
import cats.effect._, org.http4s._, org.http4s.dsl.io._
import org.typelevel.ci.CIString
import org.http4s.headers.`Set-Cookie`
import org.http4s.server.Router
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.blaze.client.BlazeClientBuilder
import sttp.client3._
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3.http4s._


val validCookie = "valid-cookie=276848643;Path=/;Domain=.testdomain.com;Expires=Sat, 28 Jun 2025 16:04:41 GMT;Max-Age=94608000;Secure"
val invalidCookie = "invalid-cookie=276848643;Path=/;Domain=.testdomain.com;Expires=Sat, 28-Jun-2025 16:04:41 GMT;Max-Age=94608000;Secure"
val responseHeaders = Headers(List(Header.Raw(`Set-Cookie`.name, validCookie), Header.Raw(`Set-Cookie`.name, invalidCookie)))

val testCookiesService = HttpRoutes.of[IO] {
  case GET -> Root / "cookies" =>
    Ok("Enjoy!").map(_.withHeaders(responseHeaders))
}


val httpApp = Router("/" -> testCookiesService).orNotFound
val serverBuilder = BlazeServerBuilder[IO].bindHttp(8080, "localhost").withHttpApp(httpApp)

val http4sClientReq = BlazeClientBuilder[IO].resource.use { client =>
    client.get("http://localhost:8080/cookies"){ resp =>
        IO.pure(resp.cookies)
    }
}


 val backend: Resource[IO, SttpBackend[IO, Fs2Streams[IO]]] = Http4sBackend.usingDefaultBlazeClientBuilder[IO]()

 val sttpClientRequest = backend.use { be =>
    for {
        resp <- basicRequest.get(uri"http://localhost:8080/cookies").send(be)
    } yield resp.cookies.flatMap(_.toOption)
}



val program = for {
    fiber <-  serverBuilder.resource.use(_ => IO.never).start
    http4sCookies <- http4sClientReq
    sttpCookies <- sttpClientRequest
} yield {
    println("HTTP4S COOKIES")
    println(http4sCookies)
    
    println("STTP COOKIES")
    println(sttpCookies)
}



@main
def main() = {
    program.unsafeRunSync()
}