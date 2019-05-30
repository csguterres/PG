package br.ufes.scap.services

import br.ufes.scap.models.{Global, User, TipoUser}

object AuthenticatorService {

   def isLoggedIn() : Boolean ={
      if (Global.SESSION_KEY != 0){
        return true
      }else{
        return false
      }
    }
    
    def isAutor(professor : Option[User]) : Boolean ={
        if (professor == None){
            return false
        }else{
          if(professor.get.id == Global.SESSION_KEY){
            return true
          }else{
            return false
          }
        }
    }
    
    def isSecretario() : Boolean ={ 
      if (Global.SESSION_TIPO.equals(TipoUser.Sec.toString())){
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
      if (Global.SESSION_TIPO.equals(TipoUser.Prof.toString())){
        return true
      }else{
        return false
      }
    }
    
}