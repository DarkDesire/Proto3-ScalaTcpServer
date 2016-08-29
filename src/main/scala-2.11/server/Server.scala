package server

import akka.actor.{ActorSystem, Props}
import server.frontend.AkkaIoTcpServer
import server.services.{AuthService, DBStub, TaskService}

object Server extends App{
  // create the actor system and actors
  val actorSystem = ActorSystem("system")

  val actorTaskService =      actorSystem.actorOf(Props[TaskService],"TaskService")
  val actorTcpServer =        actorSystem.actorOf(AkkaIoTcpServer.props("127.0.0.1",8080),"TcpServer")
  val actorAuthService =      actorSystem.actorOf(Props[AuthService],"AuthService")
  val actorDBService =        actorSystem.actorOf(Props[DBStub],"DBService")
}
