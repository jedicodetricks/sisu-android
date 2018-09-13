package co.sisu.mobile.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ClientItemAdapter extends DragItemAdapter<Pair<Long, Object>, ClientItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private ClientMessagingEvent mClientMessagingEvent;

    public ClientItemAdapter(ArrayList<Pair<Long, Object>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, ClientMessagingEvent clientMessagingEvent) {
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
        if(mItemList.get(position).second.equals("Pipeline")) {
            holder.itemView.setTag(mItemList.get(position));
            //This is a header
            ImageView thumbnail = holder.itemView.findViewById(R.id.client_list_thumbnail);
            ImageView editButton = holder.itemView.findViewById(R.id.editButton);
            TextView titleTextView = holder.itemView.findViewById(R.id.client_list_title);
            TextView subtitleTextView = holder.itemView.findViewById(R.id.client_list_subtitle);
            ImageView textImage = holder.itemView.findViewById(R.id.leftButton);
            ImageView phoneImage = holder.itemView.findViewById(R.id.centerButton);
            ImageView emailImage = holder.itemView.findViewById(R.id.rightButton);
            ConstraintLayout buttonLayout = holder.itemView.findViewById(R.id.buttonLayout);
            RelativeLayout textLayout = holder.itemView.findViewById(R.id.client_list_item_text_layout);

            if(editButton != null) {
                editButton.setVisibility(View.GONE);
            }
            thumbnail.setVisibility(View.GONE);
            textImage.setVisibility(View.GONE);
            phoneImage.setVisibility(View.GONE);
            emailImage.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
            subtitleTextView.setVisibility(View.GONE);
//            textLayout.setGravity(10);
//            textLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            titleTextView.setGravity(Gravity.CENTER);
            //TODO: This will need to use the colorSchemeManager
            titleTextView.setTextColor(holder.itemView.getResources().getColor(R.color.colorCorporateOrange));

            titleTextView.setText(mItemList.get(position).second.toString());
        }
        else {
            final ClientObject clientObject = (ClientObject) mItemList.get(position).second;
            if(clientObject.getFirst_name().equalsIgnoreCase("MICHAELA")) {
                Log.e("Michaela", "TRUE");
            }
            holder.itemView.setTag(mItemList.get(position));

//          Get view for row item
            ImageView thumbnail = holder.itemView.findViewById(R.id.client_list_thumbnail);
            if(clientObject.getType_id().equalsIgnoreCase("b")) {
                thumbnail.setImageResource(R.drawable.buyer_icon);
            }
            else {
                thumbnail.setImageResource(R.drawable.seller_icon_active);
            }
//
//          // Get title element
            TextView titleTextView = holder.itemView.findViewById(R.id.client_list_title);
//
//          // Get subtitle element
            TextView subtitleTextView = holder.itemView.findViewById(R.id.client_list_subtitle);
//
//          //Get the images
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
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = itemView.findViewById(R.id.client_list_title);

        }

        @Override
        public void onItemClicked(View view) {

            try {
                Pair<Long, Object> clientPair = mItemList.get((int) this.mItemId);
                final ClientObject clientObject = (ClientObject) clientPair.second;
                mClientMessagingEvent.onItemClicked(clientObject);
            }catch (ClassCastException cce) {}

        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }
    }
}
