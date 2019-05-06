package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Solicitacao, ManifestacaoForm, EncaminhamentoForm, UserLoginForm, SolicitacaoForm}
import play.api.mvc._
import br.ufes.scap.services.{SolicitacaoService, UserService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent._

class SolicitacoesController extends Controller { 
  
  def index = Action { implicit request =>
      val solicitacoes = Await.result(SolicitacaoService.listAllSolicitacoes, Duration.Inf)
      if (Global.isLoggedIn()){
         val userTipo = Global.SESSION_TIPO
         Ok(br.ufes.scap.views.html.listSolicitacoes(SolicitacaoForm.form, solicitacoes, userTipo))
      }else{
          Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }

  def deleteSolicitacao(id: Long) = Action.async { implicit request =>
      val sol = Await.result(SolicitacaoService.getSolicitacao(id),Duration.Inf)
      if (sol.get.idProfessor == Global.SESSION_KEY){
        val newSolicitacao = SolicitacaoService.cancelaSolicitacao(sol)
        SolicitacaoService.deleteSolicitacao(id)
        SolicitacaoService.update(newSolicitacao).map(res =>
            Redirect(routes.LoginController.menu())
        )
      }else{
          SolicitacaoService.listAllSolicitacoesBySolicitante(0) map { solicitacoes =>
            Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
          }       
      }
  }
  
  def verSolicitacao(id : Long) = Action { implicit request =>
    var userTipo = Global.SESSION_TIPO
    val oldSolicitacao = Await.result(SolicitacaoService.getSolicitacao(id), Duration.Inf)
    if(oldSolicitacao.get.idProfessor == Global.SESSION_KEY){
      userTipo = "AUTOR"
    }
    val solicitacaoFull = SolicitacaoService.turnSolicitacaoIntoSolicitacaoFull(oldSolicitacao)
    Ok(br.ufes.scap.views.html.verSolicitacao(solicitacaoFull, userTipo))
  }
  
  def addSolicitacao() = Action.async { implicit request =>
    SolicitacaoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addSolicitacao(errorForm, Seq.empty[Solicitacao]))),
      data => {
      if (Global.isProfessor()){
        val iniAfast = new Timestamp(data.dataIniAfast.getTime())
        val fimAfast = new Timestamp(data.dataFimAfast.getTime())
        val iniEvento = new Timestamp(data.dataIniEvento.getTime())
        val fimEvento = new Timestamp(data.dataFimEvento.getTime())
        val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
        val newSolicitacao = Solicitacao(0, Global.SESSION_KEY, 0, dataAtual,
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
  
  def encaminharSolicitacaoPre(idSolicitacao : Long) = Action { implicit request =>
    if (Global.isPresidenteOuVice(Global.SESSION_KEY)){
        val users = Await.result(UserService.listAllUsersByTipo("PROFESSOR"), Duration.Inf)
        val solicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf)
        val user = Await.result(UserService.getUser(solicitacao.get.idProfessor),Duration.Inf)
        Ok(br.ufes.scap.views.html.encaminharSolicitacao(EncaminhamentoForm.form, users, solicitacao, user))
    }else{
        Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def encaminharSolicitacao(idSolicitacao : Long) = Action.async { implicit request =>
      EncaminhamentoForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.encaminharSolicitacao(errorForm, Seq.empty[User], None, None))),
        data => {
        if (Global.isPresidenteOuVice(Global.SESSION_KEY)){
          val oldSolicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf)
          val newSolicitacao = SolicitacaoService.addRelator(oldSolicitacao, data.idRelator)
          SolicitacaoService.update(newSolicitacao).map(res =>
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