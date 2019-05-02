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
    idRelator : Long, dataSolicitacao : Timestamp, 
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
			      s => Global.checaData(s.dataIniAfast, s.dataFimAfast))
			  .verifying("Erro: Data de inicio do evento posterior a data de fim do evento", 
			      s => Global.checaData(s.dataIniEvento, s.dataFimEvento))
  )
  

}

class SolicitacaoTableDef(tag: Tag) extends Table[Solicitacao](tag, "solicitacao") {
   
   def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
   def idProfessor = column[Long]("idProfessor")
   def idRelator = column[Long]("idRelator")
   def dataSolicitacao = column[Timestamp]("dataSolicitacao")
   def dataIniAfast = column[Timestamp]("dataIniAfast")
   def dataFimAfast = column[Timestamp]("dataFimAfast")
   def dataIniEvento = column[Timestamp]("dataIniEvento")
   def dataFimEvento = column[Timestamp]("dataFimEvento")
   def nomeEvento = column[String]("nomeEvento")
   def cidade = column[String]("cidade")
   def onus = column[String]("onus")
   def tipoAfastamento = column[String]("tipoAfastamento")
   def statusSolicitacao = column[String]("statusSolicitacao")
   def motivoCancelamento = column[String]("motivoCancelamento")
   def dataJulgamentoAfast = column[Timestamp]("dataJulgamentoAfast")

   override def * =
    (id, idProfessor, idRelator, dataSolicitacao, 
        dataIniAfast, dataFimAfast, dataIniEvento, 
        dataFimEvento, nomeEvento, cidade, onus, 
        tipoAfastamento, statusSolicitacao, 
        motivoCancelamento, dataJulgamentoAfast)<>(Solicitacao.tupled, Solicitacao.unapply)
}