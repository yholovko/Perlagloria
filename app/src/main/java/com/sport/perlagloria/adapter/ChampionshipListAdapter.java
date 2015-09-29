package com.sport.perlagloria.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sport.perlagloria.R;
import com.sport.perlagloria.model.Customer;

import java.util.Collections;
import java.util.List;

public class ChampionshipListAdapter extends RecyclerView.Adapter<ChampionshipListAdapter.MyViewHolder> {
    private List<Customer> data = Collections.emptyList();
    private int lastSelectedIndex;
    private OnCheckboxCheckedListener onCheckboxCheckedListener;

    public ChampionshipListAdapter(List<Customer> data, OnCheckboxCheckedListener onCheckboxCheckedListener) {
        super();
        this.data = data;
        this.onCheckboxCheckedListener = onCheckboxCheckedListener;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_championship_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Customer current = data.get(position);

        holder.champItemValue.setText(current.getName());
        holder.champItemCheckBox.setChecked(current.isSelected());
        holder.champItemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < data.size(); i++) {
                    if (i != position) {            //&& data.get(i).isSelected()
                        data.get(i).setIsSelected(false);
                    } else {
                        data.get(position).setIsSelected(true); //1 item always selected
                    }
                    notifyItemChanged(i);
                }

                lastSelectedIndex = position;
                onCheckboxCheckedListener.onCheckboxChecked(getItem(position));  //send message back to fragment (to change selected championship title)
            }
        });

        holder.dividerView.setVisibility((data.size() - 1 == position) ? View.GONE : View.VISIBLE);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public Customer getItem(int position) {
        return data.get(position);
    }

    public Customer getSelectedItem() {
        return getItem(lastSelectedIndex);
    }

    public interface OnCheckboxCheckedListener {
        void onCheckboxChecked(Customer customer);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = MyViewHolder.class.getSimpleName();

        TextView champItemValue;
        CheckBox champItemCheckBox;
        View dividerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            champItemValue = (TextView) itemView.findViewById(R.id.champItemValue);
            champItemCheckBox = (CheckBox) itemView.findViewById(R.id.champItemCheckBox);
            dividerView = itemView.findViewById(R.id.dividerView);
        }
    }
}
