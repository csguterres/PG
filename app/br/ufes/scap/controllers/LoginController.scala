package br.ufes.scap.controllers

import br.ufes.scap.models.{User, UserForm, UserLoginForm, Global}
import play.api.mvc._
import br.ufes.scap.services.{UserService, AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import play.api.data.Forms._
import play.api.data._
import scala.concurrent.Future

class LoginController extends Controller {

    def loginForm() = Action {
        Ok(br.ufes.scap.views.html.userLogin(UserLoginForm.form))
    }
    
    def login() = Action.async { implicit request =>
    UserLoginForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.userLogin(errorForm))),
      data => {
        val user = UserService.getUserByMatricula(data.matricula)
        Global.SESSION_KEY = user.get.id 
        Global.SESSION_TIPO = user.get.tipo
        Global.SESSION_MATRICULA = user.get.matricula
        Global.SESSION_EMAIL = user.get.email
        Global.SESSION_PASS = user.get.password
        Global.isPresidenteOuVice()
        UserService.getUserByMatricula(data.matricula)
        Future.successful(Ok(br.ufes.scap.views.html.menu(UserForm.form, user)))
      })
  }
    
    def logout() = Action {
        Global.SESSION_KEY = 0
        Global.SESSION_TIPO = ""
        Global.SESSION_MATRICULA = ""
        Global.SESSION_EMAIL = ""
        Global.SESSION_PASS = ""
        Redirect(routes.LoginController.login)
    }
    
    def menu() = Action{ implicit request =>
      if (AuthenticatorService.isLoggedIn()){
        val user = UserService.getUser(Global.SESSION_KEY)
        Ok(br.ufes.scap.views.html.menu(UserForm.form, user))
      }else{
        Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
    }
    
}