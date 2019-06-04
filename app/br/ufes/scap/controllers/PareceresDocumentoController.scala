package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, ParecerDocumento, 
 ParecerDocumentoForm, 
  StatusSolicitacao, TipoJulgamento, Setor}
import play.api.mvc._
import br.ufes.scap.services.{ParecerDocumentoService, 
  SolicitacaoService, EmailService, AuthenticatorService, 
  AuthenticatedUsuarioAction, AuthenticatedProfessorAction, 
  AuthenticatedSecretarioAction}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.nio.file.{Files, Paths}
import java.io.FileOutputStream;
import java.io.File;

class PareceresDocumentoController extends Controller { 

  def registrarParecerDocumentoForm(idSolicitacao : Long) = Action { implicit request =>
    val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
    if (AuthenticatorService.isSecretario()){
      Ok(br.ufes.scap.views.html.addParecerDocumento(ParecerDocumentoForm.form, solicitacao))
    }else{
      Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def registrarParecerDocumento(idSolicitacao : Long) = Action.async { implicit request =>
    ParecerDocumentoForm.form.bindFromRequest.fold(
        errorForm => Future.successful
        (
            BadRequest
            (br.ufes.scap.views.html.addParecerDocumento
              (errorForm, SolicitacaoService.getSolicitacao(idSolicitacao))
            )
        ),
        data => {
          val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
          if (AuthenticatorService.isSecretario()){
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val byteArray = Files.readAllBytes(Paths.get(data.filePath))
              val newParecerDocumento = ParecerDocumento(0, idSolicitacao, data.tipo, data.julgamento, byteArray, dataAtual)   
              if (data.julgamento.equals(TipoJulgamento.Contra.toString())){
                  SolicitacaoService.mudaStatus(solicitacao,StatusSolicitacao.Reprovada.toString())
                  EmailService.enviarEmailParaSolicitante(solicitacao.id, solicitacao.professor, StatusSolicitacao.Reprovada.toString())
              }else{
                if (data.julgamento.equals(TipoJulgamento.AFavor.toString()) && data.tipo.equals(Setor.CT.toString())){
                  SolicitacaoService.mudaStatus(solicitacao,StatusSolicitacao.AprovadaCT.toString())
                  EmailService.enviarEmailParaSolicitante(solicitacao.id, solicitacao.professor, StatusSolicitacao.AprovadaCT.toString())
                }else{
                   if (data.julgamento.equals(TipoJulgamento.Contra.toString()) && data.tipo.equals(Setor.PRPPG.toString())){
                     SolicitacaoService.mudaStatus(solicitacao,StatusSolicitacao.AprovadaPRPPG.toString())
                     EmailService.enviarEmailParaSolicitante(solicitacao.id, solicitacao.professor, StatusSolicitacao.AprovadaPRPPG.toString())
                   }
                }
              }
              ParecerDocumentoService.addParecerDocumento(newParecerDocumento)
              Future.successful(  Redirect(routes.LoginController.menu())
              )
          }else{
              Future.successful(  Ok(br.ufes.scap.views.html.erro()))            
          }
    
        }
    )
    }
  
    def verParecer(idParecer: Long) = Action{
      val parecer = ParecerDocumentoService.getParecer(idParecer)
      Ok(br.ufes.scap.views.html.verParecerDocumento(parecer))
    }
  
    def DownloadFile(idParecer : Long) = Action {
          val parecerDocumento = ParecerDocumentoService.getParecer(idParecer)
          val currentDirectory = new java.io.File(".").getCanonicalPath
          val output = new FileOutputStream(new File(currentDirectory + "/Pareceres/" + "PARECER-" + parecerDocumento.tipo));
          System.out.println("Getting file please be patient..");
    
          output.write(parecerDocumento.fileData);
          
          println("File writing complete !")
          Ok(br.ufes.scap.views.html.verParecerDocumento(parecerDocumento))
      }
  
  }