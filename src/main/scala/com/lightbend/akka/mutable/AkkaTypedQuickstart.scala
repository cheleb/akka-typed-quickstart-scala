package com.lightbend.akka.mutable

import akka.typed.scaladsl.Actor
import akka.typed.Behavior
import akka.typed.ActorSystem
import scala.io.StdIn
import akka.typed.ActorRef.ActorRefOps

object MutableGreeter {
  sealed trait Command
  case object Greet extends Command
  final case class WhoToGreet(who: String) extends Command

  val greeterBehavior: Behavior[Command] = Actor.mutable[MutableGreeter.Command](ctx => new MutableGreeter())

}

class MutableGreeter extends Actor.MutableBehavior[MutableGreeter.Command] {
  import MutableGreeter._

  private var greeting = "hello"

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case WhoToGreet(who) =>
        greeting = s"hello, $who"
      case Greet =>
        println(greeting)
    }
    this
  }
}

object AkkaTypedQuickstart extends App {

  val root = Actor.deferred[Nothing] { ctx =>
    import MutableGreeter._
    val greeter = ctx.spawn(greeterBehavior, "greeter")
    greeter ! Greet
    greeter ! WhoToGreet("World")
    greeter ! Greet

    Actor.empty
  }
  val system = ActorSystem[Nothing]("HelloWorld", root)
  try {
    println("Press ENTER to exit the system")
    StdIn.readLine()
  } finally {
    system.terminate()
  }

}