package com.krrtvl.court;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.krrtvl.court.domain.Visits;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class VisitsAdapter extends PagedListAdapter<Visits, VisitsAdapter.VisitsHolder> {

    private Date currentDateMinus1;
    private OnItemClickListener mListener;

    protected VisitsAdapter() {
        super(DIFF_CALLBACK);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        currentDateMinus1 = c.getTime();
    }

    @Override
    public VisitsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visits_row_item, parent, false);
        final VisitsHolder holder = new VisitsHolder(view, currentDateMinus1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date holderDate = null;
                try {
                    holderDate = new SimpleDateFormat("dd.MM.yyyy").parse(holder.date.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (mListener != null && holderDate.after(currentDateMinus1)) {
                    mListener.onItemClicked((Long)holder.itemView.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VisitsHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class VisitsHolder extends RecyclerView.ViewHolder {

        Date currentDateMinus1;
        TextView regular, solid, date, btime, etime, name;

        public VisitsHolder(View view, Date curDateMinus1) {
            super(view);
            currentDateMinus1 = curDateMinus1;
            regular = view.findViewById(R.id.circle_regular);
            solid = view.findViewById(R.id.circle_solid);
            date = view.findViewById(R.id.vr_date);
            btime = view.findViewById(R.id.vr_btime);
            etime = view.findViewById(R.id.vr_etime);
            name = view.findViewById(R.id.vr_name);
        }

        public void bind(Visits visits) {
            itemView.setClickable(true);
            Calendar c = Calendar.getInstance();
            c.setTime(visits.getDate());
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                regular.setVisibility(View.INVISIBLE);
                solid.setVisibility(View.VISIBLE);
            } else {
                regular.setVisibility(View.VISIBLE);
                solid.setVisibility(View.INVISIBLE);
            }
            if (visits.getDate().after(currentDateMinus1)) {
                int day = c.get(Calendar.DAY_OF_MONTH);
                if (day % 2 == 0) itemView.setBackgroundColor(Color.parseColor("#F7F4F4"));
                else itemView.setBackgroundColor(Color.parseColor("#00F7F4F4"));

            } else {
                itemView.setBackgroundColor(Color.parseColor("#E9E9E9"));
                itemView.setClickable(false);
            }
            if (visits == null) {
                date.setText(R.string.wait);
                btime.setText(R.string.wait);
                etime.setText(R.string.wait);
                name.setText(R.string.wait);
            } else {
                date.setText(new SimpleDateFormat("dd.MM.yyyy").format(visits.getDate()));
                itemView.setTag(visits.getId());
                btime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(visits.getBtime()));
                etime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(visits.getEtime()));
                name.setText(visits.getName());
            }
        }

    }

    public static DiffUtil.ItemCallback<Visits> DIFF_CALLBACK = new DiffUtil.ItemCallback<Visits>() {
        @Override
        public boolean areItemsTheSame(@NonNull Visits oldVisits, @NonNull Visits newVisits) {
            return oldVisits.getId() == newVisits.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Visits oldVisits, @NonNull Visits newVisits) {
            return oldVisits.equals(newVisits);
        }
    };

    public interface OnItemClickListener {
        void onItemClicked(Long id);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}

/*
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    private Context mContext;
    private List<News> mData;

    public NewsAdapter(Context mContext, List<News> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.news_row_item, viewGroup, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder newsHolder, int i) {
        newsHolder.linearLayout.setTag(mData.get(i).getId());
        newsHolder.title.setText(mData.get(i).getTitle());
        newsHolder.description.setText(mData.get(i).getDescription());
        newsHolder.date.setText(new SimpleDateFormat("MM.dd.yyyy").format(mData.get(i).getDate()));
        newsHolder.linearLayout.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewsActivity.class);
                intent.putExtra("id", v.getTag().toString());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class NewsHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView title, description, date;
        public NewsHolder(View view){
            super(view);
            linearLayout =  view.findViewById(R.id.nr_layout);
            title = view.findViewById(R.id.nr_title);
            description = view.findViewById(R.id.nr_description);
            date = view.findViewById(R.id.nr_date);
        }
    }

    public void addData(List<News> data) {
        mData.addAll(data);
    }
}
*/