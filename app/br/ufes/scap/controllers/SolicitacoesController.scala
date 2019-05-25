package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Solicitacao, ManifestacaoForm, EncaminhamentoForm, UserLoginForm, SolicitacaoForm}
import play.api.mvc._
import br.ufes.scap.services.{SolicitacaoService, UserService, EmailService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent._

class SolicitacoesController extends Controller { 
  
  def listarSolicitacoes = Action { implicit request =>
      if (Global.isProfessor()){
        val solicitacoesIniciadas = Await.result(SolicitacaoService.listAllSolicitacoesByStatus("INICIADA"), Duration.Inf)
        val solicitacoesLiberadas = Await.result(SolicitacaoService.listAllSolicitacoesByStatus("LIBERADA"), Duration.Inf)
        val solicitacoes = SolicitacaoService.mergeListas(solicitacoesIniciadas, solicitacoesLiberadas)
         val userTipo = Global.SESSION_TIPO
         Ok(br.ufes.scap.views.html.listSolicitacoes(SolicitacaoForm.form, solicitacoes))
      
      }else{
        if (Global.isSecretario()){
            val solicitacoes = Await.result(SolicitacaoService.listAllSolicitacoes, Duration.Inf)
            Ok(br.ufes.scap.views.html.listSolicitacoes(SolicitacaoForm.form, solicitacoes))
        }else{
           Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
        }
      }
  }
  
  def minhasSolicitacoes = Action { implicit request =>
     val solicitacoes = Await.result(SolicitacaoService.listAllSolicitacoesBySolicitante(Global.SESSION_KEY), Duration.Inf)
     Ok(br.ufes.scap.views.html.listSolicitacoes(SolicitacaoForm.form, solicitacoes))
  }

  def deleteSolicitacao(id: Long) = Action { implicit request =>
      val sol = Await.result(SolicitacaoService.getSolicitacao(id),Duration.Inf)
      if (sol.get.idProfessor == Global.SESSION_KEY){
        val newSolicitacao = SolicitacaoService.cancelaSolicitacao(sol)
        SolicitacaoService.update(newSolicitacao)
        EmailService.enviarEmailParaChefeCancelamento(id)
            Redirect(routes.LoginController.menu())
      }else{
            Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }
  
  def verSolicitacao(id : Long) = Action { implicit request =>
    var userTipo = Global.SESSION_TIPO
    val oldSolicitacao = Await.result(SolicitacaoService.getSolicitacao(id), Duration.Inf)
    if(oldSolicitacao.get.idProfessor == Global.SESSION_KEY){
      userTipo = "AUTOR"
    }
    if(Global.isRelator(oldSolicitacao.get.idRelator) 
        && oldSolicitacao.get.tipoAfastamento.equals("INTERNACIONAL")){
      userTipo = "RELATOR"
    }
    if(Global.SESSION_CHEFE 
        && oldSolicitacao.get.tipoAfastamento.equals("INTERNACIONAL")){
      userTipo = "CHEFE"
    }
    val solicitacaoFull = SolicitacaoService.turnSolicitacaoIntoSolicitacaoFull(oldSolicitacao)
    Ok(br.ufes.scap.views.html.verSolicitacao(solicitacaoFull, userTipo))
  }
  
  def arquivar(id : Long)  = Action { 
    val oldSolicitacao = Await.result(SolicitacaoService.getSolicitacao(id), Duration.Inf)
    val solicitacao = SolicitacaoService.mudaStatus(oldSolicitacao, "ARQUIVADO")
    SolicitacaoService.update(solicitacao)
    Redirect(routes.LoginController.menu())
  }
  
  def mudaStatus(id : Long, status : String) = Action { 
    val oldSolicitacao = Await.result(SolicitacaoService.getSolicitacao(id), Duration.Inf)
    val solicitacao = SolicitacaoService.mudaStatus(oldSolicitacao, status)
    SolicitacaoService.update(solicitacao)
    Redirect(routes.LoginController.menu())
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
        EmailService.enviarEmailParaTodos(data.nomeEvento)
        SolicitacaoService.addSolicitacao(newSolicitacao).map(res =>
          Redirect(routes.SolicitacoesController.listarSolicitacoes())
        )
      }else{
        SolicitacaoService.listAllSolicitacoesBySolicitante(0) map { solicitacoes =>
          Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
        } 
      }
      })
  }
  
  def encaminharSolicitacaoForm(idSolicitacao : Long) = Action { implicit request =>
    if (Global.SESSION_CHEFE == true){
        val users = Await.result(UserService.listAllUsersByTipo("PROFESSOR"), Duration.Inf)
        val solicitacao = SolicitacaoService.turnSolicitacaoIntoSolicitacaoFull(Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf))
        Ok(br.ufes.scap.views.html.encaminharSolicitacao(EncaminhamentoForm.form, users, solicitacao))
    }else{
        Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def encaminharSolicitacao(idSolicitacao : Long) = Action.async { implicit request =>
    EncaminhamentoForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => 
          Future.successful
          (BadRequest
              (br.ufes.scap.views.html.encaminharSolicitacao
                  (errorForm, Await.result(UserService.listAllUsersByTipo("PROFESSOR"), Duration.Inf),
SolicitacaoService.turnSolicitacaoIntoSolicitacaoFull(Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf))
                  )
              )
          ),
        data => {
        if (Global.SESSION_CHEFE == true){
          val oldSolicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf)
          val newSolicitacao = SolicitacaoService.addRelator(oldSolicitacao, data.idRelator)
          EmailService.enviarEmailParaRelator(idSolicitacao, data.idRelator)
          SolicitacaoService.update(newSolicitacao).map(res =>
            Redirect(routes.SolicitacoesController.mudaStatus(newSolicitacao.id,"LIBERADA"))
          )
        }else{
          SolicitacaoService.listAllSolicitacoesBySolicitante(0) map { solicitacoes =>
            Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
          } 
        }
     })
  }
  
}