package cc.pp.analyzer.fudan.core;

import java.util.Map;

public abstract class Extractor {

	protected CWSTagger tag;
	protected POSTagger postag;
	protected StopWords test;

	/**
	 * 权重收敛的默认阈值
	 */
	protected double precision = 0.001;

	/**
	 * 阻尼参数
	 */
	protected double dN = 0.85;

	/**
	 * 窗体大小
	 */
	protected int windowN = 10;

	/**
	 *设置窗口大小 和阻尼系数
	 */
	public void setN(int windowN,double dN){
		this.windowN = windowN;
		this.dN = dN;
	}

	/**
	 * 将权重收敛的阈值设小
	 * 算出来的关键词更精确
	 */
	public void setPrecisionHigh() {
		this.precision = 0.000000001;
	}

	/**
	 * 将权重收敛的阈值设大
	 * 算出来的关键词粗糙，但速度更快
	 */
	public void setPrecisionLow() {
		this.precision = 0.1;
	}

	/**
	 * 将权重收敛的阈值设为默认
	 */
	public void setPrecisionDefault() {
		this.precision = 0.001;
	}

	/**
	 * 权重收敛的阈值
	 */
	public void setPrecision(double precision){
		this.precision = precision;
	}

	abstract public String extract(String str, int num, boolean isWeighted);

	abstract public Map<String, Integer> extract(String str, String posstr, int num);

	abstract public Map<String, Integer> extract(String readFile, int i);

}
