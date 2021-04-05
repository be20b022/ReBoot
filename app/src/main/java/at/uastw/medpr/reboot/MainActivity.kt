package at.uastw.medpr.reboot

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import at.uastw.medpr.reboot.databinding.ActivityMainBinding

/**
 *  Main Activity
 *
 *  Single activity that holds all fragments and acts as a NavHost
 */
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    companion object {
        lateinit var preferences: SharedPreferences
    }

    /**
     *  Initializes the view as well as some vital components
     *  @param[savedInstanceState] If non-null can be used to reconstruct fragment from previous saved state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout

        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.myNavHostFragment)

        val navGraph = navController.navInflater.inflate(R.navigation.navigation)

        BluetoothWrapper.init()
        Values.refresh()

        if (BluetoothWrapper.deviceAddress == null) {
            navGraph.startDestination = R.id.selectDeviceFragment
        } else {
            navGraph.startDestination = R.id.liveViewFragment
        }

        navController.graph = navGraph

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    /**
     *  Adds support for Android's NavigateUp button
     */
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}