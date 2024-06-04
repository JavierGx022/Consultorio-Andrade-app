package com.componentes.consultorioandrade.Repository

import android.content.Context
import android.util.Log
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.Notifications.Notifications
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Calendar

class CitaRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCitas(callback: (List<Cita>) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        val citasRef = database.getReference("users/$currentUserId/Citas")
        citasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val citasList = mutableListOf<Cita>()

                for (citaSnapshot in dataSnapshot.children) {
                    val cita = citaSnapshot.getValue(Cita::class.java)
                    if (cita != null) {
                        citasList.add(cita)
                    }
                }

                callback(citasList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejo de errores
                Log.e("ERROR AL OBTENER CITAS","Error al obtener las citas: ${databaseError.message}")
            }
        })
    }

    fun getCitasPorFecha(fecha: String, callback: (List<Cita>?) -> Unit) {
        val citaRef = database.getReference("users")
        citaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val citas = mutableListOf<Cita>()

                for (userSnapshot in dataSnapshot.children) {
                    val citasMap = userSnapshot.child("Citas").value as? Map<String, Map<String, String>> ?: continue

                    for ((_, citaData) in citasMap) {
                        val fechaCita = citaData["fecha"] ?: ""
                        if (fechaCita == fecha) {
                            val hora = citaData["hora"] ?: ""
                            val idCita = citaData["id_cita"] ?: ""
                            val motivo = citaData["motivo"] ?: ""
                            val uid = citaData["uid"] ?: ""

                            val cita = Cita(idCita, uid, fechaCita, hora, motivo)
                            citas.add(cita)
                            Log.e("TAG", "la fecha que llega es: $fecha")
                        }
                    }
                }

                callback(citas)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
                callback(null)
            }
        })
    }


    fun getCitasPorId(id: String, callback: (List<Cita>?) -> Unit) {
        val citaRef = database.getReference("users")
        citaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val citas = mutableListOf<Cita>()

                for (userSnapshot in dataSnapshot.children) {
                    val citasMap = userSnapshot.child("Citas").value as? Map<String, Map<String, String>> ?: continue

                    for ((_, citaData) in citasMap) {
                        val idCita = citaData["id_cita"] ?: ""
                        if (idCita == id) {
                            val hora = citaData["hora"] ?: ""
                            val idCita = citaData["id_cita"] ?: ""
                            val motivo = citaData["motivo"] ?: ""
                            val uid = citaData["uid"] ?: ""
                            val fechaCita= citaData["fecha"]?:""

                            val cita = Cita(idCita, uid, fechaCita, hora, motivo)
                            citas.add(cita)
                            Log.e("TAG", "la id que llega es: $id")
                        }
                    }
                }

                callback(citas)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
                callback(null)
            }
        })
    }


    fun saveAppointment(cita: Cita, id:String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$currentUserId/Citas/cita$id")
        personalInformationRef.setValue(cita)
    }

    fun saveAppointmentUID(cita: Cita, id:String, uid:String) {

        val personalInformationRef = database.getReference("users/$uid/Citas/cita$id")
        personalInformationRef.setValue(cita)
    }

    fun editarCita(uid: String, cita: Cita, callback: (Boolean) -> Unit) {
        val usersRef: DatabaseReference = database.getReference("users")
        val citaRef = usersRef.child(uid).child("Citas").child("cita"+cita.id_cita)
        citaRef.setValue(cita).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }.addOnFailureListener { exception ->
            callback(false)
        }
    }


    fun eliminarCita(uid: String, idCita: String, callback: (Boolean) -> Unit) {
        val usersRef: DatabaseReference = database.getReference("users")
        val citaRef = usersRef.child(uid).child("Citas").child("cita"+idCita)
        citaRef.removeValue().addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }


}