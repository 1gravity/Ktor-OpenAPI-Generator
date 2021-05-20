package com.papsign.ktor.openapigen.exception

import io.ktor.http.ContentType

class OpenAPINoSerializerException(val contentTypes: ContentType): Exception("No serializer found for content types $contentTypes")
