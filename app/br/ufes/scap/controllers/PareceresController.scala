package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Mandato, Parecer, 
  UserLoginForm, ManifestacaoForm, ParecerForm, 
  StatusSolicitacao, TipoJulgamento}
import play.api.mvc._
import br.ufes.scap.services.{ParecerService, AuthenticatorService, ParecerDocumentoService, SolicitacaoService, EmailService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar

class PareceresController extends Controller { 

  def index(idSolicitacao : Long) = Action { 
      val pareceres = ParecerService.listAllPareceresBySolicitacao(idSolicitacao)
      val pareceresDoc = ParecerDocumentoService.listAllPareceresBySolicitacao(idSolicitacao)
      Ok(br.ufes.scap.views.html.listPareceres(pareceres, pareceresDoc))
  }
  
  def registrarParecerForm(idSolicitacao : Long) = Action { implicit request =>
    val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
    if (AuthenticatorService.isRelator(solicitacao.relator)){
      Ok(br.ufes.scap.views.html.addParecer(ParecerForm.form, solicitacao))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def verParecer(idParecer: Long) = Action{
    val parecer = ParecerService.getParecer(idParecer)
    Ok(br.ufes.scap.views.html.verParecer(parecer))
  }
  
  def manifestarContraForm(idSolicitacao : Long) = Action { implicit request =>
    if (AuthenticatorService.isProfessor()){
      val solicitacao = Some(SolicitacaoService.getSolicitacao(idSolicitacao))
      Ok(br.ufes.scap.views.html.addParecerContra(ManifestacaoForm.form, solicitacao))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def registrarParecer(idSolicitacao : Long) = Action.async { implicit request =>
    ParecerForm.form.bindFromRequest.fold(
        errorForm => Future.successful
        (
            BadRequest
            (br.ufes.scap.views.html.addParecer
              (errorForm, Some(SolicitacaoService.getSolicitacao(idSolicitacao)))
            )
        ),
        data => {
          val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val newParecer = Parecer(0, idSolicitacao, Global.SESSION_KEY, data.julgamento, data.motivo, dataAtual)   
              if (data.julgamento.equals(TipoJulgamento.AFavor.toString())){
                  SolicitacaoService.mudaStatus(solicitacao,StatusSolicitacao.AprovadaDI.toString())              
                  EmailService.enviarEmailParaSolicitante(idSolicitacao, solicitacao.professor.get, StatusSolicitacao.AprovadaDI.toString())
              }else{
                  if (data.julgamento.equals(TipoJulgamento.Contra.toString())){
                    SolicitacaoService.mudaStatus(solicitacao,StatusSolicitacao.Reprovada.toString())              
                    EmailService.enviarEmailParaSolicitante(idSolicitacao, solicitacao.professor.get, StatusSolicitacao.Reprovada.toString())
                  }
              }
              ParecerService.addParecer(newParecer)
              Future.successful(
                Redirect(routes.LoginController.menu)
              )
        }
    )
    }
  
  def manifestarContra(idSolicitacao : Long) = Action.async { implicit request =>
    ManifestacaoForm.form.bindFromRequest.fold(
        errorForm => Future.successful
        (BadRequest
            (br.ufes.scap.views.html.addParecerContra
                (errorForm, Some(SolicitacaoService.getSolicitacao(idSolicitacao))
                )
            )
        ),
        data => {
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val newParecer = Parecer(0, idSolicitacao, Global.SESSION_KEY, 
                  TipoJulgamento.Contra.toString(), data.motivo, dataAtual)
              val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
              EmailService.enviarEmailParaSolicitante(idSolicitacao, solicitacao.professor.get, "Manifestação Contrária")
              ParecerService.addParecer(newParecer)
              Future.successful(Redirect(routes.LoginController.menu()))
        }
    )
    }
    
}