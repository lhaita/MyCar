package liuhao.baway.com.monthlianxi;

import android.app.Application;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

/**
 * Created by 15218 on 2017/10/24.
 */
public class MyaPP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        File cacheDir = StorageUtils.getOwnCacheDirectory(this, "liuhao/Cache");
            File file = new File(Environment.getExternalStorageDirectory(),"liuhao1506");
            //设置图片的显示
                 DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                         .considerExifParams(true)
                         .cacheInMemory(true)
                .build();
        ImageLoaderConfiguration i = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .diskCache(new UnlimitedDiskCache(file))
                .build();
        //初始化
        ImageLoader.getInstance().init(i);

    }
}
