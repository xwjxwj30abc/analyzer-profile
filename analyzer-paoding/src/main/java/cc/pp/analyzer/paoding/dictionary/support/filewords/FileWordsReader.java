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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import cc.pp.analyzer.paoding.dictionary.Word;
import cc.pp.analyzer.paoding.knife.CharSet;

/**
 *
 * @author Zhiliang Wang [qieqie.wang@gmail.com]
 *
 * @since 1.0
 *
 */
public class FileWordsReader {

	public static Map/*<String, Set<Word>>*/<String, Set<Word>> readWords(
			String fileOrDirectory, String charsetName) throws IOException {
		SimpleReadListener l = new SimpleReadListener();
		readWords(fileOrDirectory, l, charsetName);
		return l.getResult();
	}

	@SuppressWarnings("rawtypes")
	public static Map/*<String, Collection<Word>>*/<String, Collection<Word>> readWords(String fileOrDirectory,
			String charsetName, Class<LinkedList> linkedList, String ext) throws IOException {
		SimpleReadListener2 l = new SimpleReadListener2(linkedList, ext);
		readWords(fileOrDirectory, l, charsetName);
		return l.getResult();
	}

	public static void readWords(String fileOrDirectory, ReadListener l, String charsetName)
			throws IOException {
		File file;
		if (fileOrDirectory.startsWith("classpath:")) {
			String name = fileOrDirectory.substring("classpath:".length());
			URL url = FileWordsReader.class.getClassLoader().getResource(name);
			if (url == null) {
				throw new FileNotFoundException("file \"" + name + "\" not found in classpath!");
			}
			file = new File(url.getFile());
		}
		else {
			file = new File(fileOrDirectory);
			if (!file.exists()) {
				throw new FileNotFoundException("file \"" + fileOrDirectory + "\" not found!");
			}
		}
		ArrayList/*<File>*/<File> dirs = new ArrayList/*<File>*/<File>();
		LinkedList/*<File>*/<File> dics = new LinkedList/*<File>*/<File>();
		String dir;
		if (file.isDirectory()) {
			dirs.add(file);
			dir = file.getAbsolutePath();
		} else {
			dics.add(file);
			dir = file.getParentFile().getAbsolutePath();
		}
		int index = 0;
		while (index < dirs.size()) {
			File cur = dirs.get(index++);
			File[] files = cur.listFiles();
			for (int i = 0; i < files.length; i ++) {
				File f = files[i];
				if (f.isDirectory()) {
					dirs.add(f);
				} else {
					dics.add(f);
				}
			}
		}
		for (Iterator<File> iter = dics.iterator(); iter.hasNext();) {
			File f = iter.next();
			String name = f.getAbsolutePath().substring(
						dir.length() + 1);
			name = name.replace('\\', '/');
			if (!l.onFileBegin(name)) {
				continue;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), charsetName));
			String word;
			boolean firstInDic = true;
			while ((word = in.readLine()) != null) {
				if (firstInDic) {
					firstInDic = false;
					// ref:http://www.w3.org/International/questions/qa-utf8-bom
					// ZERO WIDTH NO-BREAK SPACE
					// notepad将文件保存为unitcode或utf-8时会在文件开头保存bom字符串
					// notepad根据是否有bom来识别该文件是否是utf-8编码存储的。
					// 庖丁字典需要将这个字符从词典中去掉
					if (word.length() > 0 && CharSet.isBom(word.charAt(0))) {
						word = word.substring(1);
					}
				}
				l.onWord(word);
			}
			l.onFileEnd(name);
			in.close();
		}
	}

}
