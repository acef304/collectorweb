import ga.acef304.rest._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new mfdServlet, "/mfd/*")
    context.mount(new collectorServlet, "/*")
  }
}
