package br.ufes.scap.models

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import slick.driver.MySQLDriver.api._
import br.ufes.scap.services.UserService
import scala.concurrent.ExecutionContext.Implicits.global


case class User(id: Long, nome: String, matricula: String, email: String, password : String, tipo: String)

case class UserFormData(nome: String, matricula: String, email: String, password: String, tipo: String)
    
case class UserLoginFormData(matricula : String, password : String)

case class UserEditFormData(nome: String, email: String, password: String)

object UserEditForm{
  
  val form = Form(
      mapping(
          "nome" -> nonEmptyText,
          "email" -> email,
          "password" -> nonEmptyText
        )(UserEditFormData.apply)(UserEditFormData.unapply)
        )
}
object UserLoginForm{
  
  val form = Form(
      mapping(
      "matricula" -> nonEmptyText,
      "password" -> nonEmptyText
      )(UserLoginFormData.apply)(UserLoginFormData.unapply)
       .verifying("Matrícula ou senha inválidos",  s => UserService.checaMatriculaSenha(s.matricula, s.password))
       )
}

object UserForm {

  val form = Form(
    mapping(
      "nome" -> nonEmptyText,
      "matricula" -> nonEmptyText,
      "email" -> email,
	"password" -> nonEmptyText,	
	"tipo" -> nonEmptyText
    )(UserFormData.apply)(UserFormData.unapply)      
			  .verifying("Matricula ja cadastrada",  s => UserService.checaMatricula(s.matricula)
  ))
  
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

