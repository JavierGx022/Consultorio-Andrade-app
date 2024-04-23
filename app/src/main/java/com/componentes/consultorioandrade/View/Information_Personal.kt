package com.componentes.consultorioandrade.View

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.compose.material3.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.Model.Paciente
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.PacienteViewModel
import com.componentes.consultorioandrade.databinding.ActivityMainBinding
import com.componentes.consultorioandrade.databinding.FragmentInformationPersonalBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Information_Personal.newInstance] factory method to
 * create an instance of this fragment.
 */
class Information_Personal : Fragment(), DatePickerDialog.OnDateSetListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: PacienteViewModel
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentInformationPersonalBinding? = null

    private val binding get() = _binding!!



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInformationPersonalBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura un clic en el EditText para abrir el DatePicker
        view.findViewById<EditText>(R.id.txtDateBorn).setOnClickListener {
            showDatePicker()
        }

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this).get(PacienteViewModel::class.java)
        auth = FirebaseAuth.getInstance()



        viewModel.getPersonalInformation { personalInformation ->
            if (personalInformation != null) {
                binding.txtTipoD.setText(personalInformation.tipoDocumento)
                binding.txtNorD.setText(personalInformation.numeroDocumento)
                binding.txttName.setText(personalInformation.nombreCompleto)
                binding.txtCelular.setText(personalInformation.celular)
                binding.txtGenero.setText(personalInformation.genero)
                binding.txtDateBorn.setText(personalInformation.fechaNacimiento)
            }
        }

        binding.buttonSubmit.setOnClickListener {
            if(binding.txtTipoD.text.toString()!=null){
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val personalInformation = Paciente(
                        currentUser.uid,
                        binding.txtTipoD.text.toString(),
                        binding.txtNorD.text.toString(),
                        binding.txttName.text.toString(),
                        binding.txtCelular.text.toString(),
                        binding.txtGenero.text.toString(),
                        binding.txtDateBorn.text.toString()
                    )
                    // Llamada a la función updatePersonalInformation con las funciones de éxito y fracaso
                    viewModel.updatePersonalInformation(personalInformation,
                        onSuccess = {
                            // Mostrar un AlertDialog indicando que se guardaron los datos correctamente
                            alert("editaron")
                        },
                        onFailure = {
                            // Mostrar un Toast si ocurrió un error al actualizar la información
                            Toast.makeText(requireContext(), "No se pudo actualizar la información", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(requireContext(), "No se edito", Toast.LENGTH_SHORT).show()
                }
            }else {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val personalInformation = Paciente(
                        currentUser.uid,
                        binding.txtTipoD.text.toString(),
                        binding.txtNorD.text.toString(),
                        binding.txttName.text.toString(),
                        binding.txtCelular.text.toString(),
                        binding.txtGenero.text.toString(),
                        binding.txtDateBorn.text.toString()
                    )
                    viewModel.savePersonalInformation(personalInformation)
                } else {
                    Toast.makeText(requireContext(), "No se guardó", Toast.LENGTH_SHORT).show()
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
        val etDatePicker = requireView().findViewById<EditText>(R.id.txtDateBorn)

        // Establecer el formato de fecha deseado
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Establecer el texto del EditText con la fecha seleccionada
        etDatePicker.setText(dateFormat.format(calendar.time))
    }

    fun alert(mensaje:String){
        AlertDialog.Builder(requireContext())
            .setTitle("confirmación")
            .setIcon(com.google.android.material.R.drawable.ic_m3_chip_check)
            .setMessage("¡Los datos se $mensaje correctamente!")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}