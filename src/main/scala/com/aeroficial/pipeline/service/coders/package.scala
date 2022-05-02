package com.aeroficial.pipeline.service
import com.aeroficial.service.{BaseEvent, Event1Enum}
import com.aeroficial.service.Event1Enum.Event1Enum
import com.aeroficial.pipeline.common.coders._
import com.spotify.scio.coders.Coder

package object coders {
  implicit def event1EnumCoder: Coder[Event1Enum] = Coder.xmap(Coder[Int])(Event1Enum(_), _.id)

  implicit val baseEventCoder: Coder[BaseEvent] = Coder.gen[BaseEvent]
}
