package br.ufes.scap.models

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import slick.driver.MySQLDriver.api._
import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import java.sql.Timestamp
import java.time.LocalDate

case class Parecer(id: Long, 
    idSolicitacao : Long, idProfessor: Long, 
    julgamento: String, motivo : String, dataParecer : Timestamp)

case class ParecerFull(id: Long, 
    solicitacao : SolicitacaoFull, professor : User, 
    julgamento: String, motivo : String, dataParecer : LocalDate)
    
case class ManifestacaoFormData(motivo : String)

case class ParecerFormData(julgamento: String, motivo : String)

object ManifestacaoForm {

  val form = Form(
    mapping(
      "motivo" -> text
    )(ManifestacaoFormData.apply)(ManifestacaoFormData.unapply)      
  )
  
}

object ParecerForm {
  
  val form = Form(
    mapping(
      "julgamento" -> nonEmptyText,
      "motivo" -> text
    )(ParecerFormData.apply)(ParecerFormData.unapply)      
  )
  
}

class ParecerTableDef(tag: Tag) extends Table[Parecer](tag, "parecer") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def idProfessor = column[Long]("idProfessor")
  def idSolicitacao = column[Long]("idSolicitacao")
  def dataParecer = column[Timestamp]("dataParecer")
  def julgamento = column[String]("julgamento")
  def motivo = column[String]("motivo")

  override def * =
    (id, idSolicitacao, idProfessor, julgamento, 
        motivo, dataParecer)<>(Parecer.tupled, Parecer.unapply)
}