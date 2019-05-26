package br.ufes.scap.persistence

import br.ufes.scap.models.{User,UserForm, UserTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.data.Form
import play.api.data.Forms._

object Users extends UserDAO {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val users = TableQuery[UserTableDef]
  
  def getByMatricula(matricula: String): Future[Option[User]] = {
      dbConfig.db.run(users.filter(_.matricula === matricula).result.headOption)
  }
  
  def save(u: Any): Future[String] = {
    val user = u.asInstanceOf[User]  
    dbConfig.db.run(users += user).map(res => "User successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(users.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.id === id).result.headOption)
  }

  def update(u : Any) : Future[String] = {
    val user = u.asInstanceOf[User]  
    dbConfig.db.run(users.filter(_.id === user.id).update(user)).map(res => "User successfully added").recover {
        case ex: Exception => ex.getCause.getMessage
    }
  }

  def listAll: Future[Seq[User]] = {
    dbConfig.db.run(users.result)
  }
  
  def listAllByTipo(tipo : String): Future[Seq[User]] = {
    dbConfig.db.run(users.filter(_.tipo === tipo).result)
  }

}