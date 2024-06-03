package com.componentes.consultorioandrade.View

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.LoginViewModel
import com.componentes.consultorioandrade.ViewModel.PacienteViewModel
import com.componentes.consultorioandrade.databinding.ActivityMainBinding
import com.componentes.consultorioandrade.databinding.ActivityRegisterBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var viewModel: PacienteViewModel
    private lateinit var binding: ActivityMainBinding
    private var rol=""
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        rol= intent.getStringExtra("rol").toString()
        viewModel = ViewModelProvider(this).get(PacienteViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

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

        val menu = binding.nvMenuLateral.menu

        showMenuOptions(menu)

    }

    private fun showMenuOptions(menu:Menu){

        viewModel.getPersonalInformation { p, error->
            if (p != null) {
                when(rol.toBoolean()){
                    true-> {
                        menu.findItem(R.id.info_Per).isVisible = true
                        menu.findItem(R.id.Schedule).isVisible =true
                        menu.findItem(R.id.Appointment).isVisible = true
                        menu.findItem(R.id.AppointmentUsers).isVisible = false
                    }
                    false-> {
                        menu.findItem(R.id.info_Per).isVisible = true
                        menu.findItem(R.id.Schedule).isVisible =true
                        menu.findItem(R.id.Appointment).isVisible = false
                        menu.findItem(R.id.AppointmentUsers).isVisible = true
                    }
                }
            }else{
                menu.findItem(R.id.info_Per).isVisible = true
                menu.findItem(R.id.Schedule).isVisible =false
                menu.findItem(R.id.Appointment).isVisible = false
                menu.findItem(R.id.AppointmentUsers).isVisible = false
            }

            val headerView = binding.nvMenuLateral.getHeaderView(0)
            val textUser = headerView.findViewById<TextView>(R.id.tv_user)
            val imgUser = headerView.findViewById<ImageView>(R.id.imgUser)

            textUser.setText(p?.nombreCompleto)
            val downloadUrl = p?.imagen

            if(downloadUrl.equals("")){
                imgUser.setImageResource(R.mipmap.ic_launcher)

            }else{
                Picasso.get().load(downloadUrl).into(imgUser)
            }
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
                imgVisibility()
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
                imgVisibility()
                Toast.makeText(this, "Se presionó la opción de agendar", Toast.LENGTH_SHORT).show()
                val fragment = schedule_appointment.newInstance("$rol")

                // Reemplazar el Fragment actual en el contenedor (normalmente un FrameLayout)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

                // Cerrar el DrawerLayout después de seleccionar una opción del menú
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.Sign_off -> {
                imgVisibility()
                Toast.makeText(this, "HA CERRADO SU SESION", Toast.LENGTH_SHORT).show()
                LoginViewModel.saveLoginStatus(this, false)
                loginViewModel.logOut()

                // Navegar de regreso a la pantalla de inicio de sesión
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.Appointment -> {
                imgVisibility()
                Toast.makeText(this, "Se presionó la opción  mis citas", Toast.LENGTH_SHORT).show()
                val fragment = MyAppointments()

                // Reemplazar el Fragment actual en el contenedor (normalmente un FrameLayout)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

                // Cerrar el DrawerLayout después de seleccionar una opción del menú
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.AppointmentUsers -> {
                imgVisibility()
                Toast.makeText(this, "Se presionó la opción  citas programadas", Toast.LENGTH_SHORT).show()
                val fragment = PatientAppointments()

                // Reemplazar el Fragment actual en el contenedor (normalmente un FrameLayout)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

                // Cerrar el DrawerLayout después de seleccionar una opción del menú
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun imgVisibility(){
        binding.imgMain.visibility= View.GONE
    }


    private fun scheduleNotification(delay: Long) {
        val scheduler = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyAppointments::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val triggerAtMillis = SystemClock.elapsedRealtime() + delay
        scheduler.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent)
    }
}






