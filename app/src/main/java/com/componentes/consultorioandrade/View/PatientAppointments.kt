package com.componentes.consultorioandrade.View

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.View.AdapterRecycler.CitaAdapter

import com.componentes.consultorioandrade.ViewModel.CitaViewModell
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
    private lateinit var viewModelC: CitaViewModell
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
        viewModelC = ViewModelProvider(this).get(CitaViewModell::class.java)
        viewModel = ViewModelProvider(this).get(PacienteViewModel::class.java)
        auth = FirebaseAuth.getInstance()


        binding.consultationDate.setOnClickListener {
            showDatePicker()
        }



        initRecycler()

        binding.btnFilter.setOnClickListener {
            // Obtener la fecha deseada al hacer clic en el botón
            val fechaDeseada = binding.consultationDate.text.toString()

            // Obtener las citas por la fecha deseada
            viewModelC.getCitasPorFecha(fechaDeseada)
        }

        viewModelC.citasPorFechaLiveData.observe(viewLifecycleOwner, { citasList ->
            actualizarListView(citasList)
        })

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



    private fun initRecycler() {
        binding.rvCitas.layoutManager = LinearLayoutManager(requireContext())
        // Configurar el adaptador inicialmente con una lista vacía
        binding.rvCitas.adapter = CitaAdapter(emptyList()) { cita ->
            onItemSelected(cita)
        }

    }

    private fun actualizarListView(citasList: List<Cita>) {
        // Actualizar el adaptador con las citas obtenidas
        (binding.rvCitas.adapter as CitaAdapter).updateCitas(citasList)
    }

    private fun onItemSelected(cita: Cita) {
        val detalleAppoinmentFragment = DetalleAppoinment.newInstance(cita.id_cita)

        // Reemplaza el fragmento actual con el nuevo fragmento
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, detalleAppoinmentFragment)
            addToBackStack(null)
            commit()
        }
    }

}