package com.papsign.ktor.openapigen.exception

import com.papsign.ktor.openapigen.util.unitKType
import io.ktor.http.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Implementation of APIException.
 *
 * @param status The HTTP status code returned to the client
 * @param exceptionClass The Kclass of the exception, if an exception of exceptionClass is thrown, it will be
 *                       handled by this APIException.
 * @param example An example of the HTTP response
 * @param contentType The media type of the HTTP response
 * @param contentFn The function that creates the HTTP response
 */
class APIExceptionImpl<TException : Throwable, TMessage>(
    override val status: HttpStatusCode,
    override val exceptionClass: KClass<TException>,
    override val contentType: KType = unitKType,
    override val contentFn: ((TException) -> TMessage)? = null,
    override val example: TMessage? = null
) : APIException<TException, TMessage>
