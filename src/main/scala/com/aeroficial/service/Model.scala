package com.aeroficial.service

import com.aeroficial.common.{AnotherCommon, Common}
import com.aeroficial.service.Event1Enum.Event1Enum


sealed trait BaseEvent {
  val id: String
}

case class Event1(override val id: String, data: Common, enum: Event1Enum) extends BaseEvent

case class Event2(override val id: String, data: AnotherCommon, data2: Double) extends BaseEvent

object Event1Enum extends Enumeration {
  type Event1Enum = Value
  val V1, V2 = Value
}


sealed trait BaseResult

case class Result1(data: Common) extends BaseResult

case class Result2(data: AnotherCommon) extends BaseResult