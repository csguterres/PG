package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, ParecerDocumento, 
  UserLoginForm, ParecerDocumentoForm}
import play.api.mvc._
import br.ufes.scap.services.{ParecerDocumentoService, SolicitacaoService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent._
import java.util.Calendar
import java.nio.file.{Files, Paths}
import java.io.FileOutputStream;
import java.io.File;

class PareceresDocumentoController extends Controller { 

  def registrarParecerDocumentoForm(idSolicitacao : Long) = Action { implicit request =>
    val solicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao),Duration.Inf)
    if (Global.isSecretario()){
      Ok(br.ufes.scap.views.html.addParecerDocumento(ParecerDocumentoForm.form, solicitacao))
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
              (errorForm, Await.result(SolicitacaoService.getSolicitacao(idSolicitacao), Duration.Inf))
            )
        ),
        data => {
          val solicitacao = Await.result(SolicitacaoService.getSolicitacao(idSolicitacao), Duration.Inf)
          if (Global.isSecretario()){
              val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
              val byteArray = Files.readAllBytes(Paths.get(data.filePath))
              val newParecerDocumento = ParecerDocumento(0, idSolicitacao, data.tipo, data.julgamento, byteArray, dataAtual)   
              var status : String = "REPROVADA"
              if (data.julgamento.equals("FAVORAVEL")){
                status = "APROVADA-" + data.tipo
              }
              ParecerDocumentoService.addParecerDocumento(newParecerDocumento).map(res =>
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
  
    def verParecer(idParecer: Long) = Action{
      val parecer = Await.result(ParecerDocumentoService.getParecer(idParecer),Duration.Inf)
      Ok(br.ufes.scap.views.html.verParecerDocumento(parecer))
    }
  
    def DownloadFile(idParecer : Long) = Action {
          val parecerDocumento = Await.result(ParecerDocumentoService.getParecer(idParecer),Duration.Inf)
          val currentDirectory = new java.io.File(".").getCanonicalPath
          val output = new FileOutputStream(new File(currentDirectory + "/Pareceres/" + "PARECER-" + parecerDocumento.get.tipo));
          System.out.println("Getting file please be patient..");
    
          output.write(parecerDocumento.get.fileData);
          
          println("File writing complete !")
          Ok(br.ufes.scap.views.html.verParecerDocumento(parecerDocumento))
      }
  
  }