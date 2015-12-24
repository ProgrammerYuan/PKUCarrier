/**
 *
 */
package com.programmeryuan.PKUCarrier.utils.image;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.programmeryuan.PKUCarrier.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


/**
 * 图片加载服务提供者
 *
 * @author Michael
 */
public class ImageProvider {
    static Context c;
    static ImageLoaderConfiguration config;
    static DisplayImageOptions options;
    //    static int[][] scales_16_9 = new int[][]{new int[]{1080, 1080}, new int[]{1280, 720}, new int[]{840, 400}};
//    static int[][] scales_4_3 = new int[][]{new int[]{1920, 1200}, new int[]{1440, 900}, new int[]{640, 480}};
    static int id_fail, id_loading;
    static HashMap<String, Bitmap> chat_iamge_chache;
    static DiskCache diskCache;
    /**
     * 初始化
     *
     * @param context    上下文
     * @param dir        缓存文件夹
     * @param loading    资源ID，图片加载时显示
     * @param fail       资源ID，图片加载失败后显示
     * @param debug_mode 是否输出调试信息
     */
    public static void init(Context context, String dir, int loading, int fail, boolean debug_mode) {
        c = context;
        id_fail = fail;
        id_loading = loading;
        diskCache = new UnlimitedDiskCache(new File(dir));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loading)
                .showImageForEmptyUri(fail)
                .showImageOnFail(fail)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(false)

                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(400))
                .build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.MIN_PRIORITY + 1)
                .denyCacheImageMultipleSizesInMemory()
                .threadPoolSize(4) // default
                .memoryCache(new LruMemoryCache(20 * 1024 * 1024))
                .memoryCacheSize(20 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(diskCache) // default
//                .diskCacheSize(50 * 1024 * 1024)
//                .diskCacheFileCount(500)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .defaultDisplayImageOptions(options) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO);
        if (debug_mode) {
            builder = builder
                    .writeDebugLogs();// Remove for release app
        }
        config = builder
                .build();
        chat_iamge_chache = new HashMap<String, Bitmap>();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 将图片加载到缓存中
     *
     * @param res 图片资源
     */
    public static void loadImage(Object res) {
        if (res instanceof String) {
            try {
                Integer.parseInt(res.toString());
            } catch (NumberFormatException e) {
                ImageLoader.getInstance().loadImage(res.toString(), null);
            }

        }
    }

    public static void addToChatBitmapCache(String url, Bitmap bitmap) {
        chat_iamge_chache.put(url, bitmap);
    }

    public static Bitmap getFromChatBitmapCache(String url) {
        return chat_iamge_chache.get(url);
    }

    public static void clearChatCache() {
        chat_iamge_chache.clear();
    }

    /**
     * 将图片同步加载到缓存中。这意味着将阻塞主线程，通常为了不显示Loading图，比如在App欢迎界面时
     *
     * @param res 图片资源
     */
    public static void loadImageSync(Object res) {
        if (res instanceof String) {
            try {
                Integer.parseInt(res.toString());
            } catch (NumberFormatException e) {
                ImageLoader.getInstance().loadImageSync(res.toString());
            }

        }
    }

    /**
     * 加载图片到指定的ImageView
     *
     * @param iv  要加载图片的ImageView
     * @param res 图片资源，Uri（包括网址，本地地址等）或资源ID
     */
    public static void display(ImageView iv, Object res) {
        display(iv, res, null);
    }

    public static void displayWithoutScale(ImageView iv, Object res) {
        if (res == null) {
            iv.setImageResource(id_fail);
        } else if (res instanceof String) {
            try {
                int i = Integer.parseInt(res.toString());
                iv.setImageResource(i);
            } catch (NumberFormatException e) {
                DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//                        .showImageOnLoading(loading)
//                        .showImageForEmptyUri(fail)
//                        .showImageOnFail(fail)
                        .imageScaleType(ImageScaleType.NONE)
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)

                        .bitmapConfig(Bitmap.Config.ARGB_8888)
                        .displayer(new FadeInBitmapDisplayer(400))
                        .build();
                ImageLoader.getInstance().displayImage(res.toString(), iv, imageOptions);


            }
        } else if (res instanceof Integer) {
            iv.setImageResource((Integer) res);
        }
    }
    public static void display(ImageAware iv, Object res) {
        if (res == null) {
            iv.setImageDrawable(c.getResources().getDrawable(id_fail));
        } else if (res instanceof String) {
            try {
                int i = Integer.parseInt(res.toString());
                iv.setImageDrawable(c.getResources().getDrawable(i));
            } catch (NumberFormatException e) {
                    ImageLoader.getInstance().displayImage(res.toString(), iv);

            }
        } else if (res instanceof Integer) {
            iv.setImageDrawable(c.getResources().getDrawable((Integer) res));
        }
    }

    public static void display(ImageAware iv, Object res, ImageLoadingListener l1, ImageLoadingProgressListener l2) {
        if (res == null) {
            iv.setImageDrawable(c.getResources().getDrawable(id_fail));
        } else if (res instanceof String) {
            try {
                int i = Integer.parseInt(res.toString());
                iv.setImageDrawable(c.getResources().getDrawable(i));
            } catch (NumberFormatException e) {
                if (l1 == null)
                    ImageLoader.getInstance().displayImage(res.toString(), iv);
                else
                    ImageLoader.getInstance().displayImage(res.toString(), iv, options, l1, l2);
            }
        } else if (res instanceof Integer) {
            iv.setImageDrawable(c.getResources().getDrawable((Integer) res));
        }
    }

    /**
     * 加载图片到指定的ImageView
     *
     * @param iv  要加载图片的ImageView
     * @param res 图片资源，Uri（包括网址，本地地址等）或资源ID
     * @param l   图片加载监听器
     */
    public static void display(ImageView iv, Object res, ImageLoadingListener l) {
        if (res == null) {
            iv.setImageResource(id_fail);
        } else if (res instanceof String) {
            try {
                int i = Integer.parseInt(res.toString());
                iv.setImageResource(i);
            } catch (NumberFormatException e) {
                if (l == null) {
                    ImageLoader.getInstance().displayImage(res.toString(), iv);
                } else {
                    ImageLoader.getInstance().displayImage(res.toString(), iv, l);
                }

            }
        } else if (res instanceof Integer) {
            iv.setImageResource((Integer) res);
        }
    }

    /**
     * 生成缩略图
     *
     * @param uri 目标图片的URI
     * @return 生成的缩略图的本地地址
     */
    public static String getSmallPic(Uri uri) {
        Bitmap bitmap = null;
        bitmap = getBitmap(uri);
        String thumb_path = null;
        Bitmap thumb = bitmap;
        FileOutputStream out = null;
        File file = new File(c.getDir("temp", Context.MODE_PRIVATE).getAbsolutePath() + "/temp_" + System.currentTimeMillis() + ".jpg");
        thumb_path = file.getAbsolutePath();
        try {
            out = new FileOutputStream(file);
            thumb.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (thumb != null) {
                thumb.recycle();
            }
        }
//        Logger.out(thumb_path + " file size:" + Util.getFileSize(file));
        return thumb_path;
    }

    static Bitmap getBitmap(Uri uri) {
        ContentResolver resolver = c.getContentResolver();
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MB
            in = resolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
//            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {

                scale++;
            }
            Logger.out("scale = " + scale + ", orig-width: " + o.outWidth + ",orig - height: " + o.outHeight);

            Bitmap b = null;
            in = resolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Logger.out("1th scale operation dimenions - width: " + width + ",height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Logger.out("bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
            return b;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DiskCache getDiskCache(){
        return diskCache;
    }
}
