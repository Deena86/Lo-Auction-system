package com.lo.auctionsystem.timer

import java.util.{Timer, TimerTask}

object CountDownTimer {
  def startCountDownForItem(auctionTime: Int, itemId: String, callback: (String) => (String, Double)): Unit = {
    println(s"Bidding CountDown started for Item $itemId")
    try {
      val timer = new Timer
      timer.scheduleAtFixedRate(new TimerTask() {
        var i: Int = auctionTime
        override def run(): Unit = {
          i -= 1
          if (i < 0) {
            timer.cancel()
            val (winningBidder, winningAmount) = callback(itemId)
            println(s"Bidding CountDown ended for Item $itemId")
            println(
              s"Winning Buyer & Amount for $itemId is ${winningBidder} and ${winningAmount}"
            )
          }
        }
      }, 0, 1000)
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
