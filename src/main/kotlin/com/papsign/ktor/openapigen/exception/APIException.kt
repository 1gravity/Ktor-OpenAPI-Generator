package com.papsign.ktor.openapigen.exception

import com.papsign.ktor.openapigen.util.getKType
import com.papsign.ktor.openapigen.util.unitKType
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Interface to describe an API Exception.
 */
interface APIException<TException : Throwable, TMessage> {
    val status: HttpStatusCode
    val exceptionClass: KClass<TException>
    val contentType: KType
        get() = unitKType
    val contentFn: ((TException) -> TMessage)?
        get() = null
    val example: TMessage?
        get() = null

    companion object {
        inline fun <reified TException : Throwable> apiException(status: HttpStatusCode): APIException<TException, Unit> =
            apiException(status, null as Unit?, null)

        /**
         * Convenience function to create an APIException.
         * If @param example is null pass in Unit as TMessage (e.g. apiException<TThrowable, Unit>(status)
         */
        inline fun <reified TException : Throwable, reified TMessage> apiException(
            status: HttpStatusCode,
            example: TMessage? = null,
            noinline contentFn: ((TException) -> TMessage)? = null
        ): APIException<TException, TMessage> =
            APIExceptionImpl(
                status = status,
                exceptionClass = TException::class,
                contentType = getKType<TMessage>(),
                contentFn = contentFn,
                example = example
            )
    }

}


