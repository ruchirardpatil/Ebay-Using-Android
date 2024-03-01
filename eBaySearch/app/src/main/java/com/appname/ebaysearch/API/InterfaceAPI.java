package com.appname.ebaysearch.API;

import org.json.JSONArray;
import org.json.JSONObject;

public interface InterfaceAPI {
     void onSuccess(JSONObject response);
     void onSuccess(JSONArray response);
     void onError(Exception error);
}
