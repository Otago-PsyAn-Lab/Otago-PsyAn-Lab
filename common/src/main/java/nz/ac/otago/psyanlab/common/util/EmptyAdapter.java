
package nz.ac.otago.psyanlab.common.util;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public final class EmptyAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }
}
