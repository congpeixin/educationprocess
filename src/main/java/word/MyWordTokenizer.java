package word;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apdplat.word.recognition.StopWord;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.apdplat.word.segmentation.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by datapark-2 on 2017/6/23.
 */
public class MyWordTokenizer extends Tokenizer {
    private final CharTermAttribute charTermAttribute
            = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttribute
            = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute
            positionIncrementAttribute
            = addAttribute(PositionIncrementAttribute.class);

    private Segmentation segmentation = null;
    private BufferedReader reader = null;
//    private Reader input = ILLEGAL_STATE_READER;
//
//    /** Pending reader: not actually assigned to input until reset() */
//    private Reader inputPending = ILLEGAL_STATE_READER;
    private final Queue<Word> words = new LinkedTransferQueue<Word>();
    private int startOffset=0;

    public MyWordTokenizer() {
        segmentation = SegmentationFactory.getSegmentation(
                SegmentationAlgorithm.BidirectionalMaximumMatching);
    }
    public MyWordTokenizer(Segmentation segmentation) {
        this.segmentation = segmentation;
    }
    private Word getWord() throws IOException {
        Word word = words.poll();
        if(word == null){
            String line;
            while( (line = reader.readLine()) != null ){
                words.addAll(segmentation.seg(line));
            }
            startOffset = 0;
            word = words.poll();
        }
        return word;
    }
    @Override
    public final boolean incrementToken() throws IOException {
        reader=new BufferedReader(input);
        Word word = getWord();
        if (word != null) {
            int positionIncrement = 1;
            //忽略停用词
            while(StopWord.is(word.getText())){
                positionIncrement++;
                startOffset += word.getText().length();
                word = getWord();
                if(word == null){
                    return false;
                }
            }
            charTermAttribute.setEmpty().append(word.getText());

            offsetAttribute.setOffset(startOffset, startOffset
                    +word.getText().length());
            positionIncrementAttribute.setPositionIncrement(
                    positionIncrement);
            startOffset += word.getText().length();
            return true;
        }
        return false;
    }
    @Override
    public void reset() throws IOException {
        super.reset();
//        input = inputPending;
//        inputPending = ILLEGAL_STATE_READER;
    }
}
