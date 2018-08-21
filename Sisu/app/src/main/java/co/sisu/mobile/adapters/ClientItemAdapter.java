package co.sisu.mobile.adapters;

import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ClientMessagingEvent;
import co.sisu.mobile.models.ClientObject;

/**
 * Created by Brady Groharing on 8/20/2018.
 */

public class ClientItemAdapter extends DragItemAdapter<Pair<Long, ClientObject>, ClientItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private ClientMessagingEvent mClientMessagingEvent;

    public ClientItemAdapter(ArrayList<Pair<Long, ClientObject>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, ClientMessagingEvent clientMessagingEvent) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        mClientMessagingEvent = clientMessagingEvent;
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final ClientObject clientObject = mItemList.get(position).second;
//        holder.mText.setText(text);
        holder.itemView.setTag(mItemList.get(position));

//          Get view for row item
            ImageView thumbnail = holder.itemView.findViewById(R.id.client_list_thumbnail);
            if(clientObject.getType_id().equalsIgnoreCase("b")) {
                thumbnail.setImageResource(R.drawable.buyer_icon);
            } else {
                thumbnail.setImageResource(R.drawable.seller_icon_active);
            }
//
//            // Get title element
            TextView titleTextView = holder.itemView.findViewById(R.id.client_list_title);
//
//            // Get subtitle element
            TextView subtitleTextView = holder.itemView.findViewById(R.id.client_list_subtitle);
//
//            //Get the images
            ImageView textImage = holder.itemView.findViewById(R.id.leftButton);
            ImageView phoneImage = holder.itemView.findViewById(R.id.centerButton);
            ImageView emailImage = holder.itemView.findViewById(R.id.rightButton);
//
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

    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.client_list_title);

        }

        @Override
        public void onItemClicked(View view) {

//            mClientMessagingEvent.onItemClicked();
            Pair<Long, ClientObject> clientPair = mItemList.get((int) this.mItemId);
            final ClientObject clientObject = clientPair.second;
            mClientMessagingEvent.onItemClicked(clientObject);
//            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
//            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
