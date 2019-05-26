package br.ufes.scap.persistence

import br.ufes.scap.models.{Mandato}
import scala.concurrent.Future

trait MandatoDAO extends BaseDAO {
  
  def findByProfessor(idProfessor : Long): Future[Seq[Mandato]] 
  
  def listAll: Future[Seq[Mandato]] 

}
