package co.sisu.mobile.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;

/**
 * Created by Jeff on 5/8/2018.
 */

public class ErrorMessageFragment extends Fragment {

    String errorMessage = "";
    ParentActivity parentActivity;
    public ErrorMessageFragment () {

    }

    public void setMessage(String message) {
        this.errorMessage = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.error_layout, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView message = view.findViewById(R.id.errorMessageText);
        if(!message.equals("")) {
            message.setText(errorMessage);
        }
    }
}
