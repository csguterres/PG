package br.ufes.scap.controllers

import br.ufes.scap.models.{User, UserForm, UserLoginForm, Global}
import play.api.mvc._
import br.ufes.scap.services.UserService
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent._
import scala.concurrent.duration._
import play.api.data.Forms._
import play.api.data._
import scala.concurrent.Future

class LoginController extends Controller {

    def login() = Action.async { implicit request =>
    UserLoginForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.userLogin(errorForm))),
      data => {
        val user = Await.result(UserService.getUserByMatricula(data.matricula),Duration.Inf)
        Global.SESSION_KEY = user.get.id 
        Global.SESSION_TIPO = user.get.tipo
        Global.SESSION_MATRICULA = user.get.matricula
        if(Global.SESSION_TIPO.equals("SECRETARIO")){
          UserService.getUserByMatricula(data.matricula).map(res =>
            Ok(br.ufes.scap.views.html.menuSecretario(UserForm.form, user))
          )
        }else{
          UserService.getUserByMatricula(data.matricula).map(res =>
            Ok(br.ufes.scap.views.html.menuUsuario(UserForm.form, user))
          )
        }
      })
  }
    
    def logout() = Action {
        Global.SESSION_KEY = 0
        Global.SESSION_TIPO = ""
        Global.SESSION_MATRICULA = ""
        Redirect(routes.LoginController.login)
    }
    
    def menu() = Action{ implicit request =>
      if (Global.isLoggedIn()){
        val user = Await.result(UserService.getUser(Global.SESSION_KEY),Duration.Inf)
        if (Global.isSecretario()){
          Ok(br.ufes.scap.views.html.menuSecretario(UserForm.form, user))
        }else{
          Ok(br.ufes.scap.views.html.menuUsuario(UserForm.form, user))
        }
      }else{
        Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
    }
    
}