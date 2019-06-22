package br.ufes.scap.models

object Cargo extends Enumeration {
  type Cargo = Value
  val Pres = Value("CHEFE")
  val Vice = Value("SUBCHEFE")

}