package com.example.bluettoothmatching.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothForegroundReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val targetIntent = Intent(context, BlutoothBK::class.java)
        context.stopService(targetIntent)
    }
}