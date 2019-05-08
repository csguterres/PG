package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Mandato, Parecer, UserLoginForm, ManifestacaoForm, ParecerForm}
import play.api.mvc._
import br.ufes.scap.services.{ParecerService, SolicitacaoService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent._
import java.util.Calendar

class PareceresController extends Controller { 
/*
  def index = Action { implicit request =>
      if (Global.isSecretario()){
        val pareceres = Await.result(ParecerService.listAllPareceres, Duration.Inf)
        Ok(br.ufes.scap.views.html.listPareceres(ParecerForm.form, pareceres))
      }else{
    		Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }
  */
  def registrarParecerPre(idSolicitacao : Long) = Action { implicit request =>
    val solicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf)
    if (Global.isRelator(solicitacao.get.idRelator)){
      Ok(br.ufes.scap.views.html.addParecer(ParecerForm.form, solicitacao))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def manifestarContraPre(idSolicitacao : Long) = Action { implicit request =>
    if (Global.isProfessor()){
      val solicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf)
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
              (errorForm, Await.result(SolicitacaoService.getSolicitacao(idSolicitacao), Duration.Inf))
            )
        ),
        data => {
          val solicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao), Duration.Inf)
          if (Global.isRelator(solicitacao.get.idRelator)){
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val newParecer = Parecer(0, idSolicitacao, 0, data.julgamento, data.motivo, dataAtual)   
              var status : String = "REPROVADO-DI"
              if (data.julgamento.equals("FAVORAVEL")){
                status = "APROVADO-DI"
              }
              ParecerService.addParecer(newParecer).map(res =>
                Redirect(routes.SolicitacoesController.mudaStatus(solicitacao.get.id,status))
              )
          }else{
              SolicitacaoService.listAllSolicitacoesBySolicitante(0) map { solicitacoes =>
                Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
              } 
          }
    
        }
    )
    }
  
  def manifestarContra(idSolicitacao : Long) = Action.async { implicit request =>
    ManifestacaoForm.form.bindFromRequest.fold(
        errorForm => Future.successful
        (BadRequest
            (br.ufes.scap.views.html.addParecerContra
                (errorForm, Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf)
                )
            )
        ),
        data => {
          if (Global.isProfessor()){
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val newParecer = Parecer(0, idSolicitacao, Global.SESSION_KEY, "DESFAVORAVEL", data.motivo, dataAtual)   
              ParecerService.addParecer(newParecer).map(res =>
                Redirect(routes.LoginController.menu())
              )
          }else{
              SolicitacaoService.listAllSolicitacoesBySolicitante(0) map { solicitacoes =>
                Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
              } 
          }
    
        }
    )
    }
    
}