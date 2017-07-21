package word;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;

import java.io.Reader;

/**
 * Created by datapark-2 on 2017/6/23.
 */
public  class MyWordAnalyzer extends Analyzer {
    Segmentation segmentation = null;

    public MyWordAnalyzer() {
        segmentation = SegmentationFactory.getSegmentation(
                SegmentationAlgorithm.BidirectionalMaximumMatching);
    }


    public MyWordAnalyzer(Segmentation segmentation) {
        this.segmentation = segmentation;
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new MyWordTokenizer(segmentation);
        return new TokenStreamComponents(tokenizer);
    }
}