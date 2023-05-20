package com.project.pocketbookapp.Filters;

import android.widget.Filter;

import com.project.pocketbookapp.Adapter.AdapterPdfUser;
import com.project.pocketbookapp.Model.ModelPdf;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {

    ArrayList<ModelPdf> filterList;
    AdapterPdfUser adapterPdfUser;

    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //value to be searched should be null or empty
        if(charSequence!=null||charSequence.length()>0){
            charSequence=charSequence.toString().toUpperCase();
            ArrayList<ModelPdf>filteredModels=new ArrayList<>();

            for(int i=0;i<filterList.size();i++){
                //validate
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence)){
                    //search matches add to list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else{
            //make original list
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        //apply filter changes
        adapterPdfUser.pdfArrayList = (ArrayList<ModelPdf>) filterResults.values;

        adapterPdfUser.notifyDataSetChanged();
    }
}
