package com.app.bot

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.app.bot.config.ProUserManager
import com.app.bot.ui.dashboard.DashboardFragment
import com.app.bot.ui.navigation.UpdatesFragment
import com.app.bot.ui.settings.SettingsFragment
import com.google.android.material.navigation.NavigationView

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var proUserManager: ProUserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        
        // Inicializar manager de usuario PRO
        proUserManager = ProUserManager(this)
        val isProUser = proUserManager.isProUser()
        
        Log.d("AdminDashboard", "🔍 Usuario PRO: $isProUser")
        
        // Mostrar/ocultar menú de administrador según sea PRO
        val adminMenuGroup = navigationView.menu.findItem(R.id.admin_menu_group)
        if (adminMenuGroup != null) {
            adminMenuGroup.isVisible = isProUser
        }
        
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                // Menú básico
                R.id.nav_dashboard -> showFragment(DashboardFragment())
                R.id.nav_updates -> showFragment(UpdatesFragment())
                R.id.nav_settings -> showFragment(SettingsFragment())
                
                // Menú PRO/Admin (si el usuario tiene permisos)
                R.id.nav_admin_dashboard -> {
                    if (isProUser) {
                        Log.d("AdminDashboard", "📊 Abriendo Admin Dashboard")
                        // TODO: Implementar AdminDashboardFragment
                    }
                }
                R.id.nav_global_settings -> {
                    if (isProUser) {
                        Log.d("AdminDashboard", "⚙️ Abriendo Global Settings")
                        // TODO: Implementar GlobalSettingsFragment
                    }
                }
                R.id.nav_client_management -> {
                    if (isProUser) {
                        Log.d("AdminDashboard", "👥 Abriendo Client Management")
                        // TODO: Implementar ClientManagementFragment
                    }
                }
                R.id.nav_license_manager -> {
                    if (isProUser) {
                        Log.d("AdminDashboard", "🔑 Abriendo License Manager")
                        // TODO: Implementar LicenseManagerFragment
                    }
                }
                R.id.nav_bot_command -> {
                    if (isProUser) {
                        Log.d("AdminDashboard", "📡 Abriendo Bot Command Center")
                        // TODO: Implementar BotCommandCenterFragment
                    }
                }
                R.id.nav_analytics -> {
                    if (isProUser) {
                        Log.d("AdminDashboard", "📊 Abriendo Analytics & Logs")
                        // TODO: Implementar AnalyticsFragment
                    }
                }
                R.id.nav_security -> {
                    if (isProUser) {
                        Log.d("AdminDashboard", "🔐 Abriendo Security Settings")
                        // TODO: Implementar SecuritySettingsFragment
                    }
                }
                R.id.nav_backup -> {
                    if (isProUser) {
                        Log.d("AdminDashboard", "💾 Abriendo Backup & Restore")
                        // TODO: Implementar BackupRestoreFragment
                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_dashboard)
            showFragment(DashboardFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
