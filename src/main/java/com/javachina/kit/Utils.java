package com.javachina.kit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blade.web.http.Request;
import com.javachina.ImageTypes;
import com.javachina.ext.Funcs;
import com.javachina.ext.markdown.BlockEmitter;
import com.javachina.ext.markdown.Configuration;
import com.javachina.ext.markdown.Processor;

import blade.kit.StringKit;

public class Utils {

	public static String getIpAddr(Request request) {
		if (null == request) {
			return "0.0.0.0";
		}
		String ip = request.header("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.header("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.header("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.address();
		}
		return ip;
	}
	
	public static Set<String> getAtUsers(String str){
		Set<String> users = new HashSet<String>();
		if(StringKit.isNotBlank(str)){
			Pattern pattern= Pattern.compile("\\@([a-zA-Z_0-9-]+)\\s");
			Matcher matcher = pattern.matcher(str);
			while(matcher.find()){
//				System.out.println(matcher.group(0));
//				System.out.println(matcher.group(1));
				users.add(matcher.group(1));
			}
		}
		
		return users;
	}
	
	public static String getAvatar(String avatar, ImageTypes imageTypes){
		return QiniuKit.getUrl(avatar + '-' + imageTypes.toString());
	}
	
	public static boolean isSignup(String user_name){
		if(StringKit.isNotBlank(user_name)){
			user_name = user_name.toLowerCase();
			if(user_name.indexOf("admin") != -1 ||
					user_name.indexOf("test") != -1 ||
					user_name.indexOf("support") != -1){
				return false;
			}
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("resource")
	public static void copyFileUsingFileChannels(File source, File dest) throws IOException {    
        FileChannel inputChannel = null;    
        FileChannel outputChannel = null;    
	    try {
	        inputChannel = new FileInputStream(source).getChannel();
	        outputChannel = new FileOutputStream(dest).getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    } finally {
	        inputChannel.close();
	        outputChannel.close();
	    }
	}
	
	public static Integer getTodayTime() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		return Integer.valueOf(String.valueOf(today.getTimeInMillis()).substring(0, 10));
	}
	
	public static Integer getYesterdayTime() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, -24);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		return Integer.valueOf(String.valueOf(today.getTimeInMillis()).substring(0, 10));
	}

	public static Integer getTomorrowTime() {
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.set(Calendar.HOUR_OF_DAY, 24);
		tomorrow.set(Calendar.MINUTE, 0);
		tomorrow.set(Calendar.SECOND, 0);
		return Integer.valueOf(String.valueOf(tomorrow.getTimeInMillis()).substring(0, 10));
	}
	
	public static void run(Runnable t){
		Executors.newSingleThreadExecutor().submit(t);
	}

	final static Configuration config = Configuration.builder()
			.setSafeMode(true)
            .setCodeBlockEmitter(new CodeBlockEmitter())
            // Fenced code blocks are only available in 'extended mode'
            .forceExtentedProfile()
            .build();
	
	public static class CodeBlockEmitter implements BlockEmitter {
		@Override
		public void emitBlock(final StringBuilder out, final List<String> lines, final String meta) {
			out.append("<pre><code");
			if (meta != null && !meta.isEmpty()) {
//				out.append(" class=\"language-");
				out.append(" class=\"");
				out.append(meta);
				out.append('"');
			}
			out.append(">\r\n");
			for (final String l : lines) {
				escapedAdd(out, l);
				out.append('\n');
			}
			out.append("</code></pre>");
		}
	}
	
	public static void escapedAdd(final StringBuilder sb, final String str) {
		for (int i = 0; i < str.length(); i++) {
			final char ch = str.charAt(i);
			if (ch < 33 || Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
				sb.append(' ');
			} else {
				switch (ch) {
				case '"':
					sb.append("&quot;");
					break;
				case '\'':
					sb.append("&apos;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				default:
					sb.append(ch);
					break;
				}
			}
		}
	}
	
	public static String markdown2html(String content) {
		if(StringKit.isBlank(content)){
			return content;
		}
		
		String member = Funcs.base_url("/member/");
		String content_ = content.replaceAll("@([a-zA-Z_0-9-]+)\\s", "<a href='"+ member +"$1'>@$1</a>&nbsp;");
		
		String processed = Processor.process(content_, config);
		
		if(processed.indexOf("[mp3:") != -1){
			processed = processed.replaceAll("\\[mp3:(\\d+)\\]", "<iframe frameborder='no' border='0' marginwidth='0' marginheight='0' width=330 height=86 src='http://music.163.com/outchain/player?type=2&id=$1&auto=0&height=66'></iframe>");
		}
		
		return Funcs.emoji(processed);
	}
	
	/**
	 * 计算帖子权重
	 * 
	 * 根据点赞数、收藏数、评论数、下沉数、创建时间计算
	 * 
	 * @param loves			点赞数：权重占比1
	 * @param favorites 	点赞数：权重占比2
	 * @param comment		点赞数：权重占比2
	 * @param sinks			点赞数：权重占比-1
	 * @param create_time	创建时间，越早权重越低
	 * @return
	 */
	public static double getWeight(Long loves, Long favorites, Long comment, Long sinks, Long create_time) {
		
		long score = Math.max(loves - 1, 1) + favorites * 2 + comment * 2 - sinks;
		
		// 投票方向
		int sign = (score == 0) ? 0 : (score > 0 ? 1 : -1);
		
		// 帖子争议度
		double order = Math.log10(Math.max(Math.abs(score), 1));
		
		// 1459440000是项目创建时间
		double seconds = create_time - 1459440000;
		return Double.parseDouble(String.format("%.2f", order + sign * seconds / 45000));
	}
	
}
