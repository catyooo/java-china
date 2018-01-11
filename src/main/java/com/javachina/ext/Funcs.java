package com.javachina.ext;

import com.javachina.Constant;
import com.javachina.ImageTypes;
import com.javachina.kit.DateKit;
import com.javachina.kit.Utils;

import blade.kit.StringKit;

public class Funcs {

	/**
	 * 获取相对路径
	 * @param path
	 * @return
	 */
	public static String base_url(String path) {
		return Constant.SITE_URL + path;
	}
	
	/**
	 * 取某个区间的随机数
	 * @param max
	 * @return
	 */
	public static int random(int max) {
		int radom = Integer.valueOf(StringKit.getRandomNumber(1, max));
		if(radom == 0){
			return 1;
		}
		return radom;
	}
	
	public static String avatar_url(String avatar) {
		return Utils.getAvatar(avatar, ImageTypes.normal);
	}
	
	public static String emoji(String content){
		if(StringKit.isNotBlank(content)){
			if(content.indexOf(":") == -1){
				return content;
			}
			return content.replaceAll(":([a-z_-]{2,30}):", "<img src='"+Constant.CDN_URL+"/assets/emojis/$1.png'  height='20' width='20' />");
		}
		return "";
	}
	
	/**
	 * 格式化日期
	 * @param date
	 * @return
	 */
	public static String fmtdate(Long unixTime) {
		if(null != unixTime){
			return DateKit.formatDateByUnixTime(unixTime, "yyyy-MM-dd");
		}
		return "";
	}
	
	/**
	 * 格式化日期
	 * @param date
	 * @param patten
	 * @return
	 */
	public static String fmtdate(Long unixTime, String patten) {
		if(null != unixTime && StringKit.isNotBlank(patten)){
			return DateKit.formatDateByUnixTime(unixTime, patten);
		}
		return "";
	}
	
	public static String today(String patten){
		return fmtdate((long) DateKit.getCurrentUnixTime(), patten);
	}
	
	/**
	 * 截取字符串个数
	 * @param str
	 * @param count
	 * @return
	 */
	public static String str_count(String str, int count){
		if(StringKit.isNotBlank(str) && count > 0){
			if(str.length() <= count){
				return str;
			}
			return str.substring(0, count);
		}
		return "";
	}
	
	/**
	  * 显示时间，如果与当前时间差别小于一天，则自动用**秒(分，小时)前，如果大于一天则用format规定的格式显示
	  * 
	  * @param ctime时间
	  * @return
	  */
	public static String timespan(Long ctime) {
		String r = "";
		if (ctime == null)
			return r;

		long nowtimelong = System.currentTimeMillis();
		long ctimelong = DateKit.getDateByUnixTime(ctime).getTime();
		long result = Math.abs(nowtimelong - ctimelong);
		
		// 20秒内
		if (result < 20000){
			r = "刚刚";
		} else if (result >= 20000 && result < 60000) {
			// 一分钟内
			long seconds = result / 1000;
			r = seconds + "秒钟前";
		} else if (result >= 60000 && result < 3600000) {
			// 一小时内
			long seconds = result / 60000;
			r = seconds + "分钟前";
		} else if (result >= 3600000 && result < 86400000) {
			// 一天内
			long seconds = result / 3600000;
			r = seconds + "小时前";
		} else {
			long days = result / 3600000 / 24;
			r = days + "天前";
		}
		return r;
	}
	
}
