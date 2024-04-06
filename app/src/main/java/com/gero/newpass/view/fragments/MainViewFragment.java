package com.gero.newpass.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gero.newpass.database.DatabaseHelper;
import com.gero.newpass.databinding.FragmentMainViewBinding;
import com.gero.newpass.model.UserData;
import com.gero.newpass.view.activities.MainViewActivity;
import com.gero.newpass.view.adapters.CustomAdapter;

import java.util.ArrayList;
import java.util.Objects;


public class MainViewFragment extends Fragment {

    private FragmentMainViewBinding binding;
    private TextView noData, count;
    private DatabaseHelper myDB;
    private ArrayList<UserData> userDataList;
    private ImageView empty_imageview;
    private RecyclerView recyclerView;
    private ImageButton buttonGenerate, buttonAdd, buttonSettings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        populateUI();


        //Navigating to generate/ add password and settings fragments using the method inherited from the base activity
        Activity activity = this.getActivity();
        if (activity instanceof MainViewActivity) {
            buttonGenerate.setOnClickListener(v -> ((MainViewActivity) activity).openFragment(new GeneratePasswordFragment()));
            buttonAdd.setOnClickListener(v -> ((MainViewActivity) activity).openFragment(new AddPasswordFragment()));
            buttonSettings.setOnClickListener(v -> ((MainViewActivity) activity).openFragment(new SettingsFragment()));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding object to avoid memory leaks
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            String result = bundle.getString("resultKey");
            // Updating UI after update
            if (Objects.equals(result, "1")) {
                populateUI();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void populateUI() {
        userDataList = new ArrayList<>();
        myDB = new DatabaseHelper(requireActivity());

        storeDataInArrays();

        CustomAdapter customAdapter = new CustomAdapter(this.getActivity(), this.getContext(), userDataList);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        count.setText("[" + customAdapter.getItemCount() + "]");
    }

    private void initViews() {
        recyclerView = binding.recyclerView;
        buttonGenerate = binding.buttonGenerate;
        buttonAdd = binding.buttonAdd;
        buttonSettings = binding.buttonSettings;
        count = binding.textViewCount;
        empty_imageview = binding.emptyImageview;
        noData = binding.noData;
    }

    void storeDataInArrays() {

        Cursor cursor = myDB.readAllData();

        if (cursor.getCount() == 0) {
            empty_imageview.setVisibility((View.VISIBLE));
            noData.setVisibility((View.VISIBLE));
        } else {

            while (cursor.moveToNext()) {
                UserData userData = new UserData(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                userDataList.add(userData);
            }

            empty_imageview.setVisibility((View.INVISIBLE));
            noData.setVisibility((View.INVISIBLE));
        }
    }

}