package br.ufes.scap.services

import javax.inject.Inject
import play.api.mvc.Results._
import play.api.mvc._
import play.api.mvc.ActionBuilderImpl
import br.ufes.scap.models.{Global, TipoUsuario, User}

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedUsuarioAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) { 

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
        if (Global.SESSION_KEY == 0){
              Future.successful(Ok(br.ufes.scap.views.html.erro()))
        }else{
                val res: Future[Result] = block(request)
                res
        }
        
    }
}

class AuthenticatedSecretarioAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) { 

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
        var user_tipo = Global.SESSION_TIPO
        if (user_tipo.equals(TipoUsuario.Sec.toString())){
            val res: Future[Result] = block(request)
            res
        }else{
            Future.successful(Ok(br.ufes.scap.views.html.erro()))
        }
    }
}

class AuthenticatedProfessorAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) { 

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
        var user_tipo = Global.SESSION_TIPO
        if (user_tipo.equals(TipoUsuario.Prof.toString())){
            val res: Future[Result] = block(request)
            res
        }else{
            Future.successful(Ok(br.ufes.scap.views.html.erro()))
        }
    }
}

class AuthenticatedChefeAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) { 

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
        if (Global.SESSION_CHEFE){
            val res: Future[Result] = block(request)
            res
        }else{
            Future.successful(Ok(br.ufes.scap.views.html.erro()))
        }
    }
}

object AuthenticatorService {
    
    def isAutor(professor : User) : Boolean ={
       return(professor.id == Global.SESSION_KEY)
    }
    
    def isSecretario() : Boolean ={ 
      return (Global.SESSION_TIPO.equals(TipoUsuario.Sec.toString()))
    }
    
    def isChefe(): Boolean = {
      return Global.SESSION_CHEFE   
    }
    
    def isRelator(relator : Option[User]): Boolean ={
      if (relator == None){
        return false
      }
      return (Global.SESSION_KEY == relator.get.id)
    }
           
    def isProfessor() : Boolean ={ 
      return (Global.SESSION_TIPO.equals(TipoUsuario.Prof.toString()))
    }
    
}