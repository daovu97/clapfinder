package com.findmyphone.clapping.find.view.sound

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.recyclerview.widget.LinearLayoutManager
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.base.BaseActivity
import com.findmyphone.clapping.find.data.local.CSound
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.data.local.Sound
import com.findmyphone.clapping.find.data.local.isCustom
import com.findmyphone.clapping.find.data.local.uri
import com.findmyphone.clapping.find.databinding.ActivitySoundManagerBinding
import com.findmyphone.clapping.find.resource.customView.setOnBackClick
import com.findmyphone.clapping.find.resource.customView.setOnImageRightClick
import com.findmyphone.clapping.find.resource.utils.compatColor
import com.findmyphone.clapping.find.resource.utils.getAdmobIfNeed
import com.findmyphone.clapping.find.resource.utils.gone
import com.findmyphone.clapping.find.resource.utils.setOnSingleClickListener
import com.findmyphone.clapping.find.view.main.Duration
import com.findmyphone.clapping.find.view.sound.SoundAdapter.Companion.listSound
import com.github.file_picker.FileType
import com.github.file_picker.ListDirection
import com.github.file_picker.adapter.FilePickerAdapter
import com.github.file_picker.data.model.Media
import com.github.file_picker.extension.showFilePicker
import com.github.file_picker.listener.OnItemClickListener
import com.github.file_picker.listener.OnSubmitClickListener
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.NativeCallback


class ManagerSoundActivity : BaseActivity<ActivitySoundManagerBinding>() {

    private val listTxtButton: List<TextView> by lazy {
        return@lazy listOf(binding.txt15s, binding.txt30s, binding.txt1m, binding.txt2m)
    }

    companion object {
        val permissions =
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
            else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun makeBinding(layoutInflater: LayoutInflater): ActivitySoundManagerBinding {
        return ActivitySoundManagerBinding.inflate(layoutInflater)
    }

    private val mediaPlayer: ManagerAudio by lazy {
        ManagerAudio(this)
    }

    private lateinit var adapter: SoundAdapter

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        listTxtButton.setSelect(Duration.index(Duration.current))
        listTxtButton.forEachIndexed { index, textView ->
            textView.setOnSingleClickListener {
                listTxtButton.setSelect(index)
                Duration.current = Duration.get(index) ?: Duration.D15S
            }
        }

        adapter = SoundAdapter(this,
            onEvent = { sound, count ->
                if (sound.isCustom() && sound.custom == null) {
                    mediaPlayer.onPause()
                    pickAudio()
                    return@SoundAdapter
                }

                adapter.setSelect(sound.uri(this))
                mediaPlayer.play(sound.uri(this))
            }, onLongClick = { sound, count ->
                if (sound.isCustom() && sound.custom != null) {
                    mediaPlayer.onPause()
                    pickAudio()
                    return@SoundAdapter
                }
            }
        )
        binding.rcSound.adapter = adapter
        binding.rcSound.layoutManager = LinearLayoutManager(this)
        adapter.setSelect(Settings.soundUri(this))
        binding.header.setOnBackClick {
            finish()

        }

        binding.seekbarVolume

        binding.header.setOnImageRightClick {
            Settings.SOUND_POSITION.put(adapter.selectedPosition)
            Toast.makeText(
                this, getString(R.string.txt_choose_sound_ss,
                    adapter.getCurrent()?.nameSound?.let { getString(it) }), Toast.LENGTH_LONG
            ).show()
            finish()
        }
        loadNative()
        initSeekBarVolume()
    }

    private fun pickAudio() {
        showFilePicker(fileType = FileType.AUDIO,
            limitItemSelection = 1,
            listDirection = ListDirection.LTR,
            accentColor = compatColor(R.color.primaryColor),
            titleTextColor = compatColor(R.color.primaryColor),
            gridSpanCount = 3,
            onSubmitClickListener = object : OnSubmitClickListener {
                override fun onClick(files: List<Media>) {
                    files.firstOrNull()?.let {
                        if (it.file.exists()) {
                            val sound = Sound(
                                R.string.txt_sound,
                                custom = CSound(it.file.name, it.file.absolutePath)
                            )
                            Settings.CUSTOM_SOUND.put(it.file.name + "|<>|" + it.file.absolutePath)
                            adapter.setupData(listSound())
                            adapter.setSelect(it.file.absolutePath)
                            mediaPlayer.play(sound.uri(this@ManagerSoundActivity))
                        }
                    }
                }
            },
            onItemClickListener = object : OnItemClickListener {
                override fun onClick(media: Media, position: Int, adapter: FilePickerAdapter) {
                    if (!media.file.isDirectory) {
                        adapter.setSelected(position)
                    }
                }
            })
    }


    private fun initSeekBarVolume() {
        val max = mediaPlayer.getMax()
        binding.seekbarVolume.max = max
        val currVolume = Settings.VOLUME.get(max)
        binding.seekbarVolume.progress = currVolume
        binding.seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                Settings.VOLUME.put(i)
                mediaPlayer.setVolume(i.toFloat() / max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (!mediaPlayer.isPlaying()) {
                    adapter.getCurrent()?.uri(this@ManagerSoundActivity)
                        ?.let { mediaPlayer.play(it) }
                }
            }
        })
    }

    private fun List<TextView>.setSelect(@IntRange(from = 0, to = 3) position: Int = 0) {
        forEachIndexed { index, tv ->
            tv.changeBackground(index == position, position)
        }
    }

    private fun TextView.changeBackground(isChoose: Boolean, p: Int) {
        if (isChoose) {
            setBackgroundResource(R.drawable.bg_press_txt)
            setTextColor(compatColor(R.color.white))
        } else {
            setBackgroundResource(R.drawable.bg_press)
            setTextColor(compatColor(R.color.textColor))
        }
    }

    private fun loadNative() {
        fun hideAds() {
            binding.frAds.removeAllViews()
            binding.frAds.gone()
        }
        try {
            getAdmobIfNeed()?.loadNativeAd(this,
                getString(R.string.native_sound),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        val adView = LayoutInflater.from(this@ManagerSoundActivity)
                            .inflate(R.layout.layout_native_language, null) as NativeAdView
                        binding.frAds.removeAllViews()
                        binding.frAds.addView(adView)
                        getAdmobIfNeed()?.pushAdsToViewCustom(nativeAd, adView)
                    }

                    override fun onAdFailedToLoad() {
                        super.onAdFailedToLoad()
                        hideAds()
                    }
                }) ?: hideAds()
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
            hideAds()
        }

    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.onPause()
    }
}