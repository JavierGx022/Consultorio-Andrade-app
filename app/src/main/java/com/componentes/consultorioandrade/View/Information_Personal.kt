package com.componentes.consultorioandrade.View

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
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
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.DatePickerDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.componentes.consultorioandrade.Model.Paciente
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.ViewModel.PacienteViewModel
import com.componentes.consultorioandrade.databinding.ActivityMainBinding
import com.componentes.consultorioandrade.databinding.FragmentInformationPersonalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class Information_Personal : Fragment(), DatePickerDialog.OnDateSetListener {


    private lateinit var viewModel: PacienteViewModel
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentInformationPersonalBinding? = null
    private lateinit var storage: FirebaseStorage
    private val FILE_SELECT_CODE = 42
    private var fileUri: Uri?=null
    private var tipoD=""
    private var Genero=""
    private var validacion= false
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
        storage = FirebaseStorage.getInstance()

        // Configura un clic en el EditText para abrir el DatePicker
        view.findViewById<EditText>(R.id.txtDateBorn).setOnClickListener {
            showDatePicker()
        }



        val opciones_tipoD = listOf("Tarjeta identidad", "Cédula de ciudadanía", "Pasaporte")
        val opciones_Genero = listOf("Femenino", "Masculino", "Otro")
        val adapter_tipoD = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones_tipoD)
        val adapter_Genero= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones_Genero)
        adapter_tipoD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter_Genero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTDoc.adapter= adapter_tipoD
        binding.spGenero.adapter= adapter_Genero
        binding.spTDoc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tipoD = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Genero =parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



        // Inicializar ViewModel
        viewModel = ViewModelProvider(this).get(PacienteViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        var tUsuario=false
        var urlImg=""


        viewModel.getPersonalInformation{ personalInformation, error ->


            if(error!=null){
                Toast.makeText(requireContext(),"Error al obetener informacion: $error",Toast.LENGTH_SHORT)
            }else{
                if (personalInformation != null) {
                    var indexT=indexT(personalInformation.tipoDocumento)
                    var indexG=indexG(personalInformation.genero)

                    binding.spTDoc.setSelection(indexT)
                    binding.txtNorD.setText(personalInformation.numeroDocumento)
                    binding.txttName.setText(personalInformation.nombreCompleto)
                    binding.txtCelular.setText(personalInformation.celular)
                    binding.spGenero.setSelection(indexG)
                    binding.txtDateBorn.setText(personalInformation.fechaNacimiento)
                    validacion= true
                    tUsuario= personalInformation.paciente
                    urlImg=personalInformation.imagen

                }
            }

        }


        binding.buttonSubmit.setOnClickListener {
            uploadFileToFirebase(tUsuario, urlImg)
        }

        binding.btnFile.setOnClickListener {
               openFileSelector()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { fileUri ->
                this.fileUri = fileUri
                Log.d("TAG", "Archivo seleccionado: $fileUri")
            }
        }
    }

    private fun uploadFileToFirebase(tUser: Boolean,urlImg: String) {
        fileUri?.let { fileUri ->
            val fileName = UUID.randomUUID().toString() // Generar un nombre de archivo único
            val storageRef = storage.reference.child("files/$fileName")

            val uploadTask = storageRef.putFile(fileUri)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                // El archivo se subió correctamente
                // Obtener la URL de descarga
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    if (validacion) {
                        updateData(downloadUrl, tUser)
                    } else {
                        saveData(downloadUrl)
                    }
                }.addOnFailureListener { exception ->
                    Log.e("TAG", "Error al obtener la URL de descarga: $exception")
                }
            }.addOnFailureListener { exception ->
                Log.e("TAG", "El archivo no se subió: $exception")
            }
        } ?: run {
            // Si no se ha seleccionado ningún archivo, solo actualiza los datos sin la URL del archivo
            if (validacion) {
                updateData(urlImg, tUser)
            } else {
                saveData("")
            }
        }
    }


    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivityForResult(intent, FILE_SELECT_CODE)
    }





    private fun indexT(opcion:String):Int{
     var index=0
     when(opcion){
         "Tarjeta identidad"-> index=0
         "Cédula de ciudadanía" -> index=1
         "Pasaporte"-> index=2

     }
     return index
 }

    private fun indexG(opcion:String):Int{
        var index=0
        when(opcion){
            "Femenino"-> index=0
            "Masculino"-> index=1
            "Otro"-> index=2
        }
        return index
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

        acceptButton.setOnClickListener {
            // Acción al hacer clic en el botón de aceptar
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Cierra la actividad actual para evitar volver atrás
            alertDialog.dismiss()
        }



        alertDialog.setCancelable(false) // Impide que el diálogo se cierre al tocar fuera de él
        alertDialog.show()
    }


    private fun saveData(url:String){
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val personalInformation = Paciente(
                currentUser.uid,
                tipoD,
                binding.txtNorD.text.toString(),
                binding.txttName.text.toString(),
                binding.txtCelular.text.toString(),
                Genero,
                binding.txtDateBorn.text.toString(),
                true,
                url
            )
            viewModel.savePersonalInformation(personalInformation)
            showCustomDialog("Tus datos se han guardado correctamente","Registro de datos")
        } else {
            Toast.makeText(requireContext(), "No se guardó", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData(url:String, tUser: Boolean){
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val personalInformation = Paciente(
                currentUser.uid,
                tipoD,
                binding.txtNorD.text.toString(),
                binding.txttName.text.toString(),
                binding.txtCelular.text.toString(),
                Genero,
                binding.txtDateBorn.text.toString(),
                tUser,
                url
            )
            // Llamada a la función updatePersonalInformation con las funciones de éxito y fracaso
            viewModel.updatePersonalInformation(personalInformation,
                onSuccess = {
                    // Mostrar un AlertDialog indicando que se guardaron los datos correctamente
                    showCustomDialog("Tus datos se actualizaron correctamene","Actualizacion de datos")
                },
                onFailure = {
                    // Mostrar un Toast si ocurrió un error al actualizar la información
                    Toast.makeText(requireContext(), "No se pudo actualizar la información", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(requireContext(), "No se edito", Toast.LENGTH_SHORT).show()
        }
    }

}