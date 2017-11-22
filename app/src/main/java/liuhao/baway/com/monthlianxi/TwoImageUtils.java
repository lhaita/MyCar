package liuhao.baway.com.monthlianxi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwoImageUtils {
    public static void loadImage(String path, final ImageView imageView) {

        new AsyncTask<String, Void, Bitmap>() {


            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if (bitmap != null){
                    imageView.setImageBitmap(bitmap);
                } else{
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }

            }

            @Override
            protected Bitmap doInBackground(String... params) {

                try {
                    String path = params[0];
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);


                    int code = connection.getResponseCode();

                    if (code == 200) {
                        //得到图片数据
                        InputStream is = connection.getInputStream();
                        //BitmapFactory.decodeStream(is);//ARGB_888 一个像素占 4个字节  ARGB_565 一个像素占 2 个字节  Alpha Red、Green、Blue(颜色)
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        //在图片真正解析之前，不要解析图片数据，只需要告诉一下转换器，只要获取图片的像素(宽高)
                        options.inJustDecodeBounds = true;//告诉BitmapFactory转换工厂不去真正解析图片只需要拿到图片宽高就行

                        //假解析 API 有bug
                        BitmapFactory.decodeStream(is, null, options);
                        //压缩，根据图片采样率进行二次采样   100 * 100 是你所期望的具体图片尺寸
                        //options.inSampleSize = 2; //采样用来计算图片的宽高(1920 * 1080 -> 1920 / 2 1080 / 2)

                        //获取图片宽高，注意：图片的原始宽高
                        int width = options.outWidth;
                        int height = options.outHeight;

                        System.out.println("宽:" + width + "高 ： " + height);

                        //定义一个变量去记住我们采样率
                        int inSampleSize = 1;//默认是1 ,即不对图片进行任何压缩

                        if (width > 100 || height > 100) {

                            int halfWidth = width / 2;
                            int halfHeight = height / 2;


                            while ((halfWidth / inSampleSize) >= 100 && (halfHeight / inSampleSize) >= 100) {
                                //计算采样率
                                inSampleSize *= 2;
                            }
                        }
                        //采样率改变了
                        options.inSampleSize = inSampleSize;// 8

                        //图片压缩完之后，放行图片解析
                        options.inJustDecodeBounds = false;//告诉图片转换工厂，可以解析图片了
                        //关闭之前的流
                        is.close();
                        //重新再去得到当前这张图片的字节流数据
                        is = url.openStream();
                        //开始解析图片
                        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                        //关闭流
                        is.close();
                        // Bitmap bitmap = BitmapFactory.decodeStream(is);
                        return bitmap;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                return null;
            }
        }.execute(path);
    }
}