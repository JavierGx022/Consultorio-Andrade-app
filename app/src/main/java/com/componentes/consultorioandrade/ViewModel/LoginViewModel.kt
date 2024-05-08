package com.componentes.consultorioandrade.ViewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.util.Log

class LoginViewModel:ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    // MutableLiveData para manejar el estado del inicio de sesión
    val loginResult: MutableLiveData<Boolean> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()


    fun login(email: String, password: String, context: Context) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    loginResult.value = true
                    saveLoginStatus(context, true)
                    Log.i("TAG", "INICIO DE SESION CORRECTO")
                } else {
                    // Fallo en el inicio de sesión
                    loginResult.value = false
                    errorMessage.value = task.exception?.message ?: "Error desconocido"
                    Log.e("TAG", "Error al iniciar sesión")
                }
            }
    }

    fun register(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    loginResult.value = true
                } else {
                    // Fallo en el registro
                    loginResult.value = false
                    errorMessage.value = task.exception?.message ?: "Error desconocido"
                }
            }
    }

    fun logOut(){
        firebaseAuth.signOut()
    }

    companion object {
        // Función para guardar el estado de inicio de sesión
        fun saveLoginStatus(context: Context, isLoggedIn: Boolean) {
            val sharedPrefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            with(sharedPrefs.edit()) {
                putBoolean("isLoggedIn", isLoggedIn)
                apply()
            }
        }

        // Función para verificar el estado de inicio de sesión
        fun checkLoginStatus(context: Context): Boolean {
            val sharedPrefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            return sharedPrefs.getBoolean("isLoggedIn", false)
        }
    }


}