package com.ben.thingscam;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainScheduler extends Activity {
    private static final String TAG = MainScheduler.class.getSimpleName();
    private static final int DELAY = 30000;

    private ThingsCamera camera;
    private Handler timerHandler;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Scheduler Activity created.");

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No permission");
            return;
        }

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());

        camera = ThingsCamera.getInstance();
        camera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);

        timerHandler = new Handler();

        timerHandler.postDelayed(new Runnable(){
            public void run(){
                Log.d(TAG, "Picture Taken");
                camera.takePicture();
                timerHandler.postDelayed(this, DELAY);
            }
        }, DELAY);
    }

    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        auth.signOut();
        auth.signInAnonymously();
        Log.d(TAG, "Signed in to Firebase Anonymously");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.shutDown();

        mCameraThread.quitSafely();
    }

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
            final byte[] imageBytes = new byte[imageBuf.remaining()];
            imageBuf.get(imageBytes);
            image.close();
            onPictureTaken(imageBytes);
        }
    };

    private void onPictureTaken(final byte[] imageBytes) {
        if (imageBytes != null) {
            Log.d(TAG, "Sending Image to Cloud");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String fileName = df.format(now);
            databaseRef.child("images").child(fileName).setValue(Base64.encode(imageBytes, Base64.DEFAULT));
            StorageReference storageRef = storage.getReferenceFromUrl("gs://thingscamera.appspot.com");
            StorageReference imageRef = storageRef.child(fileName + ".jpg");
            imageRef.putBytes(imageBytes);
        }
    }


}
