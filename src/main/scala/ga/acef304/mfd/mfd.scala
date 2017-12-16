package ga.acef304.mfd

import ga.acef304.util.FileUtil

object mfd {
  def getTickers(groupId: String): List[Ticker] = {
    //val index = scala.io.Source.fromFile("~/projects/hobby/collectorweb/index.html")

    val index = scala.io.Source.fromURL(s"http://mfd.ru/export/?groupId=$groupId")
    val ind = index.getLines().toList.mkString

    val availableTicker = "<select.+? name='AvailableTickers'.+?>(.+?)</select>".r
    val options = availableTicker.findAllIn(ind).group(1)

    val option = raw"<option value='(\d+)'>([^<]+)".r

    option findAllIn options map (_ match {
      case option(id, name) => Ticker(id, groupId, name)
    }) toList
  }

  private def getData(groupId: String, tickers: Iterable[String], dataType: Int, dateStart: String, dateEnd: String): List[String] = {
    val baseUrl = "http://mfd.ru/export/handler.ashx"
    val tickersStr = tickers.mkString("%2C")
    val prefix = dataType match {
      case 0 => "ticks"
      case 1 => "min"
      case _ => "notdefined"
    }

    val recordFormat = dataType match {
      case 0 => "2"
      case 1 => "0"
      case _ => "0"
    }
    val fileName = FileUtil.getFileName(prefix, dateStart, tickers)

    val reqParams = s"TickerGroup=$groupId" :: s"Tickers=$tickersStr" :: "Alias=false" :: s"Period=$dataType" ::
      "timeframeValue=1" :: "timeframeDatePart=day" :: s"StartDate=$dateStart" :: s"EndDate=$dateEnd" ::
      "SaveFormat=0" :: "SaveMode=0" :: s"FileName=$fileName" :: "FieldSeparator=%253b" ::
      "DecimalSeparator=." :: "DateFormat=yyyyMMdd" :: "TimeFormat=HHmmss" :: "DateFormatCustom=" ::
      "TimeFormatCustom=" :: "AddHeader=true" :: s"RecordFormat=$recordFormat" :: "Fill=true" :: Nil

    val url = s"${baseUrl}/test.csv?${reqParams.mkString("&")}"
    println(url)

    scala.io.Source.fromURL(url).getLines().toList
  }

  def getTicks(groupId: String, tickers: Iterable[String], date: String): List[String] =
    getData(groupId, tickers, 0, date, date)

  def getMinutes(groupId: String, tickers: Iterable[String], dateStart: String, dateEnd: String) =
    getData(groupId, tickers, 1, dateStart, dateEnd)

}
