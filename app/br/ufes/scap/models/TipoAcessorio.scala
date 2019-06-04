package br.ufes.scap.models

object TipoAcessorio extends Enumeration {
  type TipoAcessorio = Value
  val Todos = Value("TODOS")
  val Chefe = Value("CHEFE")
  val Relator = Value("RELATOR")
  val Autor = Value("AUTOR")
}