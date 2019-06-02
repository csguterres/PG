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
import br.ufes.scap.services.MandatoService
import java.lang.Object 
import java.time.LocalDate

case class Mandato (id: Long, idProfessor: Long, cargo : String,
    dataIniMandato: Timestamp, dataFimMandato : Timestamp)
    
case class MandatoFull (id: Long, professor : User, cargo : String,
    dataIniMandato: LocalDate, dataFimMandato : LocalDate)
    
case class MandatoFormData(idProfessor : Long, cargo : String, 
    dataIniMandato : Date, dataFimMandato : Date)
    

object MandatoForm{
  
  val form = Form(
      mapping(
          "idProfessor" -> longNumber,
          "cargo" -> nonEmptyText,
          "dataIniMandato" -> date,
          "dataFimMandato" -> date
        )(MandatoFormData.apply)(MandatoFormData.unapply)
            .verifying("Erro: Data de inicio do mandato posterior a data de fim do mandato", 
			      s => Global.checaData(s.dataIniMandato, s.dataFimMandato))  
			      .verifying("Erro: Já há outro professor exercendo mandato na data prevista", 
			      s => MandatoService.checaDataOutros(s.dataIniMandato, s.dataFimMandato, s.cargo))  

			  )
}

class MandatoTableDef(tag: Tag) extends Table[Mandato](tag, "mandato") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def idProfessor = column[Long]("idProfessor")
  def cargo = column[String]("cargo")
  def dataIniMandato = column[Timestamp]("dataIniMandato")
  def dataFimMandato = column[Timestamp]("dataFimMandato")

  override def * =
    (id, idProfessor, cargo, dataIniMandato, dataFimMandato) <>(Mandato.tupled, Mandato.unapply)
}