package server.base

case class Point(var x:Int, var y:Int)

case class Player(id: Int, name:String, password: String, point: Point)
