package com.papsign.ktor.openapigen.routing

import com.fasterxml.jackson.databind.ObjectMapper
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import installJackson
import installOpenAPI
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertTrue

class RoutingTest {
    private val route = "/test"

    data class TestBodyParams(val xyz: Long)
    data class TestHeaderParams(@HeaderParam("test param") val `Test-Header`: Long)
    data class TestHeaderParamsLower(@HeaderParam("test param") val `test-header`: Long)
    @Path("{testPath}")
    data class TestPathParams(@PathParam("test param") val testPath: Long)
    data class TestQueryParams(@QueryParam("test param") val `Test-Query`: Long)
    @Path("{testPath}")
    data class TestParams(
        @HeaderParam("test param") val `Test-Header`: Long,
        @QueryParam("test param") val `Test-Query`: Long,
        @PathParam("test param") val testPath: Long
    )
    data class TestResponse(val msg: String)

    private fun Any.toJsonString(): String = ObjectMapper().writeValueAsString(this)

    @Test
    fun testGetNoParams() {
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<Unit, TestResponse> {
                        respond(TestResponse("Test Response"))
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Get, route) {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse("Test Response").toJsonString(),
                    response.content
                )
            }
        }
    }

    @Test
    fun testGetWithHeaderParams() {
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<TestHeaderParams, TestResponse> { params ->
                        respond(TestResponse("$params"))
                    }
                }
            }
        }) {
            val param = TestHeaderParams(123)
            handleRequest(HttpMethod.Get, route) {
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header", "${param.`Test-Header`}")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse(param.toString()).toJsonString(),
                    response.content
                )
            }
        }
    }

    @Test
    fun testGetWithQueryParams() {
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<TestQueryParams, TestResponse> { params ->
                        respond(TestResponse("$params"))
                    }
                }
            }
        }) {
            val param = TestQueryParams(123)
            handleRequest(HttpMethod.Get, "$route?Test-Query=${param.`Test-Query`}") {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse(param.toString()).toJsonString(),
                    response.content
                )
            }
        }
    }

    @Test
    fun testGetWithPathParams() {
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<TestPathParams, TestResponse> { params ->
                        respond(TestResponse("$params"))
                    }
                }
            }
        }) {
            val param = TestPathParams(123)
            handleRequest(HttpMethod.Get, "$route/${param.testPath}") {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse(param.toString()).toJsonString(),
                    response.content
                )
            }
        }
    }

    @Test
    fun testGetWithAllParams() {
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<TestParams, TestResponse> { params ->
                        respond(TestResponse("$params"))
                    }
                }
            }
        }) {
            val params = TestParams(123, 456, 789)
            handleRequest(HttpMethod.Get, "/test/${params.testPath}?Test-Query=${params.`Test-Query`}") {
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header", params.`Test-Header`.toString())
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse(params.toString()).toJsonString(),
                    response.content
                )
            }
        }
    }

    @Test
    fun testPostWithBodyParams() {
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<Unit, TestResponse, TestBodyParams> { _, body ->
                        respond(TestResponse("$body"))
                    }
                }
            }
        }) {
            val param = TestBodyParams(123)
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Accept, "application/json")
                setBody(param.toJsonString())
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse(param.toString()).toJsonString(),
                    response.content
                )
            }
        }
    }

    @Test
    fun testPostWithHeaderAndBodyParams() {
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<TestHeaderParams, TestResponse, TestBodyParams> { params, body ->
                        respond(TestResponse("$params -> $body"))
                    }
                }
            }
        }) {
            val headerParam = TestHeaderParams(123)
            val bodyParam = TestBodyParams(456)
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header", headerParam.`Test-Header`.toString())
                setBody(bodyParam.toJsonString())
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse("$headerParam -> $bodyParam").toJsonString(),
                    response.content
                )
            }
        }
    }

    @Test
    fun testPostWithAllParams() {
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<TestParams, TestResponse, TestBodyParams> { params, body ->
                        respond(TestResponse("$params -> $body"))
                    }
                }
            }
        }) {
            val params = TestParams(123, 456, 789)
            val bodyParam = TestBodyParams(456)

            // All parameters: case sensitive
            handleRequest(HttpMethod.Post, "/test/${params.testPath}?Test-Query=${params.`Test-Query`}") {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header", params.`Test-Header`.toString())
                setBody(bodyParam.toJsonString())
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse("$params -> $bodyParam").toJsonString(),
                    response.content
                )
            }
        }
    }

    /**
     * header: *case-insensitive*
     * https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html
     *
     * query: *case-sensitive* (except scheme and host)
     * https://datatracker.ietf.org/doc/html/rfc3986
     *
     * body: *case-sensitive*
     *
     * path: Not applicable (no key)
     */
    @Test
    fun testParamsCaseSensitivity() {
        // TParams with upper-case key, header key in lower case
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<TestHeaderParams, TestResponse> { params ->
                        respond(TestResponse("$params"))
                    }
                }
            }
        }) {
            val param = TestHeaderParams(123)
            handleRequest(HttpMethod.Get, route) {
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header".toLowerCase(), "${param.`Test-Header`}")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse(param.toString()).toJsonString(),
                    response.content
                )
            }
        }

        // TParams with lower-case key, header key in upper case
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<TestHeaderParamsLower, TestResponse> { params ->
                        respond(TestResponse("$params"))
                    }
                }
            }
        }) {
            val param = TestHeaderParamsLower(123)
            handleRequest(HttpMethod.Get, route) {
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header".toUpperCase(), "${param.`test-header`}")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    TestResponse(param.toString()).toJsonString(),
                    response.content
                )
            }
        }
    }

}
