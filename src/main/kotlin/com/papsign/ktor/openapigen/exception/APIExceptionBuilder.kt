package com.papsign.ktor.openapigen.exception

import io.ktor.http.*

/**
 * Builder for APIExceptions.
 * This allows to use a syntax like:
 *
 *     apiException<NotFoundException, ErrorMessage> {
 *         status = HttpStatusCode.NotFound.description("Customer not found")
 *         example = ErrorMessage("Customer with uuid 26d1229eaba8 not found")
 *        contentFn = { ErrorMessage(it.message ?: "Customer not found") }
 *     }
 *
 * You can also use the
 */
class APIExceptionBuilder<TException : Throwable, TMessage> {
    var status: HttpStatusCode = HttpStatusCode.BadRequest
    var example: TMessage? = null
    var contentFn: ((TException) -> TMessage)? = null

    companion object {
        inline fun <reified TException : Throwable, reified TMessage> apiException(
            block: APIExceptionBuilder<TException, TMessage>.() -> Unit
        ) : APIException<TException, TMessage> =
            APIExceptionBuilder<TException, TMessage>().run {
                block(this)
                APIException.apiException(status, example, contentFn)
            }
    }
}
