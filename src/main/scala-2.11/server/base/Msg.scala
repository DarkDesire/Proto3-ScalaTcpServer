package server.base

sealed trait Msg { def code: Int }

case object Cmd {
  case object Ping     extends Msg { val code = 1 }
  case object Auth     extends Msg { val code = 5 }
  case object AuthResp extends Msg { val code = 6 }
  case object AuthErr  extends Msg { val code = 7 }
  case object Join     extends Msg { val code = 8 }
  case object Move     extends Msg { val code = 9 }
}