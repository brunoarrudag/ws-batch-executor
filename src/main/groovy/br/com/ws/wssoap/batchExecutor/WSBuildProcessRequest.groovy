/**
 * Classe que contém os métodos para criação e execução do Request.
 */
package br.com.ws.wssoap.batchExecutor

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author bruno.a.guimaraes
 * @version 1.0
 */

/** 
 * Classe responsavél por criar o Request e executar, retornando apenas o response.
 * */
class WSBuildProcessRequest {
	/**
	 * Declaração do Logger que será utilizado dentro de toda classe.
	 */
	private Logger logFile = LoggerFactory.getLogger("batchExecutorLogFile");
	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * Método para leitura, processamento e execução do Request baseado no arquivo de entrada.
	 * Entrada: diretório do arquivo, nome do arquivo, string soapAction, string soapClient e mensagem de request
	 * Tem como retorno o response do SOAP.
	 * ----------------------------------------------------------------------------------------------------------------
	 * */ 
	public WSBuildRequest(){
		/**
		 * Declaração dos Loggers que serão utilizados apenas no método atual.
		 */
		Logger logFileSuccess = LoggerFactory.getLogger("batchExecutorLogSuccessFile");
		Logger logFileError = LoggerFactory.getLogger("batchExecutorLogErrorFile");
		Logger showConsole = LoggerFactory.getLogger("batchExecutorShowConsole");

		/**
		 * Objetos que farão as chamadas dos métodos das classes do aplicativo.
		 */
		WSLoadProperties loadProperties = new WSLoadProperties();
		WSExecuteRequest executeRequest = new WSExecuteRequest();

		Properties propertiesFile = new Properties();

		/**
		 * Variavéis temporárias para receber valores que serão apenas exibidos na tela.
		 */
		def lineBreak = "--------------------------------------------------------------------------------";

		/**
		 * Contadores de Registros para relatório final.
		 * */
		def executionFlag = " ";
		def quantitityProcSucess = 0;
		def quantitityProcError = 0;
		def quantitityProcessed = 0;
		def statusExecution = " ";

		/**
		 * Declaração de objeto Properties que deverá receber o arquivo de properties lido através do método WSReadProperties()
		 * Declaração de objeto tipo lista que receberá os valores do arquivo de entrada
		 */
		propertiesFile = loadProperties.WSReadProperties();
		def list;

		/**
		 * Cabeçalhos dos arquivos de log de sucesso e erro
		 */
		logFileSuccess.info("Registros processados com sucesso:"+ "\n" + "${lineBreak}");
		logFileError.info("Registros processados com erro:"+ "\n" + "${lineBreak}");
		
		logFile.info("Início de montagem e processamento de requests baseado no arquivo de entrada:"
				+ "\n" + "${lineBreak}");

		try{
			/**
			 * Leitura dos valores diretamente do arquivo de parâmetros.
			 * */
			def fileDir = propertiesFile.get("FILEDIR");
			def fileName = propertiesFile.get("FILENAME");
			def soapAction = propertiesFile.get("SOAPACTION");
			def soapClient = propertiesFile.get("SOAPCLIENT");
			def requestMessage = propertiesFile.get("REQUEST_MESSAGE");
			String returnCodeValue = propertiesFile.get("RETURN_CODE_VALUE");
			/**
			 * Variavél que receberá a mensagem alterada pelo método WSChangeMessageRequest().
			 */
			def requestMessageProcessed;

			/**
			 * Leitura do arquivo de entrada através do diretório e nome passados através do arquivo de parâmetros
			 * */
			list = new File(fileDir,fileName).collect {it};

			/**
			 * Valor da linha de cabeçalho
			 * */
			def listHeaderLine = list.get(0);

			/**
			 * Número de colunas que o cabeçalho possui
			 * */
			def listHeaderColumnQuantitity = listHeaderLine.split(Pattern.quote('|')).size();

			/**
			 * Cabeçalho em formato de lista
			 * */
			listHeaderLine = listHeaderLine.split(Pattern.quote('|'));

			list.eachWithIndex{ it, i ->
				if (i > 0){
					def listColumnLine = it.split(Pattern.quote('|'));

					def line = i;

					logFile.info("Montagem de request linha " + "${i}" + " do arquivo de entrada.");

					/**
					 * Alteração dos valores do template, passando a quantidade de colunas do cabeçalho, a linha de cabeçalho, e mensagem request.
					 * */
					requestMessageProcessed = WSChangeMessageRequest(listHeaderColumnQuantitity,listHeaderLine,listColumnLine,requestMessage);

					logFile.info("Execução de request linha " + "${i}" + " do arquivo de entrada.");

					/**
					 * Executa o request e recebe como retorno a flag "s" para sucesso e "e" para erro.
					 * */
					executionFlag = executeRequest.WSSendRequest(soapAction, soapClient, requestMessageProcessed, line, returnCodeValue);

					logFile.info("Finalização de montagem e execução de request na linha " + "${i}" + " do arquivo de entrada." +
							"\n" + "${lineBreak}");

					/**
					 * Validação de flag para fazer o count de registros com sucesso e erro.
					 * */
					if(executionFlag == "s"){
						quantitityProcSucess++;
					}
					if(executionFlag == "e"){
						quantitityProcError++;
					}
					quantitityProcessed++;
				}
			}
			statusExecution = "SUCESSO"; //flag para relatório geral de processamento.
		}catch(Exception nullProperties){
			if (propertiesFile == null){
				logFile.error("Arquivo de propriedades não encontrado, verificar arquivo de properties.\n" +
						"Erro:\n" + "${nullProperties.getMessage()}" +
						"\n" + "${lineBreak}");
			}
			if(list == null){
				logFile.error("Arquivo de entrada não encontrado, verificar arquivo de properties.\n" +
						"Erro:\n" + "${nullProperties.getMessage()}" +
						"\n" + "${lineBreak}");
			}
			statusExecution = "ERRO"; //flag para relatório geral de processamento.

			logFile.error("\n${lineBreak}" + "\n" + "A execução foi abortada devido ao erro:\n" + "${nullProperties.getMessage()}" + "\n" + "${lineBreak}");

			showConsole.error("Houve um erro na execucao, verifique o arquivo de log de processamento");

			logFileSuccess.info("\nNenhum registro foi processado, verificar log de processamento para mais informações.");
			logFileError.info("\nNenhum registro foi processado, verificar log de processamento para mais informações.");
		}
		/**
		 * Relatório final da execução
		 */
		logFile.info("Relatório de execução: \n" + "${lineBreak}\n" + 
				"Status da Execução: ${statusExecution}" + "\n" +
				"Total de registros processados: " + "${quantitityProcessed}" +
				"\n" +
				"Total de registros processados com sucesso: " + "${quantitityProcSucess}" +
				"\n" +
				"Total de registros processados com erro: " + "${quantitityProcError}" +
				"\n" + "${lineBreak}");
		showConsole.info("Relatorio de execucao: \n" + "${lineBreak}\n" + 
				"Status da Execucao: ${statusExecution}" + "\n" +
				"Total de registros processados: " + "${quantitityProcessed}" +
				"\n" +
				"Total de registros processados com sucesso: " + "${quantitityProcSucess}" +
				"\n" +
				"Total de registros processados com erro: " + "${quantitityProcError}" +
				"\n" + "${lineBreak}");
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * Método para alteração dos valores no request baseados no arquivo de entrada.
	 * Entrada: quantidade de colunas no cabeçalho, linha de cabeçalho, linha da coluna e mensagem de request
	 * Retorno: string contendo o Request com o valores alterados.
	 * ----------------------------------------------------------------------------------------------------------------
	 * */
	public WSChangeMessageRequest(def quantitityColumnHeader, def headerLine, def columnLine, String requestMessage){
		String requestMessageProcessed = requestMessage;
		def headerLineIndex = 0;
		while (headerLineIndex < quantitityColumnHeader) {
			requestMessageProcessed = requestMessageProcessed.replaceAll(headerLine[headerLineIndex], columnLine[headerLineIndex]);
			logFile.info("Campo " + "${headerLine[headerLineIndex]}" + " com valor: " + "${columnLine[headerLineIndex]} " +
					" alterado com sucesso.");
			headerLineIndex++;
		}
		/**@return*/
		return requestMessageProcessed;
	}
}
