package com.componentes.consultorioandrade.View

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.LoginViewModel
import com.componentes.consultorioandrade.databinding.ActivityLoginBinding
import com.componentes.consultorioandrade.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.loginResult.observe(this, Observer { RegisterSuccessful ->
            if (RegisterSuccessful) {
                Toast.makeText(getApplicationContext(), "Registrado correctamente", Toast.LENGTH_SHORT).show();

            } else {
                // Mostrar mensaje de error si el inicio de sesión falla
                val errorMessage = loginViewModel.errorMessage.value
                errorMessage?.let {
                    Toast.makeText(getApplicationContext(), "Hubo un error al registrar", Toast.LENGTH_SHORT).show();

                }
            }
        })

        binding.btnRegistrar.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPass.text.toString()
            loginViewModel.register(email, password)


        }

    }


}