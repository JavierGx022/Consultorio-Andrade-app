package com.componentes.consultorioandrade.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.componentes.consultorioandrade.Model.Paciente
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class PacienteViewModel: ViewModel() {

    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()


    fun savePersonalInformation(paciente: Paciente) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$currentUserId/paciente")
        personalInformationRef.setValue(paciente)
    }


    fun getPersonalInformation(callback: (Paciente?) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$currentUserId/paciente")
        personalInformationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val personalInformation = dataSnapshot.getValue(Paciente::class.java)
                callback(personalInformation)
                Log.i("TAG","DATOS DEL USUARIO"+personalInformation?.uid.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
                callback(null)
            }
        })
    }

    fun updatePersonalInformation(paciente: Paciente, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$currentUserId/paciente")
        personalInformationRef.updateChildren(paciente.toMap())
            .addOnSuccessListener {
                Log.i("TAG", "Datos del paciente actualizados correctamente")
                onSuccess.invoke() // Llama a la función de éxito si la operación fue exitosa
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error al actualizar datos del paciente: $e")
                onFailure.invoke() // Llama a la función de fracaso si ocurrió un error
            }
    }
}