package ga.acef304.mfd

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

  def getTicks(groupId: String, tickers: Iterable[String], date: String, fileName: String): List[String] = {
    val baseUrl = "http://mfd.ru/export/handler.ashx"
    val tickersStr = tickers.mkString("%2C")

    val reqParams = s"TickerGroup=$groupId" :: s"Tickers=$tickersStr" :: "Alias=false" :: "Period=0" ::
      "timeframeValue=1" :: "timeframeDatePart=day" :: s"StartDate=$date" :: s"EndDate=$date" ::
      "SaveFormat=0" :: "SaveMode=0" :: s"FileName=$fileName" :: "FieldSeparator=%253b" ::
      "DecimalSeparator=." :: "DateFormat=yyyyMMdd" :: "TimeFormat=HHmmss" :: "DateFormatCustom=" ::
      "TimeFormatCustom=" :: "AddHeader=true" :: "RecordFormat=2" :: "Fill=true" :: Nil

    val url = s"${baseUrl}/${fileName}?${reqParams.mkString("&")}"

    scala.io.Source.fromURL(url).getLines().toList
  }
}
