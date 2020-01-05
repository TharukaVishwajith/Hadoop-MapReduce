/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package week4.assignment;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class Question1 {

    enum HotelCount{
        HOTEL_COUNT_365
    }

    public static class Question1Mapper extends
            Mapper<Object, Text, Text, IntWritable> {
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] arr =  value.toString().split(",");
            if(arr.length == 12){
                if(arr[11].equals("365")){
                    context.getCounter(HotelCount.HOTEL_COUNT_365).increment(1L);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args)
                .getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Question 1 <in> <out>");
            System.exit(2);
        }

        Path input = new Path(otherArgs[0]);
        Path outputDir = new Path(otherArgs[1]);

        Job job = Job.getInstance(conf, "Question 1");
        job.setJarByClass(Question1.class);
        job.setMapperClass(Question1.Question1Mapper.class);
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(NullWritable.class);
        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, outputDir);
        int code = job.waitForCompletion(true) ? 0 : 1;
        if (code == 0) {
            Counter counter = job.getCounters().findCounter(HotelCount.HOTEL_COUNT_365);
            System.out.println(counter.getDisplayName() + "\t"
                    + counter.getValue());
        }
        // Clean up empty output directory
        FileSystem.get(conf).delete(outputDir, true);
        System.exit(code);
    }
}
