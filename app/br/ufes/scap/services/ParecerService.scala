package br.ufes.scap.services

import br.ufes.scap.persistence.ParecerDAOSlick
import br.ufes.scap.models.Parecer

object ParecerService {

  def addParecer(parecer: Parecer) = {
    ParecerDAOSlick.save(parecer)
  }

  def deleteParecer(id: Long) = {
    ParecerDAOSlick.delete(id)
  }

  def getParecer(id: Long): Option[Parecer] = {
    ParecerDAOSlick.get(id)
  }

  def listAllPareceres: Seq[Parecer] = {
    ParecerDAOSlick.listAll
  }
  
  def listAllPareceresBySolicitacao(idSolicitacao : Long):Seq[Parecer] = {
    ParecerDAOSlick.findBySolicitacao(idSolicitacao)
  }
    
  def update(parecer : Parecer) = { 
    ParecerDAOSlick.update(parecer)
  }
  
}
