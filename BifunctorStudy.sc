import $ivy.`org.typelevel::cats-core:2.3.0`

import cats.syntax.either._
import java.time.ZonedDateTime


case class DomainError(message: String)

// We summon a Bifunctor of Either with Bifunctor `apply` method
// `apply` will then call `catsBifunctorForEither` that will instantiate `cats.instances.either.catsStdBitraverseForEither`
// So interestingly there is no need to import Either instances to use Bifunctor functionality on Either
// The reason being Bifunctor can instantiate an instance of Bifunctor[Either] or Bifunctor[Tubple2]
// from within Bifunctor object using `apply`
// val eitherBifunctor: Bifunctor[Either] = Bifunctor[Either]

def dateTimeFromUser: Either[Throwable, ZonedDateTime] = 
  Right(ZonedDateTime.now()) 


// Question: How is Bifunctor[Either] instantiated when using `bimap` directly on an Either?
// Maybe it's not? Seems that there's a conversion from Either => EitherOps, where the latter
// implements bimap without the need to use a Bifunctor[Either] in scope.
// However where is the mechanism to convert an Either => EitherOps?
// In EitherSyntax theres an implicit method that has the capability to do this conversion, 
// but I can't see it's been explicitly called/used anywhere.
val bimaped = dateTimeFromUser.bimap(
    error => DomainError(error.getMessage),
    dateTime => dateTime.toEpochSecond()
)


println(bimaped)


// More verbose approach

// val secondApproach = eitherBifunctor.bimap(dateTimeFromUser)(
//   error => DomainError(error.getMessage),
//   dateTime => dateTime.toEpochSecond()
// )

// println(secondApproach)