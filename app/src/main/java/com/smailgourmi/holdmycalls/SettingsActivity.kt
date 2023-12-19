package com.smailgourmi.holdmycalls

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

/*
class SettingsActivity : AppCompatActivity(), 
    Preference.OnPreferenceChangeListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    class SettingsFragment() : PreferenceFragmentCompat() {
        private var masterPreference: Preference? = null
        private var slavePreference: Preference? = null
        private  lateinit var sharedPreferences: SharedPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

            masterPreference = findPreference("master")
            slavePreference  = findPreference("slave")

            updateScreen()

            slavePreference?.setOnPreferenceChangeListener { _, newValue:Any? ->
                setUserRole(UserRole.SLAVE,newValue as Boolean)
                updateScreen()
                true // Return true if the event is handled.
            }
            masterPreference?.setOnPreferenceChangeListener { _, newValue ->
                setUserRole(UserRole.MASTER,newValue as Boolean)
                updateScreen()
                true // Return true if the event is handled.
            }
        }

        private fun updateScreen() {
            val prefUserRole = sharedPreferences.getString(getString(R.string.user_role),null)
            if (prefUserRole == UserRole.MASTER.name) {
                masterPreference?.setDefaultValue(true)
                masterPreference?.isEnabled = true
                slavePreference?.setDefaultValue(false)
                slavePreference?.isEnabled = false
            }
            else if (prefUserRole == UserRole.SLAVE.name) {
                slavePreference?.setDefaultValue(true)
                slavePreference?.isEnabled = true
                masterPreference?.setDefaultValue(false)
                masterPreference?.isEnabled = false
            }else{
                masterPreference?.setDefaultValue(false)
                slavePreference?.setDefaultValue(false)
                slavePreference?.isEnabled = true
                masterPreference?.isEnabled = true
            }

        }

        private fun setUserRole(userRole:UserRole, isNotNull:Boolean) {
            // Save the user's chosen role in SharedPreferences or your preferred storage mechanism
            // For simplicity, this example uses a placeholder function
            // You might want to store this information securely and persistently
            // (e.g., in SharedPreferences, a local database, or on your server)
            // For demonstration, we'll use SharedPreferences in this example
            val editor = sharedPreferences.edit()
            if (isNotNull) {
                editor.putString("userRole", userRole.name)
            }
            else {
                editor.remove("userRole")
            }
            editor.apply()
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {

        if (preference.key == "master") {

        } else if (preference.key == "slave") {

        }
        return true
    }

}

 */