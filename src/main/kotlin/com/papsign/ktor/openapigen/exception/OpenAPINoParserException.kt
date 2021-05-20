package com.papsign.ktor.openapigen.exception

import io.ktor.http.ContentType

class OpenAPINoParserException(val contentType: ContentType): Exception("No parser found for content type $contentType")
