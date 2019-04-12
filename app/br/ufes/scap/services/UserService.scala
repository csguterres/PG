package br.ufes.scap.services

import br.ufes.scap.persistence.Users
import br.ufes.scap.models.User
import scala.concurrent.Future

object UserService {

  def addUser(user: User): Future[String] = {
    Users.add(user)
  }

  def deleteUser(id: Long): Future[Int] = {
    Users.delete(id)
  }

  def getUser(id: Long): Future[Option[User]] = {
    Users.get(id)
  }

  def listAllUsers: Future[Seq[User]] = {
    Users.listAll
  }
  
  def listAllUsersByTipo(tipo: String): Future[Seq[User]] = {
    Users.listAllByTipo(tipo)
  }
  
  def update(user : User): Future[String] = { 
    Users.update(user)
  }
  
  def getUserByMatricula(matricula : String): Future[Seq[User]] = {
    Users.getByMatricula(matricula)
  }
}
