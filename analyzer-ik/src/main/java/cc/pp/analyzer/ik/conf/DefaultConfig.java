/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 *
 *
 */
package cc.pp.analyzer.ik.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration 默认实现
 * 2012-5-8
 *
 */
public class DefaultConfig implements Configuration{

	private static Logger logger = LoggerFactory.getLogger(DefaultConfig.class);

	/*
	 * 分词器默认字典路径
	 */
	private static final String PATH_DIC_MAIN = "main2012.dic";
	private static final String PATH_DIC_QUANTIFIER = "quantifier.dic";

	/*
	 * 分词器配置文件路径
	 */
	private static final String FILE_NAME = "IKAnalyzer.cfg.xml";
	//配置属性——扩展字典
	private static final String EXT_DICT = "ext_dict";
	//配置属性——扩展停止词典
	private static final String EXT_STOP = "ext_stopwords";

	private final Properties props;
	/*
	 * 是否使用smart方式分词
	 */
	private boolean useSmart;

	/**
	 * 返回单例
	 * @return Configuration单例
	 */
	public static Configuration getInstance(){
		return new DefaultConfig();
	}

	/*
	 * 初始化配置文件
	 */
	private DefaultConfig(){

		props = new Properties();
		logger.info("初始化配置文件： " + System.getProperty("user.dir"));
		logger.info(this.getClass().getResource("").toString());
		//		InputStream input = this.getClass().getResourceAsStream(FILE_NAME);
		//		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_NAME);
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
		if(input != null){
			try {
				props.loadFromXML(input);
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 返回useSmart标志位
	 * useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
	 * @return useSmart
	 */
	@Override
	public boolean useSmart() {
		return useSmart;
	}

	/**
	 * 设置useSmart标志位
	 * useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
	 * @param useSmart
	 */
	@Override
	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	/**
	 * 获取主词典路径
	 *
	 * @return String 主词典路径
	 */
	@Override
	public String getMainDictionary(){
		return PATH_DIC_MAIN;
	}

	/**
	 * 获取量词词典路径
	 * @return String 量词词典路径
	 */
	@Override
	public String getQuantifierDicionary(){
		return PATH_DIC_QUANTIFIER;
	}

	/**
	 * 获取扩展字典配置路径
	 * @return List<String> 相对类加载器的路径
	 */
	@Override
	public List<String> getExtDictionarys() {

		List<String> extDictFiles = new ArrayList<String>(2);
		String extDictCfg = props.getProperty(EXT_DICT);
		logger.info("获取扩展字典配置路径： " + extDictCfg);
		if(extDictCfg != null){
			//使用;分割多个扩展字典配置
			String[] filePaths = extDictCfg.split(";");
			if(filePaths != null){
				for(String filePath : filePaths){
					if(filePath != null && !"".equals(filePath.trim())){
						extDictFiles.add(filePath.trim());
					}
				}
			}
		}
		return extDictFiles;
	}


	/**
	 * 获取扩展停止词典配置路径
	 * @return List<String> 相对类加载器的路径
	 */
	@Override
	public List<String> getExtStopWordDictionarys(){

		List<String> extStopWordDictFiles = new ArrayList<String>(2);
		String extStopWordDictCfg = props.getProperty(EXT_STOP);
		logger.info("获取扩展停止词典配置路径： " + extStopWordDictCfg);
		if(extStopWordDictCfg != null){
			//使用;分割多个扩展字典配置
			String[] filePaths = extStopWordDictCfg.split(";");
			if(filePaths != null){
				for(String filePath : filePaths){
					if(filePath != null && !"".equals(filePath.trim())){
						extStopWordDictFiles.add(filePath.trim());
					}
				}
			}
		}
		return extStopWordDictFiles;
	}

}
