package mu.lab.tufeedback.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mu.lab.util.Log;

/**
 * From Umeng Android Development Group.
 * Created by coderhuhy on 15/9/24.
 */
public class UmengImageUtils {

    private static final String LogTag = UmengImageUtils.class.getName();

    private static final String FILE_IMAGE_PATH = "/umeng/fb/image/";
    private static final String FILE_END_PNG = ".jpg";


    /**
     * 压缩图片
     *
     * @param path
     * @return
     */
    public static Bitmap decodeSampledBitmapFromPath(String path, int screenWidth) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, screenWidth);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 计算缩放比例
     *
     * @param options
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int screenWidth) {
        int height = options.outHeight;
        int width = options.outWidth;
        float maxLength = screenWidth * 0.36f;
        return (int) (height > width ? height / maxLength : width / maxLength);
    }

    /**
     * 回收Bitmap资源
     *
     * @param bitmap
     */
    private static void safeRecycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 开启异步任务，保存图片
     *
     * @param context
     * @param originalUri
     * @param name
     */
    public static void saveReplyImage(final Context context, final Uri originalUri, final String name, final Handler handler) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return saveImage(context, originalUri, name);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Message msg = new Message();
                    msg.obj = name;
                    handler.sendMessage(msg);
                }
            }
        }.execute();
    }

    /**
     * 保存图片
     *
     * @param context
     * @param originalUri
     * @param name
     * @return
     */
    private static boolean saveImage(Context context, Uri originalUri, String name) {
        boolean result = true;

        String path = getImagePathWithName(context, name);
        File file = new File(path);
        FileOutputStream fileOutputStream = null;

        Bitmap bmp = null;
        try {
            bmp = zoomBitmap(getBitmapWithUri(context, originalUri));
            fileOutputStream = new FileOutputStream(file);
            if (bmp != null) {
                if (bmp.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)) {
                }
            }
        } catch (Exception e) {
            if (!file.delete()) {
                Log.e(LogTag, "cannot delete file: " + file.getName());
            }
            Log.e(LogTag, "save image error.", e);
            result = false;
        } finally {
            safeRecycle(bmp);
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                Log.e(LogTag, e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 压缩图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int height = bitmap.getHeight();
        int size = bitmap.getRowBytes() * bitmap.getHeight() / 1024 / 1024;
        // size >1M
        if (size > 15) {
            Matrix matrix = new Matrix();
            matrix.postScale(0.5f, 0.5f);
            Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), height, matrix, true);
            safeRecycle(bitmap);
            return bmp;
        } else {
            return bitmap;
        }
    }

    /**
     * 通过Uri获取指定Bitmap
     *
     * @param context
     * @param originalUri
     * @return
     */
    private synchronized static Bitmap getBitmapWithUri(Context context, Uri originalUri) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        InputStream input = resolver.openInputStream(originalUri);
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        if ((bitmapOptions.outWidth == -1) || (bitmapOptions.outHeight == -1))
            return null;
        int originalSize = (bitmapOptions.outHeight > bitmapOptions.outWidth) ? bitmapOptions.outHeight : bitmapOptions.outWidth;
        int screenHeight = getScreenLength(context);
        int ratio = (originalSize > screenHeight) ? (originalSize / screenHeight) : 1;
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = ratio;
        input = resolver.openInputStream(originalUri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    /**
     * 获取屏幕的长度
     *
     * @param context
     * @return
     */
    private static int getScreenLength(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) (context
            .getSystemService(Context.WINDOW_SERVICE));
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels > metrics.widthPixels ? metrics.heightPixels : metrics.widthPixels;
    }

    /**
     * inJustDecodeBounds=false 这样是因为设置为true的时候可以decode，但是设置成false就无法decode
     * 所以还是按照true的方式来验证吧
     *
     * @param context
     * @param originalUri
     * @return
     */
    public static boolean isImage(Context context, Uri originalUri) {
        ContentResolver resolver = context.getContentResolver();
        Bitmap bitmap = null;
        try {
            InputStream input = resolver.openInputStream(originalUri);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = false;
            bitmapOptions.inSampleSize = 4;
            bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
            if ((bitmapOptions.outWidth == -1) || (bitmapOptions.outHeight == -1)) {
                return false;
            }
        } catch (Exception ignore) {
            return false;
        } finally {
            safeRecycle(bitmap);
        }
        return true;
    }

    public static String getImagePathWithName(Context context, String name) {
        return getImagePath(context) + name + FILE_END_PNG;
    }

    private static String getImagePath(Context context) {
        String path = context.getFilesDir().getAbsolutePath() + FILE_IMAGE_PATH;
        createDir(path);
        return path;
    }

    public synchronized static boolean createDir(String dirPath) {
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                Log.e(LogTag, "cannot mkdirs: " + dirFile.getAbsolutePath() + dirFile.getName());
            }
        }
        return true;
    }

    public synchronized static Bitmap getLocalImage(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                return BitmapFactory.decodeFile(path);
            }
        }
        return null;
    }

    public synchronized static void showPhotoMessage(Bitmap image, ImageView photoMessageImage, int photoSize) {
        if (image != null && photoMessageImage != null) {
            final int extras = 300;
            int width = photoSize - extras;
            ViewGroup.LayoutParams params = photoMessageImage.getLayoutParams();
            params.height = (int) (width * ((double) image.getHeight() / image.getWidth()));
            params.width = width;
            photoMessageImage.setLayoutParams(params);
            photoMessageImage.setImageBitmap(image);
        }
    }

    public static int getPhotoSize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
    }

}
