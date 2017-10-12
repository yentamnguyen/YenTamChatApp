/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
/*
 * @author: Trương Nguyễn Yên Tâm - AT110541 - AT11E
 */
package com.parse.starter;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class StarterApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Initialize code
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())

            .applicationId("c6bee1ffa1f00fdf9dbe5c6246d7920c524db1b4")
            .clientKey("a466bfcf92914536c8d81609b4607d6ec25cbad2")
            .server("http://ec2-18-221-139-198.us-east-2.compute.amazonaws.com:80/parse/")
            .build()
    );

    // ParseUser.enableAutomaticUser();

    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

  }
}
