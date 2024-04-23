package com.componentes.consultorioandrade.Model

data class Paciente(
    val uid: String = "",
    val tipoDocumento: String = "",
    val numeroDocumento: String = "",
    val nombreCompleto: String = "",
    val celular: String = "",
    val genero: String = "",
    val fechaNacimiento: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "tipoDocumento" to tipoDocumento,
            "numeroDocumento" to numeroDocumento,
            "nombreCompleto" to nombreCompleto,
            "celular" to celular,
            "genero" to genero,
            "fechaNacimiento" to fechaNacimiento
        )
    }
}