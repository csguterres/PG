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

case class Parecer(id: Long, tipoParecer : String,
    idSolicitacao : Long, idProfessor: Long, 
    julgamento: String, motivo : String, dataParecer : Timestamp)

case class ManifestacaoFormData(motivo : String)

case class ParecerFormData(tipoParecer : String, 
    julgamento: String, motivo : String)

object ManifestacaoForm {

  val form = Form(
    mapping(
      "motivo" -> text
    )(ManifestacaoFormData.apply)(ManifestacaoFormData.unapply)      
			  .verifying("Erro: Necessário apresentar um motivo para se manifestar contra o afastamento",  
			      s => Global.preenchido(s.motivo))
  )
  
}

object ParecerForm {
  
  val form = Form(
    mapping(
      "tipoParecer" -> nonEmptyText,
      "julgamento" -> nonEmptyText,
      "motivo" -> text
    )(ParecerFormData.apply)(ParecerFormData.unapply)      
  )
  
  def motivo(julgamento : String, motivoIndeferimento : String): Boolean = {
    if (julgamento.equals("DESFAVORÁVEL") && motivoIndeferimento.equals(null)){
      false
    }else{
      true
    }
  }
  
}

class ParecerTableDef(tag: Tag) extends Table[Parecer](tag, "parecer") {

  def tipoParecer = column[String]("tipoParecer")
  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def idProfessor = column[Long]("idProfessor")
  def idSolicitacao = column[Long]("idSolicitacao")
  def dataParecer = column[Timestamp]("dataParacer")
  def julgamento = column[String]("julgamento")
  def motivo = column[String]("motivo")

  override def * =
    (id, tipoParecer, idSolicitacao, idProfessor, julgamento, 
        motivo, dataParecer)<>(Parecer.tupled, Parecer.unapply)
}