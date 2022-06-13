package com.lo.auctionsystem.timer

import org.mockito.scalatest.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CountDownTimerTest
  extends AnyFlatSpec
  with Matchers
  with MockitoSugar
  with ScalaFutures
  with BeforeAndAfterEach {}
