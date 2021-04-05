package at.uastw.medpr.reboot

/**
 *  Toolbox
 *  Static class containing miscellaneous helper functions
 */
class Toolbox {
    companion object {

        /**
         *  Converts a measurement passed in as a String to the users chosen weight unit or percent and returns it
         *  as a String
         *
         *  @param[value] Value to be converted
         *  @param[precision] Number of decimal places in the result
         *  @return Formatted String of the value
         */
        fun convertToUserSetting(value: String, precision: Int): String {
            val data = value.toFloat()

            val result = if (data == -1.0f) {
                -1.0f
            } else {
                if (Values.useBodyWeightPercent) {
                    (data / Values.bodyWeight) * 100
                } else {
                    when (Values.weightUnit) {
                        Values.WEIGHT_LBS -> data * 2.205f
                        else -> data
                    }
                }
            }

            return String.format("%.${precision}f", result)
        }
    }
}