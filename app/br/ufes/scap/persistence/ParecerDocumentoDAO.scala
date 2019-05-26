package br.ufes.scap.persistence

import br.ufes.scap.models.{ParecerDocumento}
import scala.concurrent.Future

trait ParecerDocumentoDAO extends BaseDAO {
  
  def findBySolicitacao(idSolicitacao : Long): Future[Seq[ParecerDocumento]] 
  
  def listAll: Future[Seq[ParecerDocumento]] 


}
