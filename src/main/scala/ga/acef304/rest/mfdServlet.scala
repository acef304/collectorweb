package ga.acef304.rest

class mfdServlet extends CollectorwebStack {
  get("/ticker/all") {
    //val index = scala.io.Source.fromFile("/Users/lesha/projects/hobby/collectorweb/index.html")
    val index = scala.io.Source.fromURL("http://mfd.ru/export/?groupId=16")
    val ind = index.getLines().toList.mkString

    val availableTicker = "<select.+? name='AvailableTickers'.+?>(.+?)</select>".r
    val options = availableTicker.findAllIn(ind).group(1)

    val option = raw"<option value='(\d+)'>([^<]+)".r

    val tickerList = option findAllIn options map (_ match {
      case option(id, name) => Ticker(id.toInt, name)
    }) toList

    tickerList
  }
}

case class Ticker(id: Int, name: String)
