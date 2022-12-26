package contributors

import scala.util.Try
import scala.util.chaining._
import util._
import cats.implicits._

object DottyContributors01a extends App {

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

  def parseBody(body: String): Either[String, List[Contributor]] = for {
    contributorsJson <- Try {
                          ujson.read(body).arr.toList
                        }.toEither.leftMap(_.toString)
    contributors     <- contributorsJson.traverse(contributorJson2Contributor)
  } yield contributors.sortBy(_.contributions).reverse

  def contributorJson2Contributor(contributorJson: ujson.Value): Either[String, Contributor] = {
    val tryy: Try[Contributor] = for {
      login         <- Try(contributorJson("login").str)
      contributions <- Try(contributorJson("contributions").num.toInt)
    } yield Contributor(login, contributions)
    tryy.toEither.leftMap(_.toString)
  }

  line80.green pipe println
}
