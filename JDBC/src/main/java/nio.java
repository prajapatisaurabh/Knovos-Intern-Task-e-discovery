import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class nio {
    public static void main(String[] args) throws IOException {
        FileInputStream fin = new FileInputStream("C:\\Users\\TJava07\\Project\\demo_data\\ExampleHadoop\\Input\\example.txt");
        ReadableByteChannel rbc = fin.getChannel();
        FileOutputStream fo = new FileOutputStream("C:\\Users\\TJava07\\Project\\demo_data\\ExampleHadoop\\Input\\output.txt");
        WritableByteChannel destination = fo.getChannel();
        copyData(rbc, destination);
        rbc.close();
        destination.close();
    }
    private static void copyData(ReadableByteChannel src, WritableByteChannel des) throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(20*1024);
        while (src.read(buffer)!= -1){
            buffer.flip();
            while(buffer.hasRemaining()){
                des.write(buffer);
            }
            buffer.clear();
        }
    }
}
