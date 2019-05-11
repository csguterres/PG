package br.ufes.scap.services

import br.ufes.scap.persistence.Solicitacoes
import br.ufes.scap.models.{Solicitacao, SolicitacaoFull, User}
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent._

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
  
  def listAllSolicitacoesByStatus(status : String): Future[Seq[Solicitacao]] = {
    Solicitacoes.findByStatus(status)
  }
  
  def update(solicitacao : Solicitacao): Future[String] = { 
    Solicitacoes.update(solicitacao)
  }
  
  def mergeListas(listSolicitacao1 : Seq[Solicitacao], listSolicitacao2 : Seq[Solicitacao]) : Seq[Solicitacao] = {
      var solicitacoes : Seq[Solicitacao] = Seq()
      for (s <- listSolicitacao1){
        solicitacoes = solicitacoes :+ s
      }
      for (s <- listSolicitacao2){
        solicitacoes = solicitacoes :+ s
      }
      return solicitacoes
  }
  
  def mudaStatus(oldSolicitacao : Option[Solicitacao], status : String): Solicitacao = {
    val solicitacao = Solicitacao(oldSolicitacao.get.id, oldSolicitacao.get.idProfessor,
              oldSolicitacao.get.idRelator, oldSolicitacao.get.dataSolicitacao, 
              oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
              oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
              oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
              oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
              status, oldSolicitacao.get.motivoCancelamento, 
              oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }
  
  def turnSolicitacaoIntoSolicitacaoFull
  (oldSolicitacao : Option[Solicitacao]): SolicitacaoFull = {
    val professor = Await.result(UserService.getUser(oldSolicitacao.get.idProfessor),Duration.Inf)
    var relator : Option[User] = None
    if (oldSolicitacao.get.idRelator == 0){
      relator = None
    }else{
      relator = Await.result(UserService.getUser(oldSolicitacao.get.idRelator),Duration.Inf)
    }
    val solicitacao = SolicitacaoFull(oldSolicitacao.get.id, professor,
              relator, oldSolicitacao.get.dataSolicitacao, 
              oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
              oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
              oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
              oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
              oldSolicitacao.get.status,oldSolicitacao.get.motivoCancelamento, 
              oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }
  
  def addRelator(oldSolicitacao : Option[Solicitacao], idRelator : Long): Solicitacao = {
    val solicitacao = Solicitacao(oldSolicitacao.get.id, 
      oldSolicitacao.get.idProfessor,
      idRelator, oldSolicitacao.get.dataSolicitacao, 
      oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
      oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
      oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
      oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
      oldSolicitacao.get.status,oldSolicitacao.get.motivoCancelamento, 
      oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }
  
    def cancelaSolicitacao(oldSolicitacao : Option[Solicitacao]): Solicitacao = {
    val solicitacao = Solicitacao(oldSolicitacao.get.id, 
      oldSolicitacao.get.idProfessor,
      oldSolicitacao.get.idRelator, oldSolicitacao.get.dataSolicitacao, 
      oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
      oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
      oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
      oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
      "CANCELADO",oldSolicitacao.get.motivoCancelamento, 
      oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }

}
