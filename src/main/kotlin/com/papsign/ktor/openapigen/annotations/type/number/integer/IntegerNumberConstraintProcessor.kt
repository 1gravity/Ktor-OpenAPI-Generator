package com.papsign.ktor.openapigen.annotations.type.number.integer

import com.papsign.ktor.openapigen.util.getKType
import com.papsign.ktor.openapigen.annotations.type.number.NumberConstraintProcessor

abstract class IntegerNumberConstraintProcessor<A: Annotation>: NumberConstraintProcessor<A>(listOf(
    getKType<Int>(),
    getKType<Long>(),
    getKType<Float>(),
    getKType<Double>()
))
