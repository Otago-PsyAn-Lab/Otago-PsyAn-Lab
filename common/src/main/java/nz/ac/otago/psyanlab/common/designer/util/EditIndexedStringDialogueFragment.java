
/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.designer.util;

import android.os.Bundle;

public class EditIndexedStringDialogueFragment extends EditStringDialogueFragment {
    public static final String RESULT_INDEX = "result_index";

    protected static final String ARG_INDEX = "arg_index";

    public static EditIndexedStringDialogueFragment init(
            EditIndexedStringDialogueFragment fragment, int titleResId, String defaultValue,
            int hintResId, int requestCode, int index) {
        init(fragment, titleResId, defaultValue, hintResId, requestCode);
        return addIndex(fragment, index);
    }

    public static EditIndexedStringDialogueFragment init(
            EditIndexedStringDialogueFragment fragment, int titleResId, String defaultValue,
            int hintResId, int requestCode, int index, int layoutResId) {
        EditStringDialogueFragment.init(fragment, titleResId, defaultValue, hintResId, requestCode,
                layoutResId);
        return addIndex(fragment, index);
    }

    private static EditIndexedStringDialogueFragment addIndex(
            EditIndexedStringDialogueFragment fragment, int index) {
        Bundle args = fragment.getArguments();
        args.putInt(ARG_INDEX, index);
        args.putBoolean(ARG_DELETE_ENABLED, true);
        return fragment;
    }

    @Override
    protected Bundle getResult() {
        Bundle result = super.getResult();
        result.putInt(RESULT_INDEX, getArguments().getInt(ARG_INDEX));
        return result;
    }

    @Override
    protected Bundle getResultDelete() {
        Bundle result = super.getResultDelete();
        result.putInt(RESULT_INDEX, getArguments().getInt(ARG_INDEX));
        return result;
    }
}
