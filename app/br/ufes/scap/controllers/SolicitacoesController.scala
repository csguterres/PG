package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, Solicitacao, UserLoginForm, SolicitacaoForm}
import play.api.mvc._
import br.ufes.scap.services.SolicitacaoService
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent._

class SolicitacoesController extends Controller { 
  
  def index = Action { implicit request =>
    if (Global.isLoggedIn()){
      if (Global.isSecretario()){
        val solicitacoes = Await.result(SolicitacaoService.listAllSolicitacoes, Duration.Inf)
        Ok(br.ufes.scap.views.html.listSolicitacoes(SolicitacaoForm.form, solicitacoes))
      }else{
        val solicitacoes = Await.result(SolicitacaoService.listAllSolicitacoesBySolicitante(Global.SESSION_KEY), Duration.Inf)
        Ok(br.ufes.scap.views.html.listSolicitacoes(SolicitacaoForm.form, solicitacoes))
      }
    }else{
       Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
     }
  }

  def deleteSolicitacao(id: Long) = Action { implicit request =>
    if(Global.isSecretario()){
      SolicitacaoService.deleteSolicitacao(id)
      Redirect(routes.SolicitacoesController.index())
    }else{
      val sol = Await.result(SolicitacaoService.getSolicitacao(id),Duration.Inf)
      if (sol.get.idProfessor == Global.SESSION_KEY){
        SolicitacaoService.deleteSolicitacao(id)
        Redirect(routes.SolicitacoesController.index())
      }else{
        Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
    }
  }
  
  def addSolicitacao() = Action.async { implicit request =>
    SolicitacaoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addSolicitacao(errorForm, Seq.empty[Solicitacao]))),
      data => {
      if (Global.isLoggedIn() && !Global.isSecretario()){
        val iniAfast = new Timestamp(data.dataIniAfast.getTime())
        val fimAfast = new Timestamp(data.dataFimAfast.getTime())
        val iniEvento = new Timestamp(data.dataIniEvento.getTime())
        val fimEvento = new Timestamp(data.dataFimEvento.getTime())
        
        val newSolicitacao = Solicitacao(0, Global.SESSION_KEY,
            iniAfast, fimAfast, iniEvento, fimEvento, 
            data.nomeEvento, data.cidade, data.onus, data.tipoAfastamento,
            "INICIADA", "", iniEvento)
        SolicitacaoService.addSolicitacao(newSolicitacao).map(res =>
          Redirect(routes.SolicitacoesController.index())
        )
      }else{
        SolicitacaoService.listAllSolicitacoesBySolicitante(0) map { solicitacoes =>
          Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
        } 
      }
      })
  }
 
}