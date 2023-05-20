package com.project.pocketbookapp.Filters;

import android.widget.Filter;

import com.project.pocketbookapp.Adapter.CategoryAdapter;
import com.project.pocketbookapp.Model.CategoryModel;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    //arraylist in which we want to search
    ArrayList<CategoryModel> filterList;
    //adapter in which filter need to be implemented
    CategoryAdapter adapterCategory;

    //constructor
    public FilterCategory(ArrayList<CategoryModel> filterList, CategoryAdapter adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();
        //value should not be null and empty
        if(constraint!=null && constraint.length()>0){
            //to avoid sensitivity change to uppercase or lowercase
            constraint=constraint.toString().toUpperCase();
            ArrayList<CategoryModel> filteredModels=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                if(filterList.get(i).getCategory().toUpperCase().contains(constraint)) {
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
        adapterCategory.categoryArrayList=(ArrayList<CategoryModel>)filterResults.values;

        //notify changes
        adapterCategory.notifyDataSetChanged();
    }
}
