package br.ufes.scap.models

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import slick.driver.MySQLDriver.api._
import br.ufes.scap.services.{UserService, ParentescoService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

case class Parentesco(id: Long, idProfessor1 : Long, idProfessor2 : Long)

case class ParentescoFull(id : Long, professor1 : User, professor2 : User)

case class ParentescoFormData(idProfessor1 : Long, idProfessor2 : Long)

object ParentescoForm{
  
  val form = Form(
      mapping(
          "idProfessor1" -> longNumber,
          "idProfessor2" -> longNumber
        )(ParentescoFormData.apply)(ParentescoFormData.unapply)
        .verifying("Erro: Escolha um segundo professor(a) diferente do primeiro", s => ParentescoService.checaDiferente(s.idProfessor1, s.idProfessor2))
        .verifying("Erro: Parentesco já registrado", s => ParentescoService.naoExisteParentesco(s.idProfessor1, s.idProfessor2))
        )
        
  }

class ParentescoTableDef(tag: Tag) extends Table[Parentesco](tag, "parentesco") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def idProfessor1 = column[Long]("idProfessor1")
  def idProfessor2 = column[Long]("idProfessor2")

  override def * =
    (id, idProfessor1, idProfessor2) <>(Parentesco.tupled, Parentesco.unapply)
}