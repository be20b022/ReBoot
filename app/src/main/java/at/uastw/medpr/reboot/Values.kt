package at.uastw.medpr.reboot

import android.util.Log

/**
 *  Data model class that holds the current state of the application settings
 */
class Values {
    companion object {
        const val WEIGHT_KG = "K"
        const val WEIGHT_LBS = "L"
        var weightUnit = ""

        var useBodyWeightPercent = false

        var bodyWeight = -1.0f

        var usePerSensorThresholds = false

        var sensor1Threshold = -1.0f
        var sensor2Threshold = -1.0f
        var sensor3Threshold = -1.0f
        var sensor4Threshold = -1.0f

        /**
         *  Updates the model
         */
        fun refresh() {
            weightUnit = PrefWrapper.loadString("weight_unit")!!

            useBodyWeightPercent = PrefWrapper.loadBoolean("use_body_weight_percent")
            bodyWeight = PrefWrapper.loadFloat("body_weight")

            usePerSensorThresholds = PrefWrapper.loadBoolean("use_per_sensor_thresholds")
        }

        /**
         *  Setter for the user's body weight
         *  @param[value] body weight
         *  @param[fromDevice] value was generated internally (= safe to assume kg)
         */
        fun setBodyWeight(value: Float, fromDevice: Boolean) {
            bodyWeight = if (fromDevice) {
                value
            } else {
                when (weightUnit) {
                    WEIGHT_LBS -> value / 2.205f
                    else -> value
                }
            }
        }

        /**
         *  Getter for the user's body weight
         *  @param[precision] Number of decimal places in the result
         *  @return Formatted String of the user's body weight
         */
        fun getBodyWeight(precision: Int): String {
            val result = when (weightUnit) {
                WEIGHT_LBS -> bodyWeight * 2.205f
                else -> bodyWeight
            }

            Log.i("Bodyweight (GET)", result.toString())
            return String.format("%.${precision}f", result)
        }

        /**
         *  Setter for the sensor thresholds of the device
         *  @param[sensor] ID of the sensor (1 = Heel, 2 = Ball lat., 3 = Ball med., 4 = Toe)
         *  @param[value] Threshold to set
         *  @param[fromDevice] value was generated internally (= safe to assume kg)
         */
        fun setSensorThreshold(sensor: Int, value: Float, fromDevice: Boolean) {
            val data = if (fromDevice) {
                value
            } else {
                when (weightUnit) {
                    WEIGHT_LBS -> value / 2.205f
                    else -> value
                }
            }

            when (sensor) {
                1 -> sensor1Threshold = data
                2 -> sensor2Threshold = data
                3 -> sensor3Threshold = data
                4 -> sensor4Threshold = data
            }
        }

        /**
         *  Getter for the sensor thresholds of the device
         *  @param[sensor] ID of the sensor (1 = Heel, 2 = Ball lat., 3 = Ball med., 4 = Toe)
         *  @param[precision] Number of decimal places in the result
         *  @return Formatted String of the selected sensor's threshold
         */
        fun getSensorThreshold(sensor: Int, precision: Int): String {
            val data = when (sensor) {
                1 -> sensor1Threshold
                2 -> sensor2Threshold
                3 -> sensor3Threshold
                else -> sensor4Threshold
            }

            val result = if (data == -1.0f) {
                -1.0f
            } else {
                if (useBodyWeightPercent) {
                    (data / bodyWeight) * 100
                } else {
                    when (weightUnit) {
                        WEIGHT_LBS -> data * 2.205f
                        else -> data
                    }
                }
            }

            return String.format("%.${precision}f", result)
        }
    }
}