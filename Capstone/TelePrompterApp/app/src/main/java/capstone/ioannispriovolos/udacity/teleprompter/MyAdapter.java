package capstone.ioannispriovolos.udacity.teleprompter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

// https://www.androidhive.info/2016/01/android-working-with-recycler-view/
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.viewHolder> {

    private ArrayList<Text> texts;
    private OnClickListener listener;

    public MyAdapter(ArrayList<Text> mTexts, OnClickListener mListener) {

        listener = mListener;
        texts = mTexts;
    }

    public interface OnClickListener{

        void onClickListener(int position);
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_script;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem,parent,shouldAttachToParentImmediately);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {

        holder.bind(texts.get(position).title,texts.get(position).desc);
        holder.itemView.setContentDescription(texts.get(position).title);
    }

    @Override
    public int getItemCount() {

        if(texts == null)
            return 0;
        return texts.size();
    }

    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.text_title)
        TextView title;
        @BindView(R.id.text_desc)
        TextView desc;

        public viewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(String mTitle,String mDesc){
            title.setText(mTitle.substring(1));
            desc.setText(mDesc);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onClickListener(position);
        }
    }
}
