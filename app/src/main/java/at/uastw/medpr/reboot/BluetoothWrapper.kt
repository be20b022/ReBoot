package at.uastw.medpr.reboot

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import at.uastw.medpr.reboot.ui.fragments.OnConnectedListener
import com.harrysoft.androidbluetoothserial.BluetoothManager
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *  Bluetooth Wrapper
 *
 *  Static wrapper class that provides easy access and an additional layer of abstraction for using
 *  Harry Phillip's BT serial library (https://github.com/harry1453/android-bluetooth-serial)
 */
class BluetoothWrapper {
    companion object {
        private val bluetoothManager: BluetoothManager = BluetoothManager.getInstance()
        private var deviceInterface: SimpleBluetoothDeviceInterface? = null
        private val deviceList: ArrayList<Array<String>> = ArrayList()
        private val onConnectedListeners: ArrayList<OnConnectedListener?> = ArrayList()
        var isConnected = false

        var deviceAddress: String? = null
        get() {
            return if (field == null) {
                Log.i("[BT/Address]", "null")
                null
            } else {
                Log.i("[BT/Address]", field!!)
                field
            }
        }

        var deviceName: String? = null

        /**
         *  Tries to load previously saved device data
         */
        fun init() {
            deviceName = PrefWrapper.loadString("device_name")
            deviceAddress = PrefWrapper.loadString("device_mac_address")

            if (deviceName.isNullOrEmpty()) deviceName = null
            if (deviceAddress.isNullOrEmpty()) deviceAddress = null
        }

        /**
         *  Adds compatible devices to a list in tuples of name and address
         *  @param[context] Application context
         *  @return List of compatible devices
         */
        fun getCompatibleDevices(context: Context): List<Array<String>> {
            deviceList.clear()

            for (device: BluetoothDevice in bluetoothManager.pairedDevicesList) {
                if (device.name.toString().startsWith(context.getString(R.string.product_name))) {
                    deviceList.add(arrayOf(device.name, device.address))
                }
            }

            return deviceList
        }

        /**
         *  Fills the data model with the selected device's name and address for further use
         */
        fun selectDevice(position: Int) {
            Log.i("DEBUG", "selectDevice")
            deviceAddress = deviceList[position][1]
            deviceName = deviceList[position][0]

            Log.i("DEBUG", "$deviceName ($deviceAddress)")
        }

        /**
         *  Stores the selected device's name and address in the application preferences
         */
        fun saveDevice() {
            PrefWrapper.saveString("device_mac_address", deviceAddress!!)
            PrefWrapper.saveString("device_name", deviceName!!)
        }

        /**
         *  Tries to establish a connection with the selected device
         */
        @SuppressLint("CheckResult")
        fun connect() {
            Log.i("[BT/Connect]", "Connecting to " +
                        deviceName + " (" +
                        deviceAddress + ")")

            bluetoothManager.openSerialDevice(deviceAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onConnectError)
        }

        /**
         *  Adds a Rx listener to the list of Rx listeners
         *  @param[listener] Rx listener to add
         */
        fun addRxListener(listener: SimpleBluetoothDeviceInterface.OnMessageReceivedListener) {
            deviceInterface?.setMessageReceivedListener(listener) ?: return
        }

        /**
         *  Adds a Tx listener to the list of Tx listeners
         *  @param[listener] Tx listener to add
         */
        fun addTxListener(listener: SimpleBluetoothDeviceInterface.OnMessageSentListener) {
            deviceInterface?.setMessageSentListener(listener) ?: return
        }

        /**
         *  Adds an error listener to the list of error listeners
         *  @param[listener] Error listener to add
         */
        fun addErrListener(listener: SimpleBluetoothDeviceInterface.OnErrorListener) {
            deviceInterface?.setErrorListener(listener) ?: return
        }

        /**
         *  Comfort function that adds all 3 types of listeners at once
         *  @param[messageReceivedListener] Rx listener to add
         *  @param[messageSentListener] Tx listener to add
         *  @param[errorListener] Error listener to add
         */
        fun addListeners(messageReceivedListener: SimpleBluetoothDeviceInterface.OnMessageReceivedListener,
            messageSentListener: SimpleBluetoothDeviceInterface.OnMessageSentListener,
            errorListener: SimpleBluetoothDeviceInterface.OnErrorListener) {
            deviceInterface?.setListeners(messageReceivedListener, messageSentListener, errorListener)
        }

        /**
         *  Sends a message to the device
         *  @param[message] Message
         */
        fun sendMessage(message: String) {
            deviceInterface?.sendMessage(message) ?: return
        }

        /**
         *  Adds an OnConnectedListener to the list of OnConnectedListeners
         *  @param[listener] OnConnectedListener to add
         */
        fun addOnConnectedListener(listener: OnConnectedListener) {
            onConnectedListeners.add(listener)
        }

        /**
         *  Sets the state of the Wrapper to isConnected and propagates the change to the listeners
         */
        private fun onConnected(connectedDevice: BluetoothSerialDevice) {
            Log.i("[BT/Connect]", "Connection established.")
            deviceInterface = connectedDevice.toSimpleDeviceInterface()
            isConnected = true
            triggerOnConnectedListeners()
        }

        /**
         *  Logs connection errors
         */
        private fun onConnectError(error: Throwable) {
            Log.e("[BT/SelectDev/ConError]", "" + error.message)
        }

        /**
         *  Triggers all available OnConnectListeners and removes them from the list
         */
        private fun triggerOnConnectedListeners() {
            for (listener: OnConnectedListener? in onConnectedListeners) {
                listener?.onConnected() ?: continue
                onConnectedListeners.remove(listener)
            }
        }

        /**
         *  Bluetooth messages are encapsulated between '<'and '>' to ensure that the full message has been received
         *  Messages loosely follow the CSV format, fields are separated with ';'
         *  @param[message] Message to parse
         *  @return List containing individual fields, emptyList if message is not complete
         */
        fun parseMessage(message: String): List<String> {
            Log.i("[BT/Message]", message)
            if (message.contains("<") && message.contains(">")) {
                val msg = message.substring(message.indexOf('<') + 1, message.indexOf('>'))
                return msg.split(";")
            }
            return emptyList()
        }
    }
}