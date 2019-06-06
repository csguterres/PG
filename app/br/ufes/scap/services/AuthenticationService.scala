package br.ufes.scap.services

import br.ufes.scap.models.{Global, TipoUsuario, User}

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