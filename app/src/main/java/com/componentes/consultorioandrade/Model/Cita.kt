package com.componentes.consultorioandrade.Model

data class Cita(
    val id_cita:String="",
    val uid:String="",
    val fecha:String="",
    val hora: String="",
    val motivo:String= "",
    ){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id_Cita" to id_cita,
            "uid" to uid,
            "fecha" to fecha,
            "hora" to hora,
            "motivo" to motivo
        )
    }
}
