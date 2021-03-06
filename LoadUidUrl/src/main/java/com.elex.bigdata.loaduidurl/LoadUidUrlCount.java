package com.elex.bigdata.loaduidurl;


import com.elex.bigdata.loaduidurl.utils.HTableUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.elex.bigdata.loaduidurl.utils.ScanRangeUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: yb
 * Date: 3/3/14
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadUidUrlCount {
  /*
     accumulate the uid:url-count key-value
     the data stored into htable 'uidUrlCount_22find'
      uidUrlCount_22find
      rk: uidurl
      columns: result:count,timestamp
      get the sum and increase by count
  */
  public static Date startScanTime = null,endScanTime = null;
  public static String timeRange=null;
  public static DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
  private static Logger logger=Logger.getLogger(LoadUidUrlCount.class);
  public static void main(String[] args) throws IOException, ConfigurationException, ParseException {
    // input is args[0],which actual is the path of countuidurl outPut path.
    // output is args[1] which is the name of htable.
    String input=args[0];
    String tableName=args[1];

    Date scanUnitTime=getScanUnit();
    char type=args[2].charAt(0);
    if(type=='s'){
      startScanTime=format.parse(args[2].substring(1));
      endScanTime=getEndScanTime(startScanTime,scanUnitTime);
    }
    else if(type=='e'){
      endScanTime=format.parse(args[2].substring(1));
      startScanTime=getStartScanTime(endScanTime,scanUnitTime);
    }
    else {
      logger.error("args[1] should start with 's' to indicate startTime or 'e' to indicate endTime");
    }
    timeRange=format.format(startScanTime)+"-"+format.format(endScanTime);

    Configuration conf= HBaseConfiguration.create();
    conf.set("table",tableName);
    conf.set("timeRange",timeRange);
    logger.info("config hTable "+tableName);
    Job job=Job.getInstance(conf);
    job.setMapperClass(LoadUidUrlCountMapper.class);
    job.setReducerClass(LoadUidUrlCountReducer.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(NullWritable.class);
    FileInputFormat.addInputPath(job, new Path(input));
    FileOutputFormat.setOutputPath(job,new Path(tableName));
    job.setJarByClass(LoadUidUrlCount.class);
    TableMapReduceUtil.initTableReducerJob(tableName,LoadUidUrlCountReducer.class,job);
    try {
      job.waitForCompletion(true);
    } catch (InterruptedException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (ClassNotFoundException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    logger.info("htable flush after job completion");
  }

}
