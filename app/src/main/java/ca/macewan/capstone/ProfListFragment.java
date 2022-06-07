package ca.macewan.capstone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import ca.macewan.capstone.adapter.ViewPagerAdapter;

public class ProfListFragment extends Fragment {

    public ProfListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prof_list, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tl_main);
        ViewPager2 viewPager2 = view.findViewById(R.id.vp2_main);

        ViewPagerAdapter adapter = new ViewPagerAdapter(requireActivity());
        viewPager2.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("All");
                        break;
                    case 1:
                        tab.setText("Invited");
                        break;
                }
            }
        }).attach();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.menu_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent menuIntent = new Intent(getActivity(), ProposalCreationActivity.class);
            startActivity(menuIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}