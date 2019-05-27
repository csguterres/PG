package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, ParecerDocumento, 
  UserLoginForm, ParecerDocumentoForm}
import play.api.mvc._
import br.ufes.scap.services.{ParecerDocumentoService, SolicitacaoService, EmailService, AuthenticatorService}
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
      Ok(br.ufes.scap.views.html.addParecerDocumento(ParecerDocumentoForm.form, Some(solicitacao)))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def registrarParecerDocumento(idSolicitacao : Long) = Action.async { implicit request =>
    ParecerDocumentoForm.form.bindFromRequest.fold(
        errorForm => Future.successful
        (
            BadRequest
            (br.ufes.scap.views.html.addParecerDocumento
              (errorForm, Some(SolicitacaoService.getSolicitacao(idSolicitacao)))
            )
        ),
        data => {
          val solicitacao = SolicitacaoService.getSolicitacao(idSolicitacao)
          if (AuthenticatorService.isSecretario()){
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val byteArray = Files.readAllBytes(Paths.get(data.filePath))
              val newParecerDocumento = ParecerDocumento(0, idSolicitacao, data.tipo, data.julgamento, byteArray, dataAtual)   
              var status : String = "REPROVADA"
              if (data.julgamento.equals("FAVORAVEL")){
                status = "APROVADA-" + data.tipo
              }
              EmailService.enviarEmailParaSolicitante(idSolicitacao, solicitacao.professor.get, status)
              ParecerDocumentoService.addParecerDocumento(newParecerDocumento)
              Future.successful(  Redirect(routes.SolicitacoesController.mudaStatus(solicitacao.id,status))
              )
          }else{
              Future.successful(  Ok(br.ufes.scap.views.html.erro(UserLoginForm.form)))            
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
          val output = new FileOutputStream(new File(currentDirectory + "/Pareceres/" + "PARECER-" + parecerDocumento.get.tipo));
          System.out.println("Getting file please be patient..");
    
          output.write(parecerDocumento.get.fileData);
          
          println("File writing complete !")
          Ok(br.ufes.scap.views.html.verParecerDocumento(parecerDocumento))
      }
  
  }