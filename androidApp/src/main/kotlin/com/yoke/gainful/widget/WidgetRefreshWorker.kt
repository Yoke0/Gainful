package com.yoke.gainful.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.yoke.gainful.di.initKoin
import com.yoke.gainful.widget.domain.GetTodayPnlUseCase
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit

class WidgetRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return runCatching {
            if (!WidgetContextHolder.isInitialized()) {
                WidgetContextHolder.init(applicationContext)
            }
            initKoin(applicationContext)

            val title = applicationContext.getString(com.yoke.gainful.R.string.widget_today_pnl_title)
            val noData = applicationContext.getString(com.yoke.gainful.R.string.widget_no_data)
            val useCase = GlobalContext.get().get<GetTodayPnlUseCase>()
            val pnl = useCase.compute(title, noData)
            syncWidgetData(pnl)

            TodayPnlWidget().updateAll(applicationContext)

            Result.success()
        }.getOrElse { Result.retry() }
    }

    companion object {
        private const val WORK_NAME = "widget_refresh"

        fun enqueue(context: Context) {
            val request =
                PeriodicWorkRequestBuilder<WidgetRefreshWorker>(15, TimeUnit.MINUTES)
                    .setInitialDelay(1, TimeUnit.MINUTES)
                    .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request,
                )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
