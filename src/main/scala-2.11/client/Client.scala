package client

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import client.TcpClient.SendLogin
import server.base.Cmd

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control._
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout._
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{LinearGradient, Stops}
import scalafx.scene.text.{Text, TextAlignment}
import scalafx.scene.{Node, Scene}

object Client extends JFXApp {

  val textArea = new TextArea(){
    editable = false
  }

  val system = ActorSystem("system")
  val tcpClient = system.actorOf(TcpClient.props(new InetSocketAddress("127.0.0.1",8080),textArea))

  val emailField = new TextField() {
    prefColumnCount = 10
    promptText = "Your email"
  }

  val passwordField = new PasswordField() {
    prefColumnCount = 10
    promptText = "Your password"
  }
  val authButton = new Button("Auth") {
    onAction = handle {
      tcpClient ! SendLogin(Cmd.Auth,emailField.getText,passwordField.getText)
    }
  }

  private val borderStyle = "" +
    "-fx-background-color: white;" +
    "-fx-border-color: black;" +
    "-fx-border-width: 1;" +
    "-fx-border-radius: 6;" +
    "-fx-padding: 6;"

  stage = new JFXApp.PrimaryStage() {

    val panelsPane = new AnchorPane() {
      val appName = new Text {
        text = "Simple TCP Application"
        style = "-fx-font-size: 30pt"
        textAlignment = TextAlignment.Center
        fill = new LinearGradient(
          endX = 0,
          stops = Stops(Cyan, DodgerBlue)
        )
        effect = new DropShadow {
          color = DodgerBlue
          radius = 25
          spread = 0.25
        }
      }
      val commandPanel = createCommandPanel

      val textAreaPanel = createTextAreaPanel(textArea)

      children = new VBox(10){
        padding = Insets(20)
        children = Seq(appName, commandPanel, textAreaPanel)
      }
      alignmentInParent = Pos.Center
      style = borderStyle
    }

    title = "Simple TCP Application"
    scene = new Scene(400, 480) {
      root = new BorderPane() {
        center = panelsPane
      }
    }
  }

  private def createCommandPanel: Node = {
    val toggleGroup1 = new ToggleGroup()

    new HBox(10) {
      children = Seq(
        new VBox(10) {
          padding = Insets(0,0,0,20)
          children = Seq(emailField, passwordField)
        },
        authButton ,
        new Button("Join") {
          onAction = handle { println("Join")}
        },
        new Button("Move") {
          onAction = handle { println("Move")}
        },
        new Button("Another") {
          onAction = handle { println("Another")}
        }
      )

      alignment = Pos.TopLeft
      style = borderStyle
    }
  }

  private def createTextAreaPanel(textArea: TextArea): Node =
    new HBox(10) {
    children = Seq(
      textArea
    )
    alignment = Pos.TopLeft
    style = borderStyle
  }
}