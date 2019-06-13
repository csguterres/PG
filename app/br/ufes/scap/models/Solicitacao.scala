package br.ufes.scap.models

import slick.driver.MySQLDriver.api._
import play.api.data.Form
import play.api.data.Forms._
import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import java.sql.Timestamp
import br.ufes.scap.services.{ParentescoService, SharedServices}
import java.time.{LocalDate, LocalDateTime}

case class Solicitacao(id: Long, idProfessor : Long, 
    idRelator : Long, dataSolicitacao : Timestamp, 
    dataIniAfast : Timestamp, dataFimAfast : Timestamp, 
    dataIniEvento: Timestamp, dataFimEvento : Timestamp, 
    nomeEvento : String, cidade : String, onus : String,
    tipoAfastamento : String, status : String,
    motivoCancelamento : String)
    
   
case class SolicitacaoFull(id: Long, professor : User, 
    relator : Option[User], dataSolicitacao : Timestamp, 
    dataIniAfast : LocalDate, dataFimAfast : LocalDate, 
    dataIniEvento: LocalDate, dataFimEvento : LocalDate, 
    nomeEvento : String, cidade : String, onus : String,
    tipoAfastamento : String, status : String,
    motivoCancelamento : String)

case class EncaminhamentoFormData(idProfessor : Long, idRelator : Long)

case class SolicitacaoFormData(dataIniAfast : Date, dataFimAfast : 
    Date, dataIniEvento: Date, dataFimEvento : Date, 
    nomeEvento : String, cidade : String, onus : String,
    tipoAfastamento : String
    )

object EncaminhamentoForm {
  
  val form = Form(
      mapping(
          "idProfessor" -> longNumber,
          "idRelator" -> longNumber
      )(EncaminhamentoFormData.apply)(EncaminhamentoFormData.unapply)
      .verifying("Erro: Você não pode designar o próprio solicitante como relator", 
          s => ParentescoService.checaDiferente(s.idProfessor,s.idRelator))
      .verifying("Erro: Você não pode designar um membro da família do solicitante como relator", 
          s => ParentescoService.naoExisteParentesco(s.idProfessor,s.idRelator))
  )
}

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
			      s => SharedServices.checaData(s.dataIniAfast, s.dataFimAfast))
			  .verifying("Erro: Data de inicio do evento posterior a data de fim do evento", 
			      s => SharedServices.checaData(s.dataIniEvento, s.dataFimEvento))
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
   def status = column[String]("status")
   def motivoCancelamento = column[String]("motivoCancelamento")

   override def * =
    (id, idProfessor, idRelator, dataSolicitacao, 
        dataIniAfast, dataFimAfast, dataIniEvento, 
        dataFimEvento, nomeEvento, cidade, onus, 
        tipoAfastamento, status, 
        motivoCancelamento)<>(Solicitacao.tupled, Solicitacao.unapply)
}