// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.json.JSONObject;

/** Class contains the name, state, and fips of a county. */
public class County {

  // FCC api url for county information and file locations.
  protected static final int COUNTY_COUNT = 3142;
  protected static final String FCC_BASE_URL = "https://geo.fcc.gov/api/census/area?lat=";
  protected static final String FCC_END_URL = "&format=json";
  protected static final String PERCENTILE_LOCATION_UPDATED =
      "WEB-INF/classes/county_percentile_updated.csv";
  protected static final String PERCENTILE_LOCATION_BACKUP =
      "WEB-INF/classes/county_percentile.csv";
  protected static final String POPULATION_LOCATION = "WEB-INF/classes/county_population.csv";

  // Class properties of a county.
  protected String countyName;
  protected String stateName;
  protected String countyFips;

  /** Class contains the name, state, and fips of a county. */
  public County(String countyName, String stateName, String countyFips) {
    this.countyName = countyName;
    this.stateName = stateName;
    this.countyFips = countyFips;
  }

  public String getCountyName() {
    return countyName;
  }

  public String getStateName() {
    return stateName;
  }

  public String getCountyFips() {
    return countyFips;
  }

  /** Returns the county based on the coordinates of a store, or empty county if error. */
  public static County getCounty(Store store) {
    try {

      // Read response of call to FCC API given lat and lng.
      URL fccUrl =
          new URL(
              FCC_BASE_URL + store.getLatitude() + "&lon=" + store.getLongitude() + FCC_END_URL);
      BufferedReader reader = new BufferedReader(new InputStreamReader(fccUrl.openStream()));

      // Store response in json, by reading each line.
      StringBuilder json = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        json.append(line);
      }
      reader.close();

      // Convert json to json object with just the first result.
      JSONObject result = new JSONObject(new String(json)).getJSONArray("results").getJSONObject(0);

      // Return county using strings from the results.
      return new County(
          result.getString("county_name"),
          result.getString("state_name"),
          result.getString("county_fips"));
    } catch (Exception e) {

      // If error, log error and return empty county object
      e.printStackTrace();
      return new County("", "", "");
    }
  }

  /** Get a county score based on the county percentile csv file. */
  public double getCountyScore() {
    try {

      // See if fips in the updated csv file, if so, return the score.
      CSVReader reader = new CSVReader(new FileReader(PERCENTILE_LOCATION_UPDATED));
      String[] nextLine = reader.readNext();
      while ((nextLine = reader.readNext()) != null) {
        if (Integer.parseInt(countyFips) == Integer.parseInt(nextLine[0])) {
          return Double.parseDouble(nextLine[1]) * 10;
        }
      }

      // See if fips in the backup csv file, if so, return the score.
      reader = new CSVReader(new FileReader(PERCENTILE_LOCATION_BACKUP));
      nextLine = reader.readNext();
      while ((nextLine = reader.readNext()) != null) {
        if (Integer.parseInt(countyFips) == Integer.parseInt(nextLine[0])) {
          System.out.println("Using backup score for " + countyName + ", " + stateName);
          return Double.parseDouble(nextLine[1]) * 10;
        }
      }

      // Otherwise, log failure and return 5.0.
      System.out.println("Unable to get the score for " + countyName + ", " + stateName);
      return 5.0;
    } catch (Exception e) {

      // If there is an exception, send error message and return 5.0.
      e.printStackTrace();
      System.out.println(
          "An error occured while getting the score for " + countyName + ", " + stateName);
      return 5.0;
    }
  }

  /** Given a county, find the population using the population csv file. */
  public long getCountyPopulationFromCsv() {
    try {

      // See if fips in the csv file, if so, return the population.
      CSVReader reader = new CSVReader(new FileReader(POPULATION_LOCATION));
      String[] nextLine = reader.readNext();
      while ((nextLine = reader.readNext()) != null) {
        if (Integer.parseInt(countyFips) == Integer.parseInt(nextLine[3])) {
          return Long.parseLong(nextLine[2]);
        }
      }

      // Otherwise, print error message and return 0.
      System.out.println("Unable to get population for " + countyName + ", " + stateName);
      return 0;
    } catch (Exception e) {

      // If there is an exception, log error, print error message, and return 0.
      e.printStackTrace();
      System.out.println(
          "An error occured while getting the population for " + countyName + ", " + stateName);
      return 0;
    }
  }
}
