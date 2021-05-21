package com.papsign.ktor.openapigen.model

import com.papsign.ktor.openapigen.util.cleanEmptyValues
import com.papsign.ktor.openapigen.util.convertToValue
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

interface DataModel {

    fun serialize(): Map<String, Any?> =
        this::class.memberProperties
            .associateBy { it.name }
            .mapValues { (_, prop) -> convertToValue((prop as KProperty1<DataModel, *>).get(this)) }
            .cleanEmptyValues()

}
