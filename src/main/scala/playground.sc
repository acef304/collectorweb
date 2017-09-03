def parserTest = {
  val index = scala.io.Source.fromFile("~/projects/hobby/collectorweb/index.html")
  val ind = index.getLines().toList.mkString
  val r = "<select.+? name='AvailableTickers'.+?>(.+?)</select>".r
  val mi = r.findAllIn(ind)

  mi.group(0)
  val options = mi.group(1)

  val t = raw"<option value='(\d+)'>([^<]+)".r

  val mi2 = t.findAllIn(options)

  mi2.group(0)
  mi2.group(1)
  mi2.group(2)

  case class Ticker(id: Int, name: String)

  t findAllIn options map (_ match {
    case t(id, name) => Ticker(id.toInt, name)
  }) toList


  val et = "http://mfd.ru/export/handler.ashx/%D0%A1%D0%B1%D0%B5%D1%80%D0%B1%D0%B0%D0%BD%D0%BA_1min_01082017_26082017.txt?TickerGroup=16&Tickers=1463&Alias=false&Period=1&timeframeValue=1&timeframeDatePart=day&StartDate=01.08.2017&EndDate=26.08.2017&SaveFormat=0&SaveMode=0&FileName=%D0%A1%D0%B1%D0%B5%D1%80%D0%B1%D0%B0%D0%BD%D0%BA_1min_01082017_26082017.txt&FieldSeparator=%253b&DecimalSeparator=.&DateFormat=yyyyMMdd&TimeFormat=HHmmss&DateFormatCustom=&TimeFormatCustom=&AddHeader=true&RecordFormat=0&Fill=false"
}
