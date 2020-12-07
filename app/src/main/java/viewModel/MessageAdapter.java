package viewModel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anshutiwari.baatchit.MessageActivity;
import com.anshutiwari.baatchit.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Model.Chat;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    Context context;
    List<Chat> chatList;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            return new MessageHolder(LayoutInflater.from(context).inflate(R.layout.cell_right_chat, parent, false));
        } else {
            return new MessageHolder(LayoutInflater.from(context).inflate(R.layout.cell_left_chat, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.mTvShowMsg.setText(chat.getMessage());
        holder.mTvMsgTime.setText(chat.getMsgTime());

        //Todo for showing date of sent messages in the message activity(It is commented for the time being)

        /*String date = "default";
        for (int i =0;i<chatList.size();i++){
            Chat chatCurrent = chatList.get(i);

            if (chatCurrent.getMsgDate().equals(date)){
                holder.mTvMsgDate.setVisibility(View.GONE);

            }else{
                date = chatCurrent.getMsgDate();
                holder.mTvMsgDate.setVisibility(View.VISIBLE);
                holder.mTvMsgDate.setText(chatCurrent.getMsgDate());
            }
        }*/



        int lastMsgIndex = chatList.size() - 1;
        if (position == lastMsgIndex) {

            if (chat.getisSeen()) {
                holder.mTvSeen.setText("Seen");
            } else {
                holder.mTvSeen.setText("Delivered");
            }
        } else {
            holder.mTvSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        private TextView mTvShowMsg;
        private TextView mTvSeen;
        private TextView mTvMsgDate;
        private TextView mTvMsgTime;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            mTvShowMsg = itemView.findViewById(R.id.tv_show_message);
            mTvSeen = itemView.findViewById(R.id.tv_msg_seen);
            mTvMsgDate = itemView.findViewById(R.id.tv_msg_date);
            mTvMsgTime = itemView.findViewById(R.id.tv_msg_time);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}

