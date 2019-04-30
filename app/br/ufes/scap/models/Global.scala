package br.ufes.scap.models

import java.util.Date

@javax.inject.Singleton
object Global {

    var SESSION_KEY : Long = 0
    var SESSION_TIPO : String = ""
    var SESSION_MATRICULA: String = ""

    var CURRENT_USER : Option[User] = None 
    
    def isLoggedIn() : Boolean ={
      if (this.SESSION_KEY != 0){
        true
      }else{
        false
      }
    }
    
    def isSecretario() : Boolean ={ 
      if (this.SESSION_TIPO.equals("SECRETARIO")){
        true
      }else{
        false
      }
    }
    
    def checaData(dataInicio : Date, dataFim : Date): Boolean = {
    if (dataInicio.after(dataFim)){
      false
    }else{
      true
    }
  }
}
