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
import co.sisu.mobile.models.ClientObject;

/**
 * Created by Brady Groharing on 3/15/2018.
 */

public class ClientListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ClientObject> mDataSource;

    public ClientListAdapter(Context context, List<ClientObject> items) {
        mContext = context;
        mDataSource = (ArrayList<ClientObject>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        if(clientObject.getHome_phone() == null && clientObject.getMobile_phone() == null) {
            phoneImage.setVisibility(View.INVISIBLE);
        } else {
            phoneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent callIntent = new Intent(Intent.ACTION_CALL).addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);//may not need the flag, but it may be helpful when relaunching app after call
//                    if(clientObject.getMobile_phone() != null) {
//                        callIntent.setData(Uri.parse("tel:" + clientObject.getMobile_phone()));
//                    } else {
//                        callIntent.setData(Uri.parse("tel:" + clientObject.getHome_phone()));
//                    }
//
//                    if (ContextCompat.checkSelfPermission(parentActivity,
//                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
                    
//                    ContextCompat.startActivity(callIntent);
                }
            });
        }

        if(clientObject.getMobile_phone() == null) {
            textImage.setVisibility(View.INVISIBLE);
        } else {
            textImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ContextCompat.startActivity(parentActiviy, new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
//                            + clientObject.getMobile_phone())));
                }
            });
        }

        if(clientObject.getEmail() == null) {
            emailImage.setVisibility(View.INVISIBLE);
        } else {
            emailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//
//                    emailIntent.setType("plain/text");
//                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{clientObject.getEmail()});
//                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
//                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");
//                    ContextCompat.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            });
        }
        titleTextView.setText(clientObject.getFirst_name() + " " + clientObject.getLast_name());
        String splitString = clientObject.getGross_commission_amt().substring(0, clientObject.getGross_commission_amt().indexOf("."));//getting rid of the .0
        subtitleTextView.setText("$" + splitString);


        return rowView;
    }
}
