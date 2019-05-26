package br.ufes.scap.persistence

import br.ufes.scap.models.{Solicitacao}
import scala.concurrent.Future

trait SolicitacaoDAO extends BaseDAO {

  def findBySolicitante(idProfessor : Long): Future[Seq[Solicitacao]] 
  
  def findByStatus(status : String): Future[Seq[Solicitacao]] 
  
  def listAll: Future[Seq[Solicitacao]] 

}
