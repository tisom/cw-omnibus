/***
  Copyright (c) 2012-2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.mapsv2.markers;

import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AbstractMapActivity implements
    OnNavigationListener {
  private static final String STATE_NAV="nav";
  private static final int[] MAP_TYPE_NAMES= { R.string.normal,
      R.string.hybrid, R.string.satellite, R.string.terrain };
  private static final int[] MAP_TYPES= { GoogleMap.MAP_TYPE_NORMAL,
      GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,
      GoogleMap.MAP_TYPE_TERRAIN };
  private GoogleMap map=null;
  private LatLngBounds.Builder builder=new LatLngBounds.Builder();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (readyToGo()) {
      setContentView(R.layout.activity_main);

      SupportMapFragment mapFrag=
          (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

      initListNav();

      map=mapFrag.getMap();

      addMarker(map, 40.748963847316034, -73.96807193756104,
                R.string.un, R.string.united_nations);
      addMarker(map, 40.76866299974387, -73.98268461227417,
                R.string.lincoln_center,
                R.string.lincoln_center_snippet);
      addMarker(map, 40.765136435316755, -73.97989511489868,
                R.string.carnegie_hall, R.string.practice_x3);
      addMarker(map, 40.70686417491799, -74.01572942733765,
                R.string.downtown_club, R.string.heisman_trophy);

      if (savedInstanceState == null) {
        findViewById(android.R.id.content).post(new Runnable() {
          @Override
          public void run() {
            CameraUpdate allTheThings=
                CameraUpdateFactory.newLatLngBounds(builder.build(), 32);

            map.moveCamera(allTheThings);
          }
        });
      }
    }
  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    map.setMapType(MAP_TYPES[itemPosition]);

    return(true);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);

    savedInstanceState.putInt(STATE_NAV,
                              getSupportActionBar().getSelectedNavigationIndex());
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_NAV));
  }

  private void initListNav() {
    ArrayList<String> items=new ArrayList<String>();
    ArrayAdapter<String> nav=null;
    ActionBar bar=getSupportActionBar();

    for (int type : MAP_TYPE_NAMES) {
      items.add(getString(type));
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      nav=
          new ArrayAdapter<String>(
                                   bar.getThemedContext(),
                                   android.R.layout.simple_spinner_item,
                                   items);
    }
    else {
      nav=
          new ArrayAdapter<String>(
                                   this,
                                   android.R.layout.simple_spinner_item,
                                   items);
    }

    nav.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    bar.setListNavigationCallbacks(nav, this);
  }

  private void addMarker(GoogleMap map, double lat, double lon,
                         int title, int snippet) {
    Marker marker=
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                                         .title(getString(title))
                                         .snippet(getString(snippet)));

    builder.include(marker.getPosition());
  }
}
