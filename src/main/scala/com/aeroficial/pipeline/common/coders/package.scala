package com.aeroficial.pipeline.common

import com.aeroficial.common.{AnotherCommon, Common}
import com.spotify.scio.coders.Coder

package object coders {
  implicit val commonCoder: Coder[Common] = Coder.gen[Common]
  implicit val anotherCommonCoder: Coder[AnotherCommon] = Coder.gen[AnotherCommon]
}
