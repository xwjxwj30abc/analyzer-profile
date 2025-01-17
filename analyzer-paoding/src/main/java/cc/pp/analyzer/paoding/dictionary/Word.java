package cc.pp.analyzer.paoding.dictionary;

public class Word implements Comparable<Object>, CharSequence {

	public static final int DEFAUL = 0;
	private String text;
	private int modifiers = DEFAUL;

	public Word() {
	}

	public Word(String text) {
		this.text = text;
	}

	public Word(String text, int modifiers) {
		this.text = text;
		this.modifiers = modifiers;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getModifiers() {
		return modifiers;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	public int compareTo(Object obj) {
		return this.text.compareTo(((Word) obj).text);
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public int length() {
		return text.length();
	}

	public boolean startsWith(Word word) {
		return text.startsWith(word.text);
	}

	@Override
	public char charAt(int j) {
		return text.charAt(j);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return text.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return text.equals(((Word) obj).text);
	}

	public void setNoiseCharactor() {
		modifiers |= 1;
	}

	public void setNoiseWord() {
		modifiers |= (1 << 1);
	}

	public boolean isNoiseCharactor() {
		return (modifiers & 1) == 1;
	}

	public boolean isNoise() {
		return isNoiseCharactor() || isNoiseWord();
	}

	public boolean isNoiseWord() {
		return (modifiers >> 1 & 1) == 1;
	}

	public static void main(String[] args) {
		Word w = new Word("");
		System.out.println(w.isNoiseCharactor());
		w.setNoiseCharactor();
		System.out.println(w.isNoiseCharactor());
		System.out.println(w.isNoiseWord());
		w.setNoiseWord();
		System.out.println(w.isNoiseWord());
	}

}
