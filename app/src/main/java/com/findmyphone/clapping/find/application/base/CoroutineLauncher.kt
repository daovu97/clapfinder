package com.findmyphone.clapping.find.application.base

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class CoroutineLauncher : CoroutineScope {

    open val dispatcher: CoroutineDispatcher = Dispatchers.Main
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = dispatcher + supervisorJob

    fun launch(action: suspend CoroutineScope.() -> Unit) = launch(block = action)

    fun cancelCoroutines() {
        supervisorJob.cancelChildren()
        supervisorJob.cancel()
    }
}