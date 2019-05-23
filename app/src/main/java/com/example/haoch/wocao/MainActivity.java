package com.example.haoch.wocao;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private AccountFragment accountFragment;
    private NearbyDeviceFragment nearbyDeviceFragment;
    private BeaconFragment beaconFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainFrame = (FrameLayout)findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView)findViewById(R.id.main_nav);

//        beaconFragment = new BeaconFragment();
//        nearbyDeviceFragment = new NearbyDeviceFragment();
//        settingFragment = new SettingFragment();
//        accountFragment = new AccountFragment();

        shiftFragment(1);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_beacon:
//                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                      shiftFragment(1);
                        return true;
                    case R.id.nav_nearby:
//                        mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        shiftFragment(2);
                        return true;
                    case R.id.nav_setting:
//                        mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        shiftFragment(3);
                        return true;
                    case R.id.nav_account:
//                        mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        shiftFragment(4);
                        return true;

                    default:
                        return false;
                }
            }

        });
    }

    private void shiftFragment(int index){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        hideFragment(fragmentTransaction);
        switch (index){
            case 1:
                if (beaconFragment!=null)
                    fragmentTransaction.show(beaconFragment);
                else{
                    beaconFragment = new BeaconFragment();
                    fragmentTransaction.add(R.id.main_frame, beaconFragment);
                }
                break;
            case 2:
                if (nearbyDeviceFragment!=null)
                    fragmentTransaction.show(nearbyDeviceFragment);
                else{
                    nearbyDeviceFragment = new NearbyDeviceFragment();
                    fragmentTransaction.add(R.id.main_frame, nearbyDeviceFragment);
                }
                break;
            case 3:
                if (settingFragment!=null)
                    fragmentTransaction.show(settingFragment);
                else{
                    settingFragment = new SettingFragment();
                    fragmentTransaction.add(R.id.main_frame, settingFragment);
                }
                break;
            case 4:
                if (accountFragment!=null)
                    fragmentTransaction.show(accountFragment);
                else{
                    accountFragment = new AccountFragment();
                    fragmentTransaction.add(R.id.main_frame, accountFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (beaconFragment!=null){
            fragmentTransaction.hide(beaconFragment);
        }
        if (nearbyDeviceFragment!=null){
            fragmentTransaction.hide(nearbyDeviceFragment);
        }
        if (settingFragment!=null){
            fragmentTransaction.hide(settingFragment);
        }
        if (accountFragment!=null){
            fragmentTransaction.hide(accountFragment);
        }
    }
}
