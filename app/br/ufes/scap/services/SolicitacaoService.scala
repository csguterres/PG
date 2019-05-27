package br.ufes.scap.services

import br.ufes.scap.persistence.SolicitacaoDAOSlick
import br.ufes.scap.models.{Solicitacao, SolicitacaoFull, User}

object SolicitacaoService {

  def addSolicitacao(solicitacao: Solicitacao) = {
    SolicitacaoDAOSlick.save(solicitacao)
  }

  def deleteSolicitacao(id: Long) = {
    SolicitacaoDAOSlick.delete(id)
  }

  def getSolicitacao(id: Long): SolicitacaoFull = {
    this.turnSolicitacaoIntoSolicitacaoFull(SolicitacaoDAOSlick.get(id))
  }
  
  def getSolicitacaoNormal(id: Long): Option[Solicitacao] = {
    SolicitacaoDAOSlick.get(id)
  }

  def listAllSolicitacoes: Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.listAll
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def turnSeqSolIntoSeqSolFull(solicitacoes : Seq[Solicitacao]): Seq[SolicitacaoFull] = {
    var solicitacoesFull : Seq[SolicitacaoFull] = Seq()
    for (s <- solicitacoes){
      solicitacoesFull = solicitacoesFull :+ this.turnSolicitacaoIntoSolicitacaoFull(Some(s))
    }
    return solicitacoesFull
  }
  
  def listAllSolicitacoesBySolicitante(idProfessor : Long): Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.findBySolicitante(idProfessor)
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def listAllSolicitacoesByRelator(idRelator : Long): Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.findByRelator(idRelator)
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def listAllSolicitacoesByStatus(status : String): Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.findByStatus(status)
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def update(solicitacao : SolicitacaoFull) = { 
    val sol = this.turnSolicitacaoFullIntoSolicitacao(Some(solicitacao))
    SolicitacaoDAOSlick.update(Some(sol))
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
  
  def mudaStatus(oldSolicitacao : Option[SolicitacaoFull], status : String): SolicitacaoFull = {
    val solicitacao = SolicitacaoFull(oldSolicitacao.get.id, oldSolicitacao.get.professor,
              oldSolicitacao.get.relator, oldSolicitacao.get.dataSolicitacao, 
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
    val professor = UserService.getUser(oldSolicitacao.get.idProfessor)
    var relator : Option[User] = None
    if (oldSolicitacao.get.idRelator == 0){
      relator = None
    }else{
      relator = UserService.getUser(oldSolicitacao.get.idRelator)
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
  
  
  def turnSolicitacaoFullIntoSolicitacao
  (oldSolicitacao : Option[SolicitacaoFull]): Solicitacao = {
    val idProfessor = oldSolicitacao.get.professor.get.id
    var idRelator : Long = 0
    if (oldSolicitacao.get.relator == None){
      idRelator = 0
    }else{
      idRelator = oldSolicitacao.get.relator.get.id
    }
    val solicitacao = Solicitacao(oldSolicitacao.get.id, idProfessor,
              idRelator, oldSolicitacao.get.dataSolicitacao, 
              oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
              oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
              oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
              oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
              oldSolicitacao.get.status,oldSolicitacao.get.motivoCancelamento, 
              oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }
  
  def addRelator(oldSolicitacao : Option[SolicitacaoFull], idRelator : Long): SolicitacaoFull = {
    val relator = UserService.getUser(idRelator)
    val solicitacao = SolicitacaoFull(oldSolicitacao.get.id, 
      oldSolicitacao.get.professor,
      relator, oldSolicitacao.get.dataSolicitacao, 
      oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
      oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
      oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
      oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
      oldSolicitacao.get.status,oldSolicitacao.get.motivoCancelamento, 
      oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }
  
    def cancelaSolicitacao(oldSolicitacao : Option[SolicitacaoFull]): SolicitacaoFull = {
    val solicitacao = SolicitacaoFull(oldSolicitacao.get.id, 
      oldSolicitacao.get.professor,
      oldSolicitacao.get.relator, oldSolicitacao.get.dataSolicitacao, 
      oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
      oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
      oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
      oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
      "CANCELADO",oldSolicitacao.get.motivoCancelamento, 
      oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }

}
