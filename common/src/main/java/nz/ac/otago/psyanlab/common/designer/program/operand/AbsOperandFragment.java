
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

package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;

public abstract class AbsOperandFragment extends TonicFragment {
    protected static final String ARG_CALLER_ID = "arg_caller_id";

    protected static final String ARG_CALLER_KIND = "arg_caller_kind";

    protected static final String ARG_TYPE = "arg_type";

    public static <T extends TonicFragment> T init(T f, int callerKind, long callerId,
            long operandId, int type) {
        f = TonicFragment.init(f, operandId);
        Bundle args = f.getArguments();
        args.putInt(ARG_TYPE, type);
        args.putInt(ARG_CALLER_KIND, callerKind);
        args.putLong(ARG_CALLER_ID, callerId);
        f.setArguments(args);
        return f;
    }

    protected long mCallerId;

    protected int mCallerKind;

    protected int mOperandType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mOperandType = savedInstanceState.getInt(ARG_TYPE);
        } else {
            Bundle args = getArguments();
            if (args != null) {
                if (!args.containsKey(ARG_TYPE)) {
                    throw new RuntimeException("Expected operand type requirement.");
                }
                if (!args.containsKey(ARG_CALLER_KIND)) {
                    throw new RuntimeException("Expected caller kind.");
                }
                if (!args.containsKey(ARG_CALLER_ID)) {
                    throw new RuntimeException("Expected caller id.");
                }
                mOperandType = args.getInt(ARG_TYPE);
                mCallerId = args.getLong(ARG_CALLER_ID);
                mCallerKind = args.getInt(ARG_CALLER_KIND);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_TYPE, mOperandType);
    }

    abstract public void saveOperand();
}
