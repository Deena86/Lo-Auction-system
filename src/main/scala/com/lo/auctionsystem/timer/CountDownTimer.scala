package com.lo.auctionsystem.timer

import java.util.{Timer, TimerTask}

/** CountDown Timer for the Auction Item
  *
  * Once the auction item is added into the system, the timer task starts
  * for the duration of the auction specified in the input json.
  * Once the timer expires, a callback function will print the winning bid
  */
object CountDownTimer {
  def startCountDownForItem(
    auctionTime: Int,
    itemId: String,
    callback: (String) => Option[(String, Double)]
  ): Unit = {
    println(s"Bidding CountDown started for Item $itemId")
    try {
      val timer = new Timer
      timer.scheduleAtFixedRate(
        new TimerTask() {
          var i: Int = auctionTime
          override def run(): Unit = {
            i -= 1
            if (i < 0) {
              timer.cancel()
              callback(itemId).map(i => {
                println(s"Winning Buyer & Amount for $itemId is ${i._1} and ${i._2}")
              })
              println(s"Bidding CountDown ended for Item $itemId")
            }
          }
        },
        0,
        1000
      )
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
