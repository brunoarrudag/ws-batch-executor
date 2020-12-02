/**
 * ----------------------------------------------------------------------------------------------------------------
 * Classe que faz a leitura do Arquivo de Properties utilizando o objeto InputStream. 
 * O arquivo de properties deverá estar na mesma pasta do shell command (windows ou linux)
 * será feita a chamada do arquivo apenas pelo nome, sem precisar setar valores de diretório.
 * Ficará tudo já pré definido no arquivo ".jar"
 * ----------------------------------------------------------------------------------------------------------------
 */
package br.com.ws.wssoap.batchExecutor

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bruno.a.guimaraes
 * @version 1.0
 */

class WSLoadProperties {

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * Método para leitura do arquivo de properties
	 * ----------------------------------------------------------------------------------------------------------------
	 */
	public Properties WSReadProperties(){
		Logger logFile = LoggerFactory.getLogger("batchExecutorLogFile");
		Properties propertiesFile = new Properties();
		try{
			/**
			 * Utilização do objeto InputStream para leitura do arquivo de properties que deverá estar
			 * no mesmo folder do shell command de execução do aplicativo.
			 */
			logFile.info("Início de leitura do arquivo de propriedades");
			InputStream inputStream = new FileInputStream((new File("wsbatchexecutor.properties")).getAbsoluteFile());
			propertiesFile.load(inputStream);
			logFile.info("Arquivo de propriedades lido com sucesso.");
		}
		catch(Exception propertiesNotFound){
			propertiesFile = null;
		}
		return propertiesFile;
	}
}
