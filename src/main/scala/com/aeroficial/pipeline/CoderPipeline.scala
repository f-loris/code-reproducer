package com.aeroficial.pipeline

import com.aeroficial.common.{AnotherCommon, Common}
import com.aeroficial.pipeline.common.coders._
import com.aeroficial.pipeline.service.coders._
import com.aeroficial.service._
import com.spotify.scio._
import com.spotify.scio.coders.{Coder, CoderMaterializer}
import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.state.{StateSpecs, ValueState}
import org.apache.beam.sdk.transforms.{DoFn, ParDo, SerializableFunction, WithKeys}
import org.apache.beam.sdk.transforms.DoFn.{ProcessElement, StateId}
import org.apache.beam.sdk.values.KV
import org.joda.time.Duration

/*
sbt "runMain com.aeroficial.pipeline.CoderPipeline
  --project=[PROJECT] --runner=DataflowRunner --zone=[ZONE]"
*/
object CoderPipeline {


  def main(cmdlineArgs: Array[String]): Unit = {
    val (sc, args) = ContextAndArgs(cmdlineArgs)


    sc.customInput("Trigger event", GenerateSequence.from(0).withRate(1, Duration.millis(250)))
      .withName("construct event").map[BaseEvent] { i =>
      val id = i % 100
      if (i % 2 == 0) {
        Event1(id = id.toString, data = Common("test", Some(1.0)), `enum` = Event1Enum.V1)
      } else {
        Event2(id = id.toString, data = AnotherCommon(1.2d, Common("test", Some(1.0))), data2 = 1.2)
      }
    }
      .withName("by key").applyKvTransform(WithKeys.of(new SerializableFunction[BaseEvent, String]() {
      override def apply(e: BaseEvent): String = e.id
    }))
      .withName("stateful processing").applyTransform(ParDo.of(new StatefulDoFn))
      .withName("print").tap(println)


    val result = sc.run().waitUntilFinish()
  }

}

object StatefulDoFn {
  final val StateId = "customState"

  type DoFnT = DoFn[KV[String, BaseEvent], BaseResult]

  val stateCoder = CoderMaterializer.beamWithDefault(Coder[CustomState])
}

case class CustomState(prev: BaseEvent)

class StatefulDoFn extends StatefulDoFn.DoFnT {

  @StateId(StatefulDoFn.StateId) private val CustomStateSpec = StateSpecs.value[CustomState](StatefulDoFn.stateCoder)

  @ProcessElement
  def processElement(context: StatefulDoFn.DoFnT#ProcessContext,
                     @StateId(StatefulDoFn.StateId) stateContainer: ValueState[CustomState]): Unit = {
    val event = context.element().getValue
    val state = Option(stateContainer.read())
    val result = event match {
      case Event1(id, data, enum) => Result1(data)
      case Event2(id, data, data2) => Result2(data)
    }
    stateContainer.write(CustomState(event))
    context.output(result)
  }

}
