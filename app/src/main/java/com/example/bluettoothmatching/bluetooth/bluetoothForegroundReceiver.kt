package com.example.bluettoothmatching.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// Bluetoothのフォアグラウンドの状態を監視
class BluetoothForegroundReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val targetIntent = Intent(context, BluetoothBK::class.java)
        context.stopService(targetIntent)
    }
}