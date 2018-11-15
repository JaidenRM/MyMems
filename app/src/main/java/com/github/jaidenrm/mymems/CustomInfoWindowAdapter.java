package com.github.jaidenrm.mymems;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View window;
    private Context context;
    private HashMap<String, String> idToImage;

    static class MarkerCallback implements Callback {
        Marker marker=null;

        MarkerCallback(Marker marker) {
            this.marker=marker;
        }

        @Override
        public void onError(Exception err) {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }
    }

    public CustomInfoWindowAdapter(Context context, HashMap<String, String> idToImage) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        this.idToImage = idToImage;
    }

    private void RenderWindowText(final Marker marker, View view) {
        TextView title = view.findViewById(R.id.ciw_title);
        TextView description = view.findViewById(R.id.ciw_description);
        ImageView image = view.findViewById(R.id.ciw_image);

        String markerTitle = marker.getTitle();
        String markerSnippet = marker.getSnippet();
        String imageURL = idToImage.get(marker.getId());

        if(!markerTitle.equals("")) { title.setText(markerTitle); }
        if(!markerSnippet.equals("")) { description.setText(markerSnippet); }
        if(!imageURL.equals("")) { Picasso.get().setLoggingEnabled(true); Picasso.get().load(imageURL).resize(150, 150).centerInside().into(image, new MarkerCallback(marker)); }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        RenderWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        RenderWindowText(marker, window);
        return window;
    }
}
