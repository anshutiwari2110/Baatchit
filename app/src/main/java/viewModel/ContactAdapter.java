package viewModel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anshutiwari.baatchit.MessageActivity;
import com.anshutiwari.baatchit.R;
import com.anshutiwari.baatchit.VideoConfig.CallingActivity;
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

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

    Context context;
    List<User> userList;
    private boolean isChat;
    String lastMessage;
    String lastMsgDate;

    public ContactAdapter(Context context, List<User> userList, boolean isChat) {
        this.context = context;
        this.userList = userList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactHolder(LayoutInflater.from(context).inflate(R.layout.cell_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {

        User user = userList.get(position);
        holder.mTvContactUser.setText(user.getUsername());
        if (!user.getImageURL().equals("default")) {
            Glide.with(context).load(user.getImageURL()).into(holder.mCivUserDp);
        } else {
            holder.mCivUserDp.setImageResource(R.mipmap.ic_launcher);
        }

        if (isChat) {
            lastMsg(user.getId(), holder.mTvLastMsg, holder.mTvUnseenMsg,holder.mTvLastMsgDate);
        } else {
            holder.mTvLastMsgDate.setVisibility(View.GONE);
            holder.mTvLastMsg.setText(user.getAbout());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("visit_user_id", user.getId());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent callingIntent = new Intent(context, CallingActivity.class);
                callingIntent.putExtra("visit_user_id",user.getId());
                context.startActivity(callingIntent);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCivUserDp;
        private TextView mTvContactUser;
        private TextView mTvLastMsg;
        private TextView mTvUnseenMsg;
        private TextView mTvLastMsgDate;

        public ContactHolder(@NonNull View itemView) {
            super(itemView);

            mCivUserDp = itemView.findViewById(R.id.civ_contact_image);
            mTvContactUser = itemView.findViewById(R.id.tv_contact_user);
            mTvLastMsg = itemView.findViewById(R.id.tv_last_msg);
            mTvUnseenMsg = itemView.findViewById(R.id.tv_unseen_msg);
            mTvLastMsgDate = itemView.findViewById(R.id.tv_last_msg_time);

        }
    }

    private void lastMsg(String userId, TextView mTvLastMsg, TextView mTvUnseenMsg, TextView mTvLastMsgDate) {
        lastMessage = "default";
        lastMsgDate = "dd/mm/yyyy";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid()) ||
                            chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        lastMessage = chat.getMessage();
                        lastMsgDate = chat.getMsgDate();

                    }
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) && !chat.getisSeen()) {
                        unread++;
                    }
                }

                if (unread == 0) {
                    mTvUnseenMsg.setVisibility(View.GONE);
                } else {
                    mTvUnseenMsg.setVisibility(View.VISIBLE);
                    mTvUnseenMsg.setText(" "+String.valueOf(unread)+" ");
                }

                switch (lastMessage) {
                    case "default":
                        mTvLastMsg.setText("Start the conversation");
                        break;
                    default:
                        mTvLastMsg.setText(lastMessage);
                        mTvLastMsgDate.setText(lastMsgDate);
                        break;
                }
                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
