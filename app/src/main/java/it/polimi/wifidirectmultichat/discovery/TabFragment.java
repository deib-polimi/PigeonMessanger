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
import it.polimi.wifidirectmultichat.discovery.services.WiFiP2pServicesListFragment;
import lombok.Getter;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the interface
 * to handle interaction events.
 * Use the {@link TabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment {

    @Getter private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    @Getter ViewPager mViewPager;

    @Getter private static WiFiP2pServicesListFragment wiFiP2pServicesListFragment;
    @Getter private static List<WiFiChatFragment> wiFiChatFragmentList;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DownloadNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment newInstance() {
        TabFragment fragment = new TabFragment();
        wiFiChatFragmentList = new ArrayList<>();

        WiFiChatFragment frag = WiFiChatFragment.newInstance();
        frag.setTabNumber(new Integer(1));
        wiFiChatFragmentList.add(frag);

        frag = WiFiChatFragment.newInstance();
        frag.setTabNumber(new Integer(2));
        wiFiChatFragmentList.add(frag);

        frag = WiFiChatFragment.newInstance();
        frag.setTabNumber(new Integer(3));
        wiFiChatFragmentList.add(frag);

        frag = WiFiChatFragment.newInstance();
        frag.setTabNumber(new Integer(4));
        wiFiChatFragmentList.add(frag);

        frag = WiFiChatFragment.newInstance();
        frag.setTabNumber(new Integer(5));
        wiFiChatFragmentList.add(frag);

        wiFiP2pServicesListFragment = WiFiP2pServicesListFragment.newInstance();

        return fragment;
    }

    public TabFragment() {
    }

    public int getItemTabNumber(WiFiChatFragment fragment) {
        return this.mSectionsPagerAdapter.getItemPosition(fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_tab, container, false);


        // Set up the action bar.
//        final ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d("log", "swishato il tab in posizione: " + position);
            }
        });

        return rootView;
    }


    /**
     * A FragmentPagerAdapter that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        //
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //
        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0) {
                return wiFiP2pServicesListFragment;
            } else {
                return wiFiChatFragmentList.get(position-1);
            }
        }

        @Override
        public int getCount() {
            return wiFiChatFragmentList.size() + 1; //perche' il primo fragment (non nella lista) e' quello con la lista dei servizi
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return (new String("Services")).toUpperCase(l);
                default :
                    return (new String("Chat") + position).toUpperCase(l);
            }
        }


    }


    public WiFiChatFragment getChatFragmentByTab(int tabNumber) {
        return wiFiChatFragmentList.get(tabNumber - 1);
    }
}
