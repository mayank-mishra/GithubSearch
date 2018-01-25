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
 * Created by mayank on 19/01/2018.
 */

public class ContributerRepoListAdapter extends RecyclerView.Adapter<ContributerRepoListAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<RepoObject> repoList;
    private List<RepoObject> repoListFiltered;
    private RepoAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, fullName;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            fullName = view.findViewById(R.id.phone);
            thumbnail = view.findViewById(R.id.thumbnail);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected repo in callback
                    listener.onrepoSelected(repoListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public ContributerRepoListAdapter(Context context, List<RepoObject> repoList, ContributerRepoListAdapter.RepoAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.repoList = repoList;
        this.repoListFiltered = repoList;
    }

    @Override
    public ContributerRepoListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ContributerRepoListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContributerRepoListAdapter.MyViewHolder holder, final int position) {
        final RepoObject repo = repoListFiltered.get(position);
        holder.name.setText(AppUtils.NullChecker(repo.name));
        holder.fullName.setText(AppUtils.NullChecker(repo.full_name));

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
