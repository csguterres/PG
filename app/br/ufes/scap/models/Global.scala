package br.ufes.scap.models

import java.util.Date
import br.ufes.scap.services.MandatoService


@javax.inject.Singleton
object Global {

    var SESSION_KEY : Long = 0
    var SESSION_TIPO : String = ""
    var SESSION_MATRICULA: String = ""
    var SESSION_CHEFE : Boolean = false
    
    def isLoggedIn() : Boolean ={
      if (this.SESSION_KEY != 0){
        return true
      }else{
        return false
      }
    }
    
    def isSecretario() : Boolean ={ 
      if (this.SESSION_TIPO.equals("SECRETARIO")){
        return true
      }else{
        return false
      }
    }
    
    def isRelator(idRelator : Long): Boolean ={
      if (this.SESSION_KEY == idRelator){
        return true
      }else{
        return false
      }
    }
    
    def isPresidenteOuVice(): Int ={
      val mandatos = MandatoService.getMandatoAtual()
      for (m <- mandatos){
        if (SESSION_KEY == m.idProfessor){
          SESSION_CHEFE = true
          return 0
        }
      }
      SESSION_CHEFE = false
      return 0
    }
        
    def isProfessor() : Boolean ={ 
      if (this.SESSION_TIPO.equals("PROFESSOR")){
        return true
      }else{
        return false
      }
    }
    
    def checaData(dataInicio : Date, dataFim : Date): Boolean = {
      if (dataInicio.after(dataFim)){
        return false
      }else{
        return true
      }
    }
  
}
