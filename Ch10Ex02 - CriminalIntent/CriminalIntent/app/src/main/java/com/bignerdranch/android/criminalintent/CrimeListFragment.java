package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mLastClickedPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            if(mLastClickedPosition < 0)
            {
                mAdapter.notifyDataSetChanged();
            }
            else{
                mAdapter.notifyItemChanged(mLastClickedPosition);
                mLastClickedPosition = -1;
            }
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageVIew;

        public CrimeHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageVIew = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy");
            Date date = mCrime.getDate();
            String dateStr = formatter.format(date);
            mDateTextView.setText(dateStr);
            mSolvedImageVIew.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
//            if(this.getItemViewType() == 1){
//                Button b = itemView.findViewById(R.id.contact_police);
//                b.setOnClickListener(e -> {
//                    Toast.makeText(getActivity(), R.string.call_police,
//                            Toast.LENGTH_SHORT).show();
//                });
//            }
        }

        @Override
        public void onClick(View v) {
            mLastClickedPosition = getAdapterPosition();
            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view;
            int layout;

//            if(viewType == 0){
//                layout = R.layout.list_item_crime;
//            }
//            else {
//                layout = R.layout.list_item_crime_police;
//            }

            layout = R.layout.list_item_crime;

            view = layoutInflater.inflate(layout, parent, false);

            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position){
            Crime crime = mCrimes.get(position);
            return crime.isRequiresPolice() ? 1 : 0;
        }
    }
}
