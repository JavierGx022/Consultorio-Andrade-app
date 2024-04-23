package com.componentes.consultorioandrade.View

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import com.componentes.consultorioandrade.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [schedule_appointment.newInstance] factory method to
 * create an instance of this fragment.
 */
class schedule_appointment : Fragment(), DatePickerDialog.OnDateSetListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_appointment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerHour = view.findViewById<Spinner>(R.id.spHours)
        val spinnerReasson = view.findViewById<Spinner>(R.id.spReason)

        // Configura un clic en el EditText para abrir el DatePicker
        view.findViewById<EditText>(R.id.txtDateAppointment).setOnClickListener {
            showDatePicker()
        }

        // Crea una lista de datos para el Spinner
        val opciones_h = listOf("8:00 a.m", "9:00 a.m", "10:00 a.m", "11:00 a.m", "12:00 a.m")
        val opciones_r = listOf("Consulta", "Calza", "Extraccion")


        // Crea un ArrayAdapter con la lista de opciones
        val adapter_h = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones_h)
        adapter_h.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapter_r = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones_r)
        adapter_r.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Establece el adaptador en el Spinner
        spinnerHour.adapter = adapter_h
        spinnerReasson.adapter= adapter_r

        spinnerHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val opcionSeleccionada = parent?.getItemAtPosition(position).toString()
                // Realiza las acciones necesarias con la opción seleccionada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerReasson.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val opcionSeleccionada = parent?.getItemAtPosition(position).toString()
                // Realiza las acciones necesarias con la opción seleccionada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment schedule_appointment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            schedule_appointment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
        val etDatePicker = requireView().findViewById<EditText>(R.id.txtDateAppointment)

        // Establecer el formato de fecha deseado
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Establecer el texto del EditText con la fecha seleccionada
        etDatePicker.setText(dateFormat.format(calendar.time))
    }
}