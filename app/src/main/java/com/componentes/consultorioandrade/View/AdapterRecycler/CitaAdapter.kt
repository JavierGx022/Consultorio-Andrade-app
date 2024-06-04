package com.componentes.consultorioandrade.View.AdapterRecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.componentes.consultorioandrade.Model.Cita
import com.componentes.consultorioandrade.R

class CitaAdapter(private var citasList: List<Cita>, private val onClickListener: (Cita)->Unit) : RecyclerView.Adapter<CitaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_row, parent, false)
        return CitaViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return citasList.size
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val item = citasList[position]
        holder.render(item, onClickListener)
    }

    fun updateCitas(newCitas: List<Cita>) {
        citasList = newCitas
        notifyDataSetChanged()
    }

}