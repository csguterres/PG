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

case class Solicitacao(id: Long, idProfessor : Long, 
    dataIniAfast : Timestamp, dataFimAfast : Timestamp, 
    dataIniEvento: Timestamp, dataFimEvento : Timestamp, 
    nomeEvento : String, cidade : String, onus : String,
    tipoAfastamento : String, statusSolicitacao : String,
    motivoCancelamento : String, dataJulgamentoAfast : Timestamp
    )
    
case class SolicitacaoFormData(dataIniAfast : Date, dataFimAfast : 
    Date, dataIniEvento: Date, dataFimEvento : Date, 
    nomeEvento : String, cidade : String, onus : String,
    tipoAfastamento : String
    )

object SolicitacaoForm {

  val form = Form(
    mapping(
      "dataIniAfast" -> date,
      "dataFimAfast" -> date,
      "dataIniEvento" -> date,
      "dataFimEvento" -> date,
      "nomeEvento" -> nonEmptyText,
      "cidade" -> nonEmptyText,
      "onus" -> nonEmptyText,
      "tipoAfastamento" -> nonEmptyText
    )(SolicitacaoFormData.apply)(SolicitacaoFormData.unapply)      
			  .verifying("Erro: Data de inicio do afastamento posterior a data de fim do afastamento",  
			      s => checaData(s.dataIniAfast, s.dataFimAfast))
			  .verifying("Erro: Data de inicio do evento posterior a data de fim do evento", 
			      s => checaData(s.dataIniEvento, s.dataFimEvento))
  )
  
  def checaData(dataInicio : Date, dataFim : Date): Boolean = {
    if (dataInicio.after(dataFim)){
      false
    }else{
      true
    }
  }
}

class SolicitacaoTableDef(tag: Tag) extends Table[Solicitacao](tag, "solicitacao") {
   
   def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
   def idProfessor = column[Long]("id professor")
   def dataIniAfast = column[Timestamp]("data inicio afastamento")
   def dataFimAfast = column[Timestamp]("data fim afastamento")
   def dataIniEvento = column[Timestamp]("data inicio evento")
   def dataFimEvento = column[Timestamp]("data fim evento")
   def nomeEvento = column[String]("nome do evento")
   def cidade = column[String]("cidade")
   def onus = column[String]("onus")
   def tipoAfastamento = column[String]("tipo do afastamento")
   def statusSolicitacao = column[String]("status da solicitacao")
   def motivoCancelamento = column[String]("motivo do cancelamento")
   def dataJulgamentoAfast = column[Timestamp]("data fim evento")

   override def * =
    (id, idProfessor, dataIniAfast, dataFimAfast, dataIniEvento, 
        dataFimEvento, nomeEvento, cidade, onus, 
        tipoAfastamento, statusSolicitacao, 
        motivoCancelamento, dataJulgamentoAfast)<>(Solicitacao.tupled, Solicitacao.unapply)
}