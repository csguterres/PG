package br.ufes.scap.services

import br.ufes.scap.persistence.ParecerDocumentoDAOSlick
import br.ufes.scap.models.ParecerDocumento

object ParecerDocumentoService {

  def addParecerDocumento(parecer: ParecerDocumento) = {
    ParecerDocumentoDAOSlick.save(parecer)
  }

  def deleteParecerDocumento(id: Long) = {
    ParecerDocumentoDAOSlick.delete(id)
  }

  def getParecer(id: Long): Option[ParecerDocumento] = {
    ParecerDocumentoDAOSlick.get(id)
  }

  def listAllPareceres: Seq[ParecerDocumento] = {
    ParecerDocumentoDAOSlick.listAll
  }
  
  def listAllPareceresBySolicitacao(idSolicitacao : Long): Seq[ParecerDocumento] = {
    ParecerDocumentoDAOSlick.findBySolicitacao(idSolicitacao)
  }
    
  def update(parecer : ParecerDocumento) = { 
    ParecerDocumentoDAOSlick.update(parecer)
  }
  
}