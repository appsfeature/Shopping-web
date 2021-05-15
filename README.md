## Browser
  
## Setup 
Add this to your project build.gradle
``` gradle
allprojects {
    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
}
```

#### Dependency
[![](https://jitpack.io/v/appsfeature/browser.svg)](https://jitpack.io/#appsfeature/browser)
```gradle
dependencies {
        implementation 'com.github.appsfeature:browser:1.0'
}
```
#### Usage  
```java
    BrowserSdk.openAppBrowser(this, "Title", AppConstant.REQUEST_URL);
```
 
