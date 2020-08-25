package co.sisu.mobile.models;

import org.json.JSONObject;

public class FilterObject {

    private String filterName;
    private JSONObject filters;

    public FilterObject(String filterName, JSONObject filters) {
        this.filterName = filterName;
        this.filters = filters;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public JSONObject getFilters() {
        return filters;
    }

    public void setFilters(JSONObject filters) {
        this.filters = filters;
    }
}
