package br.ufes.scap.services

import javax.inject.Inject
import play.api.mvc.Results._
import play.api.mvc._
import play.api.mvc.ActionBuilderImpl
import br.ufes.scap.models.{Global, TipoUsuario}

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedUsuarioAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) { 

    private val logger = play.api.Logger(this.getClass)

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
        var user_id = Global.SESSION_KEY
        if (user_id == 0){
              Future.successful(Ok(br.ufes.scap.views.html.erro()))
        }else{
                val res: Future[Result] = block(request)
                res
        }
        
    }
}

class AuthenticatedSecretarioAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) { 

    private val logger = play.api.Logger(this.getClass)

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

    private val logger = play.api.Logger(this.getClass)

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