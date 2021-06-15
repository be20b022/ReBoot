package at.uastw.medpr.reboot.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import at.uastw.medpr.reboot.BluetoothWrapper
import at.uastw.medpr.reboot.PrefWrapper
import at.uastw.medpr.reboot.R
import at.uastw.medpr.reboot.Values
import at.uastw.medpr.reboot.databinding.FragmentSettingsBinding

/**
 * Settings Fragment
 *
 * Allows the user to alter settings for both the app, as well as the connected device
 */
class SettingsFragment : Fragment(), OnConnectedListener {
    private lateinit var binding: FragmentSettingsBinding

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

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings, container, false
        )

        Values.refresh()

        when (Values.weightUnit) {
            Values.WEIGHT_KG -> binding.settingsUseKg.isChecked = true
            Values.WEIGHT_LBS -> binding.settingsUseLbs.isChecked = true
        }

        if (Values.useBodyWeightPercent) {
            binding.settingsBodyWeightPercentSwitch.isChecked = true
            showBodyWeightSetting()
        } else {
            binding.settingsBodyWeightPercentSwitch.isChecked = false
            hideBodyWeightSetting()
        }

        if (Values.usePerSensorThresholds) {
            binding.settingsThresholdPerSensorSwitch.isChecked = true
            showAdditionalThresholds()
        } else {
            binding.settingsThresholdPerSensorSwitch.isChecked = false
            hideAdditionalThresholds()
        }

        binding.settingsUseKg.setOnClickListener {
            PrefWrapper.saveString("weight_unit", "K")
            updateViews()
        }
        binding.settingsUseLbs.setOnClickListener {
            PrefWrapper.saveString("weight_unit", "L")
            updateViews()
        }

        binding.settingsBodyWeightPercentSwitch.setOnClickListener {
            if (binding.settingsBodyWeightPercentSwitch.isChecked) {
                Values.useBodyWeightPercent = true
                PrefWrapper.saveBoolean("use_body_weight_percent", true)
                updateViews()
                showBodyWeightSetting()
            } else {
                Values.useBodyWeightPercent = false
                PrefWrapper.saveBoolean("use_body_weight_percent", false)
                updateViews()
                hideBodyWeightSetting()
            }
        }

        binding.settingsBodyWeightNumber.setText(Values.bodyWeight.toString())

        binding.settingsBodyWeightNumber.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    val input = binding.settingsBodyWeightNumber.text.toString().toFloat()

                    if (input > 0) {
                        Values.setBodyWeight(input, false)
                        PrefWrapper.saveFloat("body_weight", Values.bodyWeight)
                        updateViews()
                    } else {
                        Toast.makeText(requireContext(), R.string.invalid_body_weight_string, Toast.LENGTH_SHORT).show()
                        binding.settingsBodyWeightNumber.setText("")
                    }

                    false
                }
                else -> false
            }
        }

        binding.settingsThresholdPerSensorSwitch.setOnClickListener {
            if (binding.settingsThresholdPerSensorSwitch.isChecked) {
                Values.usePerSensorThresholds = true
                PrefWrapper.saveBoolean("use_per_sensor_thresholds", true)
                showAdditionalThresholds()
            } else {
                Values.usePerSensorThresholds = false
                PrefWrapper.saveBoolean("use_per_sensor_thresholds", false)
                hideAdditionalThresholds()
            }
        }

        binding.settingsSensor1Enabled.setOnClickListener {
            binding.settingsThresholdSensor1Number.isEnabled = binding.settingsSensor1Enabled.isChecked
        }

        binding.settingsSensor2Enabled.setOnClickListener {
            binding.settingsThresholdSensor2Number.isEnabled = binding.settingsSensor2Enabled.isChecked
        }

        binding.settingsSensor3Enabled.setOnClickListener {
            binding.settingsThresholdSensor3Number.isEnabled = binding.settingsSensor3Enabled.isChecked
        }

        binding.settingsSensor4Enabled.setOnClickListener {
            binding.settingsThresholdSensor4Number.isEnabled = binding.settingsSensor4Enabled.isChecked
        }

        binding.settingsThresholdSensor1Number.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    val input = binding.settingsThresholdSensor1Number.text.toString().toFloat()

                    if (input > 0) {
                        Values.setSensorThreshold(1, input, false)
                        PrefWrapper.saveFloat("sensor_1_threshold", Values.sensor1Threshold)
                        updateViews()
                    } else {
                        Toast.makeText(requireContext(), R.string.invalid_threshold_string, Toast.LENGTH_SHORT).show()
                        binding.settingsThresholdSensor1Number.setText("")
                    }

                    false
                }
                else -> false
            }
        }

        binding.settingsThresholdSensor2Number.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    val input = binding.settingsThresholdSensor2Number.text.toString().toFloat()

                    if (input > 0) {
                        Values.setSensorThreshold(2, input, false)
                        PrefWrapper.saveFloat("sensor_2_threshold", Values.sensor2Threshold)
                        updateViews()
                    } else {
                        Toast.makeText(requireContext(), R.string.invalid_threshold_string, Toast.LENGTH_SHORT).show()
                        binding.settingsThresholdSensor2Number.setText("")
                    }

                    false
                }
                else -> false
            }
        }

        binding.settingsThresholdSensor3Number.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    val input = binding.settingsThresholdSensor3Number.text.toString().toFloat()

                    if (input > 0) {
                        Values.setSensorThreshold(3, input, false)
                        PrefWrapper.saveFloat("sensor_3_threshold", Values.sensor3Threshold)
                        updateViews()
                    } else {
                        Toast.makeText(requireContext(), R.string.invalid_threshold_string, Toast.LENGTH_SHORT).show()
                        binding.settingsThresholdSensor3Number.setText("")
                    }

                    false
                }
                else -> false
            }
        }

        binding.settingsThresholdSensor4Number.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    val input = binding.settingsThresholdSensor4Number.text.toString().toFloat()

                    if (input > 0) {
                        Values.setSensorThreshold(4, input, false)
                        PrefWrapper.saveFloat("sensor_4_threshold", Values.sensor4Threshold)
                        updateViews()
                    } else {
                        Toast.makeText(requireContext(), R.string.invalid_threshold_string, Toast.LENGTH_SHORT).show()
                        binding.settingsThresholdSensor4Number.setText("")
                    }

                    false
                }
                else -> false
            }
        }

        binding.settingsTestLedBtn.setOnClickListener {
            BluetoothWrapper.sendMessage("<LT>")
        }

        binding.settingsTestBuzzerBtn.setOnClickListener {
            BluetoothWrapper.sendMessage("<BT>")
        }

        binding.settingsTransmitBtn.setOnClickListener {
            val isLedEnabled = if (binding.settingsLedSwitch.isChecked) 1 else 0
            val isBuzzerEnabled = if (binding.settingsBuzzerSwitch.isChecked) 1 else 0

            if (Values.usePerSensorThresholds) {
                BluetoothWrapper.sendMessage(
                    "<S;$isLedEnabled;$isBuzzerEnabled;" +
                            "${if (binding.settingsSensor1Enabled.isChecked) Values.sensor1Threshold else -1.0f};" +
                            "${if (binding.settingsSensor2Enabled.isChecked) Values.sensor2Threshold else -1.0f};" +
                            "${if (binding.settingsSensor3Enabled.isChecked) Values.sensor3Threshold else -1.0f};" +
                            "${if (binding.settingsSensor4Enabled.isChecked) Values.sensor4Threshold else -1.0f}>"
                )
            } else {
                BluetoothWrapper.sendMessage(
                    "<S;$isLedEnabled;$isBuzzerEnabled;" +
                            "${Values.sensor1Threshold};" +
                            "${Values.sensor1Threshold};" +
                            "${Values.sensor1Threshold};" +
                            "${Values.sensor1Threshold}>"
                )
            }
        }

        BluetoothWrapper.addOnConnectedListener(this)
        BluetoothWrapper.connect()

        binding.settingsNotConnectedText.visibility = View.VISIBLE
        binding.settingsDeviceSettingsLayout.visibility = View.GONE

        updateViews()

        return binding.root
    }

    /**
     * Callback function for received message events
     * Reads the settings from the device and updates the views accordingly
     *
     * @param[message] Received message
     */
    private fun onMessageReceivedListener(message: String) {
        val data = BluetoothWrapper.parseMessage(message)
        if (data[0] == "S") {
            binding.settingsLedSwitch.isChecked = data[1] == "1"
            binding.settingsBuzzerSwitch.isChecked = data[2] == "1"

            Values.setSensorThreshold(1, data[3].toFloat(), true)
            binding.settingsThresholdSensor1Number.setText(Values.getSensorThreshold(1, 1))


            Values.setSensorThreshold(2, data[4].toFloat(), true)
            binding.settingsThresholdSensor2Number.setText(Values.getSensorThreshold(2, 1))

            Values.setSensorThreshold(3, data[5].toFloat(), true)
            binding.settingsThresholdSensor3Number.setText(Values.getSensorThreshold(3, 1))

            Values.setSensorThreshold(4, data[6].toFloat(), true)
            binding.settingsThresholdSensor4Number.setText(Values.getSensorThreshold(4, 1))

            if (Values.sensor1Threshold == -1.0f) {
                binding.settingsSensor1Enabled.isChecked = false
                binding.settingsThresholdSensor1Number.isEnabled = false
            } else {
                binding.settingsSensor1Enabled.isChecked = true
                binding.settingsThresholdSensor1Number.isEnabled = true
            }

            if (Values.sensor2Threshold == -1.0f) {
                binding.settingsSensor2Enabled.isChecked = false
                binding.settingsThresholdSensor2Number.isEnabled = false
            } else {
                binding.settingsSensor2Enabled.isChecked = true
                binding.settingsThresholdSensor2Number.isEnabled = true
            }

            if (Values.sensor3Threshold == -1.0f) {
                binding.settingsSensor3Enabled.isChecked = false
                binding.settingsThresholdSensor3Number.isEnabled = false
            } else {
                binding.settingsSensor3Enabled.isChecked = true
                binding.settingsThresholdSensor3Number.isEnabled = true
            }

            if (Values.sensor4Threshold == -1.0f) {
                binding.settingsSensor4Enabled.isChecked = false
                binding.settingsThresholdSensor4Number.isEnabled = false
            } else {
                binding.settingsSensor4Enabled.isChecked = true
                binding.settingsThresholdSensor4Number.isEnabled = true
            }

            updateViews()
        }
    }

    /**
     * Show all body weight related settings
     */
    private fun showBodyWeightSetting() {
        binding.settingsBodyWeightText.visibility = View.VISIBLE
        binding.settingsBodyWeightNumber.visibility = View.VISIBLE
        binding.settingsBodyWeightUnitText.visibility = View.VISIBLE
    }

    /**
     * Hide all body weight related settings
     */
    private fun hideBodyWeightSetting() {
        binding.settingsBodyWeightText.visibility = View.GONE
        binding.settingsBodyWeightNumber.visibility = View.GONE
        binding.settingsBodyWeightUnitText.visibility = View.GONE
    }

    /**
     * Shows additional sensor threshold settings
     */
    private fun showAdditionalThresholds() {
        binding.settingsThresholdSensor1Text.text = getString(R.string.settings_threshold_sensor1_string)

        // Set left margin of sensor1Text to 8dp to fix the layout
        val density = requireContext().resources.displayMetrics.density
        (binding.settingsThresholdSensor1Text.layoutParams as ConstraintLayout.LayoutParams).leftMargin =
            (8 * density).toInt()
        binding.settingsSensor1Enabled.visibility = View.VISIBLE

        binding.settingsSensor2Enabled.visibility = View.VISIBLE
        binding.settingsThresholdSensor2Text.visibility = View.VISIBLE
        binding.settingsThresholdSensor2Number.visibility = View.VISIBLE
        binding.settingsSensor2UnitText.visibility = View.VISIBLE

        binding.settingsSensor3Enabled.visibility = View.VISIBLE
        binding.settingsThresholdSensor3Text.visibility = View.VISIBLE
        binding.settingsThresholdSensor3Number.visibility = View.VISIBLE
        binding.settingsSensor3UnitText.visibility = View.VISIBLE

        binding.settingsSensor4Enabled.visibility = View.VISIBLE
        binding.settingsThresholdSensor4Text.visibility = View.VISIBLE
        binding.settingsThresholdSensor4Number.visibility = View.VISIBLE
        binding.settingsSensor4UnitText.visibility = View.VISIBLE
    }

    /**
     * Shows only a single threshold setting
     */
    private fun hideAdditionalThresholds() {
        binding.settingsThresholdSensor1Text.text = getString(R.string.settings_threshold_string)

        // Reset the left margin of sensor1Text
        (binding.settingsThresholdSensor1Text.layoutParams as ConstraintLayout.LayoutParams).leftMargin = 0
        binding.settingsSensor1Enabled.visibility = View.GONE

        binding.settingsSensor2Enabled.visibility = View.GONE
        binding.settingsThresholdSensor2Text.visibility = View.GONE
        binding.settingsThresholdSensor2Number.visibility = View.GONE
        binding.settingsSensor2UnitText.visibility = View.GONE

        binding.settingsSensor3Enabled.visibility = View.GONE
        binding.settingsThresholdSensor3Text.visibility = View.GONE
        binding.settingsThresholdSensor3Number.visibility = View.GONE
        binding.settingsSensor3UnitText.visibility = View.GONE

        binding.settingsSensor4Enabled.visibility = View.GONE
        binding.settingsThresholdSensor4Text.visibility = View.GONE
        binding.settingsThresholdSensor4Number.visibility = View.GONE
        binding.settingsSensor4UnitText.visibility = View.GONE
    }

    /**
     * Updates all fields and units
     */
    private fun updateViews() {
        Values.refresh()
        binding.settingsBodyWeightNumber.setText(Values.getBodyWeight(1))
        binding.settingsThresholdSensor1Number.setText(Values.getSensorThreshold(1, 1))
        binding.settingsThresholdSensor2Number.setText(Values.getSensorThreshold(2, 1))
        binding.settingsThresholdSensor3Number.setText(Values.getSensorThreshold(3, 1))
        binding.settingsThresholdSensor4Number.setText(Values.getSensorThreshold(4, 1))

        when (Values.weightUnit) {
            Values.WEIGHT_KG -> {
                binding.settingsBodyWeightUnitText.text = getString(R.string.kg)
                binding.settingsSensor1UnitText.text = getString(R.string.kg)
                binding.settingsSensor2UnitText.text = getString(R.string.kg)
                binding.settingsSensor3UnitText.text = getString(R.string.kg)
                binding.settingsSensor4UnitText.text = getString(R.string.kg)
            }

            Values.WEIGHT_LBS -> {
                binding.settingsBodyWeightUnitText.text = getString(R.string.lbs)
                binding.settingsSensor1UnitText.text = getString(R.string.lbs)
                binding.settingsSensor2UnitText.text = getString(R.string.lbs)
                binding.settingsSensor3UnitText.text = getString(R.string.lbs)
                binding.settingsSensor4UnitText.text = getString(R.string.lbs)
            }
        }

        if (Values.useBodyWeightPercent) {
            binding.settingsSensor1UnitText.text = getString(R.string.percent)
            binding.settingsSensor2UnitText.text = getString(R.string.percent)
            binding.settingsSensor3UnitText.text = getString(R.string.percent)
            binding.settingsSensor4UnitText.text = getString(R.string.percent)
        }
    }

    /**
     *  Implementation of the OnConnectedListener interface
     *
     *  Callback function for successful connection attempts
     *  Shows device-specific settings and sends the signal to switch the device mode to SETTINGS
     */
    override fun onConnected() {
        BluetoothWrapper.addRxListener(this::onMessageReceivedListener)
        BluetoothWrapper.sendMessage("<s>")
        binding.settingsNotConnectedText.visibility = View.GONE
        binding.settingsDeviceSettingsLayout.visibility = View.VISIBLE
    }
}