package com.xiakee.xkxsns.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.xiakee.xkxsns.global.GlobalApplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by William on 2015/11/20.
 */
public class ImageUtils {

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    public static File getSmallBitmap(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 240, 400);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);

        FileOutputStream fos = null;
        File file = getAlbumFile();
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bm.compress(Bitmap.CompressFormat.JPEG, 60, fos);

        return file;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    /**
     * 获取保存图片的目录
     *
     * @return
     */
    private static File getAlbumFile() {
        File dir = getAlbumDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, System.currentTimeMillis() + ".jpg");
        return file;
    }

    /**
     * 获取保存 隐患检查的图片文件夹名称
     *
     * @return
     */
    public static File getAlbumDir() {
        File dir = new File(
                GlobalApplication.getContext().getCacheDir(),
                "xiakee/scaleimg");

        return dir;
    }

    /**
     * 根据路径删除图片
     *
     * @param path
     */
    public static void deleteTempFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 添加到图库
     */
    public static void galleryAddPic(Context context, String path) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
