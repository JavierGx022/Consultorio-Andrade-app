package com.componentes.consultorioandrade.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.componentes.consultorioandrade.Model.Cita
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class CitaViewModel : ViewModel(){
    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()


    fun saveAppointment(cita: Cita, id:String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = database.getReference("users/$currentUserId/Citas/cita$id")
        personalInformationRef.setValue(cita)
    }

    fun saveAppointmentUID(cita: Cita, id:String, uid:String) {

        val personalInformationRef = database.getReference("users/$uid/Citas/cita$id")
        personalInformationRef.setValue(cita)
    }

    fun getCitas(callback: (Map<String, Cita>?) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        val citaRef = database.getReference("users/$currentUserId/Citas")
        citaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val citasMap = dataSnapshot.value as? Map<String, Map<String, String>> ?: return
                val citas = mutableMapOf<String, Cita>()

                for ((citaId, citaData) in citasMap) {
                    val fecha = citaData["fecha"] ?: ""
                    val hora = citaData["hora"] ?: ""
                    val idCita = citaData["id_cita"] ?: ""
                    val motivo = citaData["motivo"] ?: ""
                    val uid = citaData["uid"] ?: ""

                    val cita = Cita(idCita,uid, fecha, hora, motivo)
                    citas[citaId] = cita
                }

                callback(citas)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
                callback(null)
                Log.e("TAG","error al buscar en viewModel")
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




    fun validarCitaExistente(fecha: String, hora: String, callback: (Boolean) -> Unit) {
        val citaRef = database.getReference("users")
        citaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var citaExistente = false

                for (userSnapshot in dataSnapshot.children) {
                    val citasMap = userSnapshot.child("Citas").value as? Map<String, Map<String, String>> ?: continue

                    for ((_, citaData) in citasMap) {
                        val fechaCita = citaData["fecha"] ?: ""
                        val horaCita = citaData["hora"] ?: ""
                        if (fechaCita == fecha && horaCita == hora) {
                            citaExistente = true
                            break
                        }
                    }

                    if (citaExistente) {
                        break
                    }
                }

                callback(citaExistente)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
                callback(false)
            }
        })
    }

}