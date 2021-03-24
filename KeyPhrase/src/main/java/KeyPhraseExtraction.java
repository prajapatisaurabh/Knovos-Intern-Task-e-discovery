import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

//import java.nio.file.FileSystem;

public class KeyPhraseExtraction extends Configured implements Tool
{

    private  static final String TF_Map_Output = "TF_Map_Output";



    public static class TF_Map extends Mapper<LongWritable, Text, Text, IntWritable>{
        private static final IntWritable one = new IntWritable(1);
        private static final Pattern Word_Boundary = Pattern.compile("\\s*\\b\\s*");
        public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
            String Line = lineText.toString();
            String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
            Text currentWord = new Text();
            for(String word : Word_Boundary.split(Line)){
                if (word.isEmpty()){
                    continue;
                }
                currentWord = new Text(word + "####" + fileName);
                context.write(currentWord, one);
            }
        }
    }


    public static class TF_Reduce extends Reducer<Text, IntWritable, Text, DoubleWritable>{
        public void reduce(Text word, Iterable<IntWritable> counts, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable count : counts){
                sum += count.get();
            }
            double tf = Math.log10(10) + Math.log10(sum);
            context.write(word, new DoubleWritable(tf));
        }
    }
    public class TF_IDFmap extends Mapper<LongWritable, Text, Text, Text>{
        private Text word_key = new Text();
        private Text file_name = new Text();
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] word_file_tf = value.toString().split("\\t");
            String[] word_file = word_file_tf[0].toString().split("####");
            this.word_key.set(word_file[0]);
            this.file_name.set(word_file[1] + "=" + word_file_tf[1]);
            context.write(word_key, file_name);
        }
    }
    public class TF_IDFred extends Reducer<Text, Text, Text, DoubleWritable>{
        private Text word_file_key = new Text();
        private double tfidf;
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
            double docswithword = 0;
            Map<String, Double> tempValues = new HashMap<String, Double>();
            for(Text v : values){
                String[] filecounter = v.toString().split("=");
                docswithword++;
                tempValues.put(filecounter[0], Double.valueOf(filecounter[1]));
            }
            int numoffiles = context.getConfiguration().getInt("totalinputfiles", 0);
            double idf = Math.log10(numoffiles/docswithword);
            for(String temp_tfidf_file : tempValues.keySet()){
                this.word_file_key.set(key.toString() + "####" + temp_tfidf_file);
                this.tfidf = tempValues.get(temp_tfidf_file)*idf;
                if(this.tfidf>1 && this.tfidf<1.4){
                    context.write(this.word_file_key, new DoubleWritable(this.tfidf) );

                }
//                context.write(this.word_file_key, new DoubleWritable(this.tfidf) );
            }
        }

    }

    public int run(String[] args) throws Exception{
        FileSystem fs = FileSystem.get(getConf());
        Path InputFilePath = new Path(args[0]);
        Path OutputPath = new Path(args[1]);
        if(fs.exists(OutputPath)){
            fs.delete(OutputPath, true);
        }

        Path TermFrequencyPath = new Path(TF_Map_Output);
        FileStatus[] FilesList = fs.listStatus(InputFilePath);
        final int totalInputFiles = FilesList.length;

        Job job1 = new Job(getConf(), "TermFrequency");
        job1.setJarByClass(this.getClass());
        job1.setMapperClass(TF_Map.class);
        job1.setReducerClass(TF_Reduce.class);

        job1.setMapOutputKeyClass(Text.class);
        job1.setMapOutputValueClass(IntWritable.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job1, InputFilePath);
        FileOutputFormat.setOutputPath(job1, TermFrequencyPath);

        job1.waitForCompletion(true);

        Job job2 = new Job(getConf(), "CalculateTFIDF");
        job2.getConfiguration().setInt("totalinputfiles", totalInputFiles);
        job2.setJarByClass(this.getClass());
        job2.setMapperClass(TF_IDFmap.class);
        job2.setReducerClass(TF_IDFred.class);
        job2.setMapOutputKeyClass(Text.class);
        job2.setMapOutputValueClass(Text.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job2, TermFrequencyPath);
        FileOutputFormat.setOutputPath(job2, OutputPath);

        return job2.waitForCompletion(true) ? 0 : 1;


    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new KeyPhraseExtraction(), args);
        System.exit(res);
    }

}