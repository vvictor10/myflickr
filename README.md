## MyFlickr - Photo Viewer based on the Flickr Open API

The app enables users to search for photos on the Flickr platform. Clicking on a search result item from the paginated list displays the image on a full screen, which can be viewed in Portrait and Landscape mode. The app also saves the photos data retrieved from Flickr in a local database, which is used a fallback during offline mode.

### Libraries

Apart from the Android SDK & other standard libraries, few other open source libraries were used in the project. Here is the list with their corresponding purposes:

1. **Dagger** - Dependency Injection.
2. **RxJava** - Asynchronous & Event based programming.
3. **OkHttp** - Networking.
4. **Retrofit** - Networking.
5. **Gson** - Serialization/De-Serialization.
6. **Picasso** - Image loading.
7. **Butterknife** - View binding.
8. **Timber** - Logging.
9. **Room** - Local database.
10. **ReactiveNetwork** - Network Monitoring.
11. **Junit** - Unit testing.
12. **AssertJ** - Assertions.
13. **Mockito** - Android Instrumentation tests.
14. **Espresso** - Instrumentation tests.

### Technicals

#### Tools & Build Instructions

I have used Android Studio 4.0.2 to develop this project. Tried to stick to tools/libraries that I am familiar with to make the best use of time. Steps to build/deploy the app:

  1. Clone the master branch from the repository.
  2. Before opening the project on your Android Studio, please ensure that Gradle 6.1.1 is installed on your machine.
  3. Open the '_myflickr_' project in your Android Studio.
  4. Do a 'Build' -> 'Clean'. If the Clean command executes successfully, you should be ready to deploy the app to a connected device or your emulator instance.
  5. The 'app' module should be auto-selected in the deployment configuration drop-down. Hit the Run app button to deploy and if all goes well, the '_MyFlickr_' app should be ready to test on your target device.

#### Unit Testing

Added few tests for the different types of test we would want to expand on. There are some unit tests and instrumentation tests as well. Given the scope of this exercise, I have limited the amount of time spent on tests.

Tests can be executed by running the gradle task: '_test_'.

#### Flickr API

The Flickr API keys are specified in the '_myflickr.properties_' file.

I use the PAW tool to test network request/responses and I have added the PAW config file for the Venues API in the '_resources_' directory.


