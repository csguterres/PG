package br.ufes.scap.services

import br.ufes.scap.persistence.UserDAOSlick
import br.ufes.scap.models.User

object UserService {

  def addUser(user: User) = {
    UserDAOSlick.save(user)
  }

  def deleteUser(id: Long) = {
    UserDAOSlick.delete(id)
  }

  def getUser(id: Long): Option[User] = {
    UserDAOSlick.get(id)
  }

  def listAllUsers: Seq[User] = {
    UserDAOSlick.listAll
  }
  
  def listAllUsersByTipo(tipo: String): Seq[User] = {
    UserDAOSlick.listAllByTipo(tipo)
  }
  
  def update(user : User) = { 
    UserDAOSlick.update(user)
  }
  
  def getUserByMatricula(matricula : String): Option[User] = {
    UserDAOSlick.getByMatricula(matricula)
  }
  
  def checaMatriculaSenha(matricula : String, password : String): Boolean = {
    val usuario = getUserByMatricula(matricula)
    val usuarioReal = usuario
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
    if (usuario == None){ 
        true
    }else{
        false
    }  
  }
  
}
