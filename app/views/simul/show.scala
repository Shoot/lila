package views.html.simul

import play.api.libs.json.Json

import lila.api.Context
import lila.app.templating.Environment._
import lila.app.ui.ScalatagsTemplate._
import lila.common.String.html.safeJsonValue

import controllers.routes

object show {

  def apply(
    sim: lila.simul.Simul,
    socketVersion: lila.socket.Socket.SocketVersion,
    data: play.api.libs.json.JsObject,
    chatOption: Option[lila.chat.UserChat.Mine],
    stream: Option[lila.streamer.Stream]
  )(implicit ctx: Context) = views.html.base.layout(
    responsive = true,
    moreCss = responsiveCssTag("simul.show"),
    title = sim.fullName,
    underchat = Some(div(
      cls := "watchers none",
      aria.live := "off",
      aria.relevant := "additions removals text"
    )(span(cls := "list inline_userlist"))),
    chat = views.html.chat.frag.some,
    moreJs = frag(
      jsAt(s"compiled/lichess.simul${isProd ?? (".min")}.js"),
      embedJs(s"""lichess.simul={
data:${safeJsonValue(data)},
i18n:${bits.jsI18n()},
socketVersion:${socketVersion.value},
userId: $jsUserIdString,
chat: ${chatOption.fold("null")(c => safeJsonValue(views.html.chat.json(c.chat, name = trans.chatRoom.txt(), timeout = c.timeout, public = true)))}}""")
    )
  ) {
    main(cls := "simul page-menu")(
      st.aside(cls := "page-menu__menu")(
      div(cls := "simul__meta")(
        div(cls := "game_infos")(
          div(cls := List(
            "variant-icons" -> true,
            "rich" -> sim.variantRich
          ))(sim.perfTypes.map { pt => span(dataIcon := pt.iconChar) }),
          span(cls := "clock")(sim.clock.config.show),
          br,
          div(cls := "setup")(
            sim.variants.map(_.name).mkString(", "),
            " • ",
            trans.casual()
          ),
          trans.simulHostExtraTime(),
          ": ",
          pluralize("minute", sim.clock.hostExtraMinutes),
          br,
          trans.hostColorX(sim.color match {
            case Some("white") => trans.white()
            case Some("black") => trans.black()
            case _ => trans.randomColor()
          })
        ),
        trans.by(usernameOrId(sim.hostId)),
        " ",
        momentFromNow(sim.createdAt)
      ),
      stream.map { s =>
        a(
          href := routes.Streamer.show(s.streamer.userId),
          cls := "context-streamer text side_box",
          dataIcon := ""
        )(usernameOrId(s.streamer.userId), " is streaming")
      }
        ),
      div(cls := "page-menu__content simul__content")
    }
}
