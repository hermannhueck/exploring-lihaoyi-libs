package contributors

import scala.util.Try
import scala.util.chaining._
import util._
import cats.implicits._

object DottyContributors02a extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = s"https://api.github.com/repos/$user/$repo/contributors"

  val responseBodyJson: Either[String, List[ujson.Value]] =
    Try {
      val responseBody = requests.get(uri).text()
      ujson.read(responseBody).arr.toList
    }.toEither.leftMap(_.toString)

  // 'Serializable' is the common supertype of 'Error' and 'String'
  val result: Either[String, List[Contributor]] =
    responseBodyJson.flatMap(parseBody)

  result match {
    case Left(error)         =>
      println(s"HTTP request error or JSON parse error: $error".red)
    case Right(contributors) =>
      // printContributors(s"$user/$repo", contributors)
      printContributorsSummary(s"$user/$repo", contributors.size, contributors.map(_.contributions).sum)
      printMostBusyContributor(s"$user/$repo", contributors)
  }

  def parseBody(contributorsJson: List[ujson.Value]): Either[String, List[Contributor]] =
    contributorsJson
      .traverse(contributorJson2Contributor)
      .map(_.sortBy(_.contributions).reverse)

  def contributorJson2Contributor(contributorJson: ujson.Value): Either[String, Contributor] = {
    val tryy: Try[Contributor] = for {
      login         <- Try(contributorJson("login").str)
      contributions <- Try(contributorJson("contributions").num.toInt)
    } yield Contributor(login, contributions)
    tryy.toEither.leftMap(_.toString)
  }

  line80.green pipe println
}
