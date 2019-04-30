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

case class Parecer(id: Long, idProfessor: Long, idSolicitacao : Long,
    julgamento: String, motivoIndeferimento : String, dataParecer : Timestamp)
    
case class ParecerFormData(dataParecer : Date,
    julgamento: String, motivoIndeferimento : String)
    
object ParecerForm {
  
  val form = Form(
    mapping(
      "dataParecer" -> date,
      "julgamento" -> nonEmptyText,
      "motivoIndeferimento" -> text
    )(ParecerFormData.apply)(ParecerFormData.unapply)      
			  .verifying("Erro: Data de inicio do afastamento posterior a data de fim do afastamento",  
			      s => motivo(s.julgamento, s.motivoIndeferimento))
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

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def idProfessor = column[Long]("idProfessor")
  def idSolicitacao = column[Long]("idSolicitacao")
  def dataParecer = column[Timestamp]("dataParacer")
  def julgamento = column[String]("julgamento")
  def motivoIndeferimento = column[String]("motivoIndeferimento")

  override def * =
    (id, idProfessor, idSolicitacao,  julgamento, 
        motivoIndeferimento, dataParecer)<>(Parecer.tupled, Parecer.unapply)
}