
package nz.ac.otago.psyanlab.common.designer.util;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public interface ExperimentObjectAdapter extends ListAdapter {
    int getObjectKind(int position);

    /**
     * Helper class to easily convert any list adapter into an experiment object
     * adapter which provides object kind data.
     */
    public class Wrapper implements ExperimentObjectAdapter {
        private ListAdapter mAdapter;

        private int mKind;

        private KindBinder mKindBinder;

        /**
         * Wrap a ListAdapter to provide addition kind data.
         * 
         * @param kind All data in the wrapped ListAdapter is of this kind.
         * @param adapter ListAdapter to wrap.
         */
        public Wrapper(int kind, ListAdapter adapter) {
            mKind = kind;
            mAdapter = adapter;
        }

        /**
         * Wrap a ListAdapter to provide additional kind data.
         * 
         * @param kindBinder Binder which provides the kind data which may
         *            differ per entry in the wrapped adapter.
         * @param adapter ListAdapter to wrap.
         */
        public Wrapper(KindBinder kindBinder, ListAdapter adapter) {
            mKindBinder = kindBinder;
            mAdapter = adapter;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return mAdapter.areAllItemsEnabled();
        }

        @Override
        public int getCount() {
            return mAdapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return mAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return mAdapter.getItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return mAdapter.getItemViewType(position);
        }

        @Override
        public int getObjectKind(int position) {
            if (mKindBinder == null) {
                return mKind;
            }

            return mKindBinder.getKind(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mAdapter.getView(position, convertView, parent);
        }

        @Override
        public int getViewTypeCount() {
            return mAdapter.getViewTypeCount();
        }

        @Override
        public boolean hasStableIds() {
            return mAdapter.hasStableIds();
        }

        @Override
        public boolean isEmpty() {
            return mAdapter.isEmpty();
        }

        @Override
        public boolean isEnabled(int position) {
            return mAdapter.isEnabled(position);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mAdapter.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mAdapter.unregisterDataSetObserver(observer);
        }

        public interface KindBinder {
            public int getKind(int position);
        }
    }
}
