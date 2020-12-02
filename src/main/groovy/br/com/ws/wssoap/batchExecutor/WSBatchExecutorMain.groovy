/**
 * ----------------------------------------------------------------------------------------------------------------
  * Classe que contém todo fluxo principal de execução do aplicativo.
 * ----------------------------------------------------------------------------------------------------------------
 */
package br.com.ws.wssoap.batchExecutor

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bruno.a.guimaraes
 * @version 1.0
 */
class WSBatchExecutorMain {
	/** 
	 * ----------------------------------------------------------------------------------------------------------------
	 * Método principal para chamada do fluxo do programa
	 * ----------------------------------------------------------------------------------------------------------------
	 * */
	static void main(def args){
		Logger logFile = LoggerFactory.getLogger("batchExecutorLogFile");
		Logger showConsole = LoggerFactory.getLogger("batchExecutorShowConsole");	
		WSBuildProcessRequest buildProcessRequest = new WSBuildProcessRequest();
						
		logFile.info("Início da aplicação");
		showConsole.info("Inicio da aplicacao");
		/**
		 * Faz a chamada do WSReadProcessFile, método que irá montar o request e executar 
		 * extraindo os dados do arquivo de properties.
		 */ 
		buildProcessRequest.WSBuildRequest();
		
		logFile.info("Fim da execução, verifique os arquivos de saída.");
		showConsole.info("Fim da execucao.");
		showConsole.info("Verifique os arquivos de saida.");
	}
}