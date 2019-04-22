package br.ufes.scap.services

import br.ufes.scap.persistence.Users
import br.ufes.scap.models.User
import scala.concurrent._
import scala.concurrent.duration._

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
  
  def getUserByMatricula(matricula : String): Future[Option[User]] = {
    Users.getByMatricula(matricula)
  }
  
  def checaMatriculaSenha(matricula : String, password : String): Boolean = {
    val usuario = getUserByMatricula(matricula)
    val usuarioReal = Await.result(usuario,Duration.Inf)
    if (usuarioReal == None){ 
        false
    }else{
        if(usuarioReal.get.password.equals(password)){
          true
        }else{
          false
        }
    }  
  }
  
  def checaMatricula(matricula : String): Boolean = {
    val usuario = getUserByMatricula(matricula)
    if (Await.result(usuario,10 seconds) == None){ 
        true
    }else{
        false
    }  
  }
  
}
