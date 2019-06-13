package br.ufes.scap.services

import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import br.ufes.scap.models.{Global,User,SolicitacaoFull}
import scala.concurrent.duration._
import scala.concurrent._

object EmailService {
 
   def sendEmail(user : User, assunto : String, message : String) =  {
      try{
          val sentTo = user.email 
          val email = new SimpleEmail();
          email.setHostName("smtp.gmail.com");
          email.setSmtpPort(465);
          email.setAuthenticator(new DefaultAuthenticator(Global.SESSION_EMAIL, Global.SESSION_PASS));
          email.setSSLOnConnect(true);
          email.setFrom(Global.SESSION_EMAIL);
          email.setSubject(assunto);
          email.setMsg(message);
          email.addTo(sentTo);
          email.send();
      }catch {
          case ex : Exception =>
              ex.printStackTrace()
      }
   }
   
  def enviarEmailParaRelator(sol : SolicitacaoFull, relator : User) = {
      val mensagem = "Você foi designado como relator de uma solicitação de afastamento (ID = " + sol.id + ") do(a) professor(a) " + sol.professor.nome + "para o evento " + sol.nomeEvento + "\nAcesse o SCAP para deferir seu parecer." 
      val assunto = "Você foi designado para ser relator"
      sendEmail(relator, assunto, mensagem)
    }
          
    def enviarEmailParaChefeCancelamento(sol : SolicitacaoFull) = {
      val chefe = UserService.getUser(Global.CHEFE_ID)
      val mensagem = "A solicitação de afastamento (ID = " + sol.id + ") do(a) professor(a) " + sol.professor.nome + "para o evento " + sol.nomeEvento + "foi cancelada pelo autor.\nAcesse o SCAP para ver mais detalhes."
      val assunto = "Uma solicitação foi cancelada"
      sendEmail(chefe.get, assunto, mensagem)
    }
    
    def enviarEmailParaSolicitante(sol : SolicitacaoFull, status : String) = {
      val mensagem = "Sua solicitação de afastamento (ID = " + sol.id + ") para o evento " + sol.nomeEvento +
      "teve o status alterada para" + status + ".\nAcesse o SCAP para ver mais detalhes." 
      val assunto = "Atualização no status da sua solicitação de afastamento"
      sendEmail(sol.professor, assunto, mensagem)
    }
    
    def enviarEmailParaTodos() = {
      val users = UserService.listAllUsersByTipo("PROFESSOR")
      val mensagem = "Uma nova solicitação de afastamento foi cadastrada no sistema. \nAcesse o SCAP para ver mais detalhes." 
      val assunto = "Nova solicitação de afastamento cadastrada no sistema."
      for (user <- users){
        sendEmail(user, assunto, mensagem)
      }
    }
   
}