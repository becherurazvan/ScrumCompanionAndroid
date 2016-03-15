package RecyclerViewHolders;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.colinearproductions.scrumcompanion.CurrentSprintFragment;
import com.colinearproductions.scrumcompanion.CurrentSprintListSprints;
import com.colinearproductions.scrumcompanion.CurrentSprintStatePageFragment;

import java.util.HashMap;

public class CurrentSprintFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[]{"User Stories", "Not Started", "Working On", "Done"};

    HashMap<Integer,String> mFragmentTags;

    FragmentManager fragmentManager;

    public CurrentSprintFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        mFragmentTags = new HashMap<>();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO for the first item, return a different kind of fragment where you hold all user stories from that sprint


        if (position == 0) {
            return new CurrentSprintListSprints();
        } else {
            return CurrentSprintStatePageFragment.newInstance(position);
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if(object instanceof Fragment){
            String tag =  ((Fragment) object).getTag();
            mFragmentTags.put(position,tag);
        }
        return object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    public Fragment getFragment(int position){
        String tag = mFragmentTags.get(position);

        if(tag==null){
            return null;
        }

        return fragmentManager.findFragmentByTag(tag);

    }

    public void updateAll(){
        for(int i=1;i<PAGE_COUNT;i++){
            getFragment(i).onResume();

        }
    }
}