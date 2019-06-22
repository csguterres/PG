package br.ufes.scap.models

object Onus extends Enumeration {
  type Onus = Value
  val Total = Value("TOTAL")
  val Parcial = Value("PARCIAL")
  val Inexistente = Value("INEXISTENTE") ;

}