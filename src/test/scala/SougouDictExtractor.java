import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * Created by Stephen on 6/24/16.
 * 将搜狗词库scel字典放到指定文件夹中，用这个程序将字典里面的数据写入到文件中
 */
public class SougouDictExtractor {

    public static void main(String[] args) {
        try {

            String dicName = "新词";
            RandomAccessFile dictFile = new RandomAccessFile("data/sougou/"+dicName+".scel", "r");
            FileChannel fc = dictFile.getChannel();
            //向txt文件中写数据
            File file = new File("data/sougou/"+dicName+".txt");
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            if (!file.exists()) {
                file.createNewFile();
            }
            ByteBuffer buffer1 = ByteBuffer.allocate(128);
            long hzPosition = 0L;
            fc.read(buffer1);
            if (buffer1.array()[4] == 0x44) {
                hzPosition = 0x2628L;
            }
            if (buffer1.array()[4] == 0x45) {
                hzPosition = 0x26C4L;
            }
            fc.position(hzPosition);

            do {
                ByteBuffer buffer2 = ByteBuffer.allocate(4);
                fc.read(buffer2);
                byte[] num = buffer2.array();
                int samePYcount = num[0] + num[1] * 256;
                int count = num[2] + num[3] * 256;
//                System.out.println("Token length: " + count / 2);//打印长度
                fc.position(fc.position() + count);

                for (int i = 0; i < samePYcount; i++) {
                    ByteBuffer buffer3 = ByteBuffer.allocate(2);
                    fc.read(buffer3);
                    num = buffer3.array();
                    buffer3.clear();
                    int hzBytecount = num[0] + num[1] * 256;
                    ByteBuffer buffer4 = ByteBuffer.allocate(hzBytecount);
                    fc.read(buffer4);
                    byte[] tData = buffer4.array();
                    buffer4.clear();
                    String token = new String(tData, StandardCharsets.UTF_16LE); // Sougou dict encoding is UTF-16 little-endian
                    bw.write(token+"\r\n");
                    System.out.println("Token: " + token);
                    fc.position(fc.position() + 12);
                }
                bw.flush();
//                bw.close();

            } while (fc.position() != fc.size());
            System.out.println("文件写入完毕");
        } catch (FileNotFoundException e) {
            System.err.println("Error: Dict file not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}