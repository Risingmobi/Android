package com.alex.tur.scheduler

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppSchedulerProvider : SchedulerProvider {

    var executor: Executor = Executors.newSingleThreadExecutor()

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun io(): Scheduler {
        return Schedulers.from(executor)
    }
}
