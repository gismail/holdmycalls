package com.smailgourmi.holdmycalls

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
/*
enum class UserRole {
    MASTER,
    SLAVE
}

class MainAppActivity : AppCompatActivity() {
    // Initialise the DrawerLayout, NavigationView and ToggleBar

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView :NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> goHome()
                R.id.logout -> logOut()
                R.id.menu_settings-> setSettings()

            }
            true
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val prefUserRole = sharedPreferences.getString("userRole",null)
        when (prefUserRole) {
            UserRole.MASTER.name -> navigateToMasterScreen()
            UserRole.SLAVE.name -> navigateToSlaveScreen()
        }

        val masterButton: Button = findViewById(R.id.masterButton)
        val slaveButton: Button = findViewById(R.id.slaveButton)

        masterButton.setOnClickListener {
            setUserRole(UserRole.MASTER)
            navigateToMasterScreen()
        }

        slaveButton.setOnClickListener {
            setUserRole(UserRole.SLAVE)
            navigateToSlaveScreen()
        }



    }

    private fun goHome() {
        val intent = Intent(this, MainAppActivity::class.java)
        startActivity(intent)
    }

    private fun setSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun setUserRole(userRole: UserRole) {
        // Save the user's chosen role in SharedPreferences or your preferred storage mechanism
        // For simplicity, this example uses a placeholder function
        // You might want to store this information securely and persistently
        // (e.g., in SharedPreferences, a local database, or on your server)
        // For demonstration, we'll use SharedPreferences in this example
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        editor.putString("userRole", userRole.name)
        editor.apply()
    }

    private fun navigateToMasterScreen() {
        // Implement the logic to navigate to the Master Screen
        // For example:
        val intent = Intent(this, MasterScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSlaveScreen() {
        // Implement the logic to navigate to the Slave Screen
        // For example:
        val intent = Intent(this, SlaveScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun  logOut(): Boolean {
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser!= null){
            auth.signOut()
        }
        // Redirect to the login or main screen after logout
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() //
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}
*/