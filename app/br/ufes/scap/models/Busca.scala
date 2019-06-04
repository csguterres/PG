package br.ufes.scap.models

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

case class BuscaFormData(idProfessor : Long, idRelator : Long, status : String)

object BuscaForm {
  val form = Form(
  mapping(
      "idProfessor" -> longNumber,
      "idRelator" -> longNumber,
      "status" -> text
      )(BuscaFormData.apply)(BuscaFormData.unapply)
      )
}
