package at.uastw.medpr.reboot

import androidx.core.content.edit

/**
 *  Pref Wrapper
 *
 *  Static wrapper class to provide easy access and abstraction to the application preferences
 */
class PrefWrapper() {
    companion object {
        /**
         *  Saves a String to the application preferences
         *  @param[key] Key to modify
         *  @param[value] Value to save
         */
        fun saveString(key: String, value: String) {
            MainActivity.preferences.edit() {
                putString(key, value)
                commit()
            }
        }

        /**
         *  Saves an Int to the application preferences
         *  @param[key] Key to modify
         *  @param[value] Value to save
         */
        fun saveInt(key: String, value: Int) {
            MainActivity.preferences.edit() {
                putInt(key, value)
                commit()
            }
        }

        /**
         *  Saves a Boolean to the application preferences
         *  @param[key] Key to modify
         *  @param[value] Value to save
         */
        fun saveBoolean(key: String, value: Boolean) {
            MainActivity.preferences.edit() {
                putBoolean(key, value)
                commit()
            }
        }

        /**
         *  Saves a Float to the application preferences
         *  @param[key] Key to modify
         *  @param[value] Value to save
         */
        fun saveFloat(key: String, value: Float) {
            MainActivity.preferences.edit() {
                putFloat(key, value)
                commit()
            }
        }

        /**
         *  Saves a Long to the application preferences
         *  @param[key] Key to modify
         *  @param[value] Value to save
         */
        fun saveLong(key: String, value: Long) {
            MainActivity.preferences.edit() {
                putLong(key, value)
                commit()
            }
        }

        /**
         *  Saves a StringSet to the application preferences
         *  @param[key] Key to modify
         *  @param[value] Value to save
         */
        fun saveStringSet(key: String, value: Set<String>) {
            MainActivity.preferences.edit() {
                putStringSet(key, value)
                commit()
            }
        }

        /**
         *  Loads a String from the application preferences
         *  @param[key] Key to load
         *  @return Loaded String, default: empty String ("")
         */
        fun loadString(key: String): String? {
            return MainActivity.preferences.getString(key, "")
        }

        /**
         *  Loads an Int from the application preferences
         *  @param[key] Key to load
         *  @return Loaded Int, default: -999
         */
        fun loadInt(key: String): Int {
            return MainActivity.preferences.getInt(key, -999)
        }

        /**
         *  Loads a Boolean from the application preferences
         *  @param[key] Key to load
         *  @return Loaded Boolean, default: false
         */
        fun loadBoolean(key: String): Boolean {
            return MainActivity.preferences.getBoolean(key, false)
        }

        /**
         *  Loads a Float from the application preferences
         *  @param[key] Key to load
         *  @return Loaded Float, default: -999f
         */
        fun loadFloat(key: String): Float {
            return MainActivity.preferences.getFloat(key, -999f)
        }

        /**
         *  Loads a Long from the application preferences
         *  @param[key] Key to load
         *  @return Loaded Long, default: -999L
         */
        fun loadLong(key: String): Long {
            return MainActivity.preferences.getLong(key, -999L)
        }

        /**
         *  Loads a StringSet from the application preferences
         *  @param[key] Key to load
         *  @return Loaded StringSet, default: empty Set<String>
         */
        fun loadStringSet(key: String): Set<String>? {
            return MainActivity.preferences.getStringSet(key, emptySet())
        }
    }
}