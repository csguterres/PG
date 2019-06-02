package br.ufes.scap.models

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import java.sql.Timestamp
import java.util.Date
import java.time.LocalDate

case class ParecerDocumento(id: Long, idSolicitacao : Long, tipo: String, julgamento : String, 
    fileData: Array[Byte], dataParecer : Timestamp)
    
case class ParecerDocumentoFull(id: Long, solicitacao : Solicitacao, tipo: String, julgamento : String, 
    fileData: Array[Byte], dataParecer : LocalDate)
    
case class ParecerDocumentoFormData(tipo: String, 
    julgamento : String, filePath: String)
    
object ParecerDocumentoForm {
  
  val form = Form(
    mapping(
      "tipo" -> nonEmptyText,
      "julgamento" -> nonEmptyText,
      "filePath" -> text
    )(ParecerDocumentoFormData.apply)(ParecerDocumentoFormData.unapply)      
  )

}

class ParecerDocumentoTableDef(tag: Tag) extends Table[ParecerDocumento](tag, "parecer_documento") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def idSolicitacao = column[Long]("idSolicitacao")
  def julgamento = column[String]("julgamento")
  def tipo = column[String]("tipo")
  def fileData = column[Array[Byte]]("fileData")
  def dataParecer = column[Timestamp]("dataParecer")

  override def * =
    (id, idSolicitacao,  tipo, julgamento,
       fileData , dataParecer)<>(ParecerDocumento.tupled, ParecerDocumento.unapply)
}

