package liuhao.baway.com.monthlianxi;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpOkUntils {
    //单例模式创建对象
    private Context context ;

    private static HttpOkUntils httpOkUntils ;

    private Handler handler = new Handler();

    private HttpOkUntils (Context context){
            this.context=context;
    }

    public static HttpOkUntils getHttpOkUntils(Context context){
        if(httpOkUntils==null){
            synchronized (HttpOkUntils.class){
                if(httpOkUntils==null){
                    httpOkUntils = new HttpOkUntils(context);
                }
            }
        }
        return  httpOkUntils ;
    }

    //封装的okhttp的get方法
    public void okHttpGet(String url,  final NetClickListener netclicklister){
        //加入拦截器
        //设置缓存的路径
        File file = context.getExternalCacheDir().getAbsoluteFile();
        OkHttpClient builder = new OkHttpClient.Builder()
        .addInterceptor(new LogInterceptor())
        .cache(new Cache(file,10*1024*1024))
        .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                //缓存
                .cacheControl(new CacheControl.Builder().maxAge(2000, TimeUnit.SECONDS).build())
                .build();
        builder.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                final String string = response.body().string();
                response.body().close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        DataBeanlist basebean = (DataBeanlist) gson.fromJson(string, DataBeanlist.class);
                        if(basebean.getCode().equals("0")){
                            //log测试输出
                            Log.d("qq","判断netclicklister为空前");
                            if(netclicklister!=null){
                                //log测试输出
                                Log.d("qq","判断netclicklister为空后");
                                //log测试输出
                                Log.d("qq","basebean:" + basebean);
                                netclicklister.Suesses(basebean);

                            }
                        }
                    }
                });
            }
        });
    }
}
//调用接口

