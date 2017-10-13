/**
 * 
 */
package czc.wxphonenumberhelper.util;

import android.content.Context;

import czc.wxphonenumberhelper.MyApplication;

/**
 */
public class ScreenUtil {

	public static int dip2px(float dpValue) {
		float scale = MyApplication.getApplication().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(float pxValue) {
		float scale = MyApplication.getApplication().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static float dip2pxf(int dpValue) {
		float scale = MyApplication.getApplication().getResources().getDisplayMetrics().density;
		return dpValue * scale + 0.5f;
	}

	public static float dip2pxf(float dpValue) {
		float scale = MyApplication.getApplication().getResources().getDisplayMetrics().density;
		return dpValue * scale + 0.5f;
	}
	
	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            (DisplayMetrics类中属性scaledDensity)
	 * @return
	 */
	public static int px2sp(float pxValue) {
		float fontScale = MyApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            (DisplayMetrics类中属性scaledDensity)
	 * @return
	 */
	public static int sp2px(float spValue) {
		float fontScale = MyApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static float sp2pxf(float spValue) {
		float fontScale = MyApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
		return spValue * fontScale + 0.5f;
	}
}
