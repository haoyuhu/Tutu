package mu.lab.tufeedback.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.HashMap;

/**
 * thread for load image
 * Created by coderhuhy on 15/9/30.
 */
public class LoadImageThread extends Thread {

    protected String mPath = null;
    protected ImageView mImage = null;
    protected int mSize = 0;

    protected static HashMap<String, Bitmap> mImageMap = new HashMap<>();

    public LoadImageThread(String path, ImageView image, int size) {
        mPath = path;
        mImage = image;
        mSize = size;
    }

    @Override
    public void run() {
        if (mPath == null) {
            return;
        }
        if (!mImageMap.containsKey(mPath)) {
            Bitmap image = UmengImageUtils.getLocalImage(mPath);
            if (image != null) {
                mImageMap.put(mPath, image);
            }
        }
        UmengImageUtils.showPhotoMessage(mImageMap.get(mPath), mImage, mSize);
    }
}
