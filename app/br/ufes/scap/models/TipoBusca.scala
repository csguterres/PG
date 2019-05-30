package br.ufes.scap.models

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

object TipoBusca extends Enumeration {
  type TipoBusca = Value
  val status = Value("STATUS")
  val prof = Value("PROFESSOR")
}

case class BuscaFormData(idProfessor : Long, status : String)

object BuscaForm {
  val form = Form(
  mapping(
      "idProfessor" -> longNumber,
      "status" -> text
      )(BuscaFormData.apply)(BuscaFormData.unapply)
      )
}
