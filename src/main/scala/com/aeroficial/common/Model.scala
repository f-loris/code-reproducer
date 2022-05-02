package com.aeroficial.common

case class Common(value1:String, value2:Option[Double])

case class AnotherCommon(value2: Double, common:Common)