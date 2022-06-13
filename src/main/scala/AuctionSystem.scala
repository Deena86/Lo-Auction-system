import com.lo.auctionsystem.controllers.AuctionController
import com.lo.auctionsystem.service.AuctionServiceImpl
import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.json.{JsValue, Json}

import scala.io.StdIn.readLine
import scala.io.{Codec, Source, StdIn}
import java.io.FileInputStream

object AuctionSystem extends App with AuctionSystemBoot {
  var userInput = 0
  do {
    userInput = {
      println("""|Please select one of the following options:
                 |  1 - Use Json file for input
                 |  2 - Submit an item to sell
                 |  3 - Submit a bid
                 |  4 - Get current listings
                 |  5 - Exit""".stripMargin)
      StdIn.readInt()
    }
  } while (menuOptions(userInput))
}

trait AuctionSystemBoot {
  val auctionService: AuctionServiceImpl = new AuctionServiceImpl
  val auctionController: AuctionController = new AuctionController(auctionService)

  val applicationConf: Config = ConfigFactory.load("application.conf")
  val ingestionRate: Int = applicationConf.getInt("auction-system.ingestionRate")
  val jsonPath: String = applicationConf.getString("auction-system.jsonPath")

  def loadJson(): Unit = {

    val fileInputStream: FileInputStream = new FileInputStream(jsonPath)

    val strItemsOrBids =
      Source
        .fromInputStream(fileInputStream)((Codec.UTF8))
        .getLines()
        .mkString
    val jsonItems = Json.parse(strItemsOrBids).as[Seq[JsValue]]

    jsonItems.foreach(i => {
      auctionController.processItemFromJson(i)
      Thread.sleep(ingestionRate * 1000)
    })
  }

  def readJsonInput(): Unit = {
    val jsonString = Iterator
      .continually(readLine())
      .takeWhile(_.nonEmpty)
      .map(line => line)
      .mkString
    try {
      val jsonItem = Json.parse(jsonString).as[JsValue]
      auctionController.processItemFromJson(jsonItem)
    } catch {
      case e: Exception => println("Invalid Json input.")
    }
  }

  def menuOptions(selectedOption: Int): Boolean =
    selectedOption match {
      case 1 =>
        println(s"Using the following json file as input: $jsonPath")
        loadJson()
        true
      case 2 | 3 =>
        println("Enter valid json input for the item")
        readJsonInput()
        true
      case 4 =>
        auctionController.printCurrentListings()
        true
      case 5 =>
        println("Exiting the program.")
        System.exit(0)
        false
      case _ => // the else case
        println("Invalid input. Please try again!")
        true
    }
}
