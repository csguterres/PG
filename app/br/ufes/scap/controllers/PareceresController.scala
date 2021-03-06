package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Mandato, Parecer, 
  ManifestacaoForm, ParecerForm, 
  StatusSolicitacao, TipoJulgamento}
import play.api.mvc._
import br.ufes.scap.services.{ParecerService, AuthenticatorService, 
  ParecerDocumentoService, SolicitacaoService, EmailService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar

class PareceresController  @Inject() 
(authenticatedUsuarioAction: AuthenticatedUsuarioAction,
    authenticatedSecretarioAction : AuthenticatedSecretarioAction, 
    authenticatedProfessorAction : AuthenticatedProfessorAction)  extends Controller { 

  def index(idSolicitacao : Long) = authenticatedUsuarioAction { 
      val pareceres = ParecerService.listAllPareceresBySolicitacao(idSolicitacao)
      val pareceresDoc = ParecerDocumentoService.listAllPareceresBySolicitacao(idSolicitacao)
      Ok(br.ufes.scap.views.html.listPareceres(pareceres, pareceresDoc))
  }
  

  def verParecer(idParecer: Long) = authenticatedUsuarioAction{
    val parecer = ParecerService.getParecer(idParecer)
    val relator = parecer.solicitacao.relator
    var ehRelator = false
    if (relator != None){
      if (relator.get.id == parecer.professor.id){
        ehRelator = true 
      }
    }
    Ok(br.ufes.scap.views.html.verParecer(parecer, ehRelator))
  }
  
  def manifestarContraForm(idSolicitacao : Long) = authenticatedProfessorAction { implicit request =>
      val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
      Ok(br.ufes.scap.views.html.addParecerContra(ManifestacaoForm.form, solicitacao))
  }
  
  def registrarParecerForm(idSolicitacao : Long) = authenticatedProfessorAction { implicit request =>
    val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
    if (AuthenticatorService.isRelator(solicitacao.relator)){
      Ok(br.ufes.scap.views.html.addParecer(ParecerForm.form, solicitacao))
    }else{
      Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def registrarParecer(idSolicitacao : Long) = authenticatedProfessorAction.async { implicit request =>
    ParecerForm.form.bindFromRequest.fold(
        errorForm => Future.successful
        (
            BadRequest
            (br.ufes.scap.views.html.addParecer
              (errorForm, SolicitacaoService.getSolicitacao(idSolicitacao))
            )
        ),
        data => {
          val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val newParecer = Parecer(0, idSolicitacao, Global.SESSION_KEY, data.julgamento, data.motivo, dataAtual)   
              if (data.julgamento.equals(TipoJulgamento.AFavor.toString())){
                  SolicitacaoService.mudaStatus(solicitacao,StatusSolicitacao.AprovadaDI.toString())
                  ParecerService.addParecer(newParecer)
                  EmailService.enviarEmailParaSolicitante(solicitacao, StatusSolicitacao.AprovadaDI.toString())
              }else{
                  //if (data.julgamento.equals(TipoJulgamento.Contra.toString())){
                    SolicitacaoService.mudaStatus(solicitacao,StatusSolicitacao.Reprovada.toString())              
                    ParecerService.addParecer(newParecer)
                    EmailService.enviarEmailParaSolicitante(solicitacao, StatusSolicitacao.Reprovada.toString())
                  //}
              }
              Future.successful(
                Redirect(routes.LoginController.menu)
              )
        }
    )
    }
  
  def manifestarContra(idSolicitacao : Long) = authenticatedProfessorAction.async { implicit request =>
    ManifestacaoForm.form.bindFromRequest.fold(
        errorForm => Future.successful
        (BadRequest
            (br.ufes.scap.views.html.addParecerContra
                (errorForm, SolicitacaoService.getSolicitacao(idSolicitacao))
            )
        ),
        data => {
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val newParecer = Parecer(0, idSolicitacao, Global.SESSION_KEY, 
                  TipoJulgamento.Contra.toString(), data.motivo, dataAtual)
              val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
              EmailService.enviarEmailParaSolicitante(solicitacao, "Bloqueada devido a uma manifestação contrária, o resultado final será decidido na próxima reunião do DI")
              ParecerService.addParecer(newParecer)
              SolicitacaoService.mudaStatus(solicitacao, StatusSolicitacao.Bloqueada.toString())
              Future.successful(Redirect(routes.LoginController.menu()))
        }
    )
    }
    
}