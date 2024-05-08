package com.componentes.consultorioandrade.View

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.CitaViewModel
import com.componentes.consultorioandrade.databinding.FragmentMyAppointmentsBinding
import com.componentes.consultorioandrade.databinding.FragmentScheduleAppointmentBinding
import com.google.firebase.auth.FirebaseAuth


class MyAppointments : Fragment() {
    private var _binding: FragmentMyAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelC: CitaViewModel
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyAppointmentsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelC = ViewModelProvider(this).get(CitaViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        val citasList = mutableListOf<Cita>()
        val adapter = CitaAdapter(requireContext(), citasList)
        val listView = binding.listCitas
        listView.adapter = adapter

        // Agregar listener de clic a la ListView
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

        // Obtener las citas del ViewModel
        viewModelC.getCitas { citas ->
            citas?.let {
                citasList.clear()
                citasList.addAll(it.values)
                adapter.notifyDataSetChanged()
            }
        }
    }






    class CitaAdapter(context: Context, private var citas: List<Cita>) :
        ArrayAdapter<Cita>(context, R.layout.list_item_cita, citas) {

        fun setCitas(citas: List<Cita>) {
            this.citas = citas
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            }

            val textViewFecha = itemView!!.findViewById<TextView>(R.id.tvFechaC)
            val textViewHora = itemView!!.findViewById<TextView>(R.id.tvHora)
            val textViewMotivo= itemView!!.findViewById<TextView>(R.id.tvPacienteN)

            val cita = getItem(position)
            textViewFecha.text = "Fecha: ${cita?.fecha}"
            textViewHora.text = "Hora: ${cita?.hora}"
            textViewMotivo.text = "Motivo+: ${cita?.motivo}"

            return itemView
        }
    }



}