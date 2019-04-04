package views.html.simul

import play.api.libs.json.Json

import lila.api.Context
import lila.app.templating.Environment._
import lila.app.ui.ScalatagsTemplate._

import controllers.routes

object home {

  def apply(
    opens: List[lila.simul.Simul],
    starteds: List[lila.simul.Simul],
    finisheds: List[lila.simul.Simul]
  )(implicit ctx: Context) = views.html.base.layout(
    responsive = true,
    moreCss = responsiveCssTag("simul.list"),
    moreJs = jsTag("simul-list.js"),
    title = trans.simultaneousExhibitions.txt(),
    openGraph = lila.app.ui.OpenGraph(
      title = trans.simultaneousExhibitions.txt(),
      url = s"$netBaseUrl${routes.Simul.home}",
      description = trans.aboutSimul.txt()
    ).some
  ) {
      main(cls := "page-menu simul-list")(
        st.aside(cls := "page-menu__menu simul-list__help")(
          p(trans.aboutSimul()),
          img(src := staticUrl("images/fischer-simul.jpg"), alt := "Simul IRL with Bobby Fischer")(
            em("[1964] ", trans.aboutSimulImage.frag()),
            p(trans.aboutSimulRealLife.frag()),
            p(trans.aboutSimulRules.frag()),
            p(trans.aboutSimulSettings.frag())
          )
        ),
        div(cls := "page-menu__content simul-list__content", dataHref := routes.Simul.homeReload())(
          homeInner(opens, starteds, finisheds)
        )
      )
    }
}
