package br.ufes.scap.persistence

import br.ufes.scap.models.{Parecer}
import scala.concurrent.Future

trait ParecerDAO extends BaseDAO {

  def findBySolicitacao(idSolicitacao : Long): Future[Seq[Parecer]] 
  
  def findByProfessor(idProfessor : Long): Future[Seq[Parecer]] 
  
  def listAll: Future[Seq[Parecer]] 

}
