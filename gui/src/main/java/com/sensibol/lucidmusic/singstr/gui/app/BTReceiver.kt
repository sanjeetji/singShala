package com.sensibol.lucidmusic.singstr.gui.app

import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.widget.Toast
import timber.log.Timber


class BTReceiver : BroadcastReceiver() {
    var state = 0
    var audioManager: AudioManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("$TAG Received: Bluetooth")
        try {
            val extras = intent.extras
            if (extras != null) { //Do something
                audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager?
                val action = intent.action
//                Toast.makeText(context, action, Toast.LENGTH_LONG).show()
                val state: Int
                if (action == BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) {
                    state = intent.getIntExtra(
                        BluetoothHeadset.EXTRA_STATE,
                        BluetoothHeadset.STATE_DISCONNECTED
                    )
                    if (state == BluetoothHeadset.STATE_CONNECTED) {
                        setModeBluetooth()
                    } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                        // Calling stopVoiceRecognition always returns false here
                        // as it should since the headset is no longer connected.
                        setModeNormal()
                        Timber.d("$TAG Headset disconnectedh")
                    }
                } else  // audio
                {
                    state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED)
                    Timber.d("$TAG Action = $action\n" + "State = $state")
                    if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED) {
                        Timber.d("Headset audio connected")
                        setModeBluetooth()
                    } else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {
                        setModeNormal()
                        Timber.d("$TAG Headset audio disconnected")
                    }
                }
            }
        } catch (e: Exception) {
            Timber.d("$TAG Exception $e")
        }
    }

    private fun setModeBluetooth() {
        try {
            audioManager!!.mode = AudioManager.MODE_IN_COMMUNICATION
            audioManager!!.startBluetoothSco()
            audioManager!!.isBluetoothScoOn = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setModeNormal() {
        try {
            audioManager!!.mode = AudioManager.MODE_NORMAL
            audioManager!!.stopBluetoothSco()
            audioManager!!.isBluetoothScoOn = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "BTReceiver"
    }
}