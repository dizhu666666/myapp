package com.android.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtils {
	/**
	 * 
	 */
	private final static String FOLDER_NAME = "/xksns/temp";
	/**
	 * 
	 */
	private static String mDataRootPath = null;
	/**
	 * 
	 */
	private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();

	public static Bitmap getBitmapfromFile(String fileName) {
		final String fullPath = getStorageDirectorystatic() + File.separator + fileName;
		return BitmapFactory.decodeFile(fullPath);
	}

	private static String getStorageDirectorystatic() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
				: mDataRootPath + FOLDER_NAME;
	}

	public FileUtils(Context context) {
		mDataRootPath = context.getCacheDir().getPath();
	}

	/**
	 * 鍒犻櫎SD鍗℃垨鑰呮墜鏈虹殑缂撳瓨鍥剧墖鍜岀洰褰?
	 */
	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		dirFile.delete();
	}

	/**
	 * 鍒犻櫎SD鍗℃垨鑰呮墜鏈虹殑缂撳瓨鍥剧墖鍜岀洰褰?
	 */
	public boolean deleteCache() {
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return true;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		return dirFile.delete();
	}

	/**
	 * 浠庢墜鏈烘垨鑰卻d鍗¤幏鍙朆itmap
	 * 
	 * @param fileName
	 * @return
	 */
	public Bitmap getAbsolutePathBitmap(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 鑾峰彇鏂囦欢鐨勫ぇ灏?
	 * 
	 * @param fileName
	 * @return
	 */
	public long getabsolutePathFileSize(String path) {
		return new File(path).length();
	}

	/**
	 * 浠庢墜鏈烘垨鑰卻d鍗¤幏鍙朆itmap
	 * 
	 * @param fileName
	 * @return
	 */
	public Bitmap getBitmap(String fileName) {
		final String pathNmae = getStorageDirectory() + File.separator + fileName;
		return BitmapFactory.decodeFile(pathNmae);
	}

	/**
	 * 鍒ゆ柇鏂囦欢鏄惁瀛樺湪
	 * 
	 * @param fileName
	 * @return
	 */
	public File getFile(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName);
	}

	/**
	 * 鍒ゆ柇鏂囦欢鏄惁瀛樺湪
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFilePath(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName).getAbsolutePath();
	}

	/**
	 * 鑾峰彇鏂囦欢鐨勫ぇ灏?
	 * 
	 * @param fileName
	 * @return
	 */
	public long getFileSize(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName).length();
	}

	public long getFileSizefromPath(String path) {
		return new File(path).length();
	}

	/**
	 * 鑾峰彇鍌ㄥ瓨Image鐨勭洰褰?
	 * 
	 * @return
	 */
	public String getStorageDirectory() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
				: mDataRootPath + FOLDER_NAME;
	}

	/**
	 * 鍒ゆ柇鏂囦欢鏄惁瀛樺湪
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isAbsolutePathFileExists(String path) {
		return new File(path).exists();
	}

	/**
	 * 鍒ゆ柇鏂囦欢鏄惁瀛樺湪
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExists(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName).exists();
	}

	public boolean Move(File srcFile, String newFileName) {
		// Destination directory
		File dir = new File(getStorageDirectory() + File.separator + newFileName);
		// Move file to new directory
		return srcFile.renameTo(dir);

	}

	/**
	 * 淇濆瓨Image鐨勬柟娉曪紝鏈塻d鍗″瓨鍌ㄥ埌sd鍗★紝娌℃湁灏卞瓨鍌ㄥ埌鎵嬫満鐩綍 ,
	 * 
	 * @param fileName
	 * @param bitmap
	 * @throws IOException
	 */
	public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null) {
			return;
		}
		String path = getStorageDirectory();
		File folderFile = new File(path);

		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		File file = new File(path + File.separator + fileName);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();

	}

	public static String savaBitmap(Context context, String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null) {
			return null;
		}
		
		String path = context.getCacheDir().getPath();
		File folderFile = new File(path);

		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		File file = new File(path + File.separator + fileName);
		file.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
		return file.getPath();
		
	}
}
