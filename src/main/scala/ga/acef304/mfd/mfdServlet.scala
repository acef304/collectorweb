package ga.acef304.mfd

import com.mongodb.casbah.commons.MongoDBObject
import ga.acef304.rest.CollectorwebStack
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
    val tickers = params.getOrElse("tickers", "")
    val date = params.getOrElse("date", "01.09.2017")
    val groupId = params.getOrElse("groupId", "16")
    val fileName = s"$tickers-$date.txt"

    val csvLines = mfd.getTicks(groupId, tickers.split(","), date, fileName)

    scala.tools.nsc.io.File(fileName).writeAll(csvLines.mkString)
    println(csvLines)
    println(s"Written to $fileName")
  }

  get("/ticker/all") {
    val groupId = params.getOrElse("groupId", "16")
    val date = params.getOrElse("date", "01.09.2017")

    val tickers = TickerDAO.find(MongoDBObject("groupId" -> groupId)).map(t => t.id).grouped(25)

    Future {
      for (t <- tickers) {
        response.getOutputStream.println(t.toList.toString)
        println("tick...")
        Thread.sleep(1000)
      }
    }

    println("Done")
    Unit
  }
}
