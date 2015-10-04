/*
 *
 * Copyright (c) 2011, Xiufeng Liu (xiliu@cs.aau.dk) and the eGovMon Consortium
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 */

package xlclient.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public final class Configure {

	public final static String CONFIG_PATH = "config.path";
	public final static String ONT_PATH = "ont.path";
	public static final String DB_URL = "db.url";
	public final static String DB_DRIVER = "db.driver";
	public final static String DB_HOST = "db.host";
	public final static String DB_PORT = "db.port";
	public final static String DB_NAME = "db.dbname";
	public final static String DB_USERNAME = "db.username";
	public final static String DB_PASSWORD = "db.password";
	public final static String USE_MP = "use.mp";
	public static final String DS_PATH = "ds.path";
	public static final String BDB_PATH = "bdb.path";
	public static final String CSV_PATH = "csv.path";
	public static final String BDB_BUFSIZE = "bdb.bufsize";
	public static final String VH_BUFSIZE = "vh.bufsize";
	public static final String VH_COMMIT = "vh.commit";
	public static final String RDF_DELIM = "rdf.delim";

	protected Properties props;
	protected String configPath;
	protected String ontText;

	public Configure() {
		props = new Properties();
		props.setProperty(RDF_DELIM, ",");
	}

	
	public void loadConfig(String filePath) {
		try {
			File configFile = new File(filePath);
			configPath = configFile.getAbsolutePath();
			props.loadFromXML(new FileInputStream(configFile));
		} catch (Exception e) {
			System.err.printf("Cannot load the configuration file at: %s\n", configPath);
		} 
	}
	
	public void setConfigPath(String configPath){
		this.configPath = configPath;
	}
	
	public String getConfigPath(){
		return this.configPath;
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}

	public void setOntPath(String ontPath) {
		this.setProperty(ONT_PATH, ontPath);
	}

	public String getOntPath() {
		return this.getProperty(ONT_PATH);
	}

	public void setDbUrl(String dbUrl) {
		this.setProperty(DB_URL, dbUrl);
	}

	public String getDbUrl() {
		return this.getProperty(DB_URL);
	}

	public void setDbDriver(String dbDriver) {
		this.setProperty(DB_DRIVER, dbDriver);
	}

	public String getDbDriver() {
		return this.getProperty(DB_DRIVER);
	}

	public void setDbHost(String dbHost) {
		this.setProperty(DB_HOST, dbHost);
	}

	public String getDbHost() {
		return this.getProperty(DB_HOST);
	}

	public void setDbPort(String dbPort) {
		this.setProperty(DB_PORT, dbPort);
	}

	public String getDbPort() {
		return this.getProperty(DB_PORT);
	}

	public void setDbName(String dbName) {
		this.setProperty(DB_NAME, dbName);
	}

	public String getDbName() {
		return this.getProperty(DB_NAME);
	}

	public void setDbUsername(String username) {
		this.setProperty(DB_USERNAME, username);
	}

	public String getDbUsername() {
		return this.getProperty(DB_USERNAME);
	}

	public void setDbPassword(String password) {
		this.setProperty(DB_PASSWORD, password);
	}

	public String getDbPassword() {
		return this.getProperty(DB_PASSWORD);
	}

	public boolean isUseMP() {
		return Boolean.parseBoolean(this.getProperty(USE_MP));
	}

	public void setUseMP(boolean useMP) {
		this.setProperty(USE_MP, String.valueOf(useMP));
	}

	public void setDataSourcePath(String dsPath) {
		this.setProperty(DS_PATH, dsPath);
	}

	public String getDataSourcePath() {
		return this.getProperty(DS_PATH);
	}

	public void setBdbPath(String bdbPath) {
		this.setProperty(BDB_PATH, bdbPath);
	}

	public String getBdbPath() {
		return this.getProperty(BDB_PATH);
	}

	public void setBdbBufSize(int bdbBufferSize) {
		this.setProperty(BDB_BUFSIZE, String.valueOf(bdbBufferSize));
	}

	public int getBdbBufSize() {
		return Integer.parseInt(this.getProperty(BDB_BUFSIZE));
	}

	public void setCsvPath(String csvPath) {
		this.setProperty(CSV_PATH, csvPath);
	}

	public String getCsvPath() {
		return this.getProperty(CSV_PATH);
	}

	public void setVhBufSize(int vhBufferSize) {
		this.setProperty(VH_BUFSIZE, String.valueOf(vhBufferSize));
	}

	public int getVhBufSize() {
		return Integer.parseInt(this.getProperty(VH_BUFSIZE));
	}

	public void setVhCommitSize(double vhCommitSize) {
		this.setProperty(VH_COMMIT, String.valueOf(vhCommitSize));
	}

	public double getVhCommitSize() {
		return Double.parseDouble(this.getProperty(VH_COMMIT));
	}

	public void setRdfDelim(String rdfDelim) {
		this.setProperty(RDF_DELIM, rdfDelim);
	}

	public String getRdfDelim() {
		return this.getProperty(RDF_DELIM);
	}
	
	public void clear(){
		props.clear();
	}
	
	public boolean isClear(){
		return this.configPath==null||"".equals(configPath);
	}
	
	public Iterator<Entry<Object, Object>> iterator(){
		Set<Entry<Object, Object>> entrySet =  props.entrySet();
		return entrySet.iterator();
	}

	public void save() throws XLException {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(this.configPath);
			props.storeToXML(fos, "3XL Configuration", "UTF-8");
		} catch (Exception e) {
			throw new XLException(e);
		}		
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(configPath==null?"":configPath).append("\n").append(props.toString());
		strBuilder.append("Ontology:").append("\n").append(this.ontText);
		return strBuilder.toString();
	}
	
	public static Configure getInstance(){
		if (instance==null){
			newInstance();
		}
		return instance;
	}
	
	public static Configure newInstance(String filePath) {
		instance = new Configure();
		instance.loadConfig(filePath);
		return instance;
	}
	
	public static Configure newInstance(){
		instance = new Configure();
		return instance;
	}
	
	
	protected static Configure instance;

	public void setOntText(String ontText) {
	 this.ontText = ontText;
	}
	
	public String getOntText(){
		return this.ontText;
	}


}
