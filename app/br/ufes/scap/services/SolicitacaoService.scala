package br.ufes.scap.services

import br.ufes.scap.persistence.Solicitacoes
import br.ufes.scap.models.Solicitacao
import scala.concurrent.Future

object SolicitacaoService {

  def addSolicitacao(solicitacao: Solicitacao): Future[String] = {
    Solicitacoes.add(solicitacao)
  }

  def deleteSolicitacao(id: Long): Future[Int] = {
    Solicitacoes.delete(id)
  }

  def getSolicitacao(id: Long): Future[Option[Solicitacao]] = {
    Solicitacoes.get(id)
  }

  def listAllSolicitacoes: Future[Seq[Solicitacao]] = {
    Solicitacoes.listAll
  }
  
  def listAllSolicitacoesBySolicitante(idProfessor : Long): Future[Seq[Solicitacao]] = {
    Solicitacoes.findBySolicitante(idProfessor)
  }
  
  def update(solicitacao : Solicitacao): Future[String] = { 
    Solicitacoes.update(solicitacao)
  }
  

}
