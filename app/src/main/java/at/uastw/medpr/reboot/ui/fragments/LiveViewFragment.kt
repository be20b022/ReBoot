package at.uastw.medpr.reboot.ui.fragments

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import at.uastw.medpr.reboot.BluetoothWrapper
import at.uastw.medpr.reboot.R
import at.uastw.medpr.reboot.Toolbox
import at.uastw.medpr.reboot.Values
import at.uastw.medpr.reboot.databinding.FragmentLiveViewBinding

/**
 *  Live View Fragment
 *
 *  Displays a live view of the sensor's values
 */

class LiveViewFragment : Fragment(), OnConnectedListener {
    private lateinit var binding: FragmentLiveViewBinding

    private val staticSole = R.drawable.static_sole
    private var isConnected = false

    private var sensor1Max = 0.0f
    private var sensor2Max = 0.0f
    private var sensor3Max = 0.0f
    private var sensor4Max = 0.0f
    private var sensorCombined = 0.0f

    /**
     *  Initializes the view
     *
     *  @param[inflater] Used to inflate views in the fragment
     *  @param[container] If non-null holds the parent view that this fragment attaches to
     *  @param[savedInstanceState] If non-null can be used to reconstruct fragment from previous saved state
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_view, container, false)

        val soleView = binding.soleView
        val soleDrawable = soleView.drawable as Animatable

        if (!isConnected) soleDrawable.start()

        // Sole animation duration is 2500 ms. Switch ImageView source to static image for performance reasons
        // afterwards.
        Handler(Looper.getMainLooper()).postDelayed({
            soleView.setImageResource(staticSole)
        }, 2550)

        binding.liveViewSensor1Text.visibility = View.GONE
        binding.liveViewSensor2Text.visibility = View.GONE
        binding.liveViewSensor3Text.visibility = View.GONE
        binding.liveViewSensor4Text.visibility = View.GONE
        binding.liveViewCombinedLoadLabel.visibility = View.GONE
        binding.liveViewCombinedLoadText.visibility = View.GONE
        binding.liveViewSensor1MaxLabel.visibility = View.GONE
        binding.liveViewSensor1MaxText.visibility = View.GONE
        binding.liveViewSensor2MaxLabel.visibility = View.GONE
        binding.liveViewSensor2MaxText.visibility = View.GONE
        binding.liveViewSensor3MaxLabel.visibility = View.GONE
        binding.liveViewSensor3MaxText.visibility = View.GONE
        binding.liveViewSensor4MaxLabel.visibility = View.GONE
        binding.liveViewSensor4MaxText.visibility = View.GONE

        binding.liveViewDeviceText.text = getString(R.string.status_connecting)
        BluetoothWrapper.addOnConnectedListener(this)
        BluetoothWrapper.connect()

        return binding.root
    }

    /**
     *  Implementation of the OnConnectedListener interface
     *
     *  Callback function for successful connection attempts
     *  Adds further listeners and sends the signal to switch the device mode to LIVE_VIEW
     */
    override fun onConnected() {
        BluetoothWrapper.addRxListener(this::onMessageReceived)
        BluetoothWrapper.addErrListener(this::onReceiveError)
        BluetoothWrapper.addTxListener(this::onMessageSent)
        BluetoothWrapper.sendMessage("<l>")

        isConnected = true
        val connectedString = getString(R.string.status_connected) + " (" + BluetoothWrapper.deviceName + ")"
        binding.liveViewDeviceText.text = connectedString
    }

    /**
     *  Callback function for sent message events
     *  Logs sent message to console
     */
    private fun onMessageSent(message: String) {
        Log.i("[BT/Tx]", message)
    }

    /**
     * Callback function for received message events
     * Fills the data model with values and puppeteers the views accordingly
     *
     * @param[message] Received message
     */
    private fun onMessageReceived(message: String) {
        Log.i("[BT/Rx]", message)

        val data = BluetoothWrapper.parseMessage(message)
        var unit = ""

        // Check if received message contains a measurement
        if (data[0] == "M") {
            unit = if (Values.useBodyWeightPercent) {
                getString(R.string.percent)
            } else {
                when (Values.weightUnit) {
                    Values.WEIGHT_LBS -> getString(R.string.lbs)
                    else -> getString(R.string.kg)
                }
            }

            // Sensor 1
            if (data[2].toFloat() > -1.0f) {
                if (data[2].toFloat() > sensor1Max) sensor1Max = data[2].toFloat()
                sensorCombined += data[2].toFloat()

                val sensor1Text = Toolbox.convertToUserSetting(data[2], 1) + " $unit"
                binding.liveViewSensor1Text.text = sensor1Text
                val max1Text = Toolbox.convertToUserSetting(sensor1Max.toString(), 1) + " $unit"
                binding.liveViewSensor1MaxText.text = max1Text
            } else {
                binding.liveViewSensor1Text.text = getString(R.string.sensor_disabled_string)
                binding.liveViewSensor1MaxText.text = getString(R.string.sensor_disabled_string)
            }
            binding.liveViewSensor1Text.visibility = View.VISIBLE

            // Sensor 2
            if (data[3].toFloat() > -1.0f) {
                if (data[3].toFloat() > sensor2Max) sensor2Max = data[3].toFloat()
                sensorCombined += data[3].toFloat()

                val sensor2Text = Toolbox.convertToUserSetting(data[3], 1) + " $unit"
                binding.liveViewSensor2Text.text = sensor2Text
                val max2Text = Toolbox.convertToUserSetting(sensor2Max.toString(), 1) + " $unit"
                binding.liveViewSensor2MaxText.text = max2Text
            } else {
                binding.liveViewSensor2Text.text = getString(R.string.sensor_disabled_string)
                binding.liveViewSensor2MaxText.text = getString(R.string.sensor_disabled_string)
            }
            binding.liveViewSensor2Text.visibility = View.VISIBLE

            // Sensor 3
            if (data[4].toFloat() > -1.0f) {
                if (data[4].toFloat() > sensor3Max) sensor3Max = data[4].toFloat()
                sensorCombined += data[4].toFloat()

                val sensor3Text = Toolbox.convertToUserSetting(data[4], 1) + " $unit"
                binding.liveViewSensor3Text.text = sensor3Text

                val max3Text = Toolbox.convertToUserSetting(sensor3Max.toString(), 1) + " $unit"
                binding.liveViewSensor3MaxText.text = max3Text
            } else {
                binding.liveViewSensor3Text.text = getString(R.string.sensor_disabled_string)
                binding.liveViewSensor3MaxText.text = getString(R.string.sensor_disabled_string)
            }
            binding.liveViewSensor3Text.visibility = View.VISIBLE

            // Sensor 4
            if (data[5].toFloat() > -1.0f) {
                if (data[5].toFloat() > sensor4Max) sensor4Max = data[5].toFloat()
                sensorCombined += data[5].toFloat()

                val sensor4Text = Toolbox.convertToUserSetting(data[5], 1) + " $unit"
                binding.liveViewSensor4Text.text = sensor4Text

                val max4Text = Toolbox.convertToUserSetting(sensor4Max.toString(), 1) + " $unit"
                binding.liveViewSensor4MaxText.text = max4Text
            } else {
                binding.liveViewSensor4Text.text = getString(R.string.sensor_disabled_string)
                binding.liveViewSensor4MaxText.text = getString(R.string.sensor_disabled_string)
            }
            binding.liveViewSensor4Text.visibility = View.VISIBLE
        }
        val combinedText = Toolbox.convertToUserSetting(sensorCombined.toString(), 1) + " $unit"

        binding.liveViewCombinedLoadText.text = combinedText
        binding.liveViewCombinedLoadLabel.visibility = View.VISIBLE
        binding.liveViewCombinedLoadText.visibility = View.VISIBLE

        binding.liveViewSensor1MaxLabel.visibility = View.VISIBLE
        binding.liveViewSensor1MaxText.visibility = View.VISIBLE

        binding.liveViewSensor2MaxLabel.visibility = View.VISIBLE
        binding.liveViewSensor2MaxText.visibility = View.VISIBLE

        binding.liveViewSensor3MaxLabel.visibility = View.VISIBLE
        binding.liveViewSensor3MaxText.visibility = View.VISIBLE

        binding.liveViewSensor4MaxLabel.visibility = View.VISIBLE
        binding.liveViewSensor4MaxText.visibility = View.VISIBLE

        // Reset sensorCombined for next cycle
        sensorCombined = 0.0f
    }

    /**
     * Logs any RxErrors to console and displays a toast to inform the user
     *
     * @param[error] The error that resulted in the function being called
     */
    private fun onReceiveError(error: Throwable) {
        Log.e("[BT/RxError]", "" + error.message)
        Toast.makeText(this.context, "ERROR: " + error.message, Toast.LENGTH_LONG).show()

    }
}