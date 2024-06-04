package com.componentes.consultorioandrade.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.Repository.CitaRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class CitaViewModell : ViewModel(){
    private val _citasLiveData = MutableLiveData<List<Cita>>()
    val citasLiveData: LiveData<List<Cita>> = _citasLiveData
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _citasPorFechaLiveData = MutableLiveData<List<Cita>>()
    val citasPorFechaLiveData: LiveData<List<Cita>> = _citasPorFechaLiveData

    private val _citasPorIdLiveData = MutableLiveData<List<Cita>>()
    val citasPorIdLiveData: LiveData<List<Cita>> = _citasPorIdLiveData

    private val repository= CitaRepository()

    fun getCitas() {
        repository.getCitas { citasList ->
            _citasLiveData.postValue(citasList)
        }
    }

    fun getCitasPorFecha(fecha: String) {
        repository.getCitasPorFecha(fecha) { citasList ->
            _citasPorFechaLiveData.postValue(citasList)
        }
    }

    fun getCitasPorId(id: String) {
        repository.getCitasPorId(id) { citasList ->
            _citasPorIdLiveData.postValue(citasList)
        }
    }


    fun saveAppointment(cita:Cita,id:String){
        repository.saveAppointment(cita,id)
    }

    fun saveAppointmentUID(cita:Cita, id:String, uid:String){
        repository.saveAppointmentUID(cita,id, uid)
    }

    fun eliminarCita(uid: String, idCita: String, callback: (Boolean) -> Unit) {
        repository.eliminarCita(uid, idCita) { exito ->
            callback(exito)
        }
    }

    fun editarCita(uid: String, cita: Cita, callback: (Boolean) -> Unit) {
        repository.editarCita(uid, cita) { exito ->
            callback(exito)
        }
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