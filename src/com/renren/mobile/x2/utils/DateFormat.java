package com.renren.mobile.x2.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @说明 日期格式输出
 * */
public class DateFormat {
	
	public static final long ONE_DAY = 86400000l;
	
	/**
	 * 1分钟内：ns;
	 *  1小时内：n分钟前;
	 *  60分钟-5小时：n小时前 ;
	 *  59分钟前 今天内（5小时外）： 12:00;
	 * 今天 23:00 -3小时内：n天前;
	 * 3天外-今年内：MM-DD;
	 * 今年外：YYYY-MM
	 * 
	 * @return 返回当前时间的字符串
	 */
	public static String getNowStr(long time) {
		StringBuffer sb = new StringBuffer();
		final long now = System.currentTimeMillis();
		final long sub = now - time;
		if (sub <  60 * 1000) {
			long s = sub/1000;
			if(s>1){
				sb.append(s+"秒前");
			}
			else{
				sb.append("1秒前");
			}
			
		} else if (sub < 60 * 60 * 1000) {
			sb.append(sub / 60 / 1000 + "分钟前");
		}else if(sub<5*60*60*1000){
			sb.append(sub /60/ 60 / 1000 + "小时前");
		}
		else {
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			calendar.setTimeInMillis(now);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			final long yesterday = calendar.getTimeInMillis();
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			final long lastyear = calendar.getTimeInMillis();
			calendar.setTimeInMillis(time);
			if (time > yesterday - 24 * 60 * 60 * 1000&&time<yesterday-2*24*60*60*1000) {
				//sb.append("昨天 ");
				sb.append((yesterday/24/60/60/1000+1)+"天前");
				return sb.toString();
			} else {
				if (time < lastyear) {
					sb.append(calendar.get(Calendar.YEAR));
					sb.append("-");
				}
				
				sb.append(zeroFill(calendar.get(Calendar.MONTH) + 1));
				sb.append("-");
				sb.append(zeroFill(calendar.get(Calendar.DAY_OF_MONTH)));
				return sb.toString();
			}
			/*int hour = calendar.get(Calendar.HOUR_OF_DAY);
			sb.append(zeroFill(hour));
			// if (hour < 10) {
			// sb.append("0");
			// }
			// sb.append(hour);
			sb.append(":");
			int minute = calendar.get(Calendar.MINUTE);
			// if (hour < 10) {
			// sb.append("0");
			// }
			// sb.append(minute);
			sb.append(zeroFill(minute));*/
		}
		return sb.toString();
	}

	/**
	 * 将 01-23 12:50 的格式转换为 今天的时间、昨天、前天或者直接返回
	 * 
	 * @param date
	 * @return
	 */
	public static String recently(String date) {
		int idx0 = date.indexOf('-');
		int idx1 = date.indexOf(' ');
		int idx2 = date.indexOf(':');
		if (idx0 > -1 && idx1 > -1) {
			try {
				String mStr = date.substring(0, idx0);
				String dStr = date.substring(idx0 + 1, idx1);
				String hStr = date.substring(idx1 + 1, idx2);
				int m = Integer.parseInt(mStr) - 1;
				int d = Integer.parseInt(dStr);
				int h = Integer.parseInt(hStr);
				cal.setTime(new Date());
				int currM = cal.get(Calendar.MONTH);
				if (m == currM) {
					int currD = cal.get(Calendar.DAY_OF_MONTH);
					if (d == currD) {
						int currH = cal.get(Calendar.HOUR_OF_DAY);
						if (h == currH) {
							return "1小时内";
						} else if (currH - h > 10) {
							return "11小时前";
						} else if (currH - h > 9) {
							return "10小时前";
						} else if (currH - h > 8) {
							return "9小时前";
						} else if (currH - h > 7) {
							return "8小时前";
						} else if (currH - h > 6) {
							return "7小时前";
						} else if (currH - h > 5) {
							return "6小时前";
						} else if (currH - h > 4) {
							return "5小时前";
						} else if (currH - h > 3) {
							return "4小时前";
						} else if (currH - h > 2) {
							return "3小时前";
						} else if (currH - h > 1) {
							return "2小时前";
						} else if (currH - h > 0) {
							return "1小时前";
						}
					} else if (currD - d > 21) {
						return "3周前";
					} else if (currD - d > 14) {
						return "2周前";
					} else if (currD - d > 7) {
						return "上周";
					} else if (currD - d > 1) {
						return "2天前";
					} else if (currD - d > 0) {
						return "昨天";
					}
				} else if (currM - m > 11) {
					return "更早";
				} else if (currM - m > 5) {
					return "半年前";
				} else if (currM - m > 1) {
					return "2个月前";
				} else if (currM - m > 0) {
					return "上个月";
				} else {
					return "更早";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 将 01-23 转换为 今天 或者 直接返回
	 * 
	 * @param date
	 * @return
	 */
	public static String birthday(long m, long d) {
		cal.setTime(new Date());
		if (m == cal.get(Calendar.MONTH) + 1 && d == cal.get(Calendar.DAY_OF_MONTH)) {
			return "今天";
		}
		return m + "月" + d + "日";
	}

	//
	// /**
	// * 将给定的日期字符串解析为Date，parse 类型为"Thu, 29 Mar 2007 10:20:03 +0800" 的日期
	// *
	// * @param sdate 日期字符串
	// * @return 返回Date对象
	// * @throws Exception
	// */
	// public static Date parse(String sdate) throws Exception {
	// if (sdate == null || sdate.indexOf(",") == -1) {
	// throw new Exception("日期格式不合法！");
	// }
	// String tmp = sdate.substring(sdate.indexOf(",") + 2);
	// RrVector arrdstr = StringProcess.splitString(" ", tmp);
	// RrVector arrtime = StringProcess.splitString(":", (String) arrdstr
	// .elementAt(3));
	// if (arrdstr.size() != 5 && arrtime.size() != 3) {
	// throw new Exception("日期格式不合法！");
	// }
	// cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt((String) arrdstr
	// .elementAt(0)));
	// cal.set(Calendar.MONTH, parseMonth((String) arrdstr.elementAt(1)));
	// cal.set(Calendar.YEAR, Integer.parseInt((String) arrdstr.elementAt(2)));
	// cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String) arrtime
	// .elementAt(0)));
	// cal.set(Calendar.MINUTE, Integer
	// .parseInt((String) arrtime.elementAt(1)));
	// cal.set(Calendar.SECOND, Integer
	// .parseInt((String) arrtime.elementAt(2)));
	// return cal.getTime();
	// }

	/**
	 * 转换为"Thu, 29 Mar 2007 10:20:03 +0800" 的日期字符串
	 * 
	 * @param date
	 *            日期对象
	 * @return 返回日期的字符串
	 */
	public static String format(Date date) {
		cal.setTime(date);
		StringBuffer sb = new StringBuffer("");
		sb.append(convertWeek(cal.get(Calendar.DAY_OF_WEEK))).append(", ");
		sb.append(cal.get(Calendar.DAY_OF_MONTH)).append(" ");
		sb.append(convertMonth(cal.get(Calendar.MONTH))).append(" ");
		sb.append(cal.get(Calendar.YEAR)).append(" ");
		sb.append(cal.get(Calendar.HOUR_OF_DAY)).append(":");
		sb.append(zeroFill(cal.get(Calendar.MINUTE))).append(":");
		sb.append(zeroFill(cal.get(Calendar.SECOND))).append(" ");
		sb.append("+0800");
		return sb.toString();
	}

	/**
	 * 获取当前月 0:一月...
	 * 
	 * @return 月份
	 */
	public static int getMonth() {
		cal.setTime(new Date());
		return cal.get(Calendar.MONTH);
	}

	/**
	 * 获得当前时间 2008/7/26 12:48:01
	 * 
	 * @return 返回当前时间的字符串
	 */
	public static String now1() {
		cal.setTime(new Date());
		StringBuffer sb = new StringBuffer("");
		sb.append(cal.get(Calendar.YEAR)).append("/");
		sb.append(parseMonth(convertMonth(cal.get(Calendar.MONTH))) + 1).append("/");
		sb.append(cal.get(Calendar.DAY_OF_MONTH)).append(" ");
		sb.append(cal.get(Calendar.HOUR_OF_DAY)).append(":");
		sb.append(zeroFill(cal.get(Calendar.MINUTE))).append(":");
		sb.append(zeroFill(cal.get(Calendar.SECOND)));
		return sb.toString();
	}

	/**
	 * 获得当前时间 20080726125221
	 * 
	 * @return 返回当前时间的字符串
	 */
	public static String now2() {
		cal.setTime(new Date());
		StringBuffer sb = new StringBuffer("");
		sb.append(cal.get(Calendar.YEAR));
		sb.append(zeroFill(parseMonth(convertMonth(cal.get(Calendar.MONTH))) + 1));
		sb.append(zeroFill(cal.get(Calendar.DAY_OF_MONTH)));
		sb.append(zeroFill(cal.get(Calendar.HOUR_OF_DAY)));
		sb.append(zeroFill(cal.get(Calendar.MINUTE)));
		sb.append(zeroFill(cal.get(Calendar.SECOND)));
		return sb.toString();
	}

	/**
	 * 获得当前时间 12:48
	 * 
	 * @return 返回当前时间的字符串
	 */
	public static String now3() {
		cal.setTime(new Date());
		StringBuffer sb = new StringBuffer("");
		sb.append(cal.get(Calendar.HOUR_OF_DAY)).append(":");
		sb.append(zeroFill(cal.get(Calendar.MINUTE)));
		return sb.toString();
	}

	/**
	 * 获取当前时间 06-23 12:48
	 * 
	 * @return
	 */
	public static String now4() {
		cal.setTime(new Date());
		StringBuffer sb = new StringBuffer("");
		sb.append(zeroFill(cal.get(Calendar.MONTH) + 1)).append("-");
		sb.append(zeroFill(cal.get(Calendar.DATE))).append(" ");
		sb.append(zeroFill(cal.get(Calendar.HOUR_OF_DAY))).append(":");
		sb.append(zeroFill(cal.get(Calendar.MINUTE)));
		return sb.toString();
	}

	/**
	 * 获得当前时间 2008-7-26 12:48:01
	 * 
	 * @return 返回当前时间的字符串
	 */

	public static String now5() {
		cal.setTime(new Date());
		StringBuffer sb = new StringBuffer("");
		sb.append(cal.get(Calendar.YEAR)).append("-");
		sb.append(parseMonth(convertMonth(cal.get(Calendar.MONTH))) + 1).append("-");
		sb.append(cal.get(Calendar.DAY_OF_MONTH)).append(" ");
		sb.append(cal.get(Calendar.HOUR_OF_DAY)).append(":");
		sb.append(zeroFill(cal.get(Calendar.MINUTE))).append(":");
		sb.append(zeroFill(cal.get(Calendar.SECOND)));
		return sb.toString();
	}

	/**
	 * 截去时间中无用的信息
	 * 
	 * @param time
	 *            年-月-日 小时-分钟-秒
	 * @return 月-日 小时-分钟
	 */
	public static String cutTime(String time) {
		StringBuffer buffer = new StringBuffer(time.substring(5, 10));
		buffer.append(" ");
		buffer.append(time.substring(11, 16));
		return buffer.toString();
	}

	/**
	 * 将整数形式的月份转为字符串形式
	 * 
	 * @param month
	 *            整数月份
	 * @return 返回字符串月份
	 */
	private static String convertMonth(int month) {
		String y = "Jan";
		switch (month) {
		case 0:
			y = "Jan";
			break;
		case 1:
			y = "Feb";
			break;
		case 2:
			y = "Mar";
			break;
		case 3:
			y = "Apr";
			break;
		case 4:
			y = "May";
			break;
		case 5:
			y = "Jun";
			break;
		case 6:
			y = "Jul";
			break;
		case 7:
			y = "Aug";
			break;
		case 8:
			y = "Sep";
			break;
		case 9:
			y = "Oct";
			break;
		case 10:
			y = "Nov";
			break;
		case 11:
			y = "Dec";
			break;
		default:
			y = "Jan";
			break;

		}
		return y;
	}

	/**
	 * 将字符串月份转为整数月份
	 * 
	 * @param month
	 *            字符串月份
	 * @return 返回整数月份
	 */
	private static int parseMonth(String month) {
		int imonth = 0;
		if ("Jan".equals(month)) {
			imonth = 0;
		} else if ("Feb".equals(month)) {
			imonth = 1;
		} else if ("Mar".equals(month)) {
			imonth = 2;
		} else if ("Apr".equals(month)) {
			imonth = 3;
		} else if ("May".equals(month)) {
			imonth = 4;
		} else if ("Jun".equals(month)) {
			imonth = 5;
		} else if ("Jul".equals(month)) {
			imonth = 6;
		} else if ("Aug".equals(month)) {
			imonth = 7;
		} else if ("Sep".equals(month)) {
			imonth = 8;
		} else if ("Oct".equals(month)) {
			imonth = 9;
		} else if ("Nov".equals(month)) {
			imonth = 10;
		} else if ("Dec".equals(month)) {
			imonth = 11;
		} else {
			imonth = 0;
		}
		return imonth;
	}

	/**
	 * 将整数星期转为字符串星期
	 * 
	 * @param month
	 *            整数星期
	 * @return 返回字符串星期
	 */
	private static String convertWeek(int week) {

		switch (week) {
		case Calendar.SUNDAY:
			return "Sun";
		case Calendar.MONDAY:
			return "Mon";
		case Calendar.TUESDAY:
			return "Tue";
		case Calendar.WEDNESDAY:
			return "Wed";
		case Calendar.THURSDAY:
			return "Thu";
		case Calendar.FRIDAY:
			return "Fri";
		case Calendar.SATURDAY:
			return "Sat";
		default:
			return "Sun";

		}
	}

	/**
	 * 不够两位数前面补零，否则输出该整数的字符串形式
	 * 
	 * @param value
	 *            一个整数
	 * @return 返回一个不小于两位的字符串
	 */
	private static String zeroFill(int value) {
		String ret = "";
		if (value < 10) {
			ret = "0" + value;
		} else {
			ret = value + "";
		}
		return ret;

	}

	// private static String longToTime(long data){
	// Date temp = new Date(data);
	// SimpleDateFormat dateFormater = new SimpleDateFormat("MM-dd HH:mm");
	// final String time = dateFormater.format(temp);
	// return time;
	// }

	// private static TimeZone tz;

	private static Calendar cal = Calendar.getInstance(TimeZone.getDefault());

	public static String getNowStrByChat(long time) {

		StringBuilder sb = new StringBuilder();
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(time);
		String hour = null;
		if(calendar.getTime().getHours()<10){
			hour = "0"+calendar.getTime().getHours();
		}else{
			hour = ""+calendar.getTime().getHours();
		};
		String minutes = null;
		if(calendar.getTime().getMinutes()<10){
			minutes = "0"+calendar.getTime().getMinutes();
		}else{
			minutes = ""+calendar.getTime().getMinutes();
		};
		
		
		
		sb.append(hour+":"+minutes);
		return sb.toString();
	}

	public static String getDateByChat(long time) {

		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(time);
		int year = calendar.getTime().getYear();
		int mouth = calendar.getTime().getMonth()+1;
		int date = calendar.getTime().getDate();
	
		long today = System.currentTimeMillis();
		calendar.setTimeInMillis(today);
		int year_today = calendar.getTime().getYear();
		int mouth_today = calendar.getTime().getMonth()+1;
		int date_today = calendar.getTime().getDate();
		
		if(year == year_today && mouth_today == mouth && date_today == date){
			return "今天";
		}
		if(year == year_today){
			if(today - time <= 86400000){
				return "昨天";
			}
		}
		
		sb.append((year+1900)+"."+mouth+"."+date);
		return sb.toString();
	}
	
	public static String getDateByChatSession(long time) {

		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(time);
		int year = calendar.getTime().getYear();
		int mouth = calendar.getTime().getMonth()+1;
		int date = calendar.getTime().getDate();
	
		long today = System.currentTimeMillis();
		calendar.setTimeInMillis(today);
		int year_today = calendar.getTime().getYear();
		int mouth_today = calendar.getTime().getMonth()+1;
		int date_today = calendar.getTime().getDate();
		
		if(year == year_today && mouth_today == mouth && date_today == date){
			return "";
		}
		long deltaTime = today - time;
		if(deltaTime<=ONE_DAY*3){
			if(deltaTime>ONE_DAY<<2){
				return "前天";
			}else{
				if(deltaTime<=ONE_DAY){
					return "昨天";
				}else{
					calendar.setTimeInMillis(time+ONE_DAY);
					if(calendar.getTime().getDate() == date_today){
						return "昨天";
					}
					return "前天";
				}
			}
		}
		
//		if(year == year_today){
//			
//			if(today - time <= ONE_DAY<<2){
//				return "昨天";
//			}
//			if(today - time <= 172800000){
//				return "前天";
//			}
//		}
		
		sb.append((year+1900)+ "-" + mouth+"-"+date);
		return sb.toString();
	}
	public static String getDateByChatSession1(long time) {
		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(time);
		int year = calendar.getTime().getYear();
		int mouth = calendar.getTime().getMonth()+1;
		int date = calendar.getTime().getDate();
		long today = System.currentTimeMillis();
		calendar.setTimeInMillis(today);

		sb.append((year+1900)+ "-" + mouth+"-"+date);
		return sb.toString();
	}
	

	/**
	 * 判断两个时间是否是在同一天
	 * */
	public static boolean isInOneDate(long start, long end) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(start);
		int start_day = calendar.getTime().getDate();
		int start_year = calendar.getTime().getYear();
		int start_mouth = calendar.getTime().getMonth()+1;
		calendar.setTimeInMillis(end);
		int end_day = calendar.getTime().getDate();
		int end_year = calendar.getTime().getYear();
		int end_mouth = calendar.getTime().getMonth()+1;
		if(start_day == end_day&&start_mouth == end_mouth && start_year == end_year){
			return true;
		}else{
			return false;
		}
	}

}
