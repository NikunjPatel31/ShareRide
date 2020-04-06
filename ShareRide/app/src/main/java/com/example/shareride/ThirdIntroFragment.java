package com.example.shareride;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdIntroFragment extends Fragment {

    private ImageView imageView;
    private TextView descriptionTV;
    private Button nextBtn;

    public ThirdIntroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third_intro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nextBtn = view.findViewById(R.id.next_button);
        imageView = view.findViewById(R.id.image_view);
        descriptionTV = view.findViewById(R.id.description_textview);

        imageView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.window_fade_in));
        descriptionTV.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.window_fade_in));
    }

    @Override
    public void onStart() {
        super.onStart();
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EmailVerificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();            }
        });
    }
}
