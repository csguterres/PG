package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, UserForm, Solicitacao,
SolicitacaoFull, ManifestacaoForm, EncaminhamentoForm, 
 SolicitacaoForm, StatusSolicitacao,
TipoUsuario, BuscaForm, TipoAfastamento, TipoAcessorio}
import play.api.mvc._
import br.ufes.scap.services.{SolicitacaoService, UserService, 
  EmailService, AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import javax.inject.Inject

class SolicitacoesController @Inject()
(authenticatedUsuarioAction: AuthenticatedUsuarioAction,
    authenticatedSecretarioAction : AuthenticatedSecretarioAction,
    authenticatedProfessorAction : AuthenticatedProfessorAction,
    authenticatedChefeAction : AuthenticatedChefeAction) extends Controller { 
  
  
  def definirBuscaForm = authenticatedUsuarioAction { 
        val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
        Ok(br.ufes.scap.views.html.buscarSolicitacoes(BuscaForm.form, users))
  }
  
  def showAfastamentosByProfessor(idProfessor : Long) = authenticatedUsuarioAction {
    val afastamentos = SolicitacaoService.listAllSolicitacoesBySolicitante(idProfessor)
    Ok(br.ufes.scap.views.html.listSolicitacoes(afastamentos))
  }
  
  def definirBusca = authenticatedUsuarioAction.async { implicit request =>
        BuscaForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => 
          Future.successful
          (BadRequest(br.ufes.scap.views.html.buscarSolicitacoes(errorForm, UserService.listAllUsers))),
        data => {
          val solicitacoes = SolicitacaoService.busca(data.idProfessor, data.idRelator, data.status)
          Future.successful(Ok(br.ufes.scap.views.html.listSolicitacoes(solicitacoes)))
        }
        )
  }
  
  def listarSolicitacoes = authenticatedUsuarioAction { 
       val solicitacoes = SolicitacaoService.listAllSolicitacoes
       Ok(br.ufes.scap.views.html.listSolicitacoes(solicitacoes))
  }

  def cancelarSolicitacao(id: Long) = authenticatedProfessorAction { implicit request =>
      val sol = SolicitacaoService.getSolicitacao(id)
      if (AuthenticatorService.isAutor(sol.professor)){
        val newSolicitacao = SolicitacaoService.cancelaSolicitacao(sol)
        SolicitacaoService.update(newSolicitacao)
        EmailService.enviarEmailParaChefeCancelamento(newSolicitacao)
            Redirect(routes.LoginController.menu())
      }else{
            Ok(br.ufes.scap.views.html.erro())
      }
  }
  
  def verSolicitacao(id : Long) = authenticatedUsuarioAction { implicit request =>
    var userTipo = Global.SESSION_TIPO
    val solicitacao = SolicitacaoService.getSolicitacao(id)
    if(AuthenticatorService.isAutor(solicitacao.professor)){
      userTipo = TipoAcessorio.Autor.toString()
    }
    if(AuthenticatorService.isRelator(solicitacao.relator) 
        && solicitacao.tipoAfastamento.equals(TipoAfastamento.Internacional.toString())){
      userTipo = TipoAcessorio.Relator.toString()
    }
    if(AuthenticatorService.isChefe()
        && solicitacao.tipoAfastamento.equals(TipoAfastamento.Internacional.toString())){
      userTipo = TipoAcessorio.Chefe.toString()
    }
    if(AuthenticatorService.isSecretario()
        && solicitacao.tipoAfastamento.equals(TipoAfastamento.Internacional.toString())){
      userTipo = TipoUsuario.Sec.toString()
    }
    Ok(br.ufes.scap.views.html.verSolicitacao(solicitacao, userTipo))
  }
  
  def arquivar(id : Long)  = authenticatedSecretarioAction {
    val oldSolicitacao = SolicitacaoService.getSolicitacao(id)
    val solicitacao = SolicitacaoService.mudaStatus(oldSolicitacao, StatusSolicitacao.Arquivada.toString())
    Redirect(routes.LoginController.menu())
  }
  
  def addSolicitacaoForm() = authenticatedProfessorAction{
    Ok(br.ufes.scap.views.html.addSolicitacao(SolicitacaoForm.form))
  }
  
  def aprovar(id : Long) = authenticatedSecretarioAction {
    val sol = SolicitacaoService.getSolicitacao(id)
    SolicitacaoService.mudaStatus(sol, StatusSolicitacao.AprovadaDI.toString())
    EmailService.enviarEmailParaSolicitante(sol, StatusSolicitacao.AprovadaDI.toString())
    Redirect(routes.SolicitacoesController.listarSolicitacoes())
  }
  
  def reprovar(id : Long) = authenticatedSecretarioAction {
    val sol = SolicitacaoService.getSolicitacao(id)
    SolicitacaoService.mudaStatus(sol, StatusSolicitacao.Reprovada.toString())
    EmailService.enviarEmailParaSolicitante(sol, StatusSolicitacao.Reprovada.toString())
    Redirect(routes.SolicitacoesController.listarSolicitacoes())
  }
  
  def addSolicitacao() = authenticatedProfessorAction.async { implicit request =>
    SolicitacaoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addSolicitacao(errorForm))),
      solicitacaoForm => {
        val iniAfast = new Timestamp(solicitacaoForm.dataIniAfast.getTime())
        val fimAfast = new Timestamp(solicitacaoForm.dataFimAfast.getTime())
        val iniEvento = new Timestamp(solicitacaoForm.dataIniEvento.getTime())
        val fimEvento = new Timestamp(solicitacaoForm.dataFimEvento.getTime())
        val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
        val newSolicitacao = Solicitacao(0, Global.SESSION_KEY, 0, dataAtual,
            iniAfast, fimAfast, iniEvento, fimEvento, 
            solicitacaoForm.nomeEvento, solicitacaoForm.cidade, solicitacaoForm.onus, solicitacaoForm.tipoAfastamento,
            StatusSolicitacao.Iniciada.toString(), "")
        SolicitacaoService.addSolicitacao(newSolicitacao)
        EmailService.enviarEmailParaTodos()
        Future.successful(Redirect(routes.SolicitacoesController.listarSolicitacoes()))
      })
  }
  
  def encaminharSolicitacaoForm(idSolicitacao : Long) = authenticatedChefeAction { implicit request =>
    if (AuthenticatorService.isChefe()){
        val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
        val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
        Ok(br.ufes.scap.views.html.encaminharSolicitacao(EncaminhamentoForm.form, users, solicitacao))
    }else{
        Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def encaminharSolicitacao(idSolicitacao : Long) = authenticatedChefeAction.async { implicit request =>
    EncaminhamentoForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => 
          Future.successful
          (BadRequest
              (br.ufes.scap.views.html.encaminharSolicitacao
                  (errorForm, UserService.listAllUsersByTipo(TipoUsuario.Prof.toString()),
SolicitacaoService.getSolicitacao(idSolicitacao)
                  )
              )
          ),
        data => {
          val oldSolicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
          val newSolicitacao = SolicitacaoService.addRelator(oldSolicitacao, data.idRelator)
          EmailService.enviarEmailParaRelator(newSolicitacao, newSolicitacao.relator.get)
          SolicitacaoService.mudaStatus(newSolicitacao,StatusSolicitacao.Liberada.toString())
          Future.successful(Ok(br.ufes.scap.views.html.menu(UserForm.form, UserService.getUser(Global.SESSION_KEY))))
          
     })
  }
  
}
