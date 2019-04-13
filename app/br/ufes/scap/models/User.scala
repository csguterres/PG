package br.ufes.scap.models

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import slick.driver.MySQLDriver.api._
import br.ufes.scap.services.UserService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

case class User(id: Long, nome: String, matricula: String, email: String, password : String, tipo: String)

case class UserFormData(nome: String, matricula: String, email: String, password: String, tipo: String)

object UserForm {

  val form = Form(
    mapping(
      "nome" -> nonEmptyText,
      "matricula" -> nonEmptyText,
      "email" -> email,
	"password" -> nonEmptyText,	
	"tipo" -> nonEmptyText
    )(UserFormData.apply)(UserFormData.unapply)      
			  .verifying("Matricula ja cadastrada",  s => checaMatricula(s.matricula)
  ))

  def checaMatricula(matricula : String): Boolean = {
    val numeroDeMatriculas = UserService.getUserByMatricula(matricula).map(_.size)
    if (Await.result(numeroDeMatriculas,10 seconds) == 0){ 
        true
    }else{
        false
    }  
  }
  
}
  
class UserTableDef(tag: Tag) extends Table[User](tag, "user") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def nome = column[String]("nome")
  def matricula = column[String]("matricula")
  def email = column[String]("email")
  def password = column[String]("password")
  def tipo = column[String]("tipo")

  override def * =
    (id, nome, matricula, email, password, tipo) <>(User.tupled, User.unapply)
}

