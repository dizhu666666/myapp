package com.android.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

//import com.lianlm.fitness.adapter.PagerAdapter;
//import com.lianlm.fitness.model.ImageLoader;

public class ImageUtil {

	public static final int TARGET_KB = 128;
	public static final int KB = 1024;
	
	public static final int REQUESR_WIDTH = 480;
	public static final int REQUEST_HEIGHT = 854;

	public static Drawable Btimap2D(Context c, Bitmap b) {
		return new BitmapDrawable(c.getResources(), b);
	}

	static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public static Bitmap D_res2Bitmap(Context c, int D) {
		return BitmapFactory.decodeResource(c.getResources(), D);
	}

	public static Bitmap D2Bitmap(Drawable D) {
		Bitmap bitmap = Bitmap.createBitmap(

				D.getIntrinsicWidth(),

				D.getIntrinsicHeight(),

				D.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

						: Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		// canvas.setBitmap(bitmap);

		D.setBounds(0, 0, D.getIntrinsicWidth(), D.getIntrinsicHeight());

		D.draw(canvas);

		return bitmap;

	}

	/**
	 * . * 鏍规嵁鎵嬫満鐨勫垎杈ㄧ巼浠? dp 鐨勫崟浣? 杞垚涓? px(鍍忕礌) .
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/********************************
	 * resize bitmap
	 ******************************/
	
	public static  Bitmap getOriginBitmap(String path)
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	private static Bitmap getResizedBitmap(Context context, int drawable, int width, int height) {
		final BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), drawable, options);
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(context.getResources(), drawable, options);
	}

	private static Bitmap getResizedBitmap(String path, int width, int height) {
		final BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// BitmapFactory.decodeByteArray(data, offset, length, opts)
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	private static Bitmap getResizedBitmap(InputStream is, int width, int height) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(is, null, options);

	}

	private static Bitmap getResizedBitmap(byte[] bytes, int reqWdith, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		options.inSampleSize = calculateInSampleSize(options, reqWdith, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}

	public static Bitmap getResizedBitmap(FileDescriptor fd, int reqWdith, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fd, null, options);
		options.inSampleSize = calculateInSampleSize(options, reqWdith, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFileDescriptor(fd, null, options);
	}

	/*********************************************************************/

	public static Drawable getResizedDrawable(Context context, int drawable, int width, int height) {
		return new BitmapDrawable(context.getResources(), getResizedBitmap(context, drawable, width, height));
	}

	// 浣跨敤BitmapFactory.Options鐨刬nSampleSize鍙傛暟鏉ョ缉鏀?
	public static Drawable getResizedDrawable(String path, int width, int height) {

		return new BitmapDrawable(getResizedBitmap(path, width, height));
	}

	public static Bitmap getRoundCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap getRoundImage(Context context, Bitmap bmp, int radius) {
		// Log.d(TAG, "sample size: " + sampleSize);
		if (bmp == null) {
			return null;
		}
		int diameter = radius * 2;
		Bitmap resultBmp = null;
		// 绘制图片
		try {
			resultBmp = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			return null;
		}
		// Bitmap resultBmp = Bitmap.createBitmap(diameter, diameter,
		// Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Canvas canvas = new Canvas(resultBmp);
		// 画圆
		canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);
		// 选择交集去上层图片
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getWidth()), new Rect(0, 0, diameter, diameter),
				paint);
		// bmp.recycle();
		return resultBmp;
	}

	public static Bitmap getRoundImage(Context context, int ResId, int radius) {
		// mImg = (ImageView) findViewById(R.id.img2);
		// 裁剪图片
		Resources res = context.getResources();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, ResId, options);
		// Log.d(TAG, "original outwidth: " + options.outWidth);
		// 此宽度是目标ImageView希望的大小，你可以自定义ImageView，然后获得ImageView的宽度。
		// 我们需要加载的图片可能很大，我们先对原有的图片进行裁剪
		int sampleSize = calculateInSampleSize(options, radius, radius);
		options.inSampleSize = sampleSize;
		options.inJustDecodeBounds = false;
		// Log.d(TAG, "sample size: " + sampleSize);
		Bitmap bmp = BitmapFactory.decodeResource(res, ResId, options);

		// 绘制图片
		Bitmap resultBmp = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Canvas canvas = new Canvas(resultBmp);
		// 画圆
		canvas.drawCircle(radius / 2, radius / 2, radius / 2, paint);
		// 选择交集去上层图片
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getWidth()), new Rect(0, 0, radius, radius), paint);
		bmp.recycle();
		return resultBmp;
	}

	public static int getViewHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return view.getMeasuredHeight();
	}

	public static int getViewWidth(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return view.getMeasuredWidth();
	}

	// 浣跨敤Bitmap鍔燤atrix鏉ョ缉鏀?
	public static Drawable resizeImage(Context context, Bitmap bitmap, int w, int h) {
		if (bitmap == null) {
			return null;
		}
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
		return new BitmapDrawable(context.getResources(), resizedBitmap);
	}

	/**
	 * 缂╂斁鍥剧墖骞堕攢姣佹簮鍥剧墖
	 * 
	 * @param bm
	 *            瑕佺缉鏀惧浘鐗?
	 * @param newWidth
	 *            瀹藉害
	 * @param newHeight
	 *            楂樺害
	 * @return澶勭悊鍚庣殑鍥剧墖
	 */
	public static Bitmap ScalImage(Bitmap bm, int newWidth, int newHeight, boolean recycle_src) {
		if (bm == null) {
			return null;
		}
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		if (recycle_src && bm != null && !bm.isRecycled()) {
			bm.recycle();// 閿?姣佸師鍥剧墖
			bm = null;
		}
		return newbm;
	}

	/**
	 * 
	 * @brief 瑁佸壀Bitmap锛岃嚜鍔ㄥ洖鏀跺師Bitmap
	 * @see ImageUtil#cropBitmap(Bitmap, int, int, int, int, boolean)
	 */

	public Bitmap cropBitmap(Bitmap src, int x, int y, int width, int height) {
		return cropBitmap(src, x, y, width, height, true);

	}

	/**
	 * @brief 瑁佸壀Bitmap
	 * @param src
	 *            -婧怋itmap
	 * @param x
	 *            -寮?濮媥鍧愭爣
	 * @param y
	 *            -寮?濮媦鍧愭爣
	 * @param width
	 *            -鎴彇瀹藉害
	 * @param height
	 *            -鎴彇楂樺害
	 * @param isRecycle
	 *            -鏄惁鍥炴敹鍘熷浘鍍?
	 * @return Bitmap
	 */

	public Bitmap cropBitmap(Bitmap src, int x, int y, int width, int height, boolean isRecycle) {
		if (x == 0 && y == 0 && width == src.getWidth() && height == src.getHeight()) {
			return src;
		}
		Bitmap dst = Bitmap.createBitmap(src, x, y, width, height);
		if (isRecycle && dst != src) {
			src.recycle();
		}
		return dst;
	}

	public static void recycleBitmap(Bitmap bm) {
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();// 閿?姣佸師鍥剧墖
			bm = null;
		}
	}

	/**
	 * @brief 缂╂斁Bitmap锛岃嚜鍔ㄥ洖鏀跺師Bitmap
	 * @see ImageUtil#scaleBitmap(Bitmap, int, int, boolean)
	 */
	public Bitmap scaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
		return scaleBitmap(src, dstWidth, dstHeight, true);
	}

	/**
	 * @brief 缂╂斁Bitmap
	 * @param src
	 *            婧怋itmap
	 * @param dstWidth
	 *            鐩爣瀹藉害
	 * @param dstHeight
	 *            鐩爣楂樺害
	 * @param isRecycle
	 *            鏄惁鍥炴敹鍘熷浘鍍?
	 * @return Bitmap
	 */
	public Bitmap scaleBitmap(Bitmap src, int dstWidth, int dstHeight, boolean isRecycle) {
		if (src.getWidth() == dstWidth && src.getHeight() == dstHeight) {
			return src;
		}
		Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
		if (isRecycle && dst != src) {
			src.recycle();
		}
		return dst;
	}

	// 按照 1080p缩放，大于1M就继续缩放
	public static Bitmap compressedBitmap(FileDescriptor fd, int width, int height) {

		 Bitmap bitmap = getResizedBitmap(fd, width, height);
		if (null == bitmap) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		while (baos.toByteArray().length / KB > TARGET_KB) { // 循环判断如果压缩后图片是否大于?k,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		bitmap = null;
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		return BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
	}

	public static Bitmap compressedBitmap(Context context, int drawable, int width, int height) {
		Bitmap bitmap = getResizedBitmap(context, drawable, width, height);
		if (bitmap == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		while (baos.toByteArray().length / KB > TARGET_KB) { // 循环判断如果压缩后图片是否大于?k,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		bitmap = null;
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		return BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片

	}

	// 按照 1080p缩放，大于1M就继续缩放
	public static Bitmap compressedBitmap(String imgPath, int width, int height) {

//		Bitmap bitmap = getResizedBitmap(imgPath, width, height);
		Bitmap bitmap = null;;
		try {
			bitmap = revitionImageSize(imgPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		while (baos.toByteArray().length / KB > TARGET_KB) { // 循环判断如果压缩后图片是否大于?k,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		bitmap = null;
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		return BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
	}

	// 按照 1080p缩放，大于1M就继续缩放,用于上传
	public static InputStream compressBitmap2InputStream(Context context, String imgPath) {

		Bitmap bitmap = getResizedBitmap(imgPath, REQUESR_WIDTH, REQUEST_HEIGHT);
		if (bitmap == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		while (baos.toByteArray().length / KB > TARGET_KB) { // 循环判断如果压缩后图片是否大于?kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 15;// 每次都减少10
		}
		bitmap = null;
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		return isBm;
	}
	
	
	public static Bitmap revitionImageSize(String path) throws IOException {

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= REQUESR_WIDTH) && (options.outHeight >> i <= REQUEST_HEIGHT)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;

		// final int reqWidth = DeviceInfo.scrrenWidth(this);
		// final int reqHeight = DeviceInfo.scrrenHeight(this);
		// return ImageUtil.compressedBitmap(path, 128, 128);
	}
	

	public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
		Bitmap bitmap;
		if (canReuseInBitmap) {
			bitmap = sentBitmap;
		} else {
			bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		}

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}

	@SuppressLint("NewApi")
	public static Bitmap blurBitmap(Context context, Bitmap bitmap) {

		// Let's create an empty bitmap with the same size of the bitmap we want
		// to blur
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		// Instantiate a new Renderscript
		RenderScript rs = RenderScript.create(context);

		// Create an Intrinsic Blur Script using the Renderscript
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

		// Create the Allocations (in/out) with the Renderscript and the in/out
		// bitmaps
		Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
		Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

		// Set the radius of the blur
		blurScript.setRadius(25f);

		// Perform the Renderscript
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);

		// Copy the final bitmap created by the out Allocation to the outBitmap
		allOut.copyTo(outBitmap);

		// recycle the original bitmap
		bitmap.recycle();

		// After finishing everything, we destroy the Renderscript.
		rs.destroy();

		return outBitmap;

	}

	/**
	 * 柔化效果(高斯模糊)(优化后比上面快三倍)
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap blurImageAmeliorate(Bitmap bmp) {
		long start = System.currentTimeMillis();
		// 高斯矩阵
		int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int delta = 16; // 值越小图片会越亮，越大则越暗

		int idx = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + pixR * gauss[idx];
						newG = newG + pixG * gauss[idx];
						newB = newB + pixB * gauss[idx];
						idx++;
					}
				}

				newR /= delta;
				newG /= delta;
				newB /= delta;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		// Log.d("may", "used time="+(end - start));
		return bitmap;
	}

	public static void gaussBlur(int[] data, int width, int height, int radius, float sigma) {

		float pa = (float) (1 / (Math.sqrt(2 * Math.PI) * sigma));
		float pb = -1.0f / (2 * sigma * sigma);

		// generate the Gauss Matrix
		float[] gaussMatrix = new float[radius * 2 + 1];
		float gaussSum = 0f;
		for (int i = 0, x = -radius; x <= radius; ++x, ++i) {
			float g = (float) (pa * Math.exp(pb * x * x));
			gaussMatrix[i] = g;
			gaussSum += g;
		}

		for (int i = 0, length = gaussMatrix.length; i < length; ++i) {
			gaussMatrix[i] /= gaussSum;
		}

		// x direction
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				float r = 0, g = 0, b = 0;
				gaussSum = 0;
				for (int j = -radius; j <= radius; ++j) {
					int k = x + j;
					if (k >= 0 && k < width) {
						int index = y * width + k;
						int color = data[index];
						int cr = (color & 0x00ff0000) >> 16;
						int cg = (color & 0x0000ff00) >> 8;
						int cb = (color & 0x000000ff);

						r += cr * gaussMatrix[j + radius];
						g += cg * gaussMatrix[j + radius];
						b += cb * gaussMatrix[j + radius];

						gaussSum += gaussMatrix[j + radius];
					}
				}

				int index = y * width + x;
				int cr = (int) (r / gaussSum);
				int cg = (int) (g / gaussSum);
				int cb = (int) (b / gaussSum);

				data[index] = cr << 16 | cg << 8 | cb | 0xff000000;
			}
		}

		// y direction
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				float r = 0, g = 0, b = 0;
				gaussSum = 0;
				for (int j = -radius; j <= radius; ++j) {
					int k = y + j;
					if (k >= 0 && k < height) {
						int index = k * width + x;
						int color = data[index];
						int cr = (color & 0x00ff0000) >> 16;
						int cg = (color & 0x0000ff00) >> 8;
						int cb = (color & 0x000000ff);

						r += cr * gaussMatrix[j + radius];
						g += cg * gaussMatrix[j + radius];
						b += cb * gaussMatrix[j + radius];

						gaussSum += gaussMatrix[j + radius];
					}
				}

				int index = y * width + x;
				int cr = (int) (r / gaussSum);
				int cg = (int) (g / gaussSum);
				int cb = (int) (b / gaussSum);
				data[index] = cr << 16 | cg << 8 | cb | 0xff000000;
			}
		}
	}

}
