package com.example.bluettoothmatching.bluetooth

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.bluettoothmatching.MainActivity
import com.example.bluettoothmatching.R

var tmpList = MutableLiveData<List<String>>()

class BlutoothBK : Service() {

    private var MacAddressSet = mutableSetOf<String?>()
    private var MacAddressList = mutableListOf<String?>()
    companion object {
        const val CHANNEL_ID = "1111"
        const val tuuti_ID = "2222"
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var gpsEnabled: Boolean = false
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            //Log.d("startSuccess1", "onReceive() success")
            val action: String? = intent.action

            when(action) {
                BluetoothDevice.ACTION_FOUND -> {

                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    Log.d("FUJI", "起動")

                    if (device == null) {
                        Log.d("nullDevice", "Device is null")
                        return
                    }

                    val deviceHardwareAddress = device?.address // MAC address

                    MacAddressSet.add(deviceHardwareAddress)
                    Log.d("MacA", MacAddressSet.toString())

                    MacAddressList.clear()
                    MacAddressList.addAll(MacAddressSet)
                    //MacAddressList.add("80:9F:F5:79:8C:E2")
                    //MacAddressList.add("58:C6:F0:5C:85:68")
                    MacAddressList.add("1")
                    //MacAddresst.add("2")
                    MacAddressList.add("3C:01:EF:0e:ee:e3")
                    MacAddressList.add("12:34:56:78:9")
                    tmpList.value = MacAddressList.filterNotNull().distinct().toMutableList()
                    Log.d("tmpList", tmpList.toString())
                    return
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->{
                    MacAddressList.clear()
                    MacAddressList.addAll(MacAddressSet)
                    MacAddressList.add("1") // todo ここはスタートに入れるものだからあとで消す
                    tmpList.value = MacAddressList.filterNotNull().distinct().toMutableList()

                    Log.d("FUJI",MacAddressSet.toString())
                }
            }
        }
    }
    override fun onCreate() {

        // 通知関連
        val name = "a"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(tuuti_ID,name,importance)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        Log.d("startSuccess2", "onCreate() success")

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.d("bluetoothAdapterTest", "null")
            onDestroy()
            return

        }
        var locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        Log.d("startSuccess3", "onCreate() success")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //MacAddressSet.clear()
        val openIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }
        val channelId = CHANNEL_ID
        val channelName = "TestService Channel"
        val channel = NotificationChannel(
            channelId, channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val sendIntent = Intent(this, BlutoothBK::class.java).apply {
            action = Intent.ACTION_SEND
        }
        val sendPendingIntent = PendingIntent.getBroadcast(this, 0, sendIntent, 0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("すれ違い検出中")
            .setContentText("終了する場合はこちらから行って下さい。")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openIntent)
            .addAction(R.drawable.ic_launcher_foreground, "スキャン終了", sendPendingIntent)
            .build()

        startForeground(2222, notification)

        Log.d("startSuccess4", "onStartCommand() success")

        startReceiver()

        if(bluetoothAdapter!!.startDiscovery()){

            Log.d("startSuccess5", "startDiscovery() success")

        }else{
            Log.d("startSuccess6", "startDiscovery() No")
        }

        setNextAlarmService(this)

        return START_NOT_STICKY

    }
    private fun setNextAlarmService(context: Context) {

        val repeatPeriod: Long = 1* 60 * 1000

        val intent = Intent(context, BlutoothBK::class.java)

        val startMillis = System.currentTimeMillis() + repeatPeriod

        val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            startMillis,
            pendingIntent
        )
    }
    private fun stopAlarmService() {
        val intent = Intent(this, BlutoothBK::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)

        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        alarmManager?.cancel(pendingIntent)
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    private fun startReceiver() {
        val intent = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothAdapter!!.cancelDiscovery()
        stopAlarmService()
        unregisterReceiver(receiver)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}


