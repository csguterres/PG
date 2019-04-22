package br.ufes.scap.models

@javax.inject.Singleton
object Global {

    var SESSION_KEY : Long = 0
    var SESSION_TIPO : String = ""
    var SESSION_MATRICULA: String = ""

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
}
