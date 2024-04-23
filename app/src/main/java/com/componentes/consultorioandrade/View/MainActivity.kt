package com.componentes.consultorioandrade.View

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.LoginViewModel
import com.componentes.consultorioandrade.databinding.ActivityMainBinding
import com.componentes.consultorioandrade.databinding.ActivityRegisterBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mtToolbar)
        supportActionBar?.title = ""

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.mtToolbar, R.string.open_drawer,
            R.string.close_drawer
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.nvMenuLateral.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
            true
        }

    }

    override fun onBackPressed() {
        binding.drawerLayout.run {
            if (isDrawerOpen(GravityCompat.START)) {
                closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun onNavigationItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.info_Per -> {
                Toast.makeText(this, "Se presionó la opción de información personal", Toast.LENGTH_SHORT).show()
                val fragment = Information_Personal()

                // Reemplazar el Fragment actual en el contenedor (normalmente un FrameLayout)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

                // Cerrar el DrawerLayout después de seleccionar una opción del menú
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.Schedule -> {
                Toast.makeText(this, "Se presionó la opción de agendar", Toast.LENGTH_SHORT).show()
                val fragment = schedule_appointment()

                // Reemplazar el Fragment actual en el contenedor (normalmente un FrameLayout)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

                // Cerrar el DrawerLayout después de seleccionar una opción del menú
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.Sign_off -> {
                Toast.makeText(this, "HA CERRADO SU SESION", Toast.LENGTH_SHORT).show()
                LoginViewModel.saveLoginStatus(this, false)

                // Navegar de regreso a la pantalla de inicio de sesión
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }



}





