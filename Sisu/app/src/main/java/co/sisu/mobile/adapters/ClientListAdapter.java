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

        final ClientObject clientObject = (ClientObject) getItem(position);
        ClientObject nextClientObject = null;
        if(position < mDataSource.size() - 1) {
            nextClientObject = (ClientObject) getItem(position + 1);
        }

        View rowView = mInflater.inflate(R.layout.adapter_client_list, parent, false);

        if(nextClientObject != null) {
            if(clientObject.getIs_priority().equals("1") && nextClientObject.getIs_priority().equals("0")) {
                rowView = mInflater.inflate(R.layout.adapter_client_list_other_hack, parent, false);
            }
        }

        // Get view for row item
        ImageView thumbnail = rowView.findViewById(R.id.client_list_thumbnail);

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

        if(clientObject.getHome_phone() == null) {
            phoneImage.setVisibility(View.INVISIBLE);
        }
        else {
            if(clientObject.getHome_phone().equals("")) {
                phoneImage.setVisibility(View.INVISIBLE);
            }
        }

        if(clientObject.getMobile_phone() == null) {
            textImage.setVisibility(View.INVISIBLE);
        } else {
            if(clientObject.getMobile_phone().equals("")) {
                textImage.setVisibility(View.INVISIBLE);
            }
            else {
                phoneImage.setVisibility(View.VISIBLE);
                textImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClientMessagingEvent.onTextClicked(clientObject.getMobile_phone(), clientObject);
                    }
                });
                phoneImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClientMessagingEvent.onPhoneClicked(clientObject.getMobile_phone() != null ? clientObject.getMobile_phone() : clientObject.getHome_phone(), clientObject);
                    }
                });
            }

        }

        if(clientObject.getEmail() == null) {
            emailImage.setVisibility(View.INVISIBLE);
        } else {
            if(clientObject.getEmail().equals("")) {
                emailImage.setVisibility(View.INVISIBLE);
            }
            else {
                emailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClientMessagingEvent.onEmailClicked(clientObject.getEmail(), clientObject);
                    }
                });
            }

        }
        String firstName = "";
        String lastName = "";

        if(clientObject.getFirst_name() != null) {
            firstName = clientObject.getFirst_name();
        }

        if(clientObject.getLast_name() != null) {
            lastName = clientObject.getLast_name();
        }
        titleTextView.setText(firstName + " " + lastName);
//        String splitString = clientObject.getCommission_amt().substring(0, clientObject.getGross_commission_amt().indexOf("."));//getting rid of the .0
        subtitleTextView.setText("$" + clientObject.getCommission_amt());

        return rowView;
    }
}
