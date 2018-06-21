package com.alex.tur.ext

import java.math.BigDecimal

fun Double.roundToDecimalPlaces(scale: Int) = BigDecimal(this).setScale(scale, BigDecimal.ROUND_HALF_UP).toDouble()
