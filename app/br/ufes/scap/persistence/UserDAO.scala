package br.ufes.scap.persistence

import br.ufes.scap.models.User
import scala.concurrent.Future

trait UserDAO extends BaseDAO {
  
  def getByMatricula(matricula: String): Future[Option[User]]
  
  def listAll: Future[Seq[User]] 
  
  def listAllByTipo(tipo : String): Future[Seq[User]]

}