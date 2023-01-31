package com.sample.bleintegration

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mx.mxSdk.ConnectManager
import com.mx.mxSdk.Device
import com.sample.bleintegration.ui.theme.BleIntegrationTheme

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>

    private var permissions = mutableListOf<Boolean>()

    private val connectManager by lazy { ConnectManager.share() }
    private val onDeviceBluetoothStateListener: ConnectManager.OnDeviceBluetoothStateListener = object: ConnectManager.OnDeviceBluetoothStateListener {
        override fun onDeviceBlueToothOpening() {
            Log.i(TAG,"deviceBtStateListener->btOpenning::opening bluetooth....")
        }

        override fun onDeviceBlueToothOpened() {
            Log.i(TAG,"deviceBtStateListener->btOpenned::Bluetooth just openned")
        }

        override fun onDeviceBlueToothClosing() {
            Log.i(TAG,"deviceBtStateListener->btClosing::closing bluetooth....")
        }

        override fun onDeviceBlueToothClosed() {
            Log.i(TAG,"deviceBtStateListener->btClosed::Bluetooth just closed")
        }
    }
    private val onDeviceDiscoverListener: ConnectManager.OnDeviceDiscoverListener = object: ConnectManager.OnDeviceDiscoverListener{
        override fun onStartDiscover() {
            Log.i(TAG,"deviceDiscoverListener->start::Bluetooth discover started....")
        }

        override fun onStopDiscover() {
            Log.i(TAG,"deviceDiscoverListener->stop::Bluetooth discover stopped....")
        }

        override fun onDiscovered(device: Device?) {
            Log.i(TAG,"deviceDiscoverListener->discovered::got device data, device name:${device?.deviceName}")
            if(device?.isBle == true){
                connect(device)
            }
        }
    }
    private val onDeviceConnectListener: ConnectManager.OnDeviceConnectListener = object: ConnectManager.OnDeviceConnectListener{
        override fun onDeviceConnectStart(p0: Device?) {
            Log.i(TAG,"deviceConnectListener->connectStart::device name: ${p0?.deviceName}")
        }

        override fun onDeviceConnectSucceed(p0: Device?) {
            Log.i(TAG,"deviceConnectListener->connectSucceed::device name: ${p0?.deviceName}")
        }

        override fun onDeviceDisconnect(p0: Device?) {
            Log.i(TAG,"deviceConnectListener->disconnect::device name: ${p0?.deviceName}")
        }

        override fun onDeviceConnectFail(p0: Device?, p1: String?) {
            Log.e(TAG,"deviceConnectListener->connectFail::device name: ${p0?.deviceName}")
        }
    }
    private val onReceiveMsgListener: ConnectManager.OnReceiveMsgListener = object: ConnectManager.OnReceiveMsgListener{
        override fun onParameterChange(p0: Device?, p1: Int, p2: Int, p3: Int, p4: Int) {
            Log.i(TAG,"receiveMsgListener->paramChange::Param has change from device name: ${p0?.deviceName}, with param data: $p1, $p2, $p3, $p4")
        }

        override fun onCirculationAndRepeatTimeChange(p0: Device?, p1: Int, p2: Int) {
            Log.i(TAG,"receiveMsgListener->circulateAndRepeatTimeChange::circulation repeat time has change from device name: ${p0?.deviceName}, with data: $p1, $p2")
        }

        override fun onDirectionChange(p0: Device?, p1: Int, p2: Int) {
            Log.i(TAG,"receiveMsgListener->directionChange::direction has change from device name: ${p0?.deviceName}, with direction data: $p1, $p2")
        }

        override fun onReadDeviceInfo(
            p0: Device?,
            p1: String,
            p2: String,
            p3: String,
            p4: String
        ) {
            Log.i(TAG,"receiveMsgListener->readDeviceInfo::device name: ${p0?.deviceName}, with device info: \n$p1, \n$p2, \n$p3, \n$p4")
            if(ParameterUtils.versionNumberOver1_7_2(p3))
                connectManager.sendCommand(Opcode.ReadPrintDirection,null)
        }

        override fun onReadTemperature(p0: Device?, p1: Int) {
            Log.i(TAG,"receiveMsgListener->readTemperature::Temperature has change from device name: ${p0?.deviceName}, with temperature: $p1")
        }

        override fun onReadBattery(p0: Device?, p1: Int) {
            Log.i(TAG,"receiveMsgListener->readBatter::device name: ${p0?.deviceName}, with battery life: $p1")
        }

        override fun onError(p0: Device?, p1: String?) {
            Log.e(TAG,"receiveMsgListener->onError::device name: ${p0?.deviceName}, has error: $p1")
        }
    }

    private fun requestPermissions(){
        val permissions =if(Build.VERSION.SDK_INT >= 31) arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )else arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        permissionLauncher.launch(permissions)
    }

    private fun isGpsEnabled() = (getSystemService(LOCATION_SERVICE) as LocationManager).isProviderEnabled(
        LocationManager.GPS_PROVIDER)

    private fun enableGPS() = intentLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

    private fun enableBluetooth(){
        val permissionsAllowed = permissions.all { it }

        if(permissionsAllowed){
            if(!connectManager.isEnable){ // bluetooth turned off
                connectManager.enable() // enable bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                intentLauncher.launch(enableBtIntent)
            }else if(connectManager.isEnable && !isGpsEnabled()){ // bt turn on, gps off
                enableGPS()
            }
        }
    }

    private fun connect(device: Device){
        if(connectManager.isDiscoveringBluetoothDevice)
            connectManager.cancelDiscoveryBluetoothDevice()
        connectManager.connect(device)
    }

    private fun initializeListeners(){
        connectManager.registerReceiveMessageListener(onReceiveMsgListener)
        connectManager.registerDeviceConnectListener(onDeviceConnectListener)
        connectManager.registerDeviceDiscoverListener(onDeviceDiscoverListener)
        connectManager.registerDeviceBluetoothStateListener(onDeviceBluetoothStateListener)
    }

    private fun destroyListeners(){
        connectManager.unregisterReceiveMessageListener(onReceiveMsgListener)
        connectManager.unregisterDeviceConnectListener(onDeviceConnectListener)
        connectManager.unregisterDeviceDiscoverListener(onDeviceDiscoverListener)
        connectManager.unregisterDeviceBluetoothStateListener(onDeviceBluetoothStateListener)
    }

    private fun sampleCase(){
        val printerHead: Byte = 0
        val distance = 0
        val d0 = (distance and 0xFF).toByte()
        val d1 = ((distance shr 8) and 0xFF).toByte()
        val d2 = ((distance shr 16) and 0xFF).toByte()
        val d3 = ((distance shr 24) and 0xFF).toByte()

        val l_pix = 600
        val p_pix = 600
        val lp0 = (l_pix and 0xFF).toByte()
        val lp1 = ((l_pix shr 8) and 0xFF).toByte()
        val pp0 = (p_pix and 0xFF).toByte()
        val pp1 = ((p_pix shr 8) and 0xFF).toByte()
        val configParams = byteArrayOf(printerHead,lp0,lp1,pp0,pp1,d0,d1,d2,d3,0,0)
        connectManager.sendCommand(Opcode.WritePrintParameters,configParams)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){
            permissions.clear() // clear all permission first to remove duplicates
            val data = it.entries.map { permission-> permission.value }
            permissions.addAll(data)
        }

        intentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            if(it.resultCode== RESULT_OK && !isGpsEnabled())
                enableGPS()
        }

        initializeListeners()

        setContent {
            val scaffoldState = rememberScaffoldState()
            BleIntegrationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        content = {padding->
                            Box(modifier = Modifier.padding(padding)) {}
                        },
                        bottomBar = {
                            Column {
                                SimpleButton(
                                    modifier = Modifier
                                        .height(64.dp)
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    buttonLabel = "Print",
                                    onClick = {
                                        sampleCase()
                                    },
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround,
                                ) {
                                    SimpleButton(
                                        buttonLabel = "Enable Bluetooth",
                                        onClick = {
                                            requestPermissions()
                                            enableBluetooth()
                                        }
                                    )
                                    SimpleButton(
                                        buttonLabel = "Scan Bluetooth",
                                        onClick = {
                                            connectManager.discoverBluetoothDevice()
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try{
            connectManager.cancelDiscoveryBluetoothDevice()
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        destroyListeners()
        super.onDestroy()
    }
}