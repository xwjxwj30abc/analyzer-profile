/**
 * Copyright 2007 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.pp.analyzer.paoding.dictionary.support.filewords;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import cc.pp.analyzer.paoding.dictionary.Word;

/**
 * 本类用于读取编译后的词典
 * @author Zhiliang Wang [qieqie.wang@gmail.com]
 *
 * @since 1.0
 *
 */
public class SimpleReadListener2 implements ReadListener {

	private final Map/* <String, Collection<Word>> */<String, Collection<Word>> dics = new Hashtable/* <String, Collection<String>> */<String, Collection<Word>>();
	private Class<?> collectionClass = HashSet.class;
	private Collection/* <Word> */<Word> words;
	private String ext = ".dic";

	@SuppressWarnings("rawtypes")
	public SimpleReadListener2(Class<LinkedList> linkedList, String ext) {
		this.ext = ext;
		this.collectionClass = linkedList;
	}

	public SimpleReadListener2() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onFileBegin(String file) {
		if (!file.endsWith(ext)) {
			return false;
		}
		try {
			words = (Collection<Word>) collectionClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void onFileEnd(String file) {
		String name = file.substring(0, file.length() - 4);
		dics.put(name, words);
		words = null;
	}

	@Override
	public void onWord(String wordText) {
		wordText = wordText.trim().toLowerCase();
		if (wordText.length() == 0 || wordText.charAt(0) == '#'
				|| wordText.charAt(0) == '-') {
			return;
		}

		if (!wordText.endsWith("]")) {
			words.add(new Word(wordText));
		}
		else {
			int index = wordText.indexOf('[');
			Word w = new Word(wordText.substring(0, index));
			int mindex = wordText.indexOf("m=", index);
			int mEndIndex = wordText.indexOf("]", mindex);
			String m = wordText.substring(mindex + "m=".length(), mEndIndex);
			w.setModifiers(Integer.parseInt(m));
			words.add(w);
		}
	}

	public Map/* <String, Collection<Word>> */<String, Collection<Word>> getResult() {
		return dics;
	}

}