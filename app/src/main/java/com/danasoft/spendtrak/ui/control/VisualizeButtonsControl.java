package com.danasoft.spendtrak.ui.control;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.danasoft.spendtrak.R;
import com.danasoft.spendtrak.listener.OnControlInteractionListener;

import org.jetbrains.annotations.NotNull;

public class VisualizeButtonsControl extends Fragment implements View.OnClickListener {

    private OnControlInteractionListener mListener;
    private Button btn_total, btn_merchant, btn_todo;

    public VisualizeButtonsControl() {}

    public static VisualizeButtonsControl newInstance() {
        VisualizeButtonsControl fragment = new VisualizeButtonsControl();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visualize_buttons_control, container, false);

        btn_total = v.findViewById(R.id.btn_total);
        btn_merchant = v.findViewById(R.id.btn_merchant);
        btn_todo = v.findViewById(R.id.btn_search);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_total.setOnClickListener(this);
        btn_merchant.setOnClickListener(this);
        btn_todo.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_total:
                mListener.onControlInteraction(ControlFactory.VTOTAL,null);
                break;
            case R.id.btn_merchant:
                mListener.onControlInteraction(ControlFactory.VMERCHANT,null);
                break;
            case R.id.btn_search:
                mListener.onControlInteraction(ControlFactory.VSEARCH,null);
                break;
        }
    }

    public void setListener(OnControlInteractionListener listener) {
        mListener = listener;
    }
}