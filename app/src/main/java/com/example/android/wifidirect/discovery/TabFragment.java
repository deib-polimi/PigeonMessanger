package com.example.android.wifidirect.discovery;

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

import java.util.Locale;

import lombok.Getter;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the interface
 * to handle interaction events.
 * Use the {@link TabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment {

    SectionsPagerAdapter mSectionsPagerAdapter;

//    private WiFiDirectConnectionFragment connectionFragment;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    @Getter ViewPager mViewPager;

    @Getter private WiFiDirectServicesList wiFiDirectServicesList;
    @Getter private WiFiChatFragment wiFiChatFragment1;
    @Getter private WiFiChatFragment wiFiChatFragment2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DownloadNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment newInstance() {
        TabFragment fragment = new TabFragment();

        return fragment;
    }

    public TabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        this.connectionFragment = WiFiDirectConnectionFragment.newInstance("","");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
//        setHasOptionsMenu(true);
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
//
//            ist = new WiFiDirectServicesList();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.container_root, servicesList, "services").commit();

            switch (position) {
                case 0:
                    wiFiDirectServicesList = new WiFiDirectServicesList();
                    return wiFiDirectServicesList;
                case 1:
                    wiFiChatFragment1 = new WiFiChatFragment();
                    return wiFiChatFragment1;
                default:
                    wiFiChatFragment2 = new WiFiChatFragment();
                    return wiFiChatFragment2;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return (new String("Connessione")).toUpperCase(l);
                case 1:
                    return (new String("Chat1")).toUpperCase(l);
                case 2:
                    return (new String("Chat2")).toUpperCase(l);
            }
            return null;
        }


    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.about, container, false);
            return rootView;
        }
    }

}
