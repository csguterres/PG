package br.ufes.scap.models

object TipoJulgamento extends Enumeration {
  type TipoJulgamento = Value
  val AFavor = Value("FAVORÁVEL")
  val Contra = Value("DESFAVORÁVEL")
}