package com.xiaoxiaomo.mahout;

import org.apache.log4j.Logger;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;

public class WordSplit {

	private String words="我们都是爱国主义者";
	private Logger logger=Logger.getLogger(WordSplit.class);
	public static void main(String[] args) {
		
		//new WordSplit().JiebaSlpit();
		new  WordSplit().IKSplit();
		
	}

	/**
	 *
	 */
//	private void JiebaSlpit(){
//		StringBuilder result = new StringBuilder();
//		//构造jieba分词器
//		 JiebaSegmenter JIEBA_SEGMENTER = new JiebaSegmenter();
//		 //开始分词
//		 //List<SegToken> process = JIEBA_SEGMENTER.process(words, SegMode.INDEX);//3116
//		 List<SegToken> process = JIEBA_SEGMENTER.process(words, SegMode.SEARCH);//3374
//		 //分词结果
//		 for (SegToken segToken : process) {
//			 result.append(segToken.word.getToken()).append("\t");
//		}
//		 logger.info(result.toString());
//	}
	
	private void IKSplit(){
	
		StringBuilder result = new StringBuilder();
			IKSegmenter ik = new IKSegmenter(new StringReader(words), true);  //561
			try {
				Lexeme word = null;
				while((word=ik.next())!=null) {
					result.append(word.getLexemeText()).append("\t");
				}
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

			logger.info(result.toString());
	}
	
}
