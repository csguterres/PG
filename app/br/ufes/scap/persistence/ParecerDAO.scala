package br.ufes.scap.persistence

import br.ufes.scap.models.{Parecer,ParecerForm,ParecerTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.data.Form
import play.api.data.Forms._

object Pareceres {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val pareceres = TableQuery[ParecerTableDef]
  
  def add(Parecer: Parecer): Future[String] = {
    dbConfig.db.run(pareceres += Parecer).map(res => 
      "Parecer successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(pareceres.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Parecer]] = {
    dbConfig.db.run(pareceres.filter(_.id === id).result.headOption)
  }
    
  def update(mandato: Parecer) : Future[String] = {
    dbConfig.db.run(pareceres.filter(_.id === mandato.id).update(mandato)).map(res => "Parecer successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findByProfessor(idProfessor : Long): Future[Seq[Parecer]] = {
    dbConfig.db.run(pareceres.filter(_.idProfessor === idProfessor).result)
  }
  
  def listAll: Future[Seq[Parecer]] = {
    dbConfig.db.run(pareceres.result)
  }


}
