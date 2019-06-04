package br.ufes.scap.services

import br.ufes.scap.persistence.ParecerDocumentoDAOSlick
import br.ufes.scap.models.{ParecerDocumento, ParecerDocumentoFull}

object ParecerDocumentoService {

  def addParecerDocumento(parecer: ParecerDocumento) = {
    ParecerDocumentoDAOSlick.save(parecer)
  }

  def deleteParecerDocumento(id: Long) = {
    ParecerDocumentoDAOSlick.delete(id)
  }

  def getParecer(id: Long): ParecerDocumentoFull = {
    turnParecerIntoParecerFull(ParecerDocumentoDAOSlick.get(id).get)
  }

  def listAllPareceres: Seq[ParecerDocumentoFull] = {
    turnSeqParecerIntoSeqParecerFull(ParecerDocumentoDAOSlick.listAll)
  }
  
  def listAllPareceresBySolicitacao(idSolicitacao : Long): Seq[ParecerDocumentoFull] = {
    turnSeqParecerIntoSeqParecerFull(ParecerDocumentoDAOSlick.findBySolicitacao(idSolicitacao))
  }
    
  def update(parecer : ParecerDocumento) = { 
    ParecerDocumentoDAOSlick.update(parecer)
  }
  
    def turnSeqParecerIntoSeqParecerFull(parentescos : Seq[ParecerDocumento]) : Seq[ParecerDocumentoFull] = {
    var Pareceres : Seq[ParecerDocumentoFull] = Seq()
    for (p <- parentescos){
       Pareceres = Pareceres :+ turnParecerIntoParecerFull(p)
    }
    return Pareceres
  }
    
  def turnParecerIntoParecerFull(oldParecer : ParecerDocumento): ParecerDocumentoFull = {
    val sol = SolicitacaoService.getSolicitacao(oldParecer.idSolicitacao) ;
    val dataParecer = SolicitacaoService.getData(oldParecer.dataParecer)
    return ParecerDocumentoFull(oldParecer.id, sol, oldParecer.tipo, oldParecer.julgamento, oldParecer.fileData, dataParecer) 
  }
  
}