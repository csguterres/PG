package br.ufes.scap.services

import br.ufes.scap.persistence.ParecerDAOSlick
import br.ufes.scap.models.{Parecer, ParecerFull}

object ParecerService {

  def addParecer(parecer: Parecer) = {
    ParecerDAOSlick.save(parecer)
  }

  def deleteParecer(id: Long) = {
    ParecerDAOSlick.delete(id)
  }
  
  def getParecer(id: Long): ParecerFull = {
    turnParecerIntoParecerFull(ParecerDAOSlick.get(id).get)
  }

  def listAllPareceres: Seq[ParecerFull] = {
    return turnSeqParecerIntoSeqParecerFull(ParecerDAOSlick.listAll)
  }
  
  def listAllPareceresBySolicitacao(idSolicitacao : Long):Seq[ParecerFull] = {
    return turnSeqParecerIntoSeqParecerFull(ParecerDAOSlick.findBySolicitacao(idSolicitacao))
  }
    
  def update(parecer : Parecer) = { 
    ParecerDAOSlick.update(parecer)
  }
  
  def turnSeqParecerIntoSeqParecerFull(parentescos : Seq[Parecer]) : Seq[ParecerFull] = {
    var Pareceres : Seq[ParecerFull] = Seq()
    for (p <- parentescos){
       Pareceres = Pareceres :+ turnParecerIntoParecerFull(p)
    }
    return Pareceres
  }
    
  def turnParecerIntoParecerFull(oldParecer : Parecer): ParecerFull = {
    val prof = UserService.getUser(oldParecer.idProfessor).get ;
    val sol = SolicitacaoService.getSolicitacao(oldParecer.idSolicitacao) ;
    val dataParecer = SharedServices.getData(oldParecer.dataParecer)
    return ParecerFull(oldParecer.id, sol, prof, oldParecer.julgamento, oldParecer.motivo, dataParecer) 
  }
  
}
