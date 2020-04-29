# Conferencing sample app for Java using Bandwidth WebRTC

To run this sample, you'll need a Bandwidth phone number, Voice API credentials and WebRTC enabled for your account. Please check with your account manager to ensure you are provisioned for WebRTC.

This sample will need to be publicly accessible to the internet in order for Bandwidth API callbacks to work properly. Otherwise you'll need a tool like [ngrok](https://ngrok.com) to provide access from Bandwidth API callbacks to localhost.

**Unless you are running on `localhost`, you will need to use HTTPS**. Most modern browsers require a secure context when accessing cameras and microphones.

Note that this sample currently works best in Chrome.

### Create a Bandwidth Voice API application
Follow the steps in [How to Create a Voice API Application](https://support.bandwidth.com/hc/en-us/articles/360035060934-How-to-Create-a-Voice-API-Application-V2-) to create your Voice API application.

In step 7 and 8, make sure they are set to POST.

In step 9, provide the publicly accessible URL of your sample app. You need to add `/callback` to the end of this URL in the Voice Application settings.

You do not need to set a callback user id or password. 

Create the application and make note of your _Application ID_. You will provide this in the settings below.

### Get the sample code
This project uses a Git submodule for the frontend. To pull this repo run:

```bash
$ git clone --recursive git@github.com:Bandwidth/webrtc-sample-conference-java.git
$ cd webrtc-sample-conference-java
```

### Configure your sample app
Copy the default configuration file

```bash
$ cp src/main/resources/application.default.yml src/main/resources/application.yml
```

Edit your `application.yml` and add your Bandwidth account settings:

```yaml
accountId: "YOUR_ACCOUNT_ID"
username: "YOUR_USERNAME"
password: "YOUR_PASSWORD
voiceCallbackUrl: "https://YOUR_WEB_SERVER_ADDRESS/callback"
voicePhoneNumber: "(999) 999-9999"
websocketUrl: "wss://device.webrtc.bandwidth.com"
```

### Build the frontend

```bash
$ ./build-frontend
```

### Build and run the backend

```bash
$ mvn spring-boot:run
```

### Start a conference
Browse to [http://localhost:8080](http://localhost:8080) and start a conference.

You should now be able to dial into your phone number, punch in your conference code, and be connected to your conference.