package br.ufes.scap.services

import br.ufes.scap.models.Global

object AuthenticatorService {

   def isLoggedIn() : Boolean ={
      if (Global.SESSION_KEY != 0){
        return true
      }else{
        return false
      }
    }
    
    def isSecretario() : Boolean ={ 
      if (Global.SESSION_TIPO.equals("SECRETARIO")){
        return true
      }else{
        return false
      }
    }
    
    def isRelator(idRelator : Long): Boolean ={
      if (Global.SESSION_KEY == idRelator){
        return true
      }else{
        return false
      }
    }
    
    def isPresidenteOuVice(): Int ={
      val mandatos = MandatoService.getMandatoAtual()
      for (m <- mandatos){
        Global.CHEFE_ID = m.idProfessor
        if (Global.SESSION_KEY == m.idProfessor){
          Global.SESSION_CHEFE = true
          return 0
        }
      }
      Global.SESSION_CHEFE = false
      return 0
    }
        
    def isProfessor() : Boolean ={ 
      if (Global.SESSION_TIPO.equals("PROFESSOR")){
        return true
      }else{
        return false
      }
    }
    
}