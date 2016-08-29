package server.frontend

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import akka.io.Tcp
import akka.io.Tcp._
import akka.util.ByteString
import packet.PacketMSG
import server.base.{Cmd, Msg}
import server.services.TaskService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object TcpConnection {
  def props(id: Long, connection: ActorRef) = Props(new TcpConnection(id, connection))
  // ----- heartbeat -----
  // Checking client connection for life
  case object Heartbeat
  val period = 10 seconds
  // ----- API -----
  // Sending message to client
  case class Send(cmd: Msg, data: Array[Byte])
}

class TcpConnection(val id: Long, connection: ActorRef) extends Actor with ActorLogging {
  import TcpConnection._

  def receive = {
    case Send(cmd, data) => sendData(cmd, data)

    case Heartbeat => sendHeartbeat()

    case Received(data) => receiveData(data)

    case PeerClosed => context stop self

    case _: Tcp.ConnectionClosed => context stop self

    case _ => log.info("unknown message")

  }

  // ----- actor -----
  override def preStart() {
    // initialization code
    scheduler = context.system.scheduler.schedule(period, period, self, Heartbeat)

    log.info("Session start: {}", toString)
  }

  // -----
  val taskService = context.actorSelection("akka://system/user/TaskService")

  // ----- heartbeat -----
  private var scheduler: Cancellable = _

  // ----- actions -----
  def receiveData(data: ByteString) {

     val comm: PacketMSG = PacketMSG.parseFrom( data.toArray )

    log.info(s"Received data: {${comm.toString}}")

    taskService ! TaskService.CommandTask( self, comm )
  }

  def sendData(cmd: Msg, data: Array[Byte]) = {
    val trp: PacketMSG = PacketMSG().withCmd(cmd.code).withData(com.google.protobuf.ByteString.copyFrom(data))
    val msg: ByteString = ByteString(trp.toByteArray)

    connection ! Write(msg)

    log.info(s"Cmd send: {$cmd}")
  }

  def sendHeartbeat(): Unit = {
    sendData(Cmd.Ping, Array[Byte]())
  }

}

