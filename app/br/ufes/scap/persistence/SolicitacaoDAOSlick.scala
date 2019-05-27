package br.ufes.scap.persistence

import br.ufes.scap.models.{Solicitacao,SolicitacaoForm, SolicitacaoTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.data.Form
import play.api.data.Forms._
import scala.concurrent._
import scala.concurrent.duration._

object SolicitacaoDAOSlick extends SolicitacaoDAO {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val solicitacoes = TableQuery[SolicitacaoTableDef]
  
  def  save(s: Any) = {
    val solicitacao = s.asInstanceOf[Solicitacao]  
    dbConfig.db.run(solicitacoes += solicitacao).map(res => 
      "Solicitacao successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long) = {
    dbConfig.db.run(solicitacoes.filter(_.id === id).delete)
  }

  def get(id: Long): Option[Solicitacao] = {
    Await.result(dbConfig.db.run(solicitacoes.filter(_.id === id).result.headOption),Duration.Inf)
  }
    
  def update(s: Any) = {
    val solicitacao = s.asInstanceOf[Solicitacao]  
    dbConfig.db.run(solicitacoes.filter(_.id === solicitacao.id).update(solicitacao)).map(res => "Solicitacao successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findById(id : Long): Seq[Solicitacao] = {
    Await.result(dbConfig.db.run(solicitacoes.filter(_.id === id).result),Duration.Inf)
  }
  
  def findBySolicitante(idProfessor : Long): Seq[Solicitacao] = {
    Await.result(dbConfig.db.run(solicitacoes.filter(_.idProfessor === idProfessor).result),Duration.Inf)
  }
  
  def findByRelator(idRelator : Long): Seq[Solicitacao] = {
    Await.result(dbConfig.db.run(solicitacoes.filter(_.idRelator === idRelator).result),Duration.Inf)
  }
  
  def findByStatus(status : String): Seq[Solicitacao] = {
    Await.result(dbConfig.db.run(solicitacoes.filter(_.status === status).result),Duration.Inf)
  }
  
  def listAll: Seq[Solicitacao] = {
    Await.result(dbConfig.db.run(solicitacoes.result),Duration.Inf)
  }


}
