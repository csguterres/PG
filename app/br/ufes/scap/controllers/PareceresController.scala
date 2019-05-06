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
  
  def RegistrarParecerPre(idSolicitacao : Long) = Action {
    if (Global.isSecretario()){
      val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
      Ok(br.ufes.scap.views.html.registrarParecer(ManifestacaoForm.form, solicitacao))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  */
  def manifestarContraPre(idSolicitacao : Long) = Action {
    if (Global.isProfessor()){
      val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
            Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))

//      Ok(br.ufes.scap.views.html.manifestarContra(ManifestacaoForm.form, solicitacao))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  /*
  def registrarParecer(idSolicitacao : Long) = Action { implicit request =>
    ParecerForm.form.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addParecer(errorForm, Seq.empty[Parecer]))),
        data => {
          val solicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao), Duration.Inf)
          if (Global.isSecretario() || (solicitacao.get.idRelator == Global.SESSION_KEY)){
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val newParecer = Parecer(0, data.tipoParecer, idSolicitacao, 0, data.julgamento, data.motivo, dataAtual)   
              ParecerService.addParecer(newParecer).map(res =>
                Redirect(routes.PareceresController.index())
              )
          }else{
              SolicitacaoService.listAllSolicitacoesBySolicitante(0) map { solicitacoes =>
                Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
              } 
          }
    
        }
    )
    }
  
  def manifestarContra(idSolicitacao : Long) = Action { implicit request =>
    ManifestacaoForm.form.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addManifestacao(errorForm, Seq.empty[Parecer]))),
        data => {
          if (Global.isProfessor()){
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val newParecer = Parecer(0, "PROFESSOR", idSolicitacao, Global.SESSION_KEY, "CONTRA", data.motivo, dataAtual)   
              ParecerService.addParecer(newParecer).map(res =>
                Redirect(routes.PareceresController.index())
              )
          }else{
              SolicitacaoService.listAllSolicitacoesBySolicitante(0) map { solicitacoes =>
                Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
              } 
          }
    
        }
    )
    }
    * 
    */
}