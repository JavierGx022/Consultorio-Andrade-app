package com.componentes.consultorioandrade.View

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.Model.Paciente
import com.componentes.consultorioandrade.R

import com.componentes.consultorioandrade.ViewModel.CitaViewModell
import com.componentes.consultorioandrade.ViewModel.PacienteViewModel
import com.componentes.consultorioandrade.databinding.FragmentInformationPersonalBinding
import com.componentes.consultorioandrade.databinding.FragmentScheduleAppointmentBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class schedule_appointment : Fragment(), DatePickerDialog.OnDateSetListener {


    private lateinit var viewModel: PacienteViewModel
    private lateinit var viewModelC: CitaViewModell
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentScheduleAppointmentBinding? = null
    private val binding get() = _binding!!
    private var myString: String? = null

    private var hora=""
    private var motivo=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScheduleAppointmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PacienteViewModel::class.java)
        viewModelC = ViewModelProvider(this).get(CitaViewModell::class.java)
        auth = FirebaseAuth.getInstance()

        var uidUser=""
        binding.btnBuscar.setOnClickListener {
            viewModel.buscarPacientePorIdentificacion(binding.txtBuscar.text.toString()){paciente ->
                if (paciente != null) {
                    Log.e("TAG", "el paciente es: "+paciente.nombreCompleto)
                    binding.tvPaciente.setText("Nombre:"+paciente.nombreCompleto+"\nNro Identificacion: "+paciente.numeroDocumento)
                    binding.tvPaciente.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    uidUser=paciente.uid
                }
            }
        }


        //Fecha de la cita
        binding.txtDateAppointment.setOnClickListener {
            showDatePicker()
        }

        // Crea una lista de datos para el Spinner
        downSpinner()



        arguments?.let {
            myString = it.getString("myString")
            // Utiliza el valor de myString aquí
            myString?.let { receivedString ->
                if(receivedString.equals("true")){
                    binding.llBuscar.visibility = View.GONE
                    binding.btnSaveAppo.setOnClickListener {

                        saveData(hora,motivo)
                    }
                }else{
                    binding.llBuscar.visibility = View.VISIBLE
                    binding.btnSaveAppo.setOnClickListener {
                        saveDataUID(hora,motivo, uidUser)
                    }
                }

            }
        }



    }

    private fun downSpinner(){
        val opciones_h = listOf("8:00 a.m", "9:00 a.m", "10:00 a.m", "11:00 a.m",
            "12:00 a.m","02:00 pm","03:00 pm","04:00 pm","05:00 pm","06:00 pm",
            "07:00 pm","08:00 pm","09:00 pm")
        val opciones_r = listOf("Consulta", "Calza", "Extraccion")

        // Crea un ArrayAdapter con la lista de opciones
        val adapter_h = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones_h)
        adapter_h.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapter_r = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones_r)
        adapter_r.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Establece el adaptador en el Spinner
        binding.spHours.adapter = adapter_h
        binding.spReason.adapter= adapter_r

        binding.spHours.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val opcionSeleccionada = parent?.getItemAtPosition(position).toString()
                hora=opcionSeleccionada
                // Realiza las acciones necesarias con la opción seleccionada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spReason.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val opcionSeleccionada = parent?.getItemAtPosition(position).toString()
                // Realiza las acciones necesarias con la opción seleccionada
                motivo=opcionSeleccionada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun saveData(hora:String, motivo:String){
        val currentUser = auth.currentUser
        val uuid = UUID.randomUUID()
        var idCita= uuid.toString()
        if (currentUser != null) {
            val cita = Cita(
                idCita,
                currentUser.uid,
                binding.txtDateAppointment.text.toString(),
                hora,
                motivo
            )
            viewModelC.validarCitaExistente(binding.txtDateAppointment.text.toString(),hora){c->
                if(c){
                    Toast.makeText(requireContext(), "Fecha u Hora no disponible", Toast.LENGTH_SHORT).show()
                }else{
                    viewModelC.saveAppointment(cita,idCita)

                    showCustomDialog("La cita se ha agendado correctamente","Agendamiento")
                }
            }



        } else {
            Toast.makeText(requireContext(), "No se guardó", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataUID(hora:String, motivo:String, uid:String){
        val currentUser = auth.currentUser
        val uuid = UUID.randomUUID()
        var idCita= uuid.toString()
        if (currentUser != null) {
            val cita = Cita(
                idCita,
                uid,
                binding.txtDateAppointment.text.toString(),
                hora,
                motivo
            )

            viewModelC.validarCitaExistente(binding.txtDateAppointment.text.toString(),hora){c->
                if(c){
                    Toast.makeText(requireContext(), "Fecha u Hora no disponible", Toast.LENGTH_SHORT).show()
                }else{
                    viewModelC.saveAppointmentUID(cita,idCita, uid)
                    showCustomDialog("La cita se ha agendado correctamente","Agendamiento")
                }
            }


        } else {
            Toast.makeText(requireContext(), "No se guardó", Toast.LENGTH_SHORT).show()
        }
    }




    private fun showDatePicker() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), this, startYear, startMonth, startDay).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Maneja la fecha seleccionada
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        // Obtén una referencia al EditText
        val etDatePicker = requireView().findViewById<EditText>(R.id.txtDateAppointment)

        // Establecer el formato de fecha deseado
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Establecer el texto del EditText con la fecha seleccionada
        etDatePicker.setText(dateFormat.format(calendar.time))
    }

    private fun showCustomDialog(mensaje: String, titulo:String) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_save, null) // R.layout.custom_dialog_game_over es el layout personalizado del diálogo
        builder.setView(dialogView)

        // Configurar elementos del diálogo personalizado
        val titleTextView = dialogView.findViewById<TextView>(R.id.title_save)
        val messageTextView = dialogView.findViewById<TextView>(R.id.tvMs)
        val acceptButton = dialogView.findViewById<Button>(R.id.btnAceptar)

        titleTextView.setText(titulo)
        messageTextView.setText(mensaje)

        val alertDialog = builder.create()
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

       // Obtener el fragmento actual
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)

        acceptButton.setOnClickListener {
            // Acción al hacer clic en el botón de aceptar
            // Verificar que el fragmento no sea nulo
            currentFragment?.let {
                fragmentTransaction.remove(it)
                fragmentTransaction.commit()
            }

// Dismiss the alert dialog
            alertDialog.dismiss()
        }



        alertDialog.setCancelable(false) // Impide que el diálogo se cierre al tocar fuera de él
        alertDialog.show()
    }


    companion object {
        private const val ARG_MY_STRING = "myString"

        fun newInstance(myString: String) =
            schedule_appointment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MY_STRING, myString)
                }
            }
    }
}