
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.BaseProgramFragment.ScrollerManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;

/**
 * A fragment that manages the display of the various fragments that make up the
 * interface for programming an experiment.
 */
public class ProgramFragment extends Fragment implements ScrollerManager {
    private static final String KEY_FRAG_IDS = "key_frag_ids";

    private ArrayList<BaseProgramFragment> mFragments;

    private HorizontalScrollView mScroller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_program, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Build and store an array of our internal ids so we can reconstruct
        // our fragment tags.
        int[] ids = new int[mFragments.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = mFragments.get(i).getScrollerPos();
        }

        outState.putIntArray(KEY_FRAG_IDS, ids);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScroller = (HorizontalScrollView)view.findViewById(R.id.scroll);

        mFragments = new ArrayList<BaseProgramFragment>();

        if (savedInstanceState == null) {
            setNextFragment(null, new LoopsListFragment());
        } else {
            // Reconstruct our fragment state.
            int[] ids = savedInstanceState.getIntArray(KEY_FRAG_IDS);
            FragmentManager cfm = getChildFragmentManager();
            for (int i = 0; i < ids.length; i++) {
                BaseProgramFragment f = (BaseProgramFragment)cfm.findFragmentByTag("program_frag"
                        + ids[i]);
                f.setScrollerPos(mFragments.size());
                f.setScrollerManager(this);
                mFragments.add(f);
            }

            // Ensure fragments are attached in the correct order.
            FragmentTransaction ft = cfm.beginTransaction();
            for (Fragment f : mFragments) {
                ft.detach(f);
            }
            ft.commit();
            cfm.executePendingTransactions();
            for (Fragment f : mFragments) {
                FragmentTransaction t = cfm.beginTransaction();
                t.attach(f);
                t.commit();
                cfm.executePendingTransactions();
            }
        }
    }

    @Override
    public void removeFragment(BaseProgramFragment f) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        snip(f.getScrollerPos(), ft);
        ft.commit();
    }

    @Override
    public void setNextFragment(BaseProgramFragment requester, BaseProgramFragment next) {
        if (next != null) {
            next.setScrollerManager(this);
        }

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        if (requester != null) {
            int snipAt = requester.getScrollerPos() + 1;
            snip(snipAt, ft);
        }

        if (next != null) {
            addFragment(next, ft);
        }

        ft.commit();

        // getChildFragmentManager().executePendingTransactions();
        //
        // mScrollView.post(new Runnable() {
        // @Override
        // public void run() {
        // mScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
        // }
        // });
    }

    private void addFragment(BaseProgramFragment fragment, FragmentTransaction ft) {
        fragment.setScrollerPos(mFragments.size());
        mFragments.add(fragment);

        Log.e("add", fragment.getScrollerPos() + "");

        ft.add(R.id.program_container, fragment, "program_frag" + fragment.getScrollerPos());

        // WARNING: It has been mentioned it is possible that the order of
        // fragments (in the container) can change on a configuration change.
    }

    private void snip(int pos, FragmentTransaction ft) {
        for (int i = mFragments.size() - 1; i >= pos; i--) {
            ft.remove(mFragments.get(i));
            Log.e("snip", i + "");
            mFragments.remove(i);
        }
    }

    @Override
    public void requestMoveTo(final int x) {
        getChildFragmentManager().executePendingTransactions();
        mScroller.post(new Runnable() {
            @Override
            public void run() {
                if (x > 0) {
                    mScroller.smoothScrollBy(x, 0);
                }
            }
        });
    }

    @Override
    public void hideNextFragment(BaseProgramFragment requester) {
        int nextPos = requester.getScrollerPos() + 1;

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        snip(nextPos + 1, ft);
        ft.commit();

        if (nextPos < mFragments.size()) {
            mFragments.get(nextPos).hide();
        }
    }
}
