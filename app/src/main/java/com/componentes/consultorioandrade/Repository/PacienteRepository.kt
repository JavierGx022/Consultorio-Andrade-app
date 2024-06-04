package com.componentes.consultorioandrade.Repository

import android.util.Log
import com.componentes.consultorioandrade.Model.Paciente
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class PacienteRepository {
    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()


    fun savePersonalInformation(paciente: Paciente) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$currentUserId/paciente")
        personalInformationRef.setValue(paciente)
    }




    fun getPersonalInformation(callback: (personalInformation: Paciente?, error: String?) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$currentUserId/paciente")
        personalInformationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val personalInformation = dataSnapshot.getValue(Paciente::class.java)
                if (personalInformation != null) {
                    callback(personalInformation, null)
                } else {
                    // Indica que no se encontraron datos
                    callback(null, "No se encontraron datos de información personal para el usuario actual.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Maneja el error
                callback(null, "Error al obtener información personal: ${databaseError.message}")
            }
        })
    }

    fun getPacieteUID(uid:String,callback: (personalInformation: Paciente?, error: String?) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$uid/paciente")
        personalInformationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val personalInformation = dataSnapshot.getValue(Paciente::class.java)
                if (personalInformation != null) {
                    callback(personalInformation, null)
                } else {
                    // Indica que no se encontraron datos
                    callback(null, "No se encontraron datos de información personal para el usuario actual.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Maneja el error
                callback(null, "Error al obtener información personal: ${databaseError.message}")
            }
        })
    }

    fun updatePersonalInformation(paciente: Paciente, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$currentUserId/paciente")
        personalInformationRef.updateChildren(paciente.toMap())
            .addOnSuccessListener {
                Log.i("TAG", "Datos del paciente actualizados correctamente")
                onSuccess.invoke()
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error al actualizar datos del paciente: $e")
                onFailure.invoke()
            }
    }


    fun buscarPacientePorIdentificacion(identificacion: String, callback: (Paciente?) -> Unit) {
        val usersRef = database.getReference("users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var pacienteEncontrado: Paciente? = null
                for (userSnapshot in snapshot.children) {
                    val pacienteSnapshot = userSnapshot.child("paciente")
                    val paciente = pacienteSnapshot.getValue(Paciente::class.java)
                    if (paciente?.numeroDocumento == identificacion) {
                        pacienteEncontrado = paciente
                        break
                    }
                }
                callback(pacienteEncontrado)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
}