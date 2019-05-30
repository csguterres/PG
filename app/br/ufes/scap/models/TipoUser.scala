package br.ufes.scap.models

object TipoUser extends Enumeration {
  type TipoUser = Value
  val Prof = Value("PROFESSOR")
  val Sec = Value("SECRETARIO")
  val Chefe = Value("CHEFE")
  val Relator = Value("RELATOR")
  val Autor = Value("AUTOR")

}