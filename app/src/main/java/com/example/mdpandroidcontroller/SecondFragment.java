package com.example.mdpandroidcontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mdpandroidcontroller.databinding.FragmentSecondBinding;
import com.example.mdpandroidcontroller.MainActivity;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private static Context context;
    //private static MapDrawer map;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //new im just trying...
        //map = new MapDrawer(context);
        //map = findViewById(R.id.gridView);
        // end of new stuff...

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });





    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}