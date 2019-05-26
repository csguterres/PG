package br.ufes.scap.services

import br.ufes.scap.persistence.PareceresDocumento
import br.ufes.scap.models.ParecerDocumento
import scala.concurrent._
import scala.concurrent.duration._
import java.util.Date

object ParecerDocumentoService {

  def addParecerDocumento(parecer: ParecerDocumento): Future[String] = {
    PareceresDocumento.save(parecer)
  }

  def deleteParecerDocumento(id: Long): Future[Int] = {
    PareceresDocumento.delete(id)
  }

  def getParecer(id: Long): Future[Option[ParecerDocumento]] = {
    PareceresDocumento.get(id)
  }

  def listAllPareceres: Future[Seq[ParecerDocumento]] = {
    PareceresDocumento.listAll
  }
  
  def listAllPareceresBySolicitacao(idSolicitacao : Long): Future[Seq[ParecerDocumento]] = {
    PareceresDocumento.findBySolicitacao(idSolicitacao)
  }
    
  def update(parecer : ParecerDocumento): Future[String] = { 
    PareceresDocumento.update(parecer)
  }
  
   def enviarEmailParaSolicitante(idRelator : Long) = {
      val mensagem = "Você foi designado como relator de uma solicitacao. \nAcesse o SCAP na seção 'Listar Solicitações em que sou Relator' para deferir seu parecer." 
      val assunto = "Relatoria"
      EmailService.sendEmail(idRelator, assunto, mensagem)
    }
  
}