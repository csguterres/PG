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

object Solicitacoes {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val solicitacoes = TableQuery[SolicitacaoTableDef]
  
  def add(solicitacao: Solicitacao): Future[String] = {
    dbConfig.db.run(solicitacoes += solicitacao).map(res => 
      "Solicitacao successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(solicitacoes.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Solicitacao]] = {
    dbConfig.db.run(solicitacoes.filter(_.id === id).result.headOption)
  }
    
  def update(solicitacao: Solicitacao) : Future[String] = {
    dbConfig.db.run(solicitacoes.filter(_.id === solicitacao.id).update(solicitacao)).map(res => "Solicitacao successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findBySolicitante(idProfessor : Long): Future[Seq[Solicitacao]] = {
    dbConfig.db.run(solicitacoes.filter(_.idProfessor === idProfessor).result)
  }
  
  def findByStatus(status : String): Future[Seq[Solicitacao]] = {
    dbConfig.db.run(solicitacoes.filter(_.status === status).result)
  }
  
  def listAll: Future[Seq[Solicitacao]] = {
    dbConfig.db.run(solicitacoes.result)
  }


}
