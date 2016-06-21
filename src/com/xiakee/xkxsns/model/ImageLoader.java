package com.xiakee.xkxsns.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.android.util.ImageUtil;
import com.android.util.StringUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;

public class ImageLoader {

	public interface ImageLoaderListener {
		void onImageLoaded(Bitmap bitmap, String url);

	}

	private static volatile ImageLoader sImageLoader;
	private Context mContext;

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	// private Set<BitmapWorkerTask> taskCollection;

	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	// LinkedHashMap<String,Bitmap> map;

	/**
	 * 图片硬盘缓存核心类。
	 */
	private DiskLruCache mDiskLruCache;
	private ExecutorService mImageThreadPool = null;

	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */

	private ImageLoader(Context context) {
		mContext = context;
		// taskCollection = new HashSet<BitmapWorkerTask>();
		initMemCache();
		initDiskCache(context, "xiakee");

	}
	
	private void initMemCache() {
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}

	public static ImageLoader Instance(Context context) {
		if (sImageLoader == null) {
			synchronized (ImageLoader.class) {
				if (sImageLoader == null) {
					sImageLoader = new ImageLoader(context);
				}
			}
		}
		return sImageLoader;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		if (null == key)
			return null;
		return mMemoryCache.get(key);
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 * 
	 * @return
	 */
	public Bitmap loadBitmap(final String imageUrl, final ImageLoaderListener listener) {
		final String key = hashKeyForDisk(imageUrl);
		final Bitmap bitmap = getBitmapFromMemoryCache(key);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;

		} else {
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					listener.onImageLoaded((Bitmap) msg.obj, imageUrl);
				}
			};
			getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					Snapshot snapShot = null;
					FileDescriptor fileDescriptor = null;
					FileInputStream fileInputStream = null;
					try {
						snapShot = mDiskLruCache.get(key);
						if (snapShot == null) {
							DiskLruCache.Editor editor = mDiskLruCache.edit(key);
							if (editor != null) {
								OutputStream outputStream = editor.newOutputStream(0);
								if (downloadUrlToStream(imageUrl, outputStream)) {
									editor.commit();
								} else {
									editor.abort();
								}
							}
							// 缓存被写入后，再次查找key对应的缓存
							snapShot = mDiskLruCache.get(key);
						}
						if (snapShot != null) {
							fileInputStream = (FileInputStream) snapShot.getInputStream(0);
							fileDescriptor = fileInputStream.getFD();
						}
						// 将缓存数据解析成Bitmap对象
						// Bitmap bitmap = null;
						Bitmap bitmap = null;
						if (fileDescriptor != null) {
							bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
						}
						if (bitmap != null) {
							// 将Bitmap对象添加到内存缓存当中
							Message msg = handler.obtainMessage();
							msg.obj = bitmap;
							handler.sendMessage(msg);
							addBitmapToMemoryCache(key, bitmap);
							bitmap = null;
						}
						// return bitmap;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (fileDescriptor == null && fileInputStream != null) {
							try {
								fileInputStream.close();
							} catch (IOException e) {
							}
						}
					}
				}
			});

			return null;
		}
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 * 
	 * @return
	 */
	public Bitmap loadBitmap(final String imageUrl, final ImageLoaderListener listener, final int reqWidth,
			final int reqHeight) {
		// final String fileName = StringUtils.getPicName(imageUrl);
		final String key = hashKeyForDisk(imageUrl);
		Bitmap bitmap = getBitmapFromMemoryCache(key);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;

		} else {
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					@SuppressWarnings("unchecked")
					HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
					listener.onImageLoaded((Bitmap) map.get("bitmap"), (String) map.get("imgUrl"));
				}
			};
			getThreadPool().execute(new LoadRunnable(handler, imageUrl, reqWidth, reqHeight));
			return null;
		}
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 * 
	 * @return
	 */
	public Bitmap loadBitmap(String imageUrl, int reqWidth, int reqHeight) {
		/*
		 * try { Bitmap bitmap = getBitmapFromMemoryCache(imageUrl); if (bitmap
		 * == null) { BitmapWorkerTask task = new BitmapWorkerTask();
		 * taskCollection.add(task); task.execute(imageUrl); } else { return
		 * bitmap; } } catch (Exception e) { e.printStackTrace(); }
		 */
		return null;
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 * 
	 * @return
	 */
	public Bitmap loadBitmapFromLocal(final String path, final ImageLoaderListener listener) {

		final String key = StringUtils.getPicName(path);
		Bitmap bitmap = getBitmapFromMemoryCache(key);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;

		} else {
			final Handler handler = new Handler() {
				@SuppressWarnings("unchecked")
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
					listener.onImageLoaded((Bitmap) map.get("bitmap"), (String) map.get("path"));
				}
			};
			getThreadPool().execute(new LoadOriginLocalRunnable(handler, path));
		}
		return null;
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 * 
	 * @return
	 */
	public Bitmap loadBitmapFromLocal(final String path, final ImageLoaderListener listener, final int reqWidth,
			final int reqHeight) {

		final String key = StringUtils.getPicName(path);
		Bitmap bitmap = getBitmapFromMemoryCache(key);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;

		} else {
			final Handler handler = new Handler() {
				@SuppressWarnings("unchecked")
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
					listener.onImageLoaded((Bitmap) map.get("bitmap"), (String) map.get("path"));
				}
			};
			getThreadPool().execute(new LoadCompressLocalRunnable(handler, path, reqWidth, reqHeight));
		}
		return null;
	}

	public Bitmap loadBitmapFromMemCache(String imgPath) {
		// TODO
		final String key = StringUtils.getPicName(imgPath);
		Bitmap bitmap = getBitmapFromMemoryCache(key);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;
		}
		return null;
	}

	/**
	 * 取消所有正在下载或等待下载的任务。
	 */
	public void cancelAllTasks() {
		/*
		 * if (taskCollection != null) { for (BitmapWorkerTask task :
		 * taskCollection) { task.cancel(false); } }
		 */
	}

	/**
	 * 根据传入的uniqueName获取硬盘缓存的路径地址。
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取当前应用程序的版本号。
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 使用MD5算法对传入的key进行加密并返回。
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	/**
	 * 将缓存记录同步到journal文件中。
	 */
	public void fluchCache() {
		if (mDiskLruCache != null) {
			try {
				mDiskLruCache.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
	 * 
	 * @return
	 */
	public ExecutorService getThreadPool() {
		if (mImageThreadPool == null) {
			synchronized (ExecutorService.class) {
				if (mImageThreadPool == null) {
					// 为了下载图片更加的流畅，我们用了4个线程来下载图片
					mImageThreadPool = Executors.newFixedThreadPool(5);
				}
			}
		}
		return mImageThreadPool;
	}

	/**
	 * 取消正在下载的任务
	 */
	public synchronized void cancelTask() {
		if (mImageThreadPool != null) {
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}



	private void initDiskCache(Context context, String uniqueName) {
		// TODO Auto-generated method stub
		try {
			// 获取图片缓存路径
			File cacheDir = getDiskCacheDir(context, uniqueName);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// 创建DiskLruCache实例，初始化缓存数据
			mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 20 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 建立HTTP请求，并获取Bitmap对象。
	 * 
	 * @param imageUrl
	 *            图片的URL地址
	 * @return 解析后的Bitmap对象
	 */
	private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
			out = new BufferedOutputStream(outputStream, 8 * 1024);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private class LoadCompressLocalRunnable implements Runnable {

		private Handler mHandler;
		private String mPath;
		private int reqWidth;
		private int reqHeight;

		public LoadCompressLocalRunnable(Handler handler, String path, int reqWidth, int reqHeight) {
			this.mHandler = handler;
			this.mPath = path;
			this.reqWidth = reqWidth;
			this.reqHeight = reqHeight;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Bitmap bitmap = ImageUtil.compressedBitmap(mPath, reqWidth, reqHeight);
			if (bitmap != null) {
				// 将Bitmap对象添加到内存缓存当中
				Message msg = mHandler.obtainMessage();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("bitmap", bitmap);
				map.put("path", mPath);
				msg.obj = map;
				mHandler.sendMessage(msg);
				final String key = StringUtils.getPicName(mPath);
				// memCache.put(key, bitmap);
				addBitmapToMemoryCache(key, bitmap);
			}
		}

	}

	private class LoadOriginLocalRunnable implements Runnable {

		private Handler mHandler;
		private String mPath;

		public LoadOriginLocalRunnable(Handler handler, String path) {
			this.mHandler = handler;
			this.mPath = path;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Bitmap bitmap = ImageUtil.getOriginBitmap(mPath);
			if (bitmap != null) {
				// 将Bitmap对象添加到内存缓存当中
				Message msg = mHandler.obtainMessage();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("bitmap", bitmap);
				map.put("path", mPath);
				msg.obj = map;
				mHandler.sendMessage(msg);
				final String key = StringUtils.getPicName(mPath);
				// memCache.put(key, bitmap);
				addBitmapToMemoryCache(key, bitmap);
			}
		}

	}

	private class LoadRunnable implements Runnable {

		private Handler mHandler;
		private String mImgUrl;
		private int reqWidth;
		private int reqHeight;

		public LoadRunnable(Handler handler, String imgUrl, int reqWidth, int reqHeight) {
			this.mHandler = handler;
			this.mImgUrl = imgUrl;
			this.reqWidth = reqWidth;
			this.reqHeight = reqHeight;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			final String key = hashKeyForDisk(mImgUrl);
			Snapshot snapShot = null;
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			try {
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(mImgUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// 缓存被写入后，再次查找key对应的缓存
					snapShot = mDiskLruCache.get(key);
				}
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// 将缓存数据解析成Bitmap对象
				// Bitmap bitmap = null;
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
					// bitmap =
					// BitmapFactory.decodeFileDescriptor(fileDescriptor);
					bitmap = ImageUtil.getResizedBitmap(fileDescriptor, reqWidth, reqHeight);
				}
				if (bitmap != null) {
					// 将Bitmap对象添加到内存缓存当中
					Message msg = mHandler.obtainMessage();
					// msg.obj = bitmap;

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("bitmap", bitmap);
					map.put("imgUrl", mImgUrl);
					msg.obj = map;
					mHandler.sendMessage(msg);
					addBitmapToMemoryCache(key, bitmap);
				}
				// return bitmap;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (fileDescriptor == null && fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}

		}

	}

}
