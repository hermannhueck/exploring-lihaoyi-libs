package contributors

import scala.util.Try
import scala.util.chaining._
import util._
import cats.implicits._

object DottyContributors01b extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = s"https://api.github.com/repos/$user/$repo/contributors"

  val body: Either[String, String] =
    Try {
      requests.get(uri).text()
    }.toEither.leftMap(_.toString)

  val result: Either[String, List[Contributor]] =
    body.flatMap(parseBody)

  result match {
    case Left(error)         =>
      println(s"HTTP request error or JSON parse error: $error".red)
    case Right(contributors) =>
      // printContributors(s"$user/$repo", contributors)
      printContributorsSummary(s"$user/$repo", contributors.size, contributors.map(_.contributions).sum)
      printMostBusyContributor(s"$user/$repo", contributors)
  }

  def parseBody(body: String): Either[String, List[Contributor]] = {
    given upickle.default.ReadWriter[Contributor] = upickle.default.macroRW[Contributor]
    Try {
      val contributors: List[Contributor] =
        upickle.default.read[List[Contributor]](body)
      contributors.sortBy(_.contributions).reverse
    }.toEither.leftMap(_.toString)
  }

  line80.green pipe println
}
