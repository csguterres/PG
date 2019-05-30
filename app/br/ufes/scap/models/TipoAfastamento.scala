package br.ufes.scap.models

object TipoAfastamento extends Enumeration {
  type TipoAfastamento = Value
  val Nacional = Value("NACIONAL")
  val Internacional = Value("INTERNACIONAL")
}