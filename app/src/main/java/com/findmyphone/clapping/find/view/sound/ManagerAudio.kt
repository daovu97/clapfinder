package com.findmyphone.clapping.find.view.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.findmyphone.clapping.find.data.local.Settings

class ManagerAudio(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    fun isPlaying() = mediaPlayer?.isPlaying == true

    fun getMax(): Int {
        val mAudioManager =
            context.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
    }

    fun play(uri: String, onStart: (() -> Unit)? = null) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            )
            isLooping = true
        }
        mediaPlayer?.play(context, uri, onStart)
        try {
            val max = getMax()
            setVolume(Settings.VOLUME.get(max).toFloat() / max)
        } catch (e: Exception) {
            Log.d("LOG", e.toString())
        }
    }

    fun onPause() {
        mediaPlayer?.pause()
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}

private fun MediaPlayer.play(context: Context, uri: String, onStart: (() -> Unit)? = null) {
    reset()
    val mediaPath = Uri.parse(uri)
    setDataSource(context, mediaPath)

    prepareAsync()
    setOnPreparedListener {
        start()
        onStart?.invoke()
    }
    isLooping = true
}