package br.ufes.scap.services

import br.ufes.scap.persistence.PareceresDocumento
import br.ufes.scap.models.ParecerDocumento
import scala.concurrent._
import scala.concurrent.duration._
import java.util.Date

object ParecerDocumentoService {

  def addParecerDocumento(parecer: ParecerDocumento): Future[String] = {
    PareceresDocumento.add(parecer)
  }

  def deleteParecerDocumento(id: Long): Future[Int] = {
    PareceresDocumento.delete(id)
  }

  def getParecerDocumento(id: Long): Future[Option[ParecerDocumento]] = {
    PareceresDocumento.get(id)
  }

  def listAllPareceresDocumento: Future[Seq[ParecerDocumento]] = {
    PareceresDocumento.listAll
  }
  
  def listAllPareceresDocumentoBySolicitacao(idSolicitacao : Long): Future[Seq[ParecerDocumento]] = {
    PareceresDocumento.findBySolicitacao(idSolicitacao)
  }
    
  def update(parecer : ParecerDocumento): Future[String] = { 
    PareceresDocumento.update(parecer)
  }
  
}