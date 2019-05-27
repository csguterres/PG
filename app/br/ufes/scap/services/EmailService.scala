package br.ufes.scap.services

import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import br.ufes.scap.models.{Global,User}
import scala.concurrent.duration._
import scala.concurrent._

object EmailService {
 
   def sendEmail(user : User, assunto : String, message : String) =  {
      
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
   }
   
  def enviarEmailParaRelator(idSolicitacao : Long, relator : User) = {
      val mensagem = "Você foi designado como relator de uma solicitação de afastamento (ID = " + idSolicitacao + "). \nAcesse o SCAP para deferir seu parecer." 
      val assunto = "Você foi designado para ser relator"
      sendEmail(relator, assunto, mensagem)
    }
          
    def enviarEmailParaChefeCancelamento(idSolicitacao : Long) = {
      val chefe = UserService.getUser(Global.CHEFE_ID)
      val mensagem = "A solicitação de afastamento (ID = " + idSolicitacao + ") foi cancelada pelo autor." 
      val assunto = "Uma Solicitação foi cancelada"
      sendEmail(chefe.get, assunto, mensagem)
    }
    
    def enviarEmailParaSolicitante(idSolicitacao : Long, solicitante : User, status : String) = {
      val mensagem = "Sua solicitação de afastamento (ID = " + idSolicitacao + ") foi "+ status + " \nAcesse o SCAP para ver mais detalhes." 
      val assunto = "Sua Solicitação foi " + status
      sendEmail(solicitante, assunto, mensagem)
    }
    
    def enviarEmailParaTodos(nome : String) = {
      val users = UserService.listAllUsersByTipo("PROFESSOR")
      val mensagem = "Uma nova solicitação de afastamento foi cadastrada no sistema. \nAcesse o SCAP para ver mais detalhes." 
      val assunto = "Nova Solicitação - Evento " + nome
      for (user <- users){
        sendEmail(user, assunto, mensagem)
      }
    }
   
}