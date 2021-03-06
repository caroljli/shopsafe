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

package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.servlets.StoresServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/** Class that tests the stores servlet. */
@RunWith(JUnit4.class)
public class StoresServletTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
  private StoresServlet storesServlet = new StoresServlet();

  @Before
  public void setUp() throws ServletException {
    helper.setUp();
    storesServlet.init();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Check if correct error message is sent for null case. */
  @Test
  public void checkNullLocation() throws IOException, ServletException {

    // Mock call for location.
    when(request.getParameter("location")).thenReturn(null);
    when(request.getParameter("latlng")).thenReturn(null);

    // Read response and save to result.
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    storesServlet.doGet(request, response);
    String result = stringWriter.getBuffer().toString().trim();
    printWriter.flush();

    Assert.assertEquals("Failed to get the location parameter from the request.", result);
  }

  /** Check if correct error message is sent for empty location. */
  @Test
  public void checkEmptyLocation() throws IOException, ServletException {

    // Mock call for location.
    when(request.getParameter("location")).thenReturn("  ");
    when(request.getParameter("latlng")).thenReturn("  ");

    // Read response and save to result.
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    storesServlet.doGet(request, response);
    String result = stringWriter.getBuffer().toString().trim();
    printWriter.flush();

    Assert.assertEquals("Failed to get location, an address must be submitted.", result);
  }

  /** Check if correct error message is sent for invalid location. */
  @Test
  public void checkNonsenseLocation() throws IOException, ServletException {

    // Mock call for location.
    when(request.getParameter("location")).thenReturn("asdflkasdvojabdskjvaewfnaeskcasdcn");
    when(request.getParameter("latlng")).thenReturn("false");

    // Read response and save to result.
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    storesServlet.doGet(request, response);
    String result = stringWriter.getBuffer().toString().trim();
    printWriter.flush();

    Assert.assertEquals(
        "Failed to find the location of: asdflkasdvojabdskjvaewfnaeskcasdcn", result);
  }

  /** Check if correct error message is sent for invalid location. */
  @Test
  public void checkLocationOutsideUs() throws IOException, ServletException {

    // Mock call for location.
    when(request.getParameter("location")).thenReturn("Toronto");
    when(request.getParameter("latlng")).thenReturn("false");

    // Read response and save to result.
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    storesServlet.doGet(request, response);
    String result = stringWriter.getBuffer().toString().trim();
    printWriter.flush();

    Assert.assertEquals("Failed to find any valid stores near the address: Toronto", result);
  }

  /** Check the formatting for a correct address location. */
  @Test
  public void checkValidLocation() throws IOException, ServletException {

    // Mock call for location.
    when(request.getParameter("location")).thenReturn("Philadelphia");
    when(request.getParameter("latlng")).thenReturn("false");

    // Read response and save to result.
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    storesServlet.doGet(request, response);
    String result = stringWriter.getBuffer().toString().trim();
    printWriter.flush();

    // Test json result to see if certain properties exist.
    try {
      JSONObject resultJson = new JSONObject(result);

      JSONArray stores = resultJson.getJSONArray("stores");
      JSONObject firstStore = stores.getJSONObject(0);
      String storeId = firstStore.getString("id");
    } catch (Exception e) {

      // If error, print error, assert False, and return.
      e.printStackTrace();
      Assert.assertTrue(false);
      return;
    }

    // If all the fields exist, return true.
    Assert.assertTrue(true);
  }

  /** Check the error response of a LatLng location of invalid length and content. */
  @Test
  public void checkInvalidLatLngLength() throws IOException, ServletException {

    // Mock call for location.
    when(request.getParameter("location")).thenReturn("Philadelphia");
    when(request.getParameter("latlng")).thenReturn("true");

    // Read response and save to result.
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    storesServlet.doGet(request, response);
    String result = stringWriter.getBuffer().toString().trim();
    printWriter.flush();

    Assert.assertEquals(
        "Location not provided in latitude, longitude format: Philadelphia", result);
  }

  /** Check the error response of a LatLng location of invalid type. */
  @Test
  public void checkInvalidLatLngType() throws IOException, ServletException {

    // Mock call for location.
    when(request.getParameter("location")).thenReturn("Hello, World");
    when(request.getParameter("latlng")).thenReturn("true");

    // Read response and save to result.
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    storesServlet.doGet(request, response);
    String result = stringWriter.getBuffer().toString().trim();
    printWriter.flush();

    Assert.assertEquals("Invalid value types for latitude, longitude format: Hello, World", result);
  }

  /** Check the formatting for a correct LatLng location. */
  @Test
  public void checkValidLatLng() throws IOException, ServletException {

    // Mock call for location.
    when(request.getParameter("location")).thenReturn("40.7978417,-77.8556184");
    when(request.getParameter("latlng")).thenReturn("true");

    // Read response and save to result.
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    storesServlet.doGet(request, response);
    String result = stringWriter.getBuffer().toString().trim();
    printWriter.flush();

    // Test json result to see if certain properties exist.
    try {
      JSONObject resultJson = new JSONObject(result);

      JSONArray stores = resultJson.getJSONArray("stores");
      JSONObject firstStore = stores.getJSONObject(0);
      String storeId = firstStore.getString("id");
    } catch (Exception e) {

      // If error, print error, assert False, and return.
      e.printStackTrace();
      Assert.assertTrue(false);
      return;
    }

    // If all the fields exist, return true.
    Assert.assertTrue(true);
  }
}
