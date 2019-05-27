package br.ufes.scap.persistence

import br.ufes.scap.models.User

trait UserDAO extends BaseDAO {
  
  def getByMatricula(matricula: String): Option[User]
    
  def listAllByTipo(tipo : String): Seq[User]

}