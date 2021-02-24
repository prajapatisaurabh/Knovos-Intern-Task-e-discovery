import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class index {
    public static void main(String[] args) throws IOException {
        Path file = null;
        BufferedReader bufferedReader = null;
        try{
            file = Paths.get("C:\\Users\\TJava07\\Project\\demo_data\\ExampleHadoop\\Input\\example.txt");
            InputStream inputStream = Files.newInputStream(file);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            System.out.println("Reading the lines:" + bufferedReader.readLine());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                bufferedReader.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
