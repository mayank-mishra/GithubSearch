package com.git.search.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.git.search.Objects.RepoObject;
import com.git.search.R;
import com.git.search.util.AppUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ravi on 16/11/17.
 */

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<RepoObject> repoList;
    private List<RepoObject> repoListFiltered;
    private RepoAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone,text_rating_count,text_watchers_count,text_share_count;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            thumbnail = view.findViewById(R.id.thumbnail);
            text_share_count = view.findViewById(R.id.text_share_count);
            text_watchers_count = view.findViewById(R.id.text_watchers_count);
            text_rating_count = view.findViewById(R.id.text_rating_count);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected repo in callback
                    listener.onrepoSelected(repoListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public RepoAdapter(Context context, List<RepoObject> repoList, RepoAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.repoList = repoList;
        this.repoListFiltered = repoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final RepoObject repo = repoListFiltered.get(position);
        holder.name.setText(AppUtils.NullChecker(repo.name));
        holder.phone.setText(AppUtils.NullChecker(repo.full_name));
        holder.text_watchers_count.setText(AppUtils.NullChecker(repo.watchers_count));
        holder.text_rating_count.setText(AppUtils.NullChecker(repo.stargazers_count));
        holder.text_share_count.setText(AppUtils.NullChecker(repo.forks_count));

        Glide.with(context)
                .load(repo.owner.avatar_url)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return repoListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    repoListFiltered = repoList;
                } else {
                    List<RepoObject> filteredList = new ArrayList<>();
                    for (RepoObject row : repoList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name.toLowerCase().contains(charString.toLowerCase()) || row.full_name.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    repoListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = repoListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                repoListFiltered = (ArrayList<RepoObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface RepoAdapterListener {
        void onrepoSelected(RepoObject repo);
    }
}
