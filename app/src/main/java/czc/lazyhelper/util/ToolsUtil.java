package czc.lazyhelper.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import czc.lazyhelper.MyApplication;
import czc.lazyhelper.R;

public class ToolsUtil {

	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.v("error", e.toString());
		}
		return false;
	}

	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public static boolean hasGPSDevice(Context context) {
		final LocationManager mgr = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (mgr == null) {
			return false;
		}
		final List<String> providers = mgr.getAllProviders();
		if (providers == null) {
			return false;
		}
		return providers.contains(LocationManager.GPS_PROVIDER);
	}

	public static boolean hasNetworkLocationDevice(Context context) {
		final LocationManager mgr = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (mgr == null) {
			return false;
		}
		final List<String> providers = mgr.getAllProviders();
		if (providers == null) {
			return false;
		}
		return providers.contains(LocationManager.NETWORK_PROVIDER);
	}

	public static int getIntRamdom(int max, int min) {
		return new Random().nextInt(max) % (max - min + 1) + min;
	}



	/**
	 * 判断当前界面是否是桌面
	 */
	public static boolean isHome(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return !rti.get(0).topActivity.getPackageName().equals(
				context.getPackageName());
	}

	public static Bitmap getBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		view.measure(View.MeasureSpec.makeMeasureSpec(0, 0),
				View.MeasureSpec.makeMeasureSpec(0, 0));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		return Bitmap.createBitmap(view.getDrawingCache());
	}

	public static Bitmap getBitmap(Context context, String paramString,
			int bgsid) {
		return getBitmap(context, paramString, bgsid, 10, Color.BLACK);
	}

	public static Bitmap getBitmap(Context context, String paramString,
			int bgsid, int textsize, int textcolor) {
		Button localButton = new Button(context);
		localButton.setBackgroundResource(bgsid);
		localButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, textsize);
		localButton.setText(paramString);
		localButton.setTextColor(textcolor);
		localButton.setDrawingCacheEnabled(true);
		localButton.measure(View.MeasureSpec.makeMeasureSpec(0, 0),
				View.MeasureSpec.makeMeasureSpec(0, 0));
		localButton.layout(0, 0, localButton.getMeasuredWidth(),
				localButton.getMeasuredHeight());
		localButton.buildDrawingCache();
		return Bitmap.createBitmap(localButton.getDrawingCache());
	}

	public static int GetFitFontSize(Context context, String str, int widght,
			int fontsize) {
		int textSize = fontsize;
		TextView tv = new TextView(context);
		tv.setTextSize(textSize);
		Paint paint = new Paint();
		paint = tv.getPaint();
		float lenght = paint.measureText(str);
		while (lenght > widght) {
			textSize--;
			tv.setTextSize(textSize);
			lenght = paint.measureText(str);
		}
		return textSize;
	}

	public static int GetFitFontSizeSp(Context context, String str, int widght,
			int fontsize) {
		int textSize = fontsize;
		TextView tv = new TextView(context);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		Paint paint = new Paint();
		paint = tv.getPaint();
		float lenght = paint.measureText(str);
		while (lenght > widght) {
			textSize--;
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
			lenght = paint.measureText(str);
		}
		return textSize;
	}

	public static float getfontsize(Context context, int fontsize) {
		if (context != null) {
			TextView tv = new TextView(context);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
			return tv.getTextSize();
		} else {
			return fontsize;
		}
	}

	public static int gettextwidth(Context context, String str, int fontsize) {
		TextView tv = new TextView(context);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
		Paint paint = new Paint();
		paint = tv.getPaint();
		return (int) paint.measureText(str);
	}

	public static float getTextHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (float) Math.ceil(fm.descent - fm.ascent);
	}

	public static int gettextwidth(Context context, String str, float fontsize) {
		TextView tv = new TextView(context);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontsize);
		Paint paint = new Paint();
		paint = tv.getPaint();
		return (int) paint.measureText(str);
	}


	// 小数点位数格式
	public static String FormatString(int ws) {
		String str = "";
		switch (ws) {
		case 0:
			str = "###";
			break;
		case 1:
			str = "##0.0";
			break;
		case 2:
			str = "##0.00";
			break;
		case 3:
			str = "##0.000";
			break;
		case 4:
			str = "##0.0000";
			break;
		case 5:
			str = "##0.00000";
			break;
		case 6:
			str = "##0.000000";
			break;
		default:
			str = "###";
			break;
		}
		return str;
	}



	/**
	 * 如获取状态条高度在源码程序中代码： height=
	 * getResources().getDimensionPixelSize(com.android
	 * .internal.R.dimen.status_bar_height);
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static int getStateBarHeight(Context context) throws Exception {
		Class c = Class.forName("com.android.internal.R$dimen");
		Object obj = c.newInstance();
		Field field = c.getField("status_bar_height");
		int x = Integer.parseInt(field.get(obj).toString());
		int y = context.getResources().getDimensionPixelSize(x); // 状态栏高度
		return y;
	}

	/**
	 * @param context
	 * @return Activity高度
	 */
	public static int getAtyHeight(Context context) {
		try {
			DisplayMetrics mDm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(mDm);
			int globalHeight = mDm.heightPixels;
			int stateBarHeight = getStateBarHeight(context);// 状态栏高度
			return (globalHeight - stateBarHeight);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * @param context
	 * @return Activity宽度
	 */
	public static int getAtyWidth(Context context) {
		try {
			DisplayMetrics mDm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(mDm);
			return mDm.widthPixels;
		} catch (Exception e) {
			return 0;
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static void hideSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
		}
	}

	public static void showSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static boolean isExternalStorageAvailabe() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean checkEmail2(String email) {
		Pattern p = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher m = null;
		m = p.matcher(email);
		System.out.println(m.matches());
		return m.matches();
	}

	public static String checknumber(String num) {
		String number = "";
		if (num != null) {
			if (num.length() >= 11) {
				if (num.startsWith("+86")) {
					number = num.substring(3, num.length());
				} else if (num.startsWith("86")) {
					number = num.substring(2, num.length());
				} else {
					number = num;
				}
				if (!isMobileNO(number)) {
					number = "";
				}
			}
		}
		return number;
	}

	public static boolean checknumber2(String num) {
		if (num != null) {
			if (num.length() >= 11) {
				String number = "";
				if (num.startsWith("+86")) {
					number = num.substring(3, num.length());
				} else if (num.startsWith("86")) {
					number = num.substring(2, num.length());
				} else {
					number = num;
				}
				if (isMobileNO(number)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(14[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/***
	 * 行列转换
	 * 
	 * @param ls
	 * @return转换后的list
	 */
	public static List<List<Float>> switctRowCol(List<List<Float>> ls) {
		List<List<Float>> ls_result = new ArrayList<List<Float>>();
		if (ls != null && ls.size() > 0) {
			for (int i = 0; i < ls.get(0).size(); i++) {
				List<Float> ls_tmp = new ArrayList<Float>();
				for (int j = 0; j < ls.size(); j++) {
					ls_tmp.add(ls.get(j).get(i));
				}
				ls_result.add(ls_tmp);
			}
		}
		return ls_result;
	}

	/**
	 * 打开assets里面的图片
	 * 
	 * @param context
	 * @param assetsPath
	 *            图片路径 例如：assets下的a.jpg,那直接传a.jpg
	 */
	public static void openAssetsImage(Context context, String assetsPath) {
		Resources resources = context.getResources();
		AssetManager am = resources.getAssets();
		File file = new File(Environment.getExternalStorageDirectory(),
				"assets.jpg");
		// if (file.exists()) {
		// Intent intent = new Intent(Intent.ACTION_VIEW);
		// intent.setDataAndType(Uri.fromFile(file), "image/*");
		// context.startActivity(intent);
		// }else {
		InputStream inputStream;
		try {
			inputStream = am.open(assetsPath);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			inputStream.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		context.startActivity(intent);
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	public static void showInstalledAppDetails(Context context,
			String packageName) {
		try {
			Intent intent = new Intent();
			final int apiLevel = Build.VERSION.SDK_INT;
			if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", packageName, null);
				intent.setData(uri);
			} else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
				// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
				final String appPkgName = (apiLevel == 8 ? "pkg"
						: "com.android.settings.ApplicationPkgName");
				intent.setAction(Intent.ACTION_VIEW);
				intent.setClassName("com.android.settings",
						"com.android.settings.InstalledAppDetails");
				intent.putExtra(appPkgName, packageName);
			}
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("awen", "启动失败...");
		}
	}

	//计算多边形面积（第一个点跟最后一个点不需要相同）
	public static float getPolygonArea(List<PointF> list) {
		float area = 0;
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				PointF p1 = list.get(i);
				PointF p2 = list.get(i + 1);
				area +=Math.abs(p1.x * p2.y - p2.x * p1.y);
			} else {
				PointF pn = list.get(i);
				PointF p0 = list.get(0);
				area += Math.abs(pn.x * p0.y - p0.x * pn.y);
			}
		}
		area = area / 2;
		return area;
	}
	
	/***
	 * 删除目录下的所有文件，不包括文件夹
	 * @param dir 根路径
	 */
	public static void deleteFiles(File dir) {
		try{
			if(!dir.exists())
				return;
	        if (dir.isDirectory()) {
	            String[] children = dir.list();
	            //递归删除目录中的子目录下
	            for (int i=0; i<children.length; i++) {
	            	deleteFiles(new File(dir, children[i]));
	            }
	        }else {
	        	dir.delete();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	/***
	 * 删除目录下的某个时间前的所有文件，不包括文件夹
	 * @param dir 根目录
	 * @param time 例如删除一个小时前的就传60*60*1000
	 */
	public static void deleteFiles(File dir,long time) {
		if(!dir.exists())
			return;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
            	deleteFiles(new File(dir, children[i]));
            }
        }else {
        	if(System.currentTimeMillis()- dir.lastModified()>time)
        		dir.delete();
		}
    }
	
	public static void checkTopActivity(Context context,String packetName) { 
	     ActivityManager manager = (ActivityManager)
	    		 context.getSystemService(Context.ACTIVITY_SERVICE) ;
	     List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
	     if(runningTaskInfos != null){
	    	 ComponentName cn= runningTaskInfos.get(0).topActivity;
	    	 if(!packetName.equals(cn.getPackageName()))
	    			Toast.makeText(context, "若显示此说明则说明" + context.getResources()
	    					.getString(R.string.app_name) + "可能被劫持。",Toast.LENGTH_LONG).show(); 
	     }
	}
	
	/**
     * 判断当前手机是否有ROOT权限
     * @return
     */
    public static boolean isRoot(){
        boolean bool = false;
        try{
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())){
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception e) {
        } 
        return bool;
    }
	
	/**
	 * 判断是否为复杂密码 大写字母、小写字母、数字,三种结合
	 */
	public static boolean isComplexPassWord(String password) {
		char num[] = password.toCharArray();// 把字符串转换为字符数组

		boolean isDigit = false;
		boolean isLowerCase = false;
		boolean isUpperCase = false;
		for (int i = 0; i < num.length; i++) {
			if (Character.isDigit(num[i])) {
				isDigit = true;
			} else if (Character.isLowerCase(num[i])) {
				isLowerCase = true;
			} else if (Character.isUpperCase(num[i])) {
				isUpperCase = true;
			}

		}

		return isDigit && isLowerCase && isUpperCase;
	}

	/** 
	 * 获取指定HTML标签的指定属性的值 
	 * @param source 要匹配的源文本 
	 * @param element 标签名称 
	 * @param attr 标签的属性名称 
	 * @return 属性值列表 
	 */  
	public static List<String> matchHtml(String source, String element, String attr) {  
	    List<String> result = new ArrayList<String>();  
	    String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?>";
	    Matcher m = Pattern.compile(reg).matcher(source);  
	    while (m.find()) {  
	        String r = m.group(1);  
	        result.add(r);  
	    }  
	    return result;  
	} 
	
	public static int String2Color(String str) {
		return new BigInteger("ff"+str.substring(1),16).intValue();
	 }
	
	private static HashMap<String, Integer> map_color= new HashMap<String, Integer>(){{
			put("RED",Color.RED);put("BLACK",Color.BLACK);put("BLUE",Color.BLUE);
			put("CYAN",Color.CYAN);put("DKGRAY",Color.DKGRAY);put("GRAY",Color.GRAY);
			put("GREEN",Color.GREEN);put("LTGRAY",Color.LTGRAY);put("MAGENTA",Color.MAGENTA);
			put("TRANSPARENT",Color.TRANSPARENT);put("WHITE",Color.WHITE);put("YELLOW",Color.YELLOW);
		}
	};
	
	public static int htmlFontColor(String s){
		List<String> result  = matchHtml(s,"font","color");
		if(result !=null && result.size()>0){
			if(result.get(0).startsWith("#"))
				return String2Color(result.get(0).replace("'", ""));
			else if(map_color.containsKey(result.get(0).toUpperCase())){
				return map_color.get(result.get(0).toUpperCase());
			}
		}
		
		return 0xffffffff;
	}
	
	/**
	 * @描述:把content放置到粘贴板中
	 */
	public static void setClipboard(Context context,String content){
		ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
		clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));  
	}
	
	/**
	 * 
	 * @描述:获取粘贴板内容
	 */
	public static CharSequence getClipboard(Context context){
		ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
		if (clipboardManager.hasPrimaryClip()){  
		    return clipboardManager.getPrimaryClip().getItemAt(0).getText();  
		}else{
			return "";
		}
	}

	/**
	 * Try to return the absolute file path from the given Uri
	 *
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath( final Context context, final Uri uri ) {
		if ( null == uri ) return null;
		final String scheme = uri.getScheme();
		String data = null;
		if ( scheme == null )
			data = uri.getPath();
		else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
			data = uri.getPath();
		} else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
			Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
			if ( null != cursor ) {
				if ( cursor.moveToFirst() ) {
					int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
					if ( index > -1 ) {
						data = cursor.getString( index );
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	public static boolean isSDCardAvailable() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}

	public static String getSDCardStoragePath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() +"/一加精灵/";
	}

	public static String getDataStoragePath() {
		Context context = MyApplication.getAppContext();
		return context.getDir("一加精灵", Context.MODE_PRIVATE).getAbsolutePath() + "/";
	}

	public static String getAvailableStoragePath() {
		String path = null;
		if (isSDCardAvailable()) {
			path = getSDCardStoragePath();
		} else {
			path = getDataStoragePath();
		}
		return path;
	}

	public static boolean saveImageToAlbum(String imagePath, Context context) {
		try {
			String url = MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, imagePath, null);
			Uri uri = Uri.parse(url);
			ContentValues values = new ContentValues();
			ContentResolver resolver = context.getContentResolver();
			long now = System.currentTimeMillis();
			values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, now);
			values.put(MediaStore.Images.ImageColumns.DATE_ADDED, now / 1000);
			values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, now / 1000);
			resolver.update(uri, values, null, null);
			if (Build.VERSION.SDK_INT < 19) {
				context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imagePath)));
			}

			String[] paths = new String[]{imagePath};
			MediaScannerConnection.scanFile(context, paths, null, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
