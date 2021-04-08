// scala 2.13.3

import $ivy.`org.typelevel::cats-core:2.1.1`

import cats._
import cats.implicits._

// Dummy Show example

final case class Money(amount: Int)
final case class Salary(size: Money)

implicit val showMoney: Show[Money] = Show.show(m => s"$$${m.amount}")

implicit val showSalary: Show[Salary] = showMoney.contramap(_.size) // Salary => Money

val showedSalary = Salary(Money(200)).show
println(showedSalary)



// Scala Ordering example

import scala.math.Ordered._

implicit val moneyOrdering: Ordering[Money] = Ordering.by(_.amount)


println(Money(100) < Money(200))


