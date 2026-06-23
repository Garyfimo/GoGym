package com.garyfimo.gogym.home.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.garyfimo.gogym.config.AppConfig
import com.garyfimo.gogym.home.model.WeeklyActivityStats

class HomeApi(
    private val client: HttpClient,
    private val appConfig: AppConfig
) {
    suspend fun getWeeklyActivityStats(): WeeklyActivityStats {
        return client.get("${appConfig.baseUrl}/api/stats/weekly").body()
    }
}
