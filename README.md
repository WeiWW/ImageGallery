# ImageGallery
## Introduce
1.  It is a image gallery app. When launch the app or the token is expired during using the app, the login dialog will pop up to ask log in and save the token.
       
<img src="img/login.png" width="200" height="400" />
2. When click the FAB will open the image folder of the device for choosing an image to upload. Once the uploading success, the image gallery would be refresh.
       
<img src="img/upload_image.png" width="200" height="400" />

3. I use Compose, Hilt and MVVM make code more flexible, readable, and easy to maintain.
4. I write some unit tests for the ViewModel and Repository.
5. I also write some instrumented tests for the UI. And connect with Firebase Test Lab to run the tests on multiple devices. Make sure app run steadily on different devices.
  
And we can see the test result on Firebase.
    
<img src="img/Firebase_test_lab_screenshot.png" width="200" height="400" />
