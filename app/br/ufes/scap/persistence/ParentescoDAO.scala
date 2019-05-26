package br.ufes.scap.persistence

import br.ufes.scap.models.{Parentesco, ParentescoForm, ParentescoTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.data.Form
import play.api.data.Forms._

object Parentescos extends BaseDAO {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val parentescos = TableQuery[ParentescoTableDef]
  
  @Override
  def  save(p: Any): Future[String] = {
    val parentesco = p.asInstanceOf[Parentesco]
    dbConfig.db.run(parentescos += parentesco).map(res => 
      "Parentesco successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  @Override
  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(parentescos.filter(_.id === id).delete)
  }

  @Override
  def get(id: Long): Future[Option[Parentesco]] = {
    dbConfig.db.run(parentescos.filter(_.id === id).result.headOption)
  }
  
  @Override
  def update(p: Any) : Future[String] = {
    val parentesco = p.asInstanceOf[Parentesco]
    dbConfig.db.run(parentescos.filter(_.id === parentesco.id).update(parentesco)).map(res => "Parentesco successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findByProfessor(idProfessor : Long): Future[Seq[Parentesco]] = {
    dbConfig.db.run(parentescos.filter(parentesco => parentesco.idProfessor1 === idProfessor || parentesco.idProfessor2 === idProfessor).result)
  }
  
  
  def listAll: Future[Seq[Parentesco]] = {
    dbConfig.db.run(parentescos.result)
  }


}
