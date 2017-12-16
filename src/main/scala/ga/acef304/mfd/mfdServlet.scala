package ga.acef304.mfd

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.mongodb.casbah.commons.MongoDBObject
import ga.acef304.rest.CollectorwebStack
import ga.acef304.util.FileUtil

import salat._
import salat.global.ctx

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class mfdServlet extends CollectorwebStack {
  get("/ticker/list") {
    val groupId = params.getOrElse("marketId", "16")
    val store = params.get("store").nonEmpty

    val tickerList = mfd.getTickers(groupId)

    if (store) TickerDAO.insert(tickerList)

    grater[Ticker].toCompactJSONArray(tickerList)
  }

  get("/ticker/ticks") {
    val tickers = params.getOrElse("tickers", "").split(",")
    val date = params.getOrElse("date", "01.11.2017")
    val groupId = params.getOrElse("groupId", "16")
    val fileName = FileUtil.getFileName("ticks", date, tickers)

    val csvLines = mfd.getTicks(groupId, tickers, date)

    scala.tools.nsc.io.File(fileName).writeAll(csvLines.mkString("\n"))
    println(s"Written to $fileName")
  }

  get("/ticker/mins") {
    val tickers = params.getOrElse("tickers", "").split(",")
    val dateEnd = params.getOrElse("date", "01.11.2017")
    val groupId = params.getOrElse("groupId", "16")
    val fileName = FileUtil.getFileName("min", dateEnd, tickers)

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateStart = formatter.format(LocalDate.parse(dateEnd, formatter).minusYears(1))

    val csvLines = mfd.getMinutes(groupId, tickers, dateStart, dateEnd)

    scala.tools.nsc.io.File(fileName).writeAll(csvLines.mkString("\n"))
    println(s"Written to $fileName")
  }

  get("/ticker/all") {
    val groupId = params.getOrElse("groupId", "16")
    val date = params.getOrElse("date", "01.11.2017")

    val start = System.currentTimeMillis()

    //val tickers = TickerDAO.find(MongoDBObject("groupId" -> groupId)).map(t => t.id).grouped(25)

    val tickers = mfd.getTickers(groupId)

    for (t <- tickers.grouped(10)) {
      val fileName = FileUtil.getFileName("ticks", date, t.map(_.id))
      val csvData = mfd.getTicks(groupId, t.map(_.id), date)
      scala.tools.nsc.io.File(fileName).writeAll(csvData.mkString("\n"))
      println(s"tick... ${tickers.indexOf(t.head) / tickers.length.toDouble}")
      Thread.sleep(1000)
    }

    val end = System.currentTimeMillis()
    println(s"Done in ${(end - start) / 1000} sec")
    Unit
  }

  get("/ticker/allMins") {
    val dateEnd = params.getOrElse("date", "01.11.2017")
    val groupId = params.getOrElse("groupId", "16")

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateStart = formatter.format(LocalDate.parse(dateEnd, formatter).minusYears(1))

    val start = System.currentTimeMillis()

    val tickers = mfd.getTickers(groupId)

    for (t <- tickers.grouped(5)) {
      println(s"mins: ${t.map(_.id).mkString(", ")}")
      val fileName = FileUtil.getFileName("min", dateEnd, t.map(_.id))
      val csvData = mfd.getMinutes(groupId, t.map(_.id), dateStart, dateEnd)

      scala.tools.nsc.io.File(fileName).writeAll(csvData.mkString("\n"))
      println(s"mins... ${tickers.indexOf(t.head) / tickers.length.toDouble}")
      Thread.sleep(5000)
    }

    val end = System.currentTimeMillis()
    println(s"Done in ${(end - start) / 1000} sec")
    Unit
  }
}
