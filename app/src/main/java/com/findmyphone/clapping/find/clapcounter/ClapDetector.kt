package com.findmyphone.clapping.find.clapcounter

import android.os.Handler
import android.os.Looper
import android.util.Log
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.onsets.PercussionOnsetDetector


class ClapDetector {

    private var isDetectClap = false
    private var lastDetect = 0L
    private var count = 1
    private var dispatcher: AudioDispatcher? = null
    private var thread: Thread? = null
    private var handler: Handler? = null
    private var mPercussionDetector: PercussionOnsetDetector? = null

    private fun startHandler(delay: Long) {
        handler?.postDelayed({
            if (isDetectClap) {
                isDetectClap = false
            }
        }, delay)
    }

    private fun removeHandler() {
        handler?.removeCallbacksAndMessages(null)
    }

    fun startDetectClap(
        counterAction: Int = 2,
        delay: Long = 500,
        threshold: Double = 6.0,
        sensitivity: Double = 40.0,
        bufferSize: Int = 1024,
        action: () -> Unit,
        onError: () -> Unit
    ) {
        handler = Handler(Looper.getMainLooper())
        try {
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, bufferSize, 0)
            mPercussionDetector = PercussionOnsetDetector(
                22050f, bufferSize,
                { _, _ ->
                    clapEvent(counterAction, delay, action)
                }, sensitivity, threshold
            )
            dispatcher?.addAudioProcessor(mPercussionDetector)
            thread = Thread(dispatcher, "Audio Dispatcher")
            thread?.start()
        } catch (e: Exception) {
            cancel()
            onError.invoke()
        }
    }

    private fun clapEvent(
        counterAction: Int = 2,
        delay: Long = 500,
        action: () -> Unit
    ) {
        val current = System.currentTimeMillis()
        log("DetectClap:${current - lastDetect}")
        if (!isDetectClap) {
            isDetectClap = true
            count = 1
            startHandler(delay)
        } else if (current - lastDetect < delay) {
            removeHandler()
            count++
            if (count == counterAction) {
                action.invoke()
                isDetectClap = false
            } else {
                startHandler(delay)
            }
        } else isDetectClap = false

        lastDetect = current
    }

    private fun log(message: String) {
        Log.d("AAA", message)
    }

    fun cancel() {
        try {
            handler?.removeCallbacksAndMessages(null)
            dispatcher?.stop()
            thread?.interrupt()
        } catch (exp: Exception) {
            log("error doing cancel: ${exp.localizedMessage}")
        }
    }

}