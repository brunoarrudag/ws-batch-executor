/**
 * ----------------------------------------------------------------------------------------------------------------
 * Classe que implementa a biblioteca wslite.soap para execução do request,
 * através do método WSSendRequest.
 * ----------------------------------------------------------------------------------------------------------------
 */
package br.com.ws.wssoap.batchExecutor

import java.io.File;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import wslite.soap.SOAPClient;
import wslite.soap.SOAPResponse;

/**
 * @author bruno.a.guimaraes
 * @version 1.0
 */
class WSExecuteRequest {
	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * Método para execução de request
	 * Tem como retorno uma flag de controle para uso no relatório sintético.
	 * ----------------------------------------------------------------------------------------------------------------
	 */		
	public WSSendRequest(def soapAction, def soapClient, def requestMessage, def line, String returnCodeValue){
		/**
		 * Declaração dos Loggers que serão utilizados.
		 */
		Logger logFile = LoggerFactory.getLogger("batchExecutorLogFile");
		Logger logFileSuccess = LoggerFactory.getLogger("batchExecutorLogSuccessFile");
		Logger logFileError = LoggerFactory.getLogger("batchExecutorLogErrorFile");

		/**
		 * Flag para ser utilizada como retorno do método.
		 * O valor será utilizado para incrementar o relatório geral de processamento. 
		 */
		def executionFlag;

		/**
		 * Variavéis que recebem valores temporários.
		 */
		def response;
		def lineBreak = "--------------------------------------------------------------------------------";

		try{
			/**
			 * Execução do serviço SOAP
			 */
			def client = new SOAPClient(soapClient);
			response = client.send(SOAPAction:soapAction, requestMessage);

			if(response.text.toString().contains(returnCodeValue)){
				logFileSuccess.info("Execução com sucesso do request na linha ${line} do arquivo de entrada.\n" +
						"Request Executado: " + "\n" + "${requestMessage}\n\n" +
						"Response retornado: " + "\n" + "${response.text}\n" +
						"${lineBreak}");
				logFile.info("Execução com sucesso do request na linha ${line} do arquivo de entrada.");
				logFile.info("Verificar arquivo de saída do log de valores processados com sucesso.");

				executionFlag = "s"; //flag para controle de relatório geral
			}
			if(!response.text.toString().contains(returnCodeValue)) {
				logFileError.error("Erro ao executar o request na linha [${line}] do arquivo de entrada.\n" +
						"Erro:" + " returnCode diferente de 0\n" +
						"Request Executado: " + "\n" + "${requestMessage}\n\n" +
						"Response retornado: " + "\n" + "${response.text}\n" +
						"${lineBreak}");
				logFile.error("Execução com erro do request na linha ${line} do arquivo de entrada.");
				logFile.info("Verificar arquivo de saída do log de valores processados com erro.");

				executionFlag = "e"; //flag para controle de relatório geral
			}
		}catch(Exception clientSoapException){
			logFileError.error("Erro ao executar o request na linha [${line}] do arquivo de entrada.\n" +
					"Erro:" + "\n" + "-> ${clientSoapException.getMessage()}\n" +
					"Response retornado: " + "\n" + "${response.text}\n" +
					"${lineBreak}");
			logFile.error("Execução com erro do request na linha ${line} do arquivo de entrada.");
			logFile.info("Verificar arquivo de saída do log de valores processados com erro.");

			executionFlag = "e"; //flag para controle de relatório geral
		}
		/**@return*/
		return executionFlag;
	}
}
