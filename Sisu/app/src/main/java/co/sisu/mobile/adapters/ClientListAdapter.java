package co.sisu.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ClientMessagingEvent;
import co.sisu.mobile.models.ClientObject;

/**
 * Created by Brady Groharing on 3/15/2018.
 */

public class ClientListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ClientObject> mDataSource;
    private ClientMessagingEvent mClientMessagingEvent;

    public ClientListAdapter(Context context, List<ClientObject> items, ClientMessagingEvent clientMessagingEvent) {
        mContext = context;
        mDataSource = (ArrayList<ClientObject>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mClientMessagingEvent = clientMessagingEvent;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // Get view for row item
        final View rowView = mInflater.inflate(R.layout.adapter_client_list, parent, false);
        ImageView thumbnail = rowView.findViewById(R.id.client_list_thumbnail);

        final ClientObject clientObject = (ClientObject) getItem(position);
        if(clientObject.getType_id().equalsIgnoreCase("b")) {
            thumbnail.setImageResource(R.drawable.buyer_icon);
        } else {
            thumbnail.setImageResource(R.drawable.seller_icon_active);
        }

        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.client_list_title);

        // Get subtitle element
        TextView subtitleTextView = rowView.findViewById(R.id.client_list_subtitle);

        //Get the images
        ImageView textImage = rowView.findViewById(R.id.leftButton);
        ImageView phoneImage = rowView.findViewById(R.id.centerButton);
        ImageView emailImage = rowView.findViewById(R.id.rightButton);

        if(clientObject.getHome_phone() == null && clientObject.getMobile_phone() == null || clientObject.getMobile_phone().equals("") && clientObject.getHome_phone().equals("")) {
            phoneImage.setVisibility(View.INVISIBLE);
        } else {
            phoneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClientMessagingEvent.onPhoneClicked(clientObject.getMobile_phone() != null ? clientObject.getMobile_phone() : clientObject.getHome_phone());
                }
            });
        }

        if(clientObject.getMobile_phone() == null || clientObject.getMobile_phone().equals("")) {
            textImage.setVisibility(View.INVISIBLE);
        } else {
            textImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClientMessagingEvent.onTextClicked(clientObject.getMobile_phone());
                }
            });
        }

        if(clientObject.getEmail() == null || clientObject.getEmail().equals("")) {
            emailImage.setVisibility(View.INVISIBLE);
        } else {
            emailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClientMessagingEvent.onEmailClicked(clientObject.getEmail());
                }
            });
        }
        titleTextView.setText(clientObject.getFirst_name() + " " + clientObject.getLast_name());
//        String splitString = clientObject.getCommission_amt().substring(0, clientObject.getGross_commission_amt().indexOf("."));//getting rid of the .0
        subtitleTextView.setText("$" + clientObject.getCommission_amt());

        return rowView;
    }
}
