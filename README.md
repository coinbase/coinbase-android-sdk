# coinbase

Integrate bitcoin into your android application with Coinbase's fully featured bitcoin payments API. Coinbase allows all major operations in bitcoin through one API. For more information, visit https://coinbase.com/docs/api/overview.

TODO(aianus) link to example repo

## Installation

### Using Maven

Add the following dependency to your project's Maven pom.xml:

```xml
<dependency>
  <groupId>com.coinbase.android</groupId>
  <artifactId>coinbase-android-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

If you're using Android Studio with Gradle, the equivalent dependency would be

```gradle
compile ('com.coinbase.android:coinbase-android-sdk:1.0.0')
```

The library will automatically be pulled from Maven Central.

### Manual

You can copy this library jar and all its dependency jars to a folder as follows:

```bash
git clone git@github.com:coinbase/coinbase-android-sdk.git
cd coinbase-android-sdk
mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=$YOUR_JAR_DIRECTORY
mvn package
cp target/coinbase-android-sdk-1.0.0.jar $YOUR_JAR_DIRECTORY
```

## Authentication

The Coinbase Android SDK can be used with both Coinbase API keys and OAuth2 authentication. Use API keys if you only need to access your own Coinbase account from within your application. Use OAuth2 if you need to access your user's accounts. Most Android apps will need to use OAuth2.

### API key authentication

See the documentation for [coinbase-java](https://github.com/coinbase/coinbase-java)

### OAuth2

The Coinbase Android SDK adds some convenient methods on top of coinbase-java to help developers quickly and easily authenticate users via OAuth2.

To use OAuth2 you will need to define a custom scheme for your app and create an intent filter for it in your Android app.

For example, if your app is called 'My Example App', you might want to define a scheme of my-example-app.

You now need to create an OAuth2 application for your Android application at [https://www.coinbase.com/oauth/applications](https://www.coinbase.com/oauth/applications). Click `+ Create an Application` and enter a name for your application. In `Permitted Redirect URIs`, you should enter "your_scheme://coinbase-oauth" - for example, if your custom URI scheme is "my-example-app", then you should enter "my-example-app://coinbase-oauth". Save the application and take note of the Client ID and Secret.

You can now integrate the OAuth2 sign in flow into your application. Use com.coinbase.android.sdk.OAuth.beginAuthorization to redirect the user to Coinbase and begin the authorization process.

```java
static final String REDIRECT_URI = "my-example-app://coinbase-oauth" // Must be the same as entered into 'Create Application' above.
// Launch the web browser or Coinbase app to authenticate the user.
OAuth.beginAuthorization(this, CLIENT_ID, "user", REDIRECT_URI, null);
```

You must add an intent filter to one of your Android activities in order for users to be redirected back to your app after the authorization process.

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="my-example-app" android:pathPrefix="coinbase-oauth" />
</intent-filter>
```

```java
import com.coinbase.android.sdk.OAuth;

public class CompleteAuthorizationTask extends RoboAsyncTask<OAuthTokensResponse> {
  private Intent mIntent;

  public CompleteAuthorizationTask(Intent intent) {
    super(MainActivity.this);
    mIntent = intent;
  }

  @Override
  public OAuthTokensResponse call() throws Exception {
    return OAuth.completeAuthorization(MainActivity.this, CLIENT_ID, CLIENT_SECRET, mIntent.getData());
  }

  @Override
  public void onSuccess(OAuthTokensResponse tokens) {
    // Do something with the tokens
  }

  @Override
  public void onException(Exception ex) {
    // Authorization failed for whatever reason
  }
}

// In the activity containing the redirect intent filter:

@Override
protected void onNewIntent(final Intent intent) {
  if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW")) {
    new CompleteAuthorizationTask(intent).execute();
  }
}
```

// TODO(aianus) link to example

## Usage

See the documentation for [coinbase-java](https://github.com/coinbase/coinbase-java) for supported methods and usage information.

## License

coinbase-android-sdk is available under the MIT license. See the LICENSE file for more info.
