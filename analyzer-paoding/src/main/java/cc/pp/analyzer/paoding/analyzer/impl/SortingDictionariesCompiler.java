package cc.pp.analyzer.paoding.analyzer.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import cc.pp.analyzer.paoding.constant.Constants;
import cc.pp.analyzer.paoding.dictionary.Dictionary;
import cc.pp.analyzer.paoding.dictionary.Word;
import cc.pp.analyzer.paoding.knife.Dictionaries;
import cc.pp.analyzer.paoding.knife.DictionariesCompiler;
import cc.pp.analyzer.paoding.knife.Knife;

public class SortingDictionariesCompiler implements DictionariesCompiler {
	public static final String VERSION = "2";
	
	public boolean shouldCompile(Properties p) throws Exception {
		String lastModifieds = p.getProperty("paoding.analysis.properties.lastModifieds");
		String files = p.getProperty("paoding.analysis.properties.files");
		String dicHome = p.getProperty("paoding.dic.home.absolute.path");
		File dicHomeFile = new File(dicHome);
		File compliedMetadataFile = new File(dicHomeFile, ".compiled/sorting/.metadata");
		if (compliedMetadataFile.exists() && compliedMetadataFile.isFile()) {
			Properties compiledProperties = new Properties();
			InputStream compiledPropertiesInput = new FileInputStream(compliedMetadataFile);
			compiledProperties.load(compiledPropertiesInput);
			compiledPropertiesInput.close();
			String compiledLastModifieds = compiledProperties.getProperty("paoding.analysis.properties.lastModifieds");
			String compiledFiles = compiledProperties.getProperty("paoding.analysis.properties.files");
			String clazz = compiledProperties.getProperty("paoding.analysis.compiler.class");
			String version = compiledProperties.getProperty("paoding.analysis.compiler.version");
			if (lastModifieds.equals(compiledLastModifieds) && files.equals(compiledFiles)
					&& this.getClass().getName().equalsIgnoreCase(clazz)
					&& VERSION.equalsIgnoreCase(version)) {
				return false;
			}
		}
		return true;
	}
	
	
	public void compile(Dictionaries dictionaries, Knife knife, Properties p) throws Exception {
		String dicHome = p.getProperty("paoding.dic.home.absolute.path");
		String noiseCharactor = getProperty(p, Constants.DIC_NOISE_CHARACTOR);
		String noiseWord = getProperty(p, Constants.DIC_NOISE_WORD);
		String unit = getProperty(p, Constants.DIC_UNIT);
		String confucianFamilyName = getProperty(p, Constants.DIC_CONFUCIAN_FAMILY_NAME);
		String combinatorics = getProperty(p, Constants.DIC_FOR_COMBINATORICS);
		String charsetName = getProperty(p, Constants.DIC_CHARSET);
		
		File dicHomeFile = new File(dicHome);
		File compiledDicHomeFile = new File(dicHomeFile, ".compiled/sorting");
		compiledDicHomeFile.mkdirs();
		
		//
		Dictionary vocabularyDictionary = dictionaries.getVocabularyDictionary();
		File vocabularyFile = new File(compiledDicHomeFile, "vocabulary.dic.compiled");
		sortCompile(vocabularyDictionary, vocabularyFile, charsetName);

		//
		Dictionary noiseCharactorsDictionary = dictionaries.getNoiseCharactorsDictionary();
		File noiseCharactorsDictionaryFile = new File(compiledDicHomeFile, noiseCharactor + ".dic.compiled");
		sortCompile(noiseCharactorsDictionary, noiseCharactorsDictionaryFile, charsetName);
		//
		Dictionary noiseWordsDictionary = dictionaries.getNoiseWordsDictionary();
		File noiseWordsDictionaryFile = new File(compiledDicHomeFile, noiseWord + ".dic.compiled");
		sortCompile(noiseWordsDictionary, noiseWordsDictionaryFile, charsetName);
		//
		Dictionary unitsDictionary = dictionaries.getUnitsDictionary();
		File unitsDictionaryFile = new File(compiledDicHomeFile, unit + ".dic.compiled");
		sortCompile(unitsDictionary, unitsDictionaryFile, charsetName);
		//
		Dictionary confucianFamilyDictionary = dictionaries.getConfucianFamilyNamesDictionary();
		File confucianFamilyDictionaryFile = new File(compiledDicHomeFile, confucianFamilyName + ".dic.compiled");
		sortCompile(confucianFamilyDictionary, confucianFamilyDictionaryFile, charsetName);
		//
		Dictionary combinatoricsDictionary = dictionaries.getCombinatoricsDictionary();
		File combinatoricsDictionaryFile = new File(compiledDicHomeFile, combinatorics + ".dic.compiled");
		sortCompile(combinatoricsDictionary, combinatoricsDictionaryFile, charsetName);

		//
		File compliedMetadataFile = new File(dicHomeFile, ".compiled/sorting/.metadata");
		if (compliedMetadataFile.exists()) {
			//compliedMetadataFile.setWritable(true);
			compliedMetadataFile.delete();
		}
		else {
			compliedMetadataFile.getParentFile().mkdirs();
		}
		OutputStream compiledPropertiesOutput = new FileOutputStream(compliedMetadataFile);
		Properties compiledProperties = new Properties();
		String lastModifiedsKey = "paoding.analysis.properties.lastModifieds";
		String filesKey = "paoding.analysis.properties.files";
		compiledProperties.setProperty(lastModifiedsKey, p.getProperty(lastModifiedsKey));
		compiledProperties.setProperty(filesKey, p.getProperty(filesKey));
		compiledProperties.setProperty("paoding.analysis.compiler.class", this.getClass().getName());
		compiledProperties.setProperty("paoding.analysis.compiler.version", VERSION);
		compiledProperties.store(compiledPropertiesOutput, "dont edit it! this file was auto generated by paoding.");
		compiledPropertiesOutput.close();
		compliedMetadataFile.setReadOnly();
	}

	
	
	private void sortCompile(final Dictionary dictionary, 
			File dicFile, String charsetName) throws FileNotFoundException,
			IOException, UnsupportedEncodingException {
		int wordsSize = dictionary.size();
		if (dicFile.exists()) {
			//dicFile.setWritable(true);
			dicFile.delete();
		}
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(dicFile), 1024 * 16);
		
		for (int i = 0; i < wordsSize; i++) {
			Word word = dictionary.get(i);
			out.write(word.getText().getBytes(charsetName));
			if (word.getModifiers() != Word.DEFAUL) {
				out.write("[m=".getBytes());
				out.write(String.valueOf(word.getModifiers()).getBytes());
				out.write(']');
			}
			out.write('\r');
			out.write('\n');
		}
		out.flush();
		out.close();
		dicFile.setReadOnly();
	}
	
	public Dictionaries readCompliedDictionaries(Properties p) {
		String dicHomeAbsolutePath = p.getProperty("paoding.dic.home.absolute.path");
		String noiseCharactor = getProperty(p, Constants.DIC_NOISE_CHARACTOR);
		String noiseWord = getProperty(p, Constants.DIC_NOISE_WORD);
		String unit = getProperty(p, Constants.DIC_UNIT);
		String confucianFamilyName = getProperty(p, Constants.DIC_CONFUCIAN_FAMILY_NAME);
		String combinatorics = getProperty(p, Constants.DIC_FOR_COMBINATORICS);
		String charsetName = getProperty(p, Constants.DIC_CHARSET);
		return new CompiledFileDictionaries(
				dicHomeAbsolutePath + "/.compiled/sorting",
				noiseCharactor, noiseWord, unit,
				confucianFamilyName, combinatorics, charsetName);
	}
	
	private static String getProperty(Properties p, String name) {
		return Constants.getProperty(p, name);
	}
	
}
