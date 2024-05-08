package com.componentes.consultorioandrade.View

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.CitaViewModel
import com.componentes.consultorioandrade.ViewModel.PacienteViewModel
import com.componentes.consultorioandrade.databinding.FragmentPatientAppointmentsBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PatientAppointments : Fragment(), DatePickerDialog.OnDateSetListener {
    private var _binding: FragmentPatientAppointmentsBinding? = null
    private lateinit var viewModel: PacienteViewModel
    private val binding get() = _binding!!
    private lateinit var viewModelC: CitaViewModel
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientAppointmentsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelC = ViewModelProvider(this).get(CitaViewModel::class.java)
        viewModel = ViewModelProvider(this).get(PacienteViewModel::class.java)
        auth = FirebaseAuth.getInstance()


        binding.consultationDate.setOnClickListener {
            showDatePicker()
        }

        val citasList = mutableListOf<Cita>()
        val adapter = CitaAdapter(requireContext(), citasList)
        val listView = binding.AppointmentsDay
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            // Obtener la cita seleccionada
            val citaSeleccionada = citasList[position]

            // Crear un Bundle para pasar datos al fragmento de destino
            val bundle = Bundle().apply {
                // Aquí puedes pasar cualquier dato adicional que necesites en el fragmento de destino
                putString("citaId", citaSeleccionada.id_cita)
            }

            // Crear una instancia del fragmento de destino
            val fragmentoDestino = DetalleAppoinment().apply {
                arguments = bundle
            }

            // Reemplazar el fragmento actual con el fragmento de destino
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentoDestino)
                .addToBackStack(null) // Opcional: agregar a la pila de retroceso para permitir volver al fragmento anterior
                .commit()
        }

        binding.btnFilter.setOnClickListener {
            // Obtener la fecha deseada al hacer clic en el botón
            val fechaDeseada = binding.consultationDate.text.toString()

            // Obtener las citas por la fecha deseada
            viewModelC.getCitasPorFecha(fechaDeseada) { citas ->
                citas?.let {
                    citasList.clear()
                    citasList.addAll(it)

                    // Crear una lista para almacenar los nombres de los pacientes
                    val nombresPacientes = mutableListOf<String>()

                    // Obtener el nombre del paciente para cada cita
                    for (cita in it) {
                        viewModel.getPacieteUID(cita.uid) { paciente, error ->
                            paciente?.let {
                                nombresPacientes.add(paciente.nombreCompleto)
                                // Actualizar el adaptador cuando se haya obtenido el nombre del paciente
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                    // Actualizar el adaptador con la lista de nombres de pacientes
                    adapter.updateNombresPacientes(nombresPacientes)
                }
            }
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
        val etDatePicker = binding.consultationDate
        // Establecer el formato de fecha deseado
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Establecer el texto del EditText con la fecha seleccionada
        etDatePicker.setText(dateFormat.format(calendar.time))
    }

    class CitaAdapter(context: Context, citas: List<Cita>) :
        ArrayAdapter<Cita>(context, R.layout.list_item_cita, citas) {

        private var nombresPacientes: List<String> = emptyList()

        fun updateNombresPacientes(nombres: List<String>) {
            nombresPacientes = nombres
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.list_item_cita, parent, false)
            }

            val tvNombrePaciente = itemView!!.findViewById<TextView>(R.id.tvPacienteN)
            val tvHora = itemView!!.findViewById<TextView>(R.id.tvHora)
            val cita = getItem(position)

            tvHora.text= "Hora: ${cita?.hora}"
            // Muestra el nombre del paciente si está disponible
            if (position < nombresPacientes.size) {
                tvNombrePaciente.text = "${nombresPacientes[position]}"
            } else {
                tvNombrePaciente.text = "Paciente: Desconocido"
            }
            return itemView
        }
    }

}