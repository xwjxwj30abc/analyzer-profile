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
package cc.pp.analyzer.paoding.analyzer.impl;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.lucene.analysis.Token;

import cc.pp.analyzer.paoding.analyzer.TokenCollector;


/**
 *
 * @author Zhiliang Wang [qieqie.wang@gmail.com]
 *
 * @since 1.1
 */
public class MaxWordLengthTokenCollector implements TokenCollector {

	/**
	 * 存储当前被knife分解而成的Token对象
	 *
	 */
	private LinkedList/* <Token> */<Token> tokens = new LinkedList/* <Token> */<Token>();

	private Token candidate;

	private Token last;

	@Override
	public Iterator/* <Token> */<Token> iterator() {
		if (candidate != null) {
			this.tokens.add(candidate);
			candidate = null;
		}
		Iterator/* <Token> */<Token> iter = this.tokens.iterator();
		this.tokens = new LinkedList/* <Token> */<Token>();
		return iter;
	}

	@Override
	public void collect(String word, int offset, int end) {
		Token c = candidate != null ? candidate : last;
		if (c == null) {
			candidate = new Token(word, offset, end);
		} else if (offset == c.startOffset()) {
			if (end > c.endOffset()) {
				candidate = new Token(word, offset, end);
			}
		} else if (offset > c.startOffset()) {
			if (candidate != null) {
				select(candidate);
			}
			if (end > c.endOffset()) {
				candidate = new Token(word, offset, end);
			} else {
				candidate = null;
			}
		} else if (end >= c.endOffset()) {
			if (last != null && last.startOffset() >= offset
					&& last.endOffset() <= end) {
				for (Iterator/* <Token> */<Token> iter = tokens.iterator(); iter.hasNext();) {
					last = iter.next();
					if (last.startOffset() >= offset && last.endOffset() <= end) {
						iter.remove();
					}
				}
			}
			last = null;
			candidate = new Token(word, offset, end);
		}
	}

	protected void select(Token t) {
		this.tokens.add(t);
		this.last = t;
	}

}
