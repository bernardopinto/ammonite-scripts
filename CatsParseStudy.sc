import $ivy.`org.typelevel::cats-parse:0.3.7`

import cats.parse.Parser
import cats.parse.Parser.charIn


val parser = charIn(Set(0x20.toChar to 0xff.toChar: _*) - 0x3b.toChar).rep0.string

val res = parser.parse("05-05-2022")

println(res)