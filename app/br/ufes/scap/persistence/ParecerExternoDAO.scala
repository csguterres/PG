package br.ufes.scap.persistence

import br.ufes.scap.models.{ParecerDocumento}

trait ParecerDocumentoDAO extends BaseDAO {
  
  def findBySolicitacao(idSolicitacao : Long): Seq[ParecerDocumento]
  

}
