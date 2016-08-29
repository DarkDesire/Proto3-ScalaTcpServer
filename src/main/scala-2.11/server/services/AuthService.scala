package server.services

import akka.actor.{Actor, ActorLogging, ActorRef}
import packet.{LoginResp, PacketMSG}
import server.base.Cmd
import server.frontend.TcpConnection
import server.services.DBStub.SomePlayer

object AuthService {
  // ----- API -----
  case class Authenticate(session: ActorRef, comm: PacketMSG)
  case class AuthenticatedFailed(session: ActorRef, comm: PacketMSG)
}


class AuthService extends Actor with ActorLogging {
  import AuthService._

  override def receive = {
      case task: Authenticate  => handleAuth(task)
      case task: SomePlayer => handleAuthenticated(task)
      case task: AuthenticatedFailed => handleFailed(task)

      case _ => log.info("unknown message")
  }

  override def preStart() {
    log.info("Started AuthService")
  }

  override def postStop() {
    // clean up resources
    log.info("Stopped AuthService")
  }
  // -----
  val taskService = context.actorSelection("akka://system/user/TaskService")
  val dbService = context.actorSelection("akka://system/user/DBService")

  // ----- actions -----
  def handleAuth(task: Authenticate) = {
    dbService ! DBStub.GetPlayerByName(task.session, task.comm)
  }

  def handleFailed(task: AuthenticatedFailed) = {
    task.session ! TcpConnection.Send(Cmd.AuthErr, Array[Byte]())
  }

  def handleAuthenticated(task: SomePlayer) = {
      // set a magic number
      val login = LoginResp().withId(task.player.id)
      task.session ! TcpConnection.Send(Cmd.AuthResp, login.toByteArray)
   //   gameService ! task
  }
}
