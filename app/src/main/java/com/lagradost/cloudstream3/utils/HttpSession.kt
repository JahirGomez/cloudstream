package com.lagradost.cloudstream3.utils


import khttp.structures.authorization.Authorization
import khttp.structures.cookie.CookieJar
import khttp.structures.files.FileLike
import khttp.responses.Response


/**
 * An HTTP session manager.
 *
 * This class simply keeps cookies across requests.
 *
 * @property sessionCookies A cookie jar.
 */
class Session {
    companion object {
        const val DEFAULT_TIMEOUT = 30.0

        fun cookieStrToMap(cookie: String): Map<String, String> {
            val cookies = mutableMapOf<String, String>()
            for (string in cookie.split("; ")) {
                val split = string.split("=").toMutableList()
                val name = split.removeFirst().trim()
                val value = if (split.size == 0) {
                    "true"
                } else {
                    split.joinToString("=")
                }
                cookies[name] = value
            }
            return cookies.toMap()
        }
    }

    public val sessionCookies = CookieJar()

    public fun get(
        url: String, headers: Map<String, String?> = mapOf(),
        params: Map<String, String> = mapOf(),
        data: Any? = null, json: Any? = null,
        auth: Authorization? = null,
        cookies: Map<String, String>? = null,
        timeout: Double = DEFAULT_TIMEOUT,
        allowRedirects: Boolean? = null,
        stream: Boolean = false, files: List<FileLike> = listOf(),
    ): Response {
        val res = khttp.get(
            url, headers, params,
            data, json, auth,
            cookies, timeout,
            allowRedirects,
            stream, files
        )
        sessionCookies.putAll(res.cookies.toMap())
        if (res.headers.containsKey("set-content")) {
            sessionCookies.putAll(cookieStrToMap(res.headers["set-content"].toString().replace("path=/,", "")))
        }
        return res
    }

    public fun post(
        url: String, headers: Map<String, String?> = mapOf(),
        params: Map<String, String> = mapOf(),
        data: Any? = null, json: Any? = null,
        auth: Authorization? = null,
        cookies: Map<String, String>? = null,
        timeout: Double = DEFAULT_TIMEOUT,
        allowRedirects: Boolean? = null,
        stream: Boolean = false, files: List<FileLike> = listOf()
    ): Response {
        val res = khttp.post(
            url, headers, params,
            data, json, auth,
            cookies, timeout,
            allowRedirects,
            stream, files
        )
        sessionCookies.putAll(res.cookies.toMap())
        if (res.headers.containsKey("set-content")) {
            sessionCookies.putAll(cookieStrToMap(res.headers["set-content"].toString().replace("path=/,", "")))
        }
        return res
    }

}
