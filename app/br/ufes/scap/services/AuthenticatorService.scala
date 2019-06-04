package br.ufes.scap.services

import br.ufes.scap.models.{Global, User, TipoUsuario}

object AuthenticatorService {

   def isLoggedIn() : Boolean ={
      if (Global.SESSION_KEY != 0){
        return true
      }else{
        return false
      }
    }
    
    def isAutor(professor : User) : Boolean ={
          if(professor.id == Global.SESSION_KEY){
            return true
          }else{
            return false
          }
    }
    
    def isSecretario() : Boolean ={ 
      if (Global.SESSION_TIPO.equals(TipoUsuario.Sec.toString())){
        return true
      }else{
        return false
      }
    }
    
    def isChefe(): Boolean = {
      return Global.SESSION_CHEFE   
    }
    
    def isRelator(relator : Option[User]): Boolean ={
      if (relator == None){
        return false
      }
      if (Global.SESSION_KEY == relator.get.id){
        return true
      }else{
        return false
      }
    }
    
    def isPresidenteOuVice(): Int ={
      val mandatos = MandatoService.getMandatoAtual()
      for (m <- mandatos){
        Global.CHEFE_ID = m.professor.id
        if (Global.SESSION_KEY == m.professor.id){
          Global.SESSION_CHEFE = true
          return 0
        }
      }
      Global.SESSION_CHEFE = false
      return 0
    }
        
    def isProfessor() : Boolean ={ 
      if (Global.SESSION_TIPO.equals(TipoUsuario.Prof.toString())){
        return true
      }else{
        return false
      }
    }
    
}