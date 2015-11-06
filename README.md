# AndroidSensor
`AndroidSensor` is a Android Studio project for collecting Android Snesor data and sending the sensor data to a HOST by using UDP Network protocol(Android as a Client).

By default,HOST is set as `192.168.1.100` and PORT is set as `8898`,which are presented in the app'a UI. You need to edit them according to your own HOST machine.For example,I usually use a Applictaion tool named `NetAssit.exe` in my Windows PC as the Server to receive the sensor data in UDP Network sent by Android app client.

HOW TO TEST:
1. Double click the icon of `NetAssist.exe` to execute;
2. Select `Protocol` as UDP;
3. Set `Local host IP` as any available IP address in the LAN,for instance,192.168.1.100;Set `Local host port` as any available port in your machine,generally a large integer,for instance 8898;
3. Click the button `Connect`,a UDP server is create and listening on the port 8898;
4. Execute the android app,edit the Host to 192.168.1.100 and edit the port to 8898;
5. Immediatlly,in the Data Receive area,something will be presented,which are received from android.
![](http://i.imgur.com/KPw6SkI.jpg)