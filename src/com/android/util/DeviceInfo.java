package com.android.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class DeviceInfo {

	public static int reqLenBaseWidth(Context context, float ratio) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return (int) (wm.getDefaultDisplay().getWidth() * ratio);
	}

	public static int reqLenBaseHeight(Context context, float ratio) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return (int) (wm.getDefaultDisplay().getHeight() * ratio);
	}

	public static int scrrenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	public static int scrrenHeight(Context context) {

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	public static int scrrenWidthPixels(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	public static int scrrenHeightPixels(Context context) {

		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	/**********************************************************/
	public static String callCmd(String cmd, String filter) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);

			// 鎵ц鍛戒护cmd锛屽彧鍙栫粨鏋滀腑鍚湁filter鐨勮繖涓?琛?
			while ((line = br.readLine()) != null && line.contains(filter) == false) {
				// result += line;
				Log.i("test", "line: " + line);
			}

			result = line;
			Log.i("test", "result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 妫?鏌ョ綉缁滄槸鍚﹀彲鐢?
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean checkEnable(Context paramContext) {
		boolean i = false;

		NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext.getSystemService("connectivity"))
				.getActiveNetworkInfo();
		if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
			return true;
		return false;
	}

	public static boolean checkWifiEnable(Context Context) {
		WifiManager wifiManager = (WifiManager) Context.getSystemService(android.content.Context.WIFI_SERVICE);
		// 鍒ゆ柇wifi鏄惁寮?鍚?
		return wifiManager.isWifiEnabled();
		// if (!wifiManager.isWifiEnabled()) {
		// wifiManager.setWifiEnabled(true);
		// }
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	/**
	 * 鑾峰彇褰撳墠ip鍦板潃
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		try {

			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			return int2ip(i);
		} catch (Exception ex) {
			return null;
		}
		// return null;
	}

	public static String getMacAddress() {
		String result = "";
		String Mac = "";
		result = callCmd("busybox ifconfig", "HWaddr");

		// 濡傛灉杩斿洖鐨剅esult == null锛屽垯璇存槑缃戠粶涓嶅彲鍙?
		if (result == null) {
			return "缃戠粶鍑洪敊锛岃妫?鏌ョ綉缁?";
		}

		// 瀵硅琛屾暟鎹繘琛岃В鏋?
		// 渚嬪锛歟th0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
		if (result.length() > 0 && result.contains("HWaddr") == true) {
			Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
			Log.i("test", "Mac:" + Mac + " Mac.length: " + Mac.length());

			if (Mac.length() > 1) {
				Mac = Mac.replaceAll(" ", "");
				result = "";
				String[] tmp = Mac.split(":");
				for (int i = 0; i < tmp.length; ++i) {
					result += tmp[i];
				}
			}
			Log.i("test", result + " result.length: " + result.length());
		}
		return result;
	}

	public static String getWifiIp(Context paramContext) {

		// 鑾峰彇wifi鏈嶅姟
		WifiManager wifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
		// 鍒ゆ柇wifi鏄惁寮?鍚?
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return intToIp(ipAddress);
	}

	/**
	 * 灏唅p鐨勬暣鏁板舰寮忚浆鎹㈡垚ip褰㈠紡
	 * 
	 * @param ipInt
	 * @return
	 */
	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	/**
	 * 鏍规嵁鎵嬫満鐨勫垎杈ㄧ巼浠? dp 鐨勫崟浣? 杞垚涓? px(鍍忕礌)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 鏍规嵁鎵嬫満鐨勫垎杈ㄧ巼浠? px(鍍忕礌) 鐨勫崟浣? 杞垚涓? dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static boolean isConnecting2Internet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)

				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		return sdf.format(new java.util.Date());
	}

	
	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context)
	{

		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}
}
