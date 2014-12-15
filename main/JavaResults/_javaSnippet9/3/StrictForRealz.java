

package com.demo;

import android.os.StrictMode;

class StrictForRealz extends StrictWrapper {
  StrictForRealz() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                .detectAll()
                                .penaltyLog()
                                .build());
  }
}
