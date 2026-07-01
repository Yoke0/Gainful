package com.yoke.gainful.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EastMoneyApiImplTest {
    private fun createApi(handler: MockEngine): EastMoneyApi {
        val client =
            HttpClient(handler) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        },
                    )
                }
            }
        return EastMoneyApiImpl(client)
    }

    @Test
    fun getQuote_parsesAllFields() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = QUOTE_RESPONSE_JSON,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            val quote = api.getQuote("1.600519")

            assertNotNull(quote)
            assertEquals("600519", quote.code)
            assertEquals(1, quote.market)
            assertEquals("贵州茅台", quote.name)
            assertEquals(1688.00, quote.latestPrice)
            assertEquals(1.25, quote.changePercent)
            assertEquals(20.80, quote.changeAmount)
            assertEquals(12345L, quote.volume)
            assertEquals(123456789.0, quote.turnover)
            assertEquals(2.15, quote.amplitude)
            assertEquals(0.58, quote.turnoverRate)
            assertEquals(45.6, quote.peDynamic)
            assertEquals(12.5, quote.pb)
            assertEquals(1695.00, quote.high)
            assertEquals(1670.00, quote.low)
            assertEquals(1675.00, quote.open)
            assertEquals(1667.20, quote.preClose)
            assertEquals(21200000000.0, quote.totalMarketCap)
            assertEquals(21200000000.0, quote.circulatingMarketCap)
            assertEquals("白酒", quote.industry)
        }

    @Test
    fun getQuote_returnsNullWhenDataIsNull() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = """{"rc": 0, "data": null}""",
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            assertEquals(null, api.getQuote("1.999999"))
        }

    @Test
    fun getBatchQuotes_parsesMultipleStocks() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = BATCH_RESPONSE_JSON,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            val quotes = api.getBatchQuotes(listOf("1.600519", "0.000858", "1.601318"))

            assertEquals(3, quotes.size)
            assertEquals("600519", quotes[0].code)
            assertEquals("贵州茅台", quotes[0].name)
            assertEquals(1271.1, quotes[0].latestPrice)
            assertEquals("000858", quotes[1].code)
            assertEquals("五 粮 液", quotes[1].name)
            assertEquals("601318", quotes[2].code)
            assertEquals("中国平安", quotes[2].name)
        }

    @Test
    fun getBatchQuotes_returnsEmptyOnNullData() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = """{"rc": 0, "data": null}""",
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            assertTrue(api.getBatchQuotes(listOf("1.600519")).isEmpty())
        }

    @Test
    fun getTrends_parsesTrendData() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = TREND_RESPONSE_JSON,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            val trend = api.getTrends("1.600519", ndays = 1)

            assertNotNull(trend)
            assertEquals(4, trend.data.size)
            assertEquals(1, trend.data[0].market)
            assertEquals(1750000000000L, trend.data[0].timestamp)
            assertEquals(1292, trend.data[0].price)
            assertEquals(1271, trend.data.last().price)
        }

    @Test
    fun getTrends_returnsNullOnNullData() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = """{"rc": 0, "data": null}""",
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            val trend = api.getTrends("1.600519", ndays = 1)
            assertNotNull(trend)
            assertTrue(trend.data.isEmpty())
        }

    @Test
    fun getKLines_parsesKLineData() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = KLINE_RESPONSE_JSON,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            val kline = api.getKLines("1.600519", klt = 101, fqt = 1, beg = "20240101", end = "20240131")

            assertNotNull(kline)
            assertEquals("600519", kline.code)
            assertEquals(1, kline.market)
            assertEquals("贵州茅台", kline.name)
            assertEquals(3, kline.klines.size)
            assertEquals("2024-01-15,1528.68,1533.68,1548.65,1528.13,17854,2931202343.00,1.34,-0.20,-3.06,0.14", kline.klines[0])
        }

    @Test
    fun getKLines_returnsNullOnNullData() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = """{"rc": 0, "data": null}""",
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            assertEquals(null, api.getKLines("1.600519", klt = 101, fqt = 1, beg = "0", end = "20500101"))
        }

    @Test
    fun search_parsesResults() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = SEARCH_RESPONSE_JSON,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            val results = api.search("茅台", count = 10)

            assertEquals(1, results.size)
            assertEquals("600519", results[0].code)
            assertEquals("贵州茅台", results[0].name)
            assertEquals("1", results[0].market)
            assertEquals("沪A", results[0].typeName)
            assertEquals("1.600519", results[0].quoteId)
            assertEquals("AStock", results[0].classify)
        }

    @Test
    fun search_returnsEmptyOnNullData() =
        runTest {
            val api =
                createApi(
                    MockEngine { _ ->
                        respond(
                            content = """{"QuotationCodeTable": null}""",
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                )

            assertTrue(api.search("不存在", count = 10).isEmpty())
        }

    companion object {
        private val QUOTE_RESPONSE_JSON =
            """
            {
              "rc": 0, "rt": 11, "svr": 177617640, "lt": 1, "full": 1, "dlmkts": "", "dsc": "0",
              "data": {
                "f2": 1688.00, "f3": 1.25, "f4": 20.80, "f5": 12345, "f6": 123456789,
                "f7": 2.15, "f8": 0.58, "f9": 45.6, "f12": "600519", "f13": 1,
                "f14": "贵州茅台", "f15": 1695.00, "f16": 1670.00, "f17": 1675.00,
                "f18": 1667.20, "f20": 21200000000, "f21": 21200000000, "f23": 12.5, "f100": "白酒"
              }
            }
            """.trimIndent()

        private val BATCH_RESPONSE_JSON =
            """
            {
              "rc": 0, "rt": 11, "svr": 177617640, "lt": 1, "full": 1, "dlmkts": "", "dsc": "0",
              "data": {
                "total": 3,
                "diff": [
                  {"f2": 1271.1, "f3": -1.61, "f4": -20.81, "f12": "600519", "f13": 1, "f14": "贵州茅台"},
                  {"f2": 79.63, "f3": -0.36, "f4": -0.29, "f12": "000858", "f13": 0, "f14": "五 粮 液"},
                  {"f2": 54.23, "f3": 0.41, "f4": 0.22, "f12": "601318", "f13": 1, "f14": "中国平安"}
                ]
              }
            }
            """.trimIndent()

        private val TREND_RESPONSE_JSON =
            """
            {
              "rc": 0, "rt": 10, "svr": 177617603, "lt": 1, "full": 1, "dlmkts": "", "dsc": "0",
              "data": [
                {"f1": 1, "f2": 1750000000000, "f3": 1292},
                {"f1": 1, "f2": 1750000060000, "f3": 1284},
                {"f1": 1, "f2": 1750000120000, "f3": 1280},
                {"f1": 1, "f2": 1750004800000, "f3": 1271}
              ]
            }
            """.trimIndent()

        private val KLINE_RESPONSE_JSON =
            """
            {
              "rc": 0, "rt": 17, "svr": 177617935, "lt": 1, "full": 0, "dlmkts": "", "dsc": "0",
              "data": {
                "code": "600519", "market": 1, "name": "贵州茅台",
                "klines": [
                  "2024-01-15,1528.68,1533.68,1548.65,1528.13,17854,2931202343.00,1.34,-0.20,-3.06,0.14",
                  "2024-01-12,1533.65,1536.74,1544.94,1533.08,11603,1908084612.00,0.77,-0.23,-3.51,0.09",
                  "2024-01-11,1533.78,1540.25,1551.62,1531.74,16441,2708764500.00,1.29,0.33,5.07,0.13"
                ]
              }
            }
            """.trimIndent()

        private val SEARCH_RESPONSE_JSON =
            """
            {
              "QuotationCodeTable": {
                "Data": [
                  {
                    "Code": "600519", "Name": "贵州茅台", "PinYin": "GZMT",
                    "ID": "6005191", "JYS": "2", "Classify": "AStock",
                    "MarketType": "1", "SecurityTypeName": "沪A", "SecurityType": "1",
                    "MktNum": "1", "TypeUS": "2", "QuoteID": "1.600519",
                    "UnifiedCode": "600519", "InnerCode": "46077278941243"
                  }
                ],
                "Status": 0, "Message": "成功", "TotalCount": 1
              }
            }
            """.trimIndent()
    }
}
