import $ivy.`com.softwaremill.sttp.client3::core:3.6.2`
import $ivy.`org.http4s::http4s-blaze-client:0.23.12`

import sttp.client3._
import sttp.model.headers.Cookie
import sttp.model.headers.CookieWithMeta
import sttp.model.StatusCode
import sttp.model.Header
import sttp.model.RequestMetadata
import sttp.client3._
import sttp.model.Method



val testCookie1 = "abc1=276848643;Expires=Saturday, 28-Jun-2025 16:04:41 GMT"
val testCookie2 = "abc2=276848643;Expires=Sat, 28 Jun 2025 16:04:41 GMT"


println(CookieWithMeta.parse(testCookie1))

println(CookieWithMeta.parse(testCookie2))


 val response = Response[String](
                    "", 
                    StatusCode.apply(200), 
                    "", 
                    List(Header("set-cookie", testCookie1), Header("set-cookie", testCookie2))
                    )

val cookies = response.cookies.flatMap(_.toOption)

println(cookies)