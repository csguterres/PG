package br.ufes.scap.models

object TipoUsuario extends Enumeration {
  type TipoUsuario = Value
  val Prof = Value("PROFESSOR")
  val Sec = Value("SECRETARIO")

}