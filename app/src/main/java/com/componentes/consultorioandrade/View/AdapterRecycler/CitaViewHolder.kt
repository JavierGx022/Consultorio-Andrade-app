package com.componentes.consultorioandrade.View.AdapterRecycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.R

class CitaViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val fechar= view.findViewById<TextView>(R.id.tvFechaR)
    val horar= view.findViewById<TextView>(R.id.tvHoraR)
    val motivo= view.findViewById<TextView>(R.id.tvMotivoR)
    fun render(cita:Cita, onClickListener: (Cita)->Unit){
        fechar.text= R.string.fecha.toString() +": "+cita.fecha
        horar.text= R.string.hora.toString()+": "+cita.hora
        motivo.text= R.string.motivo.toString()+": "+cita.motivo

        itemView.setOnClickListener{
            onClickListener(cita)
        }
    }

}