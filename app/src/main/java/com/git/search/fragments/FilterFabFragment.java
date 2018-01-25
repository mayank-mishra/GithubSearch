package com.git.search.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.git.search.Classes.FilterModel;
import com.git.search.R;

import java.util.Calendar;


/**
 * Created by krupenghetiya on 23/06/17.
 */

public class FilterFabFragment extends AAH_FabulousFragment {
    Button btn_close;
    private FilterModel filterModel;
    private int REQUEST_DATE_PICKER =0;

    public static FilterFabFragment newInstance() {
        FilterFabFragment f = new FilterFabFragment();
        return f;
    }

    Button btn_filter_star,btn_filter_foks,btn_filter_watcher,btn_filter_public,
            btn_filter_private,btn_filter_todate,btn_filter_fromdate,btn_filter_asc,btn_filter_dsc;

    @Override

    public void setupDialog(Dialog dialog, int style) {
        filterModel=new FilterModel();
        View contentView = View.inflate(getContext(), R.layout.filter_sample_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);

         btn_filter_star = (Button) contentView.findViewById(R.id.btn_filter_star);
         btn_filter_foks = (Button) contentView.findViewById(R.id.btn_filter_foks);
         btn_filter_watcher = (Button) contentView.findViewById(R.id.btn_filter_watcher);

         btn_filter_public = (Button) contentView.findViewById(R.id.btn_filter_public);
         btn_filter_private = (Button) contentView.findViewById(R.id.btn_filter_private);

         btn_filter_fromdate = (Button) contentView.findViewById(R.id.btn_filter_fromdate);
         btn_filter_todate = (Button) contentView.findViewById(R.id.btn_filter_todate);

        btn_filter_asc = (Button) contentView.findViewById(R.id.btn_filter_asc);
        btn_filter_dsc = (Button) contentView.findViewById(R.id.btn_filter_dsc);


        btn_filter_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSortByClick(R.id.btn_filter_star);
            }
        });

        btn_filter_foks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSortByClick(R.id.btn_filter_foks);
            }
        });

        btn_filter_watcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSortByClick(R.id.btn_filter_watcher);
            }
        });

        btn_filter_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInByClick(R.id.btn_filter_public);
            }
        });

        btn_filter_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInByClick(R.id.btn_filter_private);
            }
        });


        btn_filter_asc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderByClick(R.id.btn_filter_asc);
            }
        });

        btn_filter_dsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderByClick(R.id.btn_filter_dsc);
            }
        });


        btn_filter_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REQUEST_DATE_PICKER=1;
                showDatePicker();
            }
        });

        btn_filter_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REQUEST_DATE_PICKER=2;
                showDatePicker();
            }
        });


        contentView.findViewById(R.id.imgbtn_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btn_filter_fromdate.getText().toString().contains("YYYY-MM-DD") && !btn_filter_todate.getText().toString().contains("YYYY-MM-DD")) {
                    filterModel.fromDate = btn_filter_fromdate.getText().toString();
                    filterModel.toDate = btn_filter_todate.getText().toString();
                }
                closeFilter(filterModel);
            }
        });

        contentView.findViewById(R.id.imgbtn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefresh();
            }
        });

        contentView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter(new FilterModel());
            }
        });

        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
//        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

public void onRefresh(){
        filterModel.sortBy="";
        filterModel.orderBy="";
        filterModel.toDate="";
        filterModel.fromDate="";
        filterModel.is="";
    btn_filter_star.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
    btn_filter_foks.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
    btn_filter_watcher.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
    btn_filter_private.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
    btn_filter_public.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
    btn_filter_asc.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
    btn_filter_dsc.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
}

    public void onSortByClick(int id){
        btn_filter_star.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
        btn_filter_foks.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
        btn_filter_watcher.setBackground(getResources().getDrawable(R.drawable.chip_unselected));

        switch(id){
            case R.id.btn_filter_star:
                btn_filter_star.setBackground(getResources().getDrawable(R.drawable.chip_selected));
                filterModel.sortBy="stars";
                break;

            case R.id.btn_filter_foks:
                btn_filter_foks.setBackground(getResources().getDrawable(R.drawable.chip_selected));
                filterModel.sortBy="forks";
                break;

            case R.id.btn_filter_watcher:
                btn_filter_watcher.setBackground(getResources().getDrawable(R.drawable.chip_selected));
                filterModel.sortBy="watchers";
                break;
                default:
                    filterModel.sortBy="";
                    break;
        }
    }

    public void onOrderByClick(int id){

        btn_filter_asc.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
        btn_filter_dsc.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
        switch(id){
            case R.id.btn_filter_asc:
                btn_filter_asc.setBackground(getResources().getDrawable(R.drawable.chip_selected));
                filterModel.orderBy="asc";
                break;

            case R.id.btn_filter_dsc:
                btn_filter_dsc.setBackground(getResources().getDrawable(R.drawable.chip_selected));
                filterModel.orderBy="desc";
                break;
        }
    }

    public void onInByClick(int id){

        btn_filter_public.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
        btn_filter_private.setBackground(getResources().getDrawable(R.drawable.chip_unselected));
        switch(id){
            case R.id.btn_filter_public:
                btn_filter_public.setBackground(getResources().getDrawable(R.drawable.chip_selected));
                filterModel.is="public";
                break;

            case R.id.btn_filter_private:
                btn_filter_private.setBackground(getResources().getDrawable(R.drawable.chip_selected));
                filterModel.is="private";
                break;
        }
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getActivity().getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String strMonth="",strDay="";
            if(String.valueOf(monthOfYear+1).length()==1){
                strMonth="0"+String.valueOf(monthOfYear+1);
            }else{
                strMonth=String.valueOf(monthOfYear+1);
            }

            if(String.valueOf(dayOfMonth).length()==1){
                strDay="0"+String.valueOf(dayOfMonth);
            }else{
                strDay=String.valueOf(dayOfMonth);
            }
            String dateStr=String.valueOf(year) + "-" + strMonth
                    + "-" +strDay ;
            if(REQUEST_DATE_PICKER==1)
                btn_filter_fromdate.setText(dateStr);
            else
                btn_filter_todate.setText(dateStr);
        }
    };
}
