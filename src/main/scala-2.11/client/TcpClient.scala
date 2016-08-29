package client

import java.net.InetSocketAddress
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.ByteString
import packet.{Login, LoginResp, PacketMSG}
import server.base.{Cmd, Msg}
import server.frontend.TcpConnection.Send

import scalafx.scene.control.TextArea

object TcpClient {
  def props(remote: InetSocketAddress, textArea: TextArea) = Props(classOf[TcpClient], remote, textArea)

  // ----- API -----
  // Sending message to client
  case class SendLogin(cmd: Msg, email: String, password: String)

}

class TcpClient(remote: InetSocketAddress, textArea: TextArea) extends Actor with ActorLogging {
  import TcpClient._
  val tcpConnectionClient = context.actorOf(TcpConnectionClient.props(remote, self))

  def receive = {
    case data: ByteString => receiveData(data)
    case SendLogin(cmd, email, password) => sendLogin(cmd,email,password)
    case Send(cmd, data) => sendData(cmd, data)

    case value => println(value)
  }

  override def preStart() = {

  }

  def receiveData(data: ByteString) {

    val dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    val currentTime = dateFormat.format(new Date)

    val packet: PacketMSG = PacketMSG.parseFrom(data.toArray)
    val packetString = s"Received data: {${packet.toString}}"

    var cmdString = ""
    val info = packet.cmd match {
      case Cmd.Ping.code =>
        cmdString = s"Received a ping from server"
      case Cmd.AuthResp.code =>
        cmdString = s"Received auth success from serer"
        val loginResp = LoginResp.parseFrom(packet.data.toByteArray)
        cmdString = s"Login id:${loginResp.id}"
      case Cmd.AuthErr.code =>
        cmdString = s"Received auth failed from serer"
      case cmd =>
        cmdString = "Something strange"
    }
    val outputString = s"\n$cmdString \n$packetString"

    log.info(outputString)
    textArea.appendText(s"date: $currentTime $outputString\n")
  }

  def sendLogin(cmd: Msg, login: String, pass: String) = {
    val packetLogin: Login = Login().withName(login).withPass(pass)
    val packetBA = packetLogin.toByteArray
    sendData(Cmd.Auth,packetBA)
  }

  def sendData(cmd: Msg, data: Array[Byte]) = {
    val packet: PacketMSG = PacketMSG().withCmd(cmd.code).withData(com.google.protobuf.ByteString.copyFrom(data))
    val packetBA = packet.toByteArray

    tcpConnectionClient ! ByteString(packetBA)

    log.info(s"Cmd send: {$cmd}")
  }
}
