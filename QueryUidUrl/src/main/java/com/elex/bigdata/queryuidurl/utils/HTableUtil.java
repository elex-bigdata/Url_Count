package com.elex.bigdata.queryuidurl.utils;

import org.apache.hadoop.hbase.util.Bytes;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: yb
 * Date: 3/3/14
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTableUtil {
  public static int putBatch=1000;
  public static int caching=500;
  public static int getBatch=10;
  public static int cacheNum=1000;
  public static final String Query_Tables="Query.table";
  public static final String Query_Uid_Tables="Query.uidTable";
  public static byte[] getNextRk(byte[] rk){
    byte[] tail=Bytes.toBytesBinary("\\xFF\\xFF\\xFF\\xFF");
    return Bytes.add(rk,tail);
  }
}
