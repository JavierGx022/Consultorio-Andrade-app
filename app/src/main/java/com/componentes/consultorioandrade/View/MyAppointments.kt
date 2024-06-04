package com.componentes.consultorioandrade.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.View.AdapterRecycler.CitaAdapter
import com.componentes.consultorioandrade.ViewModel.CitaViewModell
import com.componentes.consultorioandrade.databinding.FragmentInfoAppointmentBinding
import com.componentes.consultorioandrade.databinding.FragmentMyAppointmentsBinding
import com.componentes.consultorioandrade.databinding.FragmentScheduleAppointmentBinding
import com.google.firebase.auth.FirebaseAuth



class MyAppointments : Fragment() {
    private var _binding: FragmentMyAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelC: CitaViewModell
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
        viewModelC = ViewModelProvider(this).get(CitaViewModell::class.java)
        auth = FirebaseAuth.getInstance()

        initRecycler()


    }


    private fun initRecycler(){
        binding.rvCitas.layoutManager= LinearLayoutManager(requireContext())
        viewModelC.citasLiveData.observe(viewLifecycleOwner, Observer { citas ->
            binding.rvCitas.adapter = CitaAdapter(citas){c->
                onItemSelected(
                    c
                )
            }
        })
        viewModelC.getCitas()

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