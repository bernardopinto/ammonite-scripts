import $ivy.`io.circe::circe-generic:0.12.3`
import $ivy.`io.circe::circe-parser:0.12.3`

import io.circe.generic.auto._
import io.circe.syntax._

import io.circe.{JsonObject, Encoder, Decoder, HCursor, Json}
import scala.concurrent.duration.FiniteDuration
import io.circe.ACursor
import java.util.concurrent.TimeUnit.MINUTES


val y = HCursor.fromJson(
    Json.fromJsonObject(
        JsonObject.fromMap(
                Map("hello" -> Json.fromInt(5), "world" -> Json.fromInt(10))
            )
        )
)

val downfield = y.downField("hello").as[Int]

downfield.map(println)

println("is right")
println(downfield.isRight)

implicit val durationEncoder: Encoder[FiniteDuration] = duration => Json.fromLong(duration.toMinutes)
implicit val durationDecoder: Decoder[FiniteDuration] = (c: HCursor) => {
    for {
        length <- c.as[Long]
    } yield FiniteDuration(length,  MINUTES)
}


final case class Movie(title: String, length: FiniteDuration)

val ironMan = Movie("Iron Man", FiniteDuration(120, MINUTES))

val ironManEncoded = ironMan.asJson


println(ironManEncoded)

val ironManDecoded = ironManEncoded.as[Movie]

println(ironManDecoded)