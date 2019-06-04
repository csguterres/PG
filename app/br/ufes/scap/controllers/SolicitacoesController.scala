package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Solicitacao,
SolicitacaoFull, ManifestacaoForm, EncaminhamentoForm, 
 SolicitacaoForm, StatusSolicitacao, 
TipoUsuario, BuscaForm, TipoAfastamento, TipoAcessorio}
import play.api.mvc._
import br.ufes.scap.services.{SolicitacaoService, UserService, 
  EmailService, AuthenticatorService, AuthenticatedUsuarioAction, 
  AuthenticatedProfessorAction, AuthenticatedSecretarioAction}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.util.Calendar
import java.text.SimpleDateFormat

class SolicitacoesController extends Controller { 
  
  
  def definirBuscaForm = Action { implicit request =>
        if (AuthenticatorService.isLoggedIn()){
            val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
            Ok(br.ufes.scap.views.html.buscarSolicitacoes(BuscaForm.form, users))
        }else{
           Ok(br.ufes.scap.views.html.erro())
        }
  }
  
  def definirBusca = Action.async { implicit request =>
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
  
  def listarSolicitacoes = Action { implicit request =>
        if (AuthenticatorService.isLoggedIn()){
            val solicitacoes = SolicitacaoService.listAllSolicitacoes
            Ok(br.ufes.scap.views.html.listSolicitacoes(solicitacoes))
        }else{
           Ok(br.ufes.scap.views.html.erro())
        }
  }

  def deleteSolicitacao(id: Long) = Action { implicit request =>
      val sol = SolicitacaoService.getSolicitacao(id)
      if (sol.professor.id == Global.SESSION_KEY){
        val newSolicitacao = SolicitacaoService.cancelaSolicitacao(sol)
        SolicitacaoService.update(newSolicitacao)
        EmailService.enviarEmailParaChefeCancelamento(id)
            Redirect(routes.LoginController.menu())
      }else{
            Ok(br.ufes.scap.views.html.erro())
      }
  }
  
  def verSolicitacao(id : Long) = Action { implicit request =>
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
  
  def arquivar(id : Long)  = Action {
    val oldSolicitacao = SolicitacaoService.getSolicitacao(id)
    val solicitacao = SolicitacaoService.mudaStatus(oldSolicitacao, StatusSolicitacao.Arquivada.toString())
    Redirect(routes.LoginController.menu())
  }
  
  def mudaStatus(id : Long, status : String) = Action { 
    val oldSolicitacao = SolicitacaoService.getSolicitacao(id)
    val solicitacao = SolicitacaoService.mudaStatus(oldSolicitacao, status)
    Redirect(routes.LoginController.menu())
  }
  
  def addSolicitacao() = Action.async { implicit request =>
    SolicitacaoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addSolicitacao(errorForm, Seq.empty[Solicitacao]))),
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
  
  def encaminharSolicitacaoForm(idSolicitacao : Long) = Action { implicit request =>
    if (AuthenticatorService.isChefe()){
        val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
        val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
        Ok(br.ufes.scap.views.html.encaminharSolicitacao(EncaminhamentoForm.form, users, solicitacao))
    }else{
        Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def encaminharSolicitacao(idSolicitacao : Long) = Action.async { implicit request =>
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
          SolicitacaoService.update(newSolicitacao)
          Future.successful(Redirect(routes.SolicitacoesController.mudaStatus(newSolicitacao.id,StatusSolicitacao.Liberada.toString()))
          )
     })
  }
  
}
