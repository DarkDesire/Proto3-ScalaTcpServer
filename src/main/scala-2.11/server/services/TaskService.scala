package server.services

import akka.actor.{Actor, ActorLogging, ActorRef}
import packet.PacketMSG
import server.base.Cmd

object TaskService {
  // ----- API -----
  case class CommandTask(session: ActorRef, comm: PacketMSG)
}

class TaskService extends Actor with ActorLogging {
  import TaskService._

  def receive = {
    case task: CommandTask => handlePacket(task)

    case _ => log.info("unknown message")
  }

  // ----- actor -----
  override def preStart() {
    log.info("Started TaskService")
  }
  override def postStop() {
    // clean up resources
    log.info("Stopped TaskService")
  }
  // -----
  val authService = context.actorSelection("akka://system/user/AuthService")

  // ----- actions -----
  def handlePacket(task: CommandTask) = {
    task.comm.cmd match {
      case Cmd.Auth.code => authService ! AuthService.Authenticate(task.session, task.comm)
   /*   case Cmd.Join.code => gameService ! GmService.JoinGame(task.session)
      case Cmd.Move.code  => gameService ! Room.PlayerMove(task.session, task.comm)*/
      case _ => // illegal, ban him?
    }
  }

}
