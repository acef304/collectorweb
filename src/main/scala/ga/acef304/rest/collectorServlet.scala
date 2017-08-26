package ga.acef304.rest

import org.scalatra._

class collectorServlet extends CollectorwebStack {

//  get("/") {
//    <html>
//      <body>
//        <h1>Hello, world!</h1>
//        Say <a href="hello-scalate">hello to Scalate</a>.
//      </body>
//    </html>
//  }

  get("/greeter") {
    s"Hello, ${params.getOrElse("name", "somebody")}"
  }

  get("/lolka") {
    Lolka("1", "test")
  }
}

case class Lolka(id: String, desc: String)