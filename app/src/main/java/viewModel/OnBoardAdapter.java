package viewModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.anshutiwari.baatchit.R;


public class OnBoardAdapter extends PagerAdapter {
    Context context;
    static LayoutInflater inflater;

    public OnBoardAdapter(Context context) {
        this.context = context;
    }

    public int[] list_image = {
            R.drawable.onboard1,
            R.drawable.onboard2,
            R.drawable.onboard3
    };

    public String[] slide_title = {
            "Chat Anytime, Anywhere",
            "Make New Friends ",
            "Perfect Chat Solution"

    };

    public String[] slide_description = {
            "Passing of any information on any device instantly is made simple at its sublime",
            "Find as many friends or connection to share your own story",
            "A lag-free text chat connection between your friend is now easy and hassle free"

    };


    @Override
    public int getCount() {
        return slide_title.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (RelativeLayout) object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cell_onboard, container, false);
//        RelativeLayout layout_slide = view.findViewById(R.id.ll_slide);
        ImageView imgSlide = view.findViewById(R.id.iv_slide);
        TextView mTvSlideTitle = view.findViewById(R.id.tv_slide_title);
        TextView mTvSlideDes = view.findViewById(R.id.tv_slide_description);
        mTvSlideDes.setText(slide_description[position]);
        imgSlide.setImageResource(list_image[position]);
        mTvSlideTitle.setText(slide_title[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

}

