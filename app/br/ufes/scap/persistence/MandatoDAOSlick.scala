package br.ufes.scap.persistence

import br.ufes.scap.models.{Mandato,MandatoForm, MandatoTableDef}
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

object MandatoDAOSlick extends MandatoDAO {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val mandatos = TableQuery[MandatoTableDef]
  
  @Override
  def save(m: Any) = {
    val mandato = m.asInstanceOf[Mandato]
    dbConfig.db.run(mandatos += mandato).map(res => 
      "Mandato successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  @Override
  def delete(id: Long) = {
    dbConfig.db.run(mandatos.filter(_.id === id).delete)
  }

  @Override
  def get(id: Long): Option[Mandato] = {
    Await.result(dbConfig.db.run(mandatos.filter(_.id === id).result.headOption),Duration.Inf)
  }

  @Override
  def update(m: Any) = {
    val mandato = m.asInstanceOf[Mandato]
    dbConfig.db.run(mandatos.filter(_.id === mandato.id).update(mandato)).map(res => "Mandato successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }
  
  def findByProfessor(idProfessor : Long): Seq[Mandato] = {
    Await.result(dbConfig.db.run(mandatos.filter(_.idProfessor === idProfessor).result),Duration.Inf)
  }
  
  def listAll: Seq[Mandato] = {
    Await.result(dbConfig.db.run(mandatos.result),Duration.Inf)
  }


}