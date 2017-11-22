package liuhao.baway.com.monthlianxi;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MyExpandListView exlv;
    private List<DataBeanlist.DataBean> databean = new ArrayList<>();
    private MyAdapter adapter  ;
    private CheckBox checkbox;
    private TextView tv_price;
    private TextView tv_num;
    private String addnum ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        loadData();
    }



    private void loadData() {
        String url = "http://120.27.23.105/product/getCarts?uid=" + 106;
         HttpOkUntils.getHttpOkUntils(this).okHttpGet(url, new NetClickListener() {
             @Override
             public void Suesses(DataBeanlist baseBean) {

                 List<DataBeanlist.DataBean> data = baseBean.getData();
                 databean.addAll(data);
                 adapter = new MyAdapter();
                 exlv.setAdapter(adapter);
                 int count = exlv.getCount();
                 for (int i=0;i<count;i++){
                     exlv.expandGroup(i);
                 }
                 //去掉箭头
                 exlv.setGroupIndicator(null);
                 exlv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                     @Override
                     public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                         return true;
                     }
                 });

             }

             @Override
             public void Error(DataBeanlist baseBean) {

             }
         });
    }

    private void initView() {

        exlv = (MyExpandListView) findViewById(R.id.expandlistview);
        checkbox = (CheckBox) findViewById(R.id.checkbox2);
        checkbox.setOnClickListener(this);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_num = (TextView) findViewById(R.id.tv_num);

    }

    @Override
    public void onClick(View view) {
        if(checkbox.isChecked()){
                    for (int i = 0 ; i < databean.size() ; i ++ ){
                        List<DataBeanlist.DataBean.ListBean> list = databean.get(i).getList();
                        databean.get(i).setGroupCheck(true);
                        for (int y = 0 ;y<list.size();y++){
                            list.get(y).setChildGroupCheck(true);
                        }
                    }
            sum();
            notifyDataSetChanged();
        }else {
            for (int i = 0 ; i < databean.size() ; i ++ ){
                List<DataBeanlist.DataBean.ListBean> list = databean.get(i).getList();
                databean.get(i).setGroupCheck(false);
                for (int y = 0 ;y<list.size();y++){
                    list.get(y).setChildGroupCheck(false);
                }
            }
            //计算
            sum();
            notifyDataSetChanged();
        }
    }


    class MyAdapter extends BaseExpandableListAdapter{
        @Override
        public int getGroupCount() {
            return  databean.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return databean.get(i).getList().size();
        }

        @Override
        public Object getGroup(int i) {
            return databean.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return databean.get(i).getList().get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            View v = View.inflate(MainActivity.this,R.layout.group_layout,null);
            CheckBox cb_group =  v.findViewById(R.id.cb_group);
            if(databean.get(i).isGroupCheck()){
                cb_group.setChecked(true);
            }else {
                cb_group.setChecked(false);
            }

            TextView tv_group_title =  v.findViewById(R.id.tv_group_title);
            tv_group_title.setText(databean.get(i).getSellerName());
            cb_group.setOnClickListener(new GroupClickLister(i,cb_group));

            return v;
        }

        @Override
        public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
            View v = View.inflate(MainActivity.this,R.layout.child_layout,null);
            CheckBox cb_child =  v.findViewById(R.id.cb_child);
            if(databean.get(i).getList().get(i1).isChildGroupCheck()){
                    cb_child.setChecked(true);
            }else {
                cb_child.setChecked(false);
            }
            TextView tv_child_title =  v.findViewById(R.id.tv_child_title);
            tv_child_title.setText(databean.get(i).getList().get(i1).getTitle());
            String[] split = databean.get(i).getList().get(i1).getImages().split("\\|");
            ImageView image_child =  v.findViewById(R.id.image_child);
            TextView tv_price =  v.findViewById(R.id.tv_child_price);
            tv_price.setText("￥" + databean.get(i).getList().get(i1).getPrice());
            ImageLoader.getInstance().displayImage(split[0],image_child);
            DiskCache diskCache = ImageLoader.getInstance().getDiskCache();
            String path1 = ImageLoader.getInstance().getDiskCache().get(split[0]).getPath();
            File directory = diskCache.getDirectory();
            //log测试输出
                    Log.d("qq","directory    " + directory);
            String path = directory.getPath();
            //log测试输出
                    Log.d("qq","path1  " + path1);
            File file = new File(Environment.getExternalStorageDirectory(),
                    "aaaa.jpg");
            Bitmap bitmap = BitmapUtils.getBitmap(file.getPath(), 50, 50);
            image_child.setImageBitmap(bitmap);
            cb_child.setOnClickListener(new ChildClickLister(i,i1,cb_child));
            return v;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }

    }
    class ChildClickLister implements View.OnClickListener{

        private int i ;
        private int i1;
        private CheckBox cb_child ;

        public ChildClickLister(int i, int i1, CheckBox cb_child) {
            this.i1 = i1;
            this.i = i;
            this.cb_child = cb_child;
        }

        @Override
        public void onClick(View view) {
            if(cb_child.isChecked()){
                databean.get(i).getList().get(i1).setChildGroupCheck(true);
            }else {
                databean.get(i).getList().get(i1).setChildGroupCheck(false);
            }
            //二级联动一级状态
            setParentCheckFlag();
            //判断全选状态
            int num = 0 ;
            for (int i = 0 ; i<databean.size();i++){
                if(!databean.get(i).isGroupCheck()){
                    num++;
                }
            }
            if(num==0){
                checkbox.setChecked(true);
            }else {
                checkbox.setChecked(false);
            }
            //计算价格
            sum();
        }

        private void setParentCheckFlag() {
            DataBeanlist.DataBean dataBean = databean.get(i);
            List<DataBeanlist.DataBean.ListBean> list = dataBean.getList();
            for (int i = 0 ; i < list.size();i++){
                 if(!list.get(i).isChildGroupCheck()){
                      dataBean.setGroupCheck(false);
                     notifyDataSetChanged();
                     return;
                 }
                if(i == list.size()-1){
                    dataBean.setGroupCheck(true);
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }
    private void sum() {

        int num = 0 ;
        int price = 0 ;
        for (DataBeanlist.DataBean dd : databean){
            List<DataBeanlist.DataBean.ListBean> list = dd.getList();
            for (DataBeanlist.DataBean.ListBean ddl : list){
                if(ddl.isChildGroupCheck()){
                    //log测试输出
                            Log.d("qq","ddl.getNum() = " + ddl.getNum());
                    num++;
                    price+=ddl.getPrice();
                }
            }
        }
        tv_num.setText("结算(" + num+")");
        tv_price.setText(price+"");
    }

    class GroupClickLister implements View.OnClickListener{

        private int i ;
        CheckBox cb_group ;

        public GroupClickLister(int i, CheckBox cb_group) {
            this.i = i;
            this.cb_group = cb_group;
        }

        @Override
        public void onClick(View view) {
            if(cb_group.isChecked()){
                setCheck(true);
            }else {
                setCheck(false);
                checkbox.setChecked(false);
            }
            notifyDataSetChanged();
        }
        public void setCheck(boolean check) {
            DataBeanlist.DataBean dataBean = databean.get(i);
            List<DataBeanlist.DataBean.ListBean> list = dataBean.getList();
            //一级状态
            dataBean.setGroupCheck(check);
            //二级状态
            for (DataBeanlist.DataBean.ListBean list1 : list){
                list1.setChildGroupCheck(check);
            }
            int num = 0 ;
                for (int i = 0 ; i<databean.size();i++){
                    if(!databean.get(i).isGroupCheck()){
                        num++;
                    }
                }
                if(num==0){
                    checkbox.setChecked(true);
                }else {
                    checkbox.setChecked(false);
            }
            sum();
        }
    }
    //刷新
    public void notifyDataSetChanged(){
            exlv.setAdapter(adapter);
        int count = exlv.getCount();
        for (int i=0;i<count;i++){
             exlv.expandGroup(i);
        }
        exlv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
    }
}
