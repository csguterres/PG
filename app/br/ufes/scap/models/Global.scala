package br.ufes.scap.models

import java.util.Date
import br.ufes.scap.services.MandatoService


@javax.inject.Singleton
object Global {

    var SESSION_KEY : Long = 0
    var SESSION_TIPO : String = ""
    var SESSION_MATRICULA: String = ""
    
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
    
    def isPresidenteOuVice(id : Long): Boolean ={
      val mandatos = MandatoService.getMandatoAtual()
      for (m <- mandatos){
        if (id == m.idProfessor){
          return true
        }
      }
      return false
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
    
    def preenchido(motivo : String): Boolean = {
      return !(motivo.trim.equals(""))
    }
  
}
