/*
 Copyright (C) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.multi;

import nz.ac.otago.psyanlab.multi.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Subclass of {@link UserListFragment} that swaps out the ListView for a
 * GridView.
 */
public class UserGridFragment extends UserListFragment {
    public UserGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_grid,
                container, false);
    }
    
    /**
     * Width of the columns in the list. The number of columns will scale to the
     * screen.
     * 
     * @param dips The desired width of the screen in display independent
     *            pixels.
     */
    public void setColumnWidth(float width) {
        ((GridView)mList).setColumnWidth((int)width);
        ((GridView)mList).setNumColumns(GridView.AUTO_FIT);
    }

    /**
     * Set the number of columns in the list.
     * 
     * @param numColumns The desired number of columns.
     */
    public void setNumColumns(int numColumns) {
        ((GridView)mList).setNumColumns(numColumns);
    }
}
