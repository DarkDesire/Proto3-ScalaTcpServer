package server.services

import akka.actor.{Actor, ActorLogging, ActorRef}
import packet.{Login, PacketMSG}
import server.base.{Player, Point}

object DBStub {
  // ----- API -----
  case class GetPlayerByName(session: ActorRef, comm: PacketMSG)
  case class SomePlayer(session: ActorRef, comm: PacketMSG, player: Player)
}

class DBStub extends Actor with ActorLogging {
  import DBStub._
  val accounts = List(
     Player(1, "Tester1", "test", Point(0, 0)),
     Player(2, "Tester2", "test", Point(0, 0)),
     Player(3, "Tester3", "test", Point(0, 0)),
     Player(4, "Tester4", "test", Point(0, 0))
  )

  override def receive = {
    case task: GetPlayerByName => handleAuth(task)

    case _ => log.info("unknown message")
  }

  override def preStart() {
    log.info("Started DBStub")
  }

  override def postStop() {
    // clean up resources
    log.info("Stopped DBStub")
  }

  // ----- actions -----
  def handleAuth(task: GetPlayerByName) = {
    // parse task
    val cmd:Login = Login.parseFrom(task.comm.data.toByteArray)
    println(s"Handle auth: ${cmd.toString}")

    val players = accounts.filter(p => p.name.equals(cmd.name) && p.password.equals(cmd.pass))
    if (players.isEmpty) sender ! AuthService.AuthenticatedFailed(task.session, task.comm)
    else {
      val player = players.head
      player match {
        case Player(_,_,_,_) => sender ! DBStub.SomePlayer(task.session, task.comm, player)
        case _ => sender ! AuthService.AuthenticatedFailed(task.session, task.comm)
      }
    }

  }
}
