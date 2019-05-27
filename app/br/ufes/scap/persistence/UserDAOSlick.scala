package br.ufes.scap.persistence

import br.ufes.scap.models.{User,UserForm, UserTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.data.Form
import play.api.data.Forms._
import scala.concurrent._
import scala.concurrent.duration._

object UserDAOSlick extends UserDAO {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val users = TableQuery[UserTableDef]
  
  def getByMatricula(matricula: String): Option[User] = {
      Await.result(dbConfig.db.run(users.filter(_.matricula === matricula).result.headOption),Duration.Inf)
  }
  
  def save(u: Any) = {
    val user = u.asInstanceOf[User]  
    dbConfig.db.run(users += user).map(res => "User successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long) = {
    dbConfig.db.run(users.filter(_.id === id).delete)
  }

  def get(id: Long): Option[User] = {
    Await.result(dbConfig.db.run(users.filter(_.id === id).result.headOption),Duration.Inf)
  }

  def update(u : Any) = {
    val user = u.asInstanceOf[User]  
    dbConfig.db.run(users.filter(_.id === user.id).update(user)).map(res => "User successfully added").recover {
        case ex: Exception => ex.getCause.getMessage
    }
  }

  def listAll: Seq[User] = {
     Await.result(dbConfig.db.run(users.result),Duration.Inf)
  }
  
  def listAllByTipo(tipo : String): Seq[User] = {
      Await.result(dbConfig.db.run(users.filter(_.tipo === tipo).result),Duration.Inf)
  }

}