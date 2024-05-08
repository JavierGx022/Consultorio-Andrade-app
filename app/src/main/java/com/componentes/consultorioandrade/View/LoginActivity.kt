package com.componentes.consultorioandrade.View

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.LoginViewModel
import com.componentes.consultorioandrade.ViewModel.PacienteViewModel
import com.componentes.consultorioandrade.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: PacienteViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel = ViewModelProvider(this).get(PacienteViewModel::class.java)

        // Verificar el estado de inicio de sesión almacenado
        if (LoginViewModel.checkLoginStatus(this)) {
            viewModel.getPersonalInformation { p, error->
                var tUser= p?.paciente.toString()

                    Log.e("TAG", "EL ROL ES: "+tUser)
                    // Si el usuario ya ha iniciado sesión, navegar a la actividad principal
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("rol",tUser)
                    startActivity(intent)
                    finish()
            }

        } else {
            // Si el usuario no ha iniciado sesión, configurar el ViewModel y la UI del LoginActivity
            setupLoginActivity()
        }
    }

    private fun setupLoginActivity() {

        loginViewModel.loginResult.observe(this, Observer { loginSuccessful ->

            if (loginSuccessful) {
                viewModel.getPersonalInformation{ p, error->
                    var tUser=p?.paciente.toString()
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        // Guardar el estado de inicio de sesión
                        LoginViewModel.saveLoginStatus(this, true)
                        // Navegar a la actividad principal
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("rol",tUser)
                        startActivity(intent)
                        finish()

                }

            } else {
                // Mostrar mensaje de error si el inicio de sesión falla
                val errorMessage = loginViewModel.errorMessage.value
                errorMessage?.let {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Configurar el listener del botón de inicio de sesión
        binding.btnIngresar.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val pass = binding.txtPass.text.toString()
            loginViewModel.login(email, pass, this)
        }

        binding.tvCreateA.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
