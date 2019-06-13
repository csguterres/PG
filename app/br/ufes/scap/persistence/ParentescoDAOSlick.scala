package br.ufes.scap.persistence

import br.ufes.scap.models.{Parentesco, ParentescoForm, ParentescoTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent._
import scala.concurrent.duration._

object ParentescoDAOSlick extends BaseDAO {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val parentescos = TableQuery[ParentescoTableDef]
  
  def  save(p: Any) = {
    val parentesco = p.asInstanceOf[Parentesco]
    dbConfig.db.run(parentescos += parentesco).map(res => 
      "Parentesco successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long) = {
    dbConfig.db.run(parentescos.filter(_.id === id).delete)
  }

  def get(id: Long) : Option[Parentesco] = {
    Await.result(dbConfig.db.run(parentescos.filter(_.id === id).result.headOption),Duration.Inf)
  }
  
  def update(p: Any)  = {
    val parentesco = p.asInstanceOf[Parentesco]
    dbConfig.db.run(parentescos.filter(_.id === parentesco.id).update(parentesco)).map(res => "Parentesco successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findByProfessor(idProfessor : Long): Seq[Parentesco] = {
    Await.result(dbConfig.db.run(parentescos.filter(parentesco => parentesco.idProfessor1 === idProfessor || parentesco.idProfessor2 === idProfessor).result),Duration.Inf)
  }
  
  
  def listAll: Seq[Parentesco] = {
    Await.result(dbConfig.db.run(parentescos.result),Duration.Inf)
  }


}