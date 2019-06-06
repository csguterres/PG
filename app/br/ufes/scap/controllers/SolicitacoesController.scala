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
  
  
  def definirBuscaForm = authenticatedUsuarioAction { implicit request =>
        val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
        Ok(br.ufes.scap.views.html.buscarSolicitacoes(BuscaForm.form, users))
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
  
  def listarSolicitacoes = authenticatedUsuarioAction { implicit request =>
       val solicitacoes = SolicitacaoService.listAllSolicitacoes
       Ok(br.ufes.scap.views.html.listSolicitacoes(solicitacoes))
  }

  def deleteSolicitacao(id: Long) = authenticatedProfessorAction { implicit request =>
      val sol = SolicitacaoService.getSolicitacao(id)
      if (AuthenticatorService.isAutor(sol.professor)){
        val newSolicitacao = SolicitacaoService.cancelaSolicitacao(sol)
        SolicitacaoService.update(newSolicitacao)
        EmailService.enviarEmailParaChefeCancelamento(id)
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
  
  def addSolicitacao() = authenticatedProfessorAction.async { implicit request =>
    SolicitacaoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addSolicitacao(errorForm))),
      data => {
        val iniAfast = new Timestamp(data.dataIniAfast.getTime())
        val fimAfast = new Timestamp(data.dataFimAfast.getTime())
        val iniEvento = new Timestamp(data.dataIniEvento.getTime())
        val fimEvento = new Timestamp(data.dataFimEvento.getTime())
        val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
        val newSolicitacao = Solicitacao(0, Global.SESSION_KEY, 0, dataAtual,
            iniAfast, fimAfast, iniEvento, fimEvento, 
            data.nomeEvento, data.cidade, data.onus, data.tipoAfastamento,
            StatusSolicitacao.Iniciada.toString(), "", iniEvento)
        SolicitacaoService.addSolicitacao(newSolicitacao)
        EmailService.enviarEmailParaTodos(data.nomeEvento)
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
          EmailService.enviarEmailParaRelator(idSolicitacao, UserService.getUser(data.idRelator).get)
          SolicitacaoService.mudaStatus(newSolicitacao,StatusSolicitacao.Liberada.toString())
          Future.successful(Ok(br.ufes.scap.views.html.menu(UserForm.form, UserService.getUser(Global.SESSION_KEY))))
          
     })
  }
  
}
