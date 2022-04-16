package com.thunder.thunderplane.tool

import android.app.Activity
import android.media.MediaPlayer
import com.thunder.thunderplane.R

object MusicTool {

    private var shootMusic: MediaPlayer? = null
    private var upgradeMusic: MediaPlayer? = null
    private var gameOverMusic: MediaPlayer? = null
    private var bgMusic: MediaPlayer? = null

    fun Activity.initMusic() {
        shootMusic = MediaPlayer.create(this, R.raw.laser2)
        shootMusic?.setVolume(0.2f,0.2f)
        upgradeMusic = MediaPlayer.create(this, R.raw.upgrade_music)
        upgradeMusic?.setVolume(0.2f,0.2f)
        gameOverMusic = MediaPlayer.create(this, R.raw.gameover)
        gameOverMusic?.setVolume(0.2f,0.2f)
        bgMusic = MediaPlayer.create(this, R.raw.background_music)
        bgMusic?.isLooping = true
    }

    fun playShootMusic() {
        shootMusic?.seekTo(0)
        shootMusic?.start()
    }

    fun playUpgradeMusic() {
        upgradeMusic?.start()
    }

    fun playGameOverMusic() {
        gameOverMusic?.start()
    }

    fun playBgMusic(){
        bgMusic?.start()
    }

    fun stopBgMusic() {
        bgMusic?.seekTo(0)
        bgMusic?.pause()
    }

}