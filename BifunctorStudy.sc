import $ivy.`org.typelevel::cats-core:2.3.0`

import cats.syntax.either._
import cats.Bifunctor
import java.time.ZonedDateTime


case class DomainError(message: String)

// We summon a Bifunctor of Either with Bifunctor `apply` method
// `apply` will then call `catsBifunctorForEither` that will instantiate `cats.instances.either.catsStdBitraverseForEither`
// So interestingly there is no need to import Either instances to use Bifunctor functionality on Either
// The reason being Bifunctor can instantiate an instance of Bifunctor[Either] or Bifunctor[Tubple2]
// from within Bifunctor object using `apply`
val eitherBifunctor: Bifunctor[Either] = Bifunctor[Either]

def dateTimeFromUser: Either[Throwable, ZonedDateTime] = 
  Right(ZonedDateTime.now()) 

val bimaped = dateTimeFromUser.bimap(
    error => DomainError(error.getMessage),
    dateTime => dateTime.toEpochSecond()
)


println(bimaped)


// More verbose approach

val secondApproach = eitherBifunctor.bimap(dateTimeFromUser)(
  error => DomainError(error.getMessage),
  dateTime => dateTime.toEpochSecond()
)

println(secondApproach)