package com.project.pocketbookapp.Filters;

import android.widget.Filter;

import com.project.pocketbookapp.Adapter.AdapterPdfAdmin;
import com.project.pocketbookapp.Adapter.CategoryAdapter;
import com.project.pocketbookapp.Model.CategoryModel;
import com.project.pocketbookapp.Model.ModelPdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {
    //arraylist in which we want to search
    ArrayList<ModelPdf> filterList;
    //adapter in which filter need to be implemented
    AdapterPdfAdmin adapterPdfAdmin;

    //constructor
    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();
        //value should not be null and empty
        if(constraint!=null && constraint.length()>0){
            //to avoid sensitivity change to uppercase or lowercase
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filteredModels=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint)) {
                    //add to filtered list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else{
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults filterResults) {
        adapterPdfAdmin.pdfArrayList=(ArrayList<ModelPdf>)filterResults.values;

        //notify changes
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
