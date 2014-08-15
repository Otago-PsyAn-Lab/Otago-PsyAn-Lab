
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

package nz.ac.otago.psyanlab.common.designer.subject;

import com.mobeta.android.dslv.DragSortListView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.LandingPageDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.QuestionDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.model.LandingPage;
import nz.ac.otago.psyanlab.common.model.Question;
import nz.ac.otago.psyanlab.common.model.subject.Toggle;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SubjectFragment extends Fragment {
    private static final String TAG_DIALOGUE = "tag_subject_question_dialogue";

    private ProgramComponentAdapter<Question> mAdapter;

    protected Callbacks mCallbacks;

    protected LandingPage mLandingPage;

    protected ViewHolder mViews;

    protected OnClickListener mOnAddSubjectQuestionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toggle question = new Toggle();
            long id = mCallbacks.addQuestion(question);
            question.name = getString(R.string.default_name_new_question, id);
            mLandingPage.questions.add(id);
            mCallbacks.storeLandingPage(mLandingPage);
            mCallbacks.putQuestion(id, question);
            showEditQuestionDialogue(id, true);
        }
    };

    protected OnItemClickListener mOnQuestionItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showEditQuestionDialogue(id, false);
        }
    };

    public TextWatcher mLandingPageTextListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mLandingPage.title = mViews.mTitle.getText().toString();
            mLandingPage.introduction = mViews.mIntroduction.getText().toString();
            mCallbacks.storeLandingPage(mLandingPage);
        }
    };

    private LandingPageDataChangeListener mLandingPageDataChangeListener = new LandingPageDataChangeListener() {
        @Override
        public void onLandingPageDataChange() {
            mLandingPage = mCallbacks.getLandingPage();
            mViews.setViewValues(mLandingPage);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_designer_subject, container, false);
        ListView list = (ListView)view.findViewById(R.id.list);
        list.addHeaderView(inflater.inflate(R.layout.header_subject_details, list, false));
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLandingPage = mCallbacks.getLandingPage();
        mCallbacks.addLandingPageDataChangeListener(mLandingPageDataChangeListener);

        mViews = new ViewHolder(view);

        mAdapter = mCallbacks.getQuestionAdapter();

        mViews.initViews(mAdapter);
        mViews.setViewValues(mLandingPage);
    }

    protected void showEditQuestionDialogue(final long id, final boolean isNew) {
        // Clear any existing dialog.
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show authentication dialog.
        DialogFragment dialog = EditQuestionDialogueFragment.newDialogue(id, isNew);
        dialog.show(ft, TAG_DIALOGUE);
    }

    public static interface Callbacks {
        void addLandingPageDataChangeListener(LandingPageDataChangeListener listener);

        void addQuestionDataChangeListener(QuestionDataChangeListener listener);

        long addQuestion(Question question);

        Question getQuestion(long id);

        void deleteQuestion(long id);

        LandingPage getLandingPage();

        void putQuestion(long id, Question question);

        void removeLandingPageDataChangeListener(LandingPageDataChangeListener listener);

        void removeQuestionDataChangeListener(QuestionDataChangeListener listener);

        void storeLandingPage(LandingPage landingPage);

        ProgramComponentAdapter<Question> getQuestionAdapter();
    }

    private class ViewHolder {
        private View mAdd;

        private EditText mIntroduction;

        private DragSortListView mList;

        private EditText mTitle;

        public ViewHolder(View view) {
            mTitle = (EditText)view.findViewById(R.id.title);
            mIntroduction = (EditText)view.findViewById(R.id.description);
            mList = (DragSortListView)view.findViewById(R.id.list);
            mAdd = view.findViewById(R.id.add);
        }

        public void initViews(ListAdapter adapter) {
            mList.setAdapter(adapter);
            mList.setOnItemClickListener(mOnQuestionItemClickListener);
            mAdd.setOnClickListener(mOnAddSubjectQuestionClickListener);
        }

        public void setViewValues(LandingPage landingPage) {
            mTitle.removeTextChangedListener(mLandingPageTextListener);
            mIntroduction.removeTextChangedListener(mLandingPageTextListener);
            if (!TextUtils.equals(mTitle.getText(), landingPage.title)) {
                mTitle.setText(landingPage.title);
            }
            if (!TextUtils.equals(mIntroduction.getText(), landingPage.introduction)) {
                mIntroduction.setText(landingPage.introduction);
            }
            mTitle.addTextChangedListener(mLandingPageTextListener);
            mIntroduction.addTextChangedListener(mLandingPageTextListener);
        }
    }
}
