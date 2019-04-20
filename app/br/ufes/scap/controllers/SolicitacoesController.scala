package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, Solicitacao, SolicitacaoForm}
import play.api.mvc._
import br.ufes.scap.services.SolicitacaoService
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat

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
  
  def addSolicitacao() = Action.async { implicit request =>
    SolicitacaoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addSolicitacao(errorForm, Seq.empty[Solicitacao]))),
      data => {
        val professorID = 1
        
        val iniAfast = new Timestamp(data.dataIniAfast.getTime())
        val fimAfast = new Timestamp(data.dataFimAfast.getTime())
        val iniEvento = new Timestamp(data.dataIniEvento.getTime())
        val fimEvento = new Timestamp(data.dataFimEvento.getTime())
        
        val newSolicitacao = Solicitacao(0, professorID,
            iniAfast, fimAfast, iniEvento, fimEvento, 
            data.nomeEvento, data.cidade, data.onus, data.tipoAfastamento,
            "INICIADA", "", iniEvento)
        SolicitacaoService.addSolicitacao(newSolicitacao).map(res =>
          Redirect(routes.SolicitacoesController.index())
        )
      })
  }
  
  /*  
  def editSolicitacao(id:Long) = Action.async { implicit request =>
    SolicitacaoService.getSolicitacao(id) map { solicitacao =>
      Ok(br.ufes.scap.views.html.editSolicitacao(SolicitacaoForm.form, solicitacao))
    }
  }
  
  
  def updateSolicitacao(id:Long) = Action.async { implicit request =>
    SolicitacaoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addUser(errorForm, Seq.empty[User]))),
      data => {
      val iniAfast = new Timestamp(data.dataIniAfast.getTime())
      val fimAfast = new Timestamp(data.dataFimAfast.getTime())
      val iniEvento = new Timestamp(data.dataIniEvento.getTime())
      val fimEvento = new Timestamp(data.dataFimEvento.getTime())
      val newSolicitacao = Solicitacao(0, professorID,
            iniAfast, fimAfast, iniEvento, fimEvento, 
            data.nomeEvento, data.cidade, data.onus, data.tipoAfastamento,
            "INICIADA", "", iniEvento)        
      SolicitacaoService.update(newSolicitacao).map(res =>
          Redirect(routes.SolicitacoesController.index())
        )
      })
  }
  */
}