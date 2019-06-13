package br.ufes.scap.persistence

import br.ufes.scap.models.{Parecer, ParecerForm, ParecerTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent._
import scala.concurrent.duration._

object ParecerDAOSlick extends ParecerDAO {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  val pareceres = TableQuery[ParecerTableDef]
  
  @Override
  def  save(p: Any) {
    val parecer = p.asInstanceOf[Parecer]
    dbConfig.db.run(pareceres += parecer).map(res => 
      "Parecer successfully added").recover {
      case ex: Exception => 
        println(ex.printStackTrace())
        ex.getClass().getName()
    }
  }

  @Override
  def delete(id: Long) = {
    dbConfig.db.run(pareceres.filter(_.id === id).delete)
  }

  @Override
  def get(id: Long): Option[Parecer] = {
    Await.result(dbConfig.db.run(pareceres.filter(_.id === id).result.headOption),Duration.Inf)
  }
  
 @Override
 def update(p: Any) = {
    val parecer = p.asInstanceOf[Parecer]
    dbConfig.db.run(pareceres.filter(_.id === parecer.id).update(parecer)).map(res => "Parecer successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findBySolicitacao(idSolicitacao : Long): Seq[Parecer] = {
    Await.result(dbConfig.db.run(pareceres.filter(_.idSolicitacao === idSolicitacao).result),Duration.Inf)
  }
  
  def findByProfessor(idProfessor : Long): Seq[Parecer] = {
    Await.result(dbConfig.db.run(pareceres.filter(_.idProfessor === idProfessor).result),Duration.Inf)
  }
  
  
  def listAll: Seq[Parecer] = {
    Await.result(dbConfig.db.run(pareceres.result),Duration.Inf)
  }


}