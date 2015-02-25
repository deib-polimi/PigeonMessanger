package it.polimi.wifidirectmultichat.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.polimi.wifidirectmultichat.R;
import it.polimi.wifidirectmultichat.discovery.chatmessages.WiFiChatFragment;
import it.polimi.wifidirectmultichat.discovery.services.WiFiP2pServicesFragment;
import lombok.Getter;


/**
 * Class that represents a Fragment with other Fragments as Tabs.
 * Created by Stefano Cappa on 05/02/15.
 */
public class TabFragment extends Fragment {

    @Getter
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @Getter
    private ViewPager mViewPager;
    @Getter
    private static WiFiP2pServicesFragment wiFiP2pServicesFragment;
    @Getter
    private static List<WiFiChatFragment> wiFiChatFragmentList;


    /**
     * Method to obtain a new Fragment's instance.
     *
     * @return This Fragment instance.
     */
    public static TabFragment newInstance() {
        TabFragment fragment = new TabFragment();
        wiFiP2pServicesFragment = WiFiP2pServicesFragment.newInstance();
        wiFiChatFragmentList = new ArrayList<>();
        return fragment;
    }

    /**
     * Default Fragment constructor.
     */
    public TabFragment() {
    }


    /**
     * Method to add a new tab if necessary.
     * It use the callerMessage to determine which is the method or portion of code that called this method.
     * Its can be very useful to understand when it's necessary to add a new tab.
     * There are some different situations that can be necessary to add a new tab, but not always.
     * I mean,
     *
     * @param callerMessage A message that contains one of this strings:
     *                      {@link it.polimi.wifidirectmultichat.discovery.Configuration}.MY_HANDLE_MSG or
     *                      {@link it.polimi.wifidirectmultichat.discovery.Configuration}.MESSAGE_READ_MSG.
     */
    public void addNewTabChatFragment(String callerMessage) {
        WiFiChatFragment frag = WiFiChatFragment.newInstance();
        //adds a new fragment, sets the tabNumber with listsize+1, because i want to add an element to this list and get
        //this position, but at the moment the list is not updated. When i use listsize+1
        // i'm considering "+1" as the new element that i want to add.
        frag.setTabNumber(wiFiChatFragmentList.size() + 1);


        //now i add the fragment to the list, and obviously tabNumber is correct, because now the list is updated.
        Log.d("prova", "wiFiChatFragmentList.size : " + wiFiChatFragmentList.size());
        Log.d("prova", "DeviceTablist.size : " + DeviceTabList.getInstance().getSize());
        Log.d("prova", "mViewPager.getAdapter.size : " + mViewPager.getAdapter().getCount());
        Log.d("prova", "mSectionsPagerAdapter.size : " + mSectionsPagerAdapter.getCount());


        //i need this because i need to add a new tab, only if its necessary

        //first check to know if is the first message exchanged between this device and our go/client.
        if (callerMessage.contains(Configuration.MY_HANDLE_MSG)) {
            Log.d("prova", "logMessage.contains(Configuration.MY_HANDLE_MSG)");
            //probably this if is useless
            if (wiFiChatFragmentList.size() <= DeviceTabList.getInstance().getSize()) {
                Log.d("prova", "logMessage.contains(Configuration.MY_HANDLE_MSG) - tabNum = " + ((MainActivity) getActivity()).getTabNum());
                //really necessary if to be sure that tabNum is higher that the size of wiFiChatFragmentList.
                //this represents a new chat and obviously a new tab to add
                //Otherwise if this condition is false, i re-enabling a older chat, and obviously is not necessary to add a new tabb, because
                //this app can reactivate previous conversations.
                if (((MainActivity) getActivity()).getTabNum() - 1 > wiFiChatFragmentList.size() - 1) {
                    wiFiChatFragmentList.add(frag);
                    Log.d("prova", "logMessage.contains(Configuration.MY_HANDLE_MSG) - ADDED!!!");
                }
            }
        } else if (callerMessage.contains(Configuration.MESSAGE_READ_MSG)) {
            Log.d("prova", "logMessage.contains(Configuration.MESSAGE_READ_MSG) - NOT ADDED!!!");
        }

        this.mSectionsPagerAdapter.notifyDataSetChanged();

        Log.d("prova-3", "wiFiChatFragmentList.size : " + wiFiChatFragmentList.size());
        Log.d("prova-3", "DeviceTablist.size : " + DeviceTabList.getInstance().getSize());
        Log.d("prova-3", "mViewPager.getAdapter.size : " + mViewPager.getAdapter().getCount());
        Log.d("prova-3", "mSectionsPagerAdapter.size : " + mSectionsPagerAdapter.getCount());
    }

    /**
     * Method to get the Fragment, specifying the position / tabnumber.
     *
     * @param tabNumber int that represents the position of this fragment inside the list of tabs.
     * @return The {@link WiFiChatFragment } at position tabNumber in the list of
     * {@link it.polimi.wifidirectmultichat.discovery.chatmessages.WiFiChatFragment}.
     */
    public WiFiChatFragment getChatFragmentByTab(int tabNumber) {
        return wiFiChatFragmentList.get(tabNumber - 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);


        // When swiping between different sections, select the corresponding
        // tab.
        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }


    /**
     * Class that represents the FragmentPagerAdapter of this Fragment, that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return wiFiP2pServicesFragment; //the first fragment reserved to the serviceListFragment
            } else {
                return wiFiChatFragmentList.get(position - 1); //chatFragments associated to this position
            }
        }

        @Override
        public int getCount() {
            return wiFiChatFragmentList.size() + 1; //because the first fragment (not inside into the list) is a WiFiP2pServicesFragment
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return ("Services").toUpperCase(l);
                default:
                    //if possibile use the devicename like tabname.
                    //Attention this isn't working. Please be careful.
//                    if(DeviceTabList.getInstance().getDevice(position)!=null) {
//                        return DeviceTabList.getInstance().getDevice(position).deviceName.toUpperCase(l);
//                    }
                    //use this to be sure
                    return ("Chat" + position).toUpperCase(l);
            }
        }
    }
}
