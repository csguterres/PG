package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, Solicitacao, SolicitacaoForm}
import play.api.mvc._
import br.ufes.scap.services.SolicitacaoService
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future

class SolicitacoesController extends Controller { 
  
  def index = Action.async { implicit request =>
    val username = 1 //request.session.get(br.ufes.scap.models.Global.SESSION_USERNAME_KEY)
    if (username == 1){
      SolicitacaoService.listAllSolicitacoes map { solicitacoes =>
        Ok(br.ufes.scap.views.html.listSolicitacoes(SolicitacaoForm.form, solicitacoes))
      }
    }else{
        SolicitacaoService.listAllSolicitacoesBySolicitante(username) map { solicitacoes =>
          Ok(br.ufes.scap.views.html.listSolicitacoes(SolicitacaoForm.form, solicitacoes))
        } 
     }
  }

  def deleteSolicitacao(id: Long) = Action.async { implicit request =>
    SolicitacaoService.deleteSolicitacao(id) map { res =>
      Redirect(routes.SolicitacoesController.index())
    }
  }
  
}