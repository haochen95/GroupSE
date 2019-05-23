package com.example.haoch.wocao.file_api;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.haoch.wocao.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Created by haoch on 2018/3/4.
 */

public class Fileview_experiment extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    LinearLayout ll_root;
    TextView root;
    ListView lv;
    List<File> list = new ArrayList<File>();

    //获取SD卡根目录，必须获取权限，权限在AndroidManifest.xml/Permissions中添加
    public static final String SDCard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GroupSE";

    // 当前文件目录
    public static String currDir = SDCard;
    FileviewAdapter adapter;

    private Context context;
    private String eTitle = "";
    private String person = "";
    private String path_name;
    private int pop;

    private String selectedPath = "";
    private String zip_file_name;
    private String selected_activity;

    private String click_pos;
    private SharedPreferences getUserName;
    private static final String PREF_NAME = "prefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        initData();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("INPUT_NAME"));
        adapter = new FileviewAdapter(Fileview_experiment.this, list);
//        lv.setAdapter(adapter);
        getAllFiles();
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                SwipeMenuCreator creator = new SwipeMenuCreator() {
                    @Override
                    public void create(SwipeMenu menu) {
                        SwipeMenuItem openItem = new SwipeMenuItem(context);
                        openItem.setBackground(new ColorDrawable(Color.GREEN));
                        openItem.setWidth(dp2px(90));
                        openItem.setTitle("Send");
                        openItem.setTitleSize(20);
                        openItem.setTitleColor(Color.WHITE);
                        menu.addMenuItem(openItem);

                        SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                        deleteItem.setBackground(new ColorDrawable(Color.LTGRAY));
                        deleteItem.setWidth(dp2px(90));
                        deleteItem.setTitle("Delete");
                        deleteItem.setTitleSize(20);
                        deleteItem.setTitleColor(Color.BLACK);
                        menu.addMenuItem(deleteItem);
                    }
                };

                SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.lv_file);
                listView.setMenuCreator(creator);

                listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        //index的值就是在SwipeMenu依次添加SwipeMenuItem顺序值，类似数组的下标。
                        //从0开始，依次是：0、1、2、3...

                        switch (index) {
                            case 0:
                                inputActivity(click_pos);
//                                sendEmail(click_pos);
                                break;

                            case 1:
                                File dir = new File(SDCard + "/" + click_pos);
                                deleteRecursive(dir);
                                list.remove(position);
                                adapter.notifyDataSetChanged();

                        }

                        // false : 当用户触发其他地方的屏幕时候，自动收起菜单。
                        // true : 不改变已经打开菜单的样式，保持原样不收起。
                        return false;
                    }
                });

                // 监测用户在ListView的SwipeMenu侧滑事件。
                listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

                    @Override
                    public void onSwipeStart(int pos) {
                        click_pos = list.get(pos).getName();
                        Log.d("位置:" + pos, "开始侧滑...");

                    }

                    @Override
                    public void onSwipeEnd(int pos) {

                        Log.d("位置:" + pos, "侧滑结束.");
                    }
                });

                listView.setAdapter(adapter);
            }
        };
        r1.run();

    }


    public int dp2px(float dipValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    // after you enter number and we can send attachedment via email;
    public void sendEmail(String data_path, String activity_name) {

        // we select a activity

        ArrayList<Uri> uris = new ArrayList<Uri>();

        File file_location = new File(SDCard + "/" + data_path);
        File[] file_son = file_location.listFiles();
        for (File loop_file : file_son) {
            File path_file = new File(SDCard + "/" + data_path + "/" + loop_file.getName().toString());
            Uri u = Uri.fromFile(path_file);
            uris.add(u);
        }


        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);

        email.setType("application/zip");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"inuidcm@gmail.com"});  //set up email
        email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);   //add attachment

        // subject should be activity and name
        getUserName = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String user_name = getUserName.getString("account", "");


        String subject_name = activity_name + "+" +  user_name;
        email.putExtra(Intent.EXTRA_SUBJECT, subject_name);
        startActivity(Intent.createChooser(email, "pick an Email provider"));
    }

    private void inputActivity(final String data_f){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select one activtiy");

        final String[] activities = new  String[]{
                "Take class",
                "Meeting",
                "Discussion",
                "Seminar",
                "Walking",
                "Others"
        };
        builder.setSingleChoiceItems(activities,
                -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // get selected item name
                        selected_activity = Arrays.asList(activities).get(i);


                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do something after ok
                sendEmail(data_f, selected_activity);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void initData() {
        ll_root = (LinearLayout) findViewById(R.id.ll_root);
        lv = (ListView) findViewById(R.id.lv_file);
        lv.setOnItemClickListener(this);

    }

    public void getAllFiles() {
        list.clear();
        File file = new File(currDir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file2 : files) {
                    list.add(file2);
                }
            }
        }

        sort();

        adapter.notifyDataSetChanged();
    }

    private void sort() {
        //使用Collection.sort排序，给定一个比较器，使用匿名内部类实现比较器接口
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
    }

    //ListView  监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        File file = list.get(position);
        if (file.isDirectory()) {
            // 下一层目录
            currDir = file.getAbsolutePath();
            //根目录名加上当前文件夹名
            addDirText(file);
            getAllFiles();
        } else {

            // open csv file
            File file_csv = new File(SDCard + "/" + file.getParentFile().getName() + "/" + file.getName());
            Uri path = Uri.fromFile(file_csv);
            Intent csvOpenintent = new Intent(Intent.ACTION_VIEW);
            csvOpenintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            csvOpenintent.setDataAndType(path, "*/*");
            try {
                startActivity(csvOpenintent);
            } catch (ActivityNotFoundException e) {

            }

//            Toast.makeText(Fileview_experiment.this, "打开" + file.getName(),
//                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addDirText(File file) {
        String name = file.getName();
        TextView tv = new TextView(this);
        tv.setText(name + ">");
        ll_root.addView(tv);
        //将当前的路径保存
        tv.setTag(file.getAbsolutePath());

        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();
                currDir = tag;
                getAllFiles();

                //将后面的所有TextView的tag移除
                //从后往前删，一个一个删
                for (int i = ll_root.getChildCount(); i > 1; i--) {
                    View view = ll_root.getChildAt(i - 1);
                    String currTag = view.getTag().toString();
                    if (!currTag.equals(currDir)) {
                        ll_root.removeViewAt(i - 1);
                    } else {
                        return;
                    }
                }
            }
        });
    }

    // Back键返回上一级
    @Override
    public void onBackPressed() {
        // 如果当前目录就是系统根目录，直接调用父类
        if (currDir.equals(SDCard)) {
            super.onBackPressed();
        } else {
            // 返回上一层，显示上一层所有文件
            currDir = new File(currDir).getParent();
            getAllFiles();

            //将当前TextView的tag移除
            //总是将最后一个TextView移除
            View view = ll_root.getChildAt(ll_root.getChildCount() - 1);
            ll_root.removeView(view);

        }
    }


    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    @Override
    public void onClick(View v) {

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            zip_file_name = intent.getStringExtra("your name");
        }
    };

    private static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }


}

