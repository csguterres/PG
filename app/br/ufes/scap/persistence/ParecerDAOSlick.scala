package br.ufes.scap.persistence

import br.ufes.scap.models.{Parecer, ParecerForm, ParecerTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.data.Form
import play.api.data.Forms._

object Pareceres extends ParecerDAO {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val pareceres = TableQuery[ParecerTableDef]
  
  @Override
  def  save(p: Any): Future[String] = {
    val parecer = p.asInstanceOf[Parecer]
    dbConfig.db.run(pareceres += parecer).map(res => 
      "Parecer successfully added").recover {
      case ex: Exception => 
        println(ex.printStackTrace())
        ex.getClass().getName()
    }
  }

  @Override
  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(pareceres.filter(_.id === id).delete)
  }

  @Override
  def get(id: Long): Future[Option[Parecer]] = {
    dbConfig.db.run(pareceres.filter(_.id === id).result.headOption)
  }
  
 @Override
 def update(p: Any) : Future[String] = {
    val parecer = p.asInstanceOf[Parecer]
    dbConfig.db.run(pareceres.filter(_.id === parecer.id).update(parecer)).map(res => "Parecer successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findBySolicitacao(idSolicitacao : Long): Future[Seq[Parecer]] = {
    dbConfig.db.run(pareceres.filter(_.idSolicitacao === idSolicitacao).result)
  }
  
  def findByProfessor(idProfessor : Long): Future[Seq[Parecer]] = {
    dbConfig.db.run(pareceres.filter(_.idProfessor === idProfessor).result)
  }
  
  
  def listAll: Future[Seq[Parecer]] = {
    dbConfig.db.run(pareceres.result)
  }


}