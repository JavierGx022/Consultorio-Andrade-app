package com.componentes.consultorioandrade.View

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.CitaViewModell
import com.componentes.consultorioandrade.ViewModel.PacienteViewModel
import com.componentes.consultorioandrade.databinding.FragmentInfoAppointmentBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class DetalleAppoinment : Fragment(), DatePickerDialog.OnDateSetListener {
    private var _binding: FragmentInfoAppointmentBinding? = null
    private lateinit var viewModelC: CitaViewModell
    private lateinit var viewModel: PacienteViewModel
    private val binding get() = _binding!!
    private var hora=""
    private var motivo=""
    private val auth = FirebaseAuth.getInstance()
    private var uidU= ""


    companion object {
        private const val ARG_MY_STRING = "citaId"

        fun newInstance(myString: String) =
            DetalleAppoinment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MY_STRING, myString)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInfoAppointmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelC = ViewModelProvider(this).get(CitaViewModell::class.java)
        viewModel = ViewModelProvider(this).get(PacienteViewModel::class.java)
        val currentUserId = auth.currentUser?.uid ?: return
        binding.txtDateAppointment.setOnClickListener {
            showDatePicker()
        }

        // Crea una lista de datos para el Spinner
        downSpinner()
        //Mostrar informacion de la cita en campos
        showInfoCita()


        //Botones de accion


        binding.btnSaveAppo.setOnClickListener {
            editDataUID(hora,motivo, uidU)
        }

        binding.btnDeleteAppo.setOnClickListener {
            delete()
        }



    }

    private fun delete(){
        val citaId = arguments?.getString("citaId").toString()
        viewModelC.eliminarCita(uidU,citaId) { exito ->
            if (exito) {
                Toast.makeText(requireContext(), "Cita eliminada", Toast.LENGTH_SHORT).show()
                val myAppoinmentFragment = MyAppointments()

                // Reemplaza el fragmento actual con el nuevo fragmento
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, myAppoinmentFragment)
                    addToBackStack(null)
                    commit()
                }
            } else {
                Toast.makeText(requireContext(), "Error al eliminar la cita", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editDataUID(hora:String, motivo:String, uid:String){
        val citaId = arguments?.getString("citaId").toString()
            val cita = Cita(
                citaId,
                uid,
                binding.txtDateAppointment.text.toString(),
                hora,
                motivo
            )

            viewModelC.validarCitaExistente(binding.txtDateAppointment.text.toString(),hora){c->
                if(c){
                    Toast.makeText(requireContext(), "Fecha u Hora no disponible", Toast.LENGTH_SHORT).show()
                }else{
                    viewModelC.editarCita(uid,cita){exito->
                        if(exito){
                            Toast.makeText(requireContext(), "Se ha editado la información", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), "FALLO", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }



    }

    private fun showInfoCita() {

        val citaId = arguments?.getString("citaId").toString()

        viewModelC.getCitasPorId(citaId)

        viewModelC.citasPorIdLiveData.observe(viewLifecycleOwner, { c ->
            var uid= c.get(0).uid
            uidU= uid
            viewModel.getPacieteUID(uid){p,e->
                if(p!=null){
                    binding.tvNombreP.setText("Nombre: "+p.nombreCompleto)
                    binding.tvCedula.setText("Nro documento: "+p.numeroDocumento)
                }else{
                    Log.e("TAG", "NULLO EN CONSULTA NOMBRE")
                    Log.e("TAG", "cedula: $uid")
                }
            }
            var indexH= indexH(c.get(0).hora)
            var indexR= indexR(c.get(0).motivo)
            binding.txtDateAppointment.setText(c.get(0).fecha)
            binding.spHours.setSelection(indexH)
            binding.spReason.setSelection(indexR)
        })



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
                motivo=opcionSeleccionada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    private fun indexH(opcion:String):Int{
        var index=0
        when(opcion){
            "8:00 a.m" -> index=0
            "9:00 a.m" -> index=1
            "10:00 a.m" -> index=2
            "11:00 a.m" -> index=3
            "12:00 a.m" ->index=4
            "02:00 pm" ->index=5
            "03:00 pm" ->index=6
            "04:00 pm" ->index=7
            "05:00 pm" ->index=8
            "06:00 pm" ->index=9
            "07:00 pm" ->index=10
            "08:00 pm" ->index=11
            "09:00 pm" ->index=12

        }
        return index
    }

    private fun indexR(opcion:String):Int{
        var index=0
        when(opcion){
            "Consulta" ->index=0
            "Calza" ->index=1
            "Extraccion"->index=2
        }
        return index
    }

}